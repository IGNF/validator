package fr.ign.validator.report;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.error.format.LegacyFormat;

/**
 * 
 * Writes an XML validation report using log4j2. 
 * 
 * Note that :
 * 
 * <ul>
 *  <li>Informations are formatted in log4j2 message using | concatenation</li>
 * 	<li>A ThreadContext variable ("path") defines output path</li>
 *  <li>log4j2.xml is embedded in validator-cli.jar</li>
 * </ul>
 * 
 * @see {@link LegacyFormat} 
 * @see validator-cli/src/main/resources/log4j2.xml
 * 
 * @author FCerizay
 *
 */
public class ReportBuilderLegacy implements ReportBuilder {

	public static final Logger log = LogManager.getLogger( ReportBuilderLegacy.class ) ;
		
	private LegacyFormat format = new LegacyFormat();
	
	/**
	 * Constructor
	 * @throws IOException 
	 */
	public ReportBuilderLegacy(File validationRapport) {
		ThreadContext.put("path", validationRapport.getAbsolutePath().toString());
	}

	@Override
	public void addError(ValidatorError error) {
		ErrorLevel level  = error.getLevel() ;
		
		String logMessage = format.write(error);
		if ( level.equals( ErrorLevel.FATAL ) ) {
			log.fatal(logMessage) ;
		}else if ( level.equals( ErrorLevel.ERROR ) ) {
			log.error(logMessage) ;
		}else if ( level.equals( ErrorLevel.WARNING ) ) {
			log.warn(logMessage) ;
		}else if ( level.equals( ErrorLevel.INFO ) ) {
			log.info(logMessage) ;
		}else if ( level.equals( ErrorLevel.DEBUG ) ) {
			log.debug(logMessage) ;
		}else {
			throw new RuntimeException( String.format(
				"Le niveau d'erreur {} n'est pas pris en compte", level 
			) ) ;
		}
	}

}
