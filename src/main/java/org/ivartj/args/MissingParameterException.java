package org.ivartj.args;

public class MissingParameterException extends Exception {
	protected MissingParameterException(String flag) {
		super("Expected parameter to " + flag);
	}
}
