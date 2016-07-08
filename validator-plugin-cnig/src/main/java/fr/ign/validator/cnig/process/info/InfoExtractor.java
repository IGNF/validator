package fr.ign.validator.cnig.process.info;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.jdom.JDOMException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.data.DataDirectory;
import fr.ign.validator.data.DataLayer;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.Document;
import fr.ign.validator.model.DocumentFile;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.reader.MetadataReader;
import fr.ign.validator.tools.TyperefExtractor;
import fr.ign.validator.utils.IdurbaUtils;

/**
 * 
 * Extraction des informations à partir du répertoire de validation
 * d'un document
 * 
 * @author CBouche
 * @author MBorne
 *
 */
public class InfoExtractor {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("INFO_EXTRACTOR") ;
	
	
	
	
	/**
	 * Récupération des informations d'un dossier
	 * @param documentName
	 * @param validationDirectory
	 * @return
	 * @throws MismatchedDimensionException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public DataDirectory parseDocument(Context context, Document document) throws MismatchedDimensionException, IOException, FactoryException, TransformException{
		File validationDirectory = context.getValidationDirectory() ;
		String documentName = document.getDocumentName() ;
		DataDirectory rootDirectory = new DataDirectory("validation") ;
		
		DataDirectory documentDirectory = parseFiles(documentName, validationDirectory) ;
		documentDirectory.setTyperef(parseTyperef(context,documentName, validationDirectory));
		parseMetadataFileIdentifier( document, documentDirectory );
		rootDirectory.addRegisterRepertory(documentDirectory);
		
		return rootDirectory ;
	}
	
	
	/**
	 * Récupération du fileIdentifier dans la fiche de métadonnée
	 * @param document
	 * @return
	 */
	private void parseMetadataFileIdentifier(Document document, DataDirectory documentDirectory) {
		List<DocumentFile> metadataFiles = document.getDocumentFiles(MetadataModel.class);
		if ( metadataFiles.isEmpty() ){
			return ;
		}
		if ( metadataFiles.size() > 1 ){
			log.warn(MARKER, "Il y a {} fiche de métadonnée, utilisation de la première",metadataFiles.size()) ;
		}
		
		File metadataPath = metadataFiles.get(0).getPath() ;
		try {
			MetadataReader reader = new MetadataReader(metadataPath);
			documentDirectory.setMetadataFileIdentifier( reader.getFileIdentifier() ) ;
			documentDirectory.setMetadataMdIdentifier( reader.getMDIdentifier() ) ;
		} catch (JDOMException e) {
			log.warn(MARKER, "Erreur dans la lecture de la fiche de métadonnée");
		} catch (IOException e) {
			log.warn(MARKER, "Erreur dans la lecture de la fiche de métadonnée");
		}
	}


	/**
	 * Récupération de la valeur du champ TYPEREF dans le fichier DOC_URBA.csv
	 * @param context 
	 * @return null si non trouvé
	 */
	private String parseTyperef(Context context, String documentName, File validationDirectory){
		
		String regexpIDURBA = IdurbaUtils.getRegexp(documentName) ;
		if ( null == regexpIDURBA ){
			return null ;
		}
		
		File documentDirectory = new File(validationDirectory,documentName);
		File dataDirectory     = new File(documentDirectory,"DATA") ;
		File docUrbaFile       = new File(dataDirectory,"DOC_URBA.csv") ;

		if ( ! docUrbaFile.exists() ){
			log.info(MARKER,"Impossible d'extraire TYPEREF, DOC_URBA non trouvée");
		}
		
		TyperefExtractor typerefExtractor = new TyperefExtractor(docUrbaFile);
		String result = typerefExtractor.findTyperef(documentName) ;
		if ( null == result ){
			context.report(
				ErrorCode.CNIG_IDURBA_NOT_FOUND, 
				IdurbaUtils.getRegexp(documentName)
			);
		}
		return result ;
	}
	
	
	/**
	 * parse repertory
	 * TODO test function recursive
	 * @param directoryName
	 * @param file
	 * @throws IOException 
	 * @throws FactoryException 
	 * @throws TransformException 
	 * @throws MismatchedDimensionException 
	 */
	private DataDirectory parseFiles( String directoryName, File directory ) throws IOException, FactoryException, MismatchedDimensionException, TransformException {
		DataDirectory registerRootRepertory = new DataDirectory( directoryName ) ;
		/*
		 * Récupération de la liste des fichiers
		 */
		File[] files = directory.listFiles() ;
		for (int i = 0; i < files.length; i++) {
			if ( files[i].isDirectory() ) {
				DataDirectory registerRepertory = parseFiles( files[i].getName(), files[i] ) ;
				registerRootRepertory.addRegisterRepertory( registerRepertory );
				continue ;
			}
			
			/*
			 * test extension du fichier
			 */
			String ext = FilenameUtils.getExtension(files[i].getName());
			if (ext.equals("shp")) {
				registerRootRepertory.addRegisterLayer( getRegisterLayerFromDataSource( files[i] ) );
			}else if(ext.equals("pdf")||ext.equals("PDF")){
				registerRootRepertory.addRegisterFile(new DataLayer(files[i].getName()));
			}
		}
		return registerRootRepertory ;
	}
	
	/**
	 * TODO Sortir dans tools et ajouter des tests unitaires
	 * @param layerFile
	 * @return
	 * @throws IOException
	 * @throws FactoryException
	 * @throws MismatchedDimensionException
	 * @throws TransformException
	 */
	private DataLayer getRegisterLayerFromDataSource( File layerFile ) throws IOException, FactoryException, MismatchedDimensionException, TransformException {
		log.debug(
			MARKER, 
			"récupération de la BBOX du fichier {}",
			layerFile
		);
	
		DataLayer layer = new DataLayer( layerFile.getName() ) ;
		/*
		 * ouverture du dataStore
		 */
		Map<String, URL> map = new HashMap<String, URL>();      
		map.put("url", layerFile.toURI().toURL());
		DataStore dataStore = DataStoreFinder.getDataStore( map );
		SimpleFeatureSource featureSource = dataStore.getFeatureSource( dataStore.getTypeNames()[0] );    
		
		/*
		 * Récupération de la bbox (remarque : les données sont normalisées en projection EPSG:4326)
		 */
		Envelope env = featureSource.getFeatures().getBounds();
		if ( ! isNullEnvelope(env) ){
			layer.setLayerBbox(env.getMinX()+","+env.getMinY()+","+env.getMaxX()+","+env.getMaxY());			
		}

		/*
		 * Fermeture du dataStore
		 */
		dataStore.dispose();
		return layer ;
	}
	
	/**
	 * Indique si l'enveloppe est nulle
	 * @param env
	 * @return
	 */
	private boolean isNullEnvelope(Envelope env){
		Envelope zero = new Envelope(0.0,0.0,0.0,0.0);
		if ( env.isNull() ){
			return true ;
		}else if (env.equals(zero) ){
			return true ;
		}else{
			return false;
		}		
	}

	
	
}
