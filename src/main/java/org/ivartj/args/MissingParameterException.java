package org.ivartj.args;

public class MissingParameterException extends Exception {
	public MissingParameterException(String flag) {
		super("Expected parameter to " + flag);
	}
}
