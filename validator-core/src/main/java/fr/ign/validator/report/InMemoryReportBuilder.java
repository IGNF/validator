package fr.ign.validator.report;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;

/**
 * 
 * Stockage des erreurs de validation en mémoire
 * 
 * @author MBorne
 *
 */
public class InMemoryReportBuilder implements ReportBuilder {

	/**
	 * La liste des erreurs rencontrées
	 */
	private List<ValidatorError> errors = new ArrayList<ValidatorError>();
	
	@Override
	public void addError(Context context, ValidatorError error) {
		errors.add(error);
	}
	
	/**
	 * Renvoie la liste des erreurs rencontrées
	 * @return
	 */
	public List<ValidatorError> getErrors(){
		return this.errors ;
	}
	
	/**
	 * Comptage du nombre total d'erreur
	 * @return
	 */
	public int countErrors(){
		return this.errors.size() ;
	}
	
	/**
	 * Comptate des erreurs en fonction d'un niveau de gravité
	 * @param level
	 * @return
	 */
	public int countErrors(ErrorLevel level){
		int result = 0 ;
		for (ValidatorError validatorError : errors) {
			if ( validatorError.getLevel().equals(level) ){
				result++;
			}
		}
		return result ;
	}

	public boolean isValid() {
		for (ValidatorError validatorError : errors) {
			if ( validatorError.getLevel().equals(ErrorLevel.FATAL) ){
				return false;
			}
			if ( validatorError.getLevel().equals(ErrorLevel.ERROR) ){
				return false;
			}
		}
		return true;
	}
	
}
