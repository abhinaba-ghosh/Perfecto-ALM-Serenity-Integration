package exceptions;

import org.pmw.tinylog.Logger;

public class ConnectorCustomException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectorCustomException(String message) {
		super(message);
		Logger.error(message);
		System.exit(1);
	}

}
