package org.ivartj.args;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A Lexer instance processes an array of command-line arguments, breaking them
 * down into more easily manageable tokens.
 */
public class Lexer {
	private int index = 0;
	private int offset = 0;
	private boolean noMoreOptions = false;
	private String args[];
	private String previousToken = null;

	private static Pattern caseBregex = Pattern.compile("^-[-a-zA-Z0-9]+=");
	private static Pattern caseCregex = Pattern.compile("^--[-a-zA-Z0-9]+$");
	private static Pattern caseDregex = Pattern.compile("^-[a-zA-Z0-9]+$");

	/**
	 * @param args          Command-line arguments to process.
	 */
	public Lexer(String args[]) {
		this.args = args;
	}

	/**
	 * @param token         String to check whether is an option.
	 *                      Typically a token returned from {@link #next()}.
	 *
	 * @return              Whether the given String appears to be an option.
	 */
	public static boolean isOption(String token) {
		return token.startsWith("-") && token.length() > 1;
	}

	/**
	 * Returns whether there are more arguments to process.
	 *
	 * @return The return value corresponds to whether {@link #next()} will
	 *         have more tokens to offer.
	 */
	public boolean hasNext() {
		if(index == args.length)
			return false;

		if(args[index].equals("--")) {
			noMoreOptions = true;
			index++;
		}

		return index != args.length;
	}

	/**
	 * Returns the next token of the broken-down arguments.
	 *
	 * @return This is either a positional argument (non-option), or an
	 *         option. If the returned option takes a parameter, call
	 *         {@link #expectParameterTo(String)} afterwards.
	 *
	 * @throws InvalidOptionException If the lexer encountered an option in
	 *                                a format not expected by this
	 *                                implementation.
	 */
	public String next() throws InvalidOptionException {
		String token = nextInternal();
		previousToken = token;
		return token;
	}

	private String nextInternal() throws InvalidOptionException {
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

	/**
	 * Processes the next token from the arguments as a parameter to the
	 * previous token.
	 *
	 * @return                           The next token.
	 * @throws MissingParameterException If no parameter was found.
	 */
	public String expectParameter() throws MissingParameterException {
		if(index == args.length)
			throw new MissingParameterException(previousToken);

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

		throw new MissingParameterException(previousToken);
	}

	/**
	 * @deprecated
	 * Processes the next token from the arguments as a parameter to the
	 * given flag.
	 *
	 * @param toFlag                     The flag to which the parameter is
	 *                                   expected. 
	 * @return                           The next token.
	 * @throws MissingParameterException If no parameter was found.
	 */
	@Deprecated
	public String expectParameterTo(String toFlag) throws MissingParameterException {
		previousToken = toFlag;
		return expectParameter();
	}
}
