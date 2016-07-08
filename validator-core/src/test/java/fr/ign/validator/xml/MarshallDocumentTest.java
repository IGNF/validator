package fr.ign.validator.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.model.file.TableModel;


public class MarshallDocumentTest extends TestCase {

	public static final Logger logger = LogManager.getRootLogger() ;
	
	private DocumentModel document ;
	
	@Override
	protected void setUp() throws Exception {
		document = new DocumentModel() ;
		document.setName("ccccc_CC_dddddddd");
		document.setRegexp("[0-9]{5}_CC_[0-9]{8}");
		
		// DOC_URBA_COM
		List<FileModel> files = new ArrayList<FileModel>();
		
		{
			FileModel file = new TableModel();
			file.setName("DOC_URBA_COM");
			file.setRegexp("(N_)?DOC(UMENT)?_URBA_COM(_(02A|02B|[0-9]{3}))?");
			file.setMandatory(FileModel.MandatoryMode.WARN);
			files.add(file);
		}
		
		// Donnees_geographiques
		{
			FileModel file = new DirectoryModel();
			file.setName("Donnees_geographiques");
			file.setRegexp("Donnees_geographiques");
			file.setMandatory(FileModel.MandatoryMode.ERROR);
			files.add(file);
		}
		
		// Donnees_geographiques/HABILLAGE_LIN
		{
			FileModel file = new TableModel(); 
			file.setName("HABILLAGE_LIN");
			file.setRegexp("Donnees_geographiques/(N_)?HABILLAGE_LIN(_[0-9]{5})?(_(02A|02B|[0-9]{3}))?");
			files.add(file) ;
		}	
		
		document.setFileModels(files);
	}
	
	@Override
	protected void tearDown() throws Exception {
		document = null ;
	}
	
	@Test
	public void testMarshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(DocumentModel.class) ;
			
			Marshaller marshaller = context.createMarshaller() ;
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			//marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new JaxbNamespacePrefixMapper());
			
			StringWriter output = new StringWriter() ;
			marshaller.marshal(document, output);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
