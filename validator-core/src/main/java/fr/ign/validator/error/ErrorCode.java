package fr.ign.validator.error;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents an error code
 * 
 * @author CBouche
 * @author MBorne
 */
public class ErrorCode {
	
	private static ConcurrentMap<String, ErrorCode> errorCodeMap = new ConcurrentHashMap<String, ErrorCode>();

	private final String name;
	
	/**
     * @param name
     */
    private ErrorCode(final String name) {
        this.name = name;
    }
    
    /**
     * Creates an ErrorCode according to a string
     * @param name
     * @return
     */
    public static ErrorCode valueOf(String name){
		errorCodeMap.putIfAbsent(name, new ErrorCode(name)) ;
		return errorCodeMap.get(name);
	}

    @Override
    @JsonValue
    public String toString() {
        return name;
    }
}
