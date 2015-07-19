package org.ivartj.args;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Lexer {
	private int index = 0;
	private int offset = 0;
	private boolean noMoreOptions = false;
	private String args[];
	private Pattern caseBregex = Pattern.compile("^-[-a-zA-Z]+=");
	private Pattern caseCregex = Pattern.compile("^--[-a-zA-Z]+$");
	private Pattern caseDregex = Pattern.compile("^-[a-zA-Z]+$");

	public Lexer(String args[]) {
		this.args = args;
	}

	public static boolean isOption(String token) {
		return token.startsWith("-") && token.length() > 1;
	}

	public boolean hasNext() {
		if(index == args.length)
			return false;

		if(args[index].equals("--")) {
			noMoreOptions = true;
			index++;
		}

		return index != args.length;
	}

	public String next() throws InvalidOptionException {
		if(!hasNext())
			return null;

		// CASE A: Positional argument, not starting with hyphen.
		//
		// args[index] = positional
		// offset      = ^
		if(!isOption(args[index]) && offset == 0) {
			return args[index++];
		}


		// CASE B: Option with parameter.
		//
		// args[index] = --long=parameter
		// offset      = ^
		//
		// args[index] = -o=parameter
		// offset      = ^
		if(caseBregex.matcher(args[index]).lookingAt() && offset == 0) {
			offset = args[index].indexOf("=") + 1;
			return args[index].substring(0, args[index].indexOf("="));
		}


		// CASE C: Long option without parameter
		//
		// args[index] = --long
		// offset      = ^
		if(caseCregex.matcher(args[index]).lookingAt() && offset == 0) {
			return args[index++];
		}


		// CASE D: One or more short options
		// 
		// args[index] = -abc
		// offset      = ^^^^
		if(caseDregex.matcher(args[index]).lookingAt()) {
			if(offset == 0)
				offset++;
			char c = args[index].charAt(offset);
			offset++;
			if(offset == args[index].length()) {
				offset = 0;
				index++;
			}
			return "-" + c;
		}

		throw new InvalidOptionException(args[index]);
	}

	public String expectParameterTo(String toFlag) throws MissingParameterException {

		if(index == args.length)
			throw new MissingParameterException(toFlag);

		// CASE: Plain argument as parameter
		if(offset == 0 && !isOption(args[index])) {
			return args[index++];
		}

		// CASE: Parameter after equal sign to long parameter
		//
		// args[index] = --long=parameter
		// offset      = ~~~~~~~^
		//
		// args[index] = -o=parameter
		// offset      = ~~~^
		//
		// args[index] = -abc=parameter
		// offset      = ~~~~~^
		if(caseBregex.matcher(args[index]).lookingAt() && offset == args[index].indexOf('=') + 1) {
			String value = args[index].substring(offset);
			index++;
			offset = 0;
			return value;
		}

		throw new MissingParameterException(toFlag);
	}
}
