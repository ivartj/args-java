package org.ivartj.args;

public class InvalidOptionException extends ArgumentException {
	public InvalidOptionException(String flag) {
		super(flag + " is not a valid option");
	}
}
