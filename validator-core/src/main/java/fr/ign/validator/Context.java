package fr.ign.validator;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Row;
import fr.ign.validator.data.file.MetadataFile;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorFactory;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Model;
import fr.ign.validator.process.CharsetPreProcess;
import fr.ign.validator.process.FilterMetadataPreProcess;
import fr.ign.validator.process.NormalizePostProcess;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.report.ReportBuilder;
import fr.ign.validator.string.StringFixer;
import fr.ign.validator.validation.Validatable;

/**
 * 
 * Validation context
 *  
 * @author MBorne
 *
 */
public class Context {

	/**
	 * Data charset (overridden by metadata provided charset)
	 * @see CharsetPreProcess
	 */
	private Charset encoding = StandardCharsets.UTF_8 ;
	
	/**
	 * Data CoordinateReferenceSystem (provided as a command line option)
	 */
	private CoordinateReferenceSystem coordinateReferenceSystem ;
	
	/**
	 * Expected data extent (same CoordinateReferenceSystem than data)
	 */
	private Geometry nativeDataExtent ;

	/**
	 * Allows to disable strict file hierarchy validation (find files by name instead of path)
	 */
	private boolean flatValidation;
	
	/**
	 * Configures deep character validation
	 */
	private StringFixer stringFixer = new StringFixer();
	
	
	/**
	 * Input - Current data directory (equivalent to documentPath)
	 * 
	 * TODO remove this variable and rely on documentPath
	 */
	private File currentDirectory ;
	
	/**
	 * Output - validation directory containing validation and normalization results
	 */
	private File validationDirectory ;
	
	/**
	 * Execution context - modelStack
	 */
	private List<Model> modelStack = new ArrayList<Model>() ;
	/**
	 * Execution context - dataStack
	 */
	private List<Validatable> dataStack = new ArrayList<Validatable>();	
	

	/**
	 * Reporting - Create errors according to configuration files (template string and ErrorLevel)
	 */
	private ErrorFactory errorFactory = new ErrorFactory();
	
	/**
	 * Reporting - Generates validation report
	 */
	private ReportBuilder reportBuilder = new InMemoryReportBuilder() ;
	

	/**
	 * Customization - validation listener
	 */
	private List<ValidatorListener> listeners = new ArrayList<ValidatorListener>() ;

	
	public Context(){
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
	 * Load defaults validation listeners
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
	 * @return
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	/**
	 * @param coordinateReferenceSystem
	 */
	public void setCoordinateReferenceSystem( CoordinateReferenceSystem coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}
	
	/**
	 * @return
	 */
	public boolean hasNativeDataExtent(){
		return nativeDataExtent != null ;
	}
	/**
	 * @return
	 */
	public Geometry getNativeDataExtent() {
		return nativeDataExtent;
	}
	/**
	 * @param extent
	 */
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
	 * TODO remove and rely on dataStack
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
	 * Push given model to stack
	 * @param model
	 */
	public void beginModel( Model model ){
		modelStack.add(model);
	}

	/**
	 * Get model stack
	 * @return
	 */
	public List<Model> getModelStack(){
		return this.modelStack ;
	}

	/**
	 * Get current model by type
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
	 * Get current document model
	 * @return
	 */
	public DocumentModel getDocumentModel() {
		return this.getModelByType(DocumentModel.class);
	}

	/**
	 * Get current document model name
	 * @param context
	 * @return
	 */
	public String getDocumentModelName(){
    	DocumentModel documentModel = getModelByType(DocumentModel.class);
		if ( documentModel != null ){
			return documentModel.getName();
		}
		return "";
	}

    /**
     * Get current file model name
     * @param context
     * @return
     */
    public String getFileModelName(){
		FileModel fileModel = getModelByType(FileModel.class);
		if ( fileModel != null ){
			return fileModel.getName();
		}
		return "";
	}

    /**
     * Get current attribute name
     * @param context
     * @return
     */
    public String getAttributeName(){
    	AttributeType<?> attribute = getModelByType(AttributeType.class);
		if ( attribute != null ){
			return attribute.getName();
		}
		return "";
	}
    
	
	/**
	 * Pop given model from stack
	 */
	public void endModel( Model model ){
		int position = modelStack.size() - 1 ;
		if ( position < 0 ){
			throw new RuntimeException("inconsistent beginModel/endModel management"); 
		}
		modelStack.remove(position) ;
	}
	
	/**
	 * Begin data validation (push data on dataStack)
	 * @param location
	 */
	public void beginData(Validatable data){
		dataStack.add(data);
	}
	/**
	 * Get data stack
	 * @return
	 */
	public List<Validatable> getDataStack(){
		return dataStack ;
	}
	/**
	 * Get data by type
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
	 * Get current scope according to data stack
	 * @return
	 */
	public ErrorScope getScope(){
		if ( getDataByType(Attribute.class) != null ){
			return ErrorScope.FEATURE;
		}else if ( getDataByType(TableFile.class) != null ){
			return ErrorScope.HEADER;
		}else if ( getDataByType(MetadataFile.class) != null ){
			return ErrorScope.METADATA;
		}else{
			return ErrorScope.DIRECTORY;
		}
	}
	
	/**
	 * Get current fileName according to data stack
	 * @param context
	 * @return
	 */
	public String getFileName(){
		DocumentFile documentFile = getDataByType(DocumentFile.class);
		if ( documentFile != null ){
			return relativize( documentFile.getPath() ) ;
		}
		Document document = getDataByType(Document.class);
		if ( document != null ){
			return document.getDocumentName()+"/";
		}
		return "";
	}
	
	/**
	 * Get current line number
	 * @param context
	 * @return
	 */
    public String getLine(){
		Row row = getDataByType(Row.class);
		if ( row != null ){
			return ""+row.getLine();
		}
		return "";
	}
	
	/**
	 * End data validation (pop data from dataStack)
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
	 * Create and report an error according to its code
	 * 
	 * @param code
	 * @param messageParams
	 */
	public void report(ErrorCode code, Object... messageParams){
		/*
		 * Create error by code
		 */
		ValidatorError validatorError = errorFactory.newError(code,messageParams) ;
		validatorError.setScope(getScope());
		/*
		 * Add model informations
		 */
		validatorError.setDocumentModel(getDocumentModelName());
		validatorError.setFileModel(getFileModelName());
		validatorError.setAttribute(getAttributeName());
		/*
		 * Add data informations
		 */
		validatorError.setFile(getFileName());
		validatorError.setId(getLine());

		reportBuilder.addError(validatorError);
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
	 * Get DATA directory ({validation_dir}/DATA)
	 * @return
	 */
	public File getDataDirectory(){
		return new File(validationDirectory, getCurrentDirectory().getName()+"/DATA");
	}

	/**
	 * Get metadata directory ({validation_dir}/METADATA)
	 * @return
	 */
	public File getMetadataDirectory() {
		return new File(validationDirectory, getCurrentDirectory().getName()+"/METADATA");
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
