package fr.ign.validator;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorFactory;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.Model;
import fr.ign.validator.process.CharsetPreProcess;
import fr.ign.validator.process.FilterMetadataPreProcess;
import fr.ign.validator.process.NormalizePostProcess;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.report.ReportBuilder;
import fr.ign.validator.string.StringFixer;
import fr.ign.validator.string.transform.DoubleUtf8Decoder;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.string.transform.EscapeForCharset;
import fr.ign.validator.string.transform.StringSimplifier;
import fr.ign.validator.validation.Validatable;

/**
 * Le contexte de validation qui porte 
 * 
 * <ul>
 * 	<li>DocumentModel : le modèle de document</li>
 *  <li>validationDirectory : le répertoire de travail pour la validation</li>
 *  <li>reportBuilder : le gestionnaire d'erreur</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class Context {
	/**
	 * L'encodage des données définit par défaut à UTF_8. Peut être modifié dans une 
	 * étape de pré-validation (lecture de métadonnées)
	 */
	private Charset encoding = StandardCharsets.UTF_8 ;
	
	/**
	 * Le système de coordonnées des données
	 */
	private CoordinateReferenceSystem coordinateReferenceSystem ;
	
	/**
	 * L'étendue dans laquelle la géométrie est attendue (projection identique à celle des données)
	 */
	private Geometry nativeDataExtent ;

	/**
	 * Le répertoire de base utilisé pour tester l'existance des fichiers
	 * (<=> documentPath)
	 */
	private File currentDirectory ;
	/**
	 * Le répertoire de validation
	 */
	private File validationDirectory ;
	/**
	 * La fabrique d'erreur
	 */
	private ErrorFactory errorFactory ;
	
	/**
	 * La pile des modèles en cours de validation
	 */
	private List<Model> modelStack = new ArrayList<Model>() ;
	/**
	 * Pile d'emplacement de validation (fichier, ligne, etc.)
	 */
	private List<Validatable> dataStack = new ArrayList<Validatable>();	
	
	/**
	 * Le générateur de rapport d'erreur
	 */
	private ReportBuilder reportBuilder = new InMemoryReportBuilder() ;
	
	/**
	 * Les écouteurs d'événements de validation
	 */
	private List<ValidatorListener> listeners = new ArrayList<ValidatorListener>() ;

	/**
	 * mode de validation
	 */
	private boolean flatValidation;
	
	/**
	 * Option de validation des caractères
	 */
	private StringFixer stringFixer = new StringFixer();

	
	public Context(){
		this(ErrorFactory.newFromRessource());
	}

	/**
	 * Construction avec une fabrique d'erreur
	 * @param errorFactory
	 */
	public Context(ErrorFactory errorFactory){
		this.errorFactory = errorFactory ;
		registerDefaultListeners();
	}
	
	/**
	 * add listener
	 * @param listener
	 */
	public void addListener( ValidatorListener listener ){
		this.listeners.add(listener);
	}
	
	/**
	 * Get validatorListeners
	 * @return
	 */
	public List<ValidatorListener> getValidatorListeners(){
		return this.listeners ;
	}
	

	/**
	 * Chargement des processus par défaut de l'application
	 * - (re-)création du dossier de validation
	 * - preparation des donnes en vue de validation (csv)
	 * - preparation des donnes en vue d'export en base (shp)
	 * - extraction d'informations sur les fichiers traitées
	 */
	private void registerDefaultListeners(){
		addListener( new FilterMetadataPreProcess() ); // before CharsetPreProcess
		addListener( new CharsetPreProcess() );
		addListener( new NormalizePostProcess() ); 
	}

	
	/**
	 * @return the encoding
	 */
	public Charset getEncoding() {
		return encoding;
	}
	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * @return the coordinateReferenceSystem
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	/**
	 * @param coordinateReferenceSystem the coordinateReferenceSystem to set
	 */
	public void setCoordinateReferenceSystem( CoordinateReferenceSystem coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	public boolean hasNativeDataExtent(){
		return nativeDataExtent != null ;
	}
	
	public Geometry getNativeDataExtent() {
		return nativeDataExtent;
	}

	public void setNativeDataExtent(Geometry extent) {
		this.nativeDataExtent = extent;
	}

	/**
	 * @return the currentDirectory
	 */
	public File getCurrentDirectory() {
		return currentDirectory;
	}
	/**
	 * @param currentDirectory the currentDirectory to set
	 */
	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	/**
	 * Get ErrorFactory
	 * @return
	 */
	public ErrorFactory getErrorFactory() {
		return errorFactory;
	}
	/**
	 * Set ErrorFactory
	 * @param errorFactory
	 */
	public void setErrorFactory(ErrorFactory errorFactory) {
		this.errorFactory = errorFactory;
	}
	
	/**
	 * Début de la validation d'un modèle
	 * @param model
	 */
	public void beginModel( Model model ){
		modelStack.add(model);
	}
	/**
	 * Renvoie la pile des modèles
	 * @return
	 */
	public List<Model> getModelStack(){
		return this.modelStack ;
	}
	/**
	 * Renvoie le modèle courant pour un type donné
	 * @param clazz
	 * @return
	 */
	public <T extends Model> T getModelByType(Class<T> clazz){
		for ( int index = modelStack.size() - 1; index >= 0; index-- ) {
			Model model = modelStack.get(index);
			if ( clazz.isInstance(model) ){
				return clazz.cast(model);
			}
		}
		return null;
	}
	
	/**
	 * Renvoie le DocumentModel courant
	 * @return
	 */
	public DocumentModel getDocumentModel() {
		return this.getModelByType(DocumentModel.class);
	}

	/**
	 * Fin de la validation d'un modèle
	 */
	public void endModel( Model model ){
		int position = modelStack.size() - 1 ;
		if ( position < 0 ){
			throw new RuntimeException("gestion inconsistente de beginModel/endModel"); 
		}
		modelStack.remove(position) ;
	}
	
	/**
	 * Début de validation d'une donnée
	 * @param location
	 */
	public void beginData(Validatable data){
		dataStack.add(data);
	}
	/**
	 * Renvoie la pile de position
	 * @return
	 */
	public List<Validatable> getDataStack(){
		return dataStack ;
	}
	/**
	 * Renvoie la données courante pour un type
	 * @param clazz
	 * @return
	 */
	public <T extends Validatable> T getDataByType(Class<T> clazz){
		for ( int index = dataStack.size() - 1; index >= 0; index-- ) {
			Validatable model = dataStack.get(index);
			if ( clazz.isInstance(model) ){
				return clazz.cast(model);
			}
		}
		return null;
	}
	/**
	 * Fin de validation d'une donnée
	 * @param location
	 */
	public void endData(Validatable data){
		int position = dataStack.size() - 1 ;
		if ( position < 0 || dataStack.get(position) != data){
			throw new RuntimeException("gestion inconsistente de beginModel/endModel"); 
		}
		dataStack.remove(position) ;
	}
	
	/**
	 * Relativize path
	 * @param path
	 * @return
	 */
	public String relativize( File path ){
		DocumentModel documentModel = getDocumentModel() ;
		if ( documentModel != null ){
			return currentDirectory.toPath().relativize(
				path.toPath()
			).toString() ;
		}else{
			return path.getName() ;
		}
	}
	
	/**
	 * Construit un message d'erreur non localisé.
	 * 
	 * @param codeError
	 * @param messageParams
	 */
	public void report(ErrorCode code, Object... messageParams){
		ValidatorError validatorError = errorFactory.newError(code,messageParams) ;
		reportBuilder.addError(this, validatorError);
	}
	
	/**
	 * Calcul et renvoie le répertoire DATA dans le répertoire validation pour le document courant
	 * @return
	 */
	public File getDataDirectory(){
		return new File(validationDirectory, getCurrentDirectory().getName()+"/DATA");
	}
	
	/**
	 * Calcul et renvoie le répertoire METADATA dans le répertoire validation pour le document courant
	 * @return
	 */
	public File getMetadataDirectory() {
		return new File(validationDirectory, getCurrentDirectory().getName()+"/METADATA");
	}
	
	
	/**
	 * Get ValidationDirectory
	 * @return
	 */
	public File getValidationDirectory() {
		return validationDirectory;
	}
	/**
	 * Set ValidationDirectory
	 * @return
	 */
	public void setValidationDirectory(File validationDirectory) {
		this.validationDirectory = validationDirectory;
	}
	
	/**
	 * Get report builder
	 * @return
	 */
	public ReportBuilder getReportBuilder() {
		return reportBuilder;
	}
	
	/**
	 * Set report builder
	 * @param reportBuilder
	 */
	public void setReportBuilder(ReportBuilder reportBuilder) {
		this.reportBuilder = reportBuilder;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isFlatValidation() {
		return flatValidation;
	}

	/**
	 * 
	 * @param flexibleValidation
	 */
	public void setFlatValidation(boolean flatValidation) {
		this.flatValidation = flatValidation;
	}

	/**
	 * @return
	 */
	public StringFixer getStringFixer() {
		return stringFixer;
	}

	/**
	 * @param stringFixer
	 */
	public void setStringFixer(StringFixer stringFixer) {
		this.stringFixer = stringFixer;
	}
	
}
