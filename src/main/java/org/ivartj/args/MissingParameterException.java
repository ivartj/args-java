package org.ivartj.args;

public class MissingParameterException extends ArgumentException {
	public MissingParameterException(String flag) {
		super("Expected parameter to " + flag);
	}
}
