package fr.ign.validator.error;

/**
 * 
 * @author CBouche
 *
 */
public class ValidatorError implements Cloneable {

	/**
	 * code 
	 */
	private ErrorCode code ;
	
	/**
	 * context
	 */
	private ErrorScope scope ;
	
	/**
	 * level
	 */
	private ErrorLevel level ;
	
	/**
	 * message
	 */
	private String message ;
	
	/**
	 * 
	 * @param code
	 */
	public ValidatorError(ErrorCode code) {
		this.code = code ;
	}

	/**
	 * get code
	 * @return
	 */
	public ErrorCode getCode() {
		return code;
	}
	
	
	/**
	 * get context
	 * @return
	 */
	public ErrorScope getScope() {
		return scope;
	}
	
	/**
	 * set context
	 * @param context
	 */
	public void setScope(ErrorScope context) {
		this.scope = context;
	}

	/**
	 * get level
	 * @return
	 */
	public ErrorLevel getLevel() {
		return level;
	}

	/**
	 * set level
	 * @param level
	 */
	public void setLevel(ErrorLevel level) {
		this.level = level;
	}

	/**
	 * get message
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * set message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	
	@Override
    protected Object clone() throws CloneNotSupportedException {
		ValidatorError cloned = (ValidatorError)super.clone();
		return cloned ;
    }
	
	@Override
	public String toString() {
		return code+"|"+scope+"|"+level+"|"+message ;
	}
	
}
