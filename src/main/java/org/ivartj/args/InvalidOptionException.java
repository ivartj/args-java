package org.ivartj.args;

public class InvalidOptionException extends Exception {
	protected InvalidOptionException(String flag) {
		super(flag + " is not a valid option");
	}
}
