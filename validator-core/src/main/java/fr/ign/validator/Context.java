package fr.ign.validator;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

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
import fr.ign.validator.model.Projection;
import fr.ign.validator.process.DocumentInfoExtractorPostProcess;
import fr.ign.validator.process.FilterMetadataPreProcess;
import fr.ign.validator.process.MetadataPreProcess;
import fr.ign.validator.process.NormalizePostProcess;
import fr.ign.validator.process.ProjectionPreProcess;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.report.ReportBuilder;
import fr.ign.validator.repository.ProjectionRepository;
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
	 * @see MetadataPreProcess
	 */
	private Charset encoding = StandardCharsets.UTF_8 ;

	/**
	 * Data CoordinateReferenceSystem (provided as a command line option)
	 */
	private Projection projection = ProjectionRepository.getInstance().findByCode("CRS:84");

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

	
	/**
	 * Tolerance for geometric operation such as intersection
	 */
	private Double dgprTolerance;

	
	/**
	 * Simplification distance
	 */
	private Double dgprSimplification;
	
	
	/**
	 * Allow using faster simplification against topologicaly safe simplification
	 */
	private Boolean dgprSafeMode;


	public Context(){
		registerDefaultListeners();
	}

	/**
	 * Get validatorListeners
	 * @return
	 */
	public List<ValidatorListener> getValidatorListeners(){
		return this.listeners ;
	}

	/**
	 * add listener
	 * @param listener
	 */
	public void addListener( ValidatorListener listener ){
		this.listeners.add(listener);
	}

	/**
	 * Add listener before the first listener of a given class
	 * @param listener
	 * @param clazz
	 */
	public void addListenerBefore(ValidatorListener listener, Class<?> clazz) {
		int index = findListener(clazz);
		if ( index < 0 ){
			index = 0 ;
		}
		this.listeners.add(index, listener);
	}

	/**
	 * Find listener for a given class
	 * @param clazz
	 * @return
	 */
	private int findListener(Class<?> clazz){
		for ( int i = 0; i < listeners.size(); i++ ) {
			if ( clazz.isInstance(listeners.get(i)) ){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Load defaults validation listeners
	 */
	private void registerDefaultListeners(){
		// Filter XML files
		addListener( new FilterMetadataPreProcess() );
		// Extract informations such as charset from metadata (after FilterMetadataPreProcess)
		addListener( new MetadataPreProcess() );
		// Info and warning about CRS
		addListener( new ProjectionPreProcess() );

		// generate CSV
		addListener( new NormalizePostProcess() );
		// produce document-info.json
		addListener( new DocumentInfoExtractorPostProcess() );
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
	public Projection getProjection(){
		return projection ;
	}

	/**
	 * TODO rename to setProjection
	 * @param coordinateReferenceSystem
	 */
	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	/**
	 * Set projection from CRS code
	 * @param code
	 */
	public void setProjection(String code){
		Projection projection = ProjectionRepository.getInstance().findByCode(code);
		if ( projection == null ){
			throw new RuntimeException("projection "+code+" non reconnue");
		}
		this.projection = projection;
	}

	/**
	 * Get CRS corresponding to projection
	 * @return
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return projection.getCRS();
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
	public <T extends Validatable> T getDataByType(Class<T> clazz) {
		for ( int index = dataStack.size() - 1; index >= 0; index-- ) {
			Validatable model = dataStack.get(index);
			if ( clazz.isInstance(model) ) {
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
		if ( getDataByType(Attribute.class) != null ) {
			return ErrorScope.FEATURE;
		} else if ( getDataByType(TableFile.class) != null ) {
			return ErrorScope.HEADER;
		} else if ( getDataByType(MetadataFile.class) != null ) {
			return ErrorScope.METADATA;
		} else {
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
	 * Get current feature bouding box
	 * @return string
	 */
	public Envelope getFeatureBBox() {
		Row row = getDataByType(Row.class);
		if (row != null) {
			return row.getFeatureBbox();
		}
		return new Envelope();
	}

	/**
	 * Get current identifiant
	 * @return
	 */
	public String getFeatureId() {
		Row row = getDataByType(Row.class);
		if (row != null) {
			return row.getFeatureId();
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
	public void report(ErrorCode code){
		ValidatorError validatorError = createError(code);
		reportBuilder.addError(validatorError);
	}

	/**
	 * Direct reporting of an existing error
	 * @param validatorError
	 */
	public void report(ValidatorError validatorError) {
		reportBuilder.addError(validatorError);
	}

	/**
	 * Generic method for building errors
	 * @param code
	 * @param messageParams
	 */
	public ValidatorError createError(ErrorCode code) {
		//TODO remove messageParams
		ValidatorError validatorError = errorFactory.newError(code);
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
		/*
		 * Add data informations (new)
		 */
		validatorError.setFeatureBbox(getFeatureBBox());
		validatorError.setFeatureId(getFeatureId());

		return validatorError;
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


	/**
	 * tolerance option
	 * used in geometric operation (dgpr plugin)
	 * @return
	 */
	public Double getDgprTolerance() {
		return this.dgprTolerance;
	}


	/**
	 * tolerance option
	 * used in geometric operation (dgpr plugin)
	 * @param topologicalTolerance
	 */
	public void setDgprTolerance(double topologicalTolerance) {
		this.dgprTolerance = topologicalTolerance;
	}

	/**
	 * simplification distance option
	 * @return distance
	 */
	public Double getDgprSimplification() {
		return dgprSimplification;
	}
	
	/**
	 * simplification distance option
	 * @param distanceSimplification
	 */
	public void setDgprSimplification(Double distanceSimplification) {
		this.dgprSimplification = distanceSimplification;
	}
	
	/**
	 * safe simplification option
	 * @return safe simplication allowed
	 */
	public Boolean isDgprSafeMode() {
		return dgprSafeMode;
	}
	
	/**
	 * safe simplification option
	 * @param safeSimplification
	 */
	public void setDgprSafeMode(boolean safeSimplification) {
		this.dgprSafeMode = safeSimplification;
	}

}
