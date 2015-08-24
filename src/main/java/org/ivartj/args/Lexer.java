package org.ivartj.args;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A Lexer instance processes an array of command-line arguments, breaking them
 * down into more easily manageable tokens.
 * <p>
 * After checking {@link #hasNext()}, options and positional arguments are returned by
 * calling {@link #next()}, and are distinguished by calling {@link
 * #isOption(String)}; parameter tokens are returned from {@link
 * #expectParameter()} after receiving an option that takes a parameter.
 * <p>
 * The argument lexer expects arguments in the following forms:
 * <table summary="Argument forms">
 *  <tr>
 *    <th>Argument</th>
 *    <th>Tokens</th>
 *  </tr>
 *  <tr>
 *    <td><code>-h</code></td>
 *    <td><code>"-h"</code></td>
 *    <td>(short option)</td>
 *  </tr>
 *  <tr>
 *    <td><code>--version</code></td>
 *    <td><code>"--version"</code></td>
 *    <td>(long option)</td>
 *  </tr>
 *  <tr>
 *    <td>
 *      <code>--output filename</code><br>
 *      <code>--output=filename</code>
 *    </td>
 *    <td><code>"--output" "filename"</code><br>
 *    <td>(long option with parameter)</td>
 *  </tr>
 *  <tr>
 *    <td>
 *      <code>-i input</code><br>
 *      <code>-i=input</code>
 *    </td>
 *    <td><code>"-i" "input"</code></td>
 *    <td>(short option with parameter)</td>
 *  </tr>
 *  <tr>
 *    <td><code>-abc</code></td>
 *    <td><code>"-a" "-b" "-c"</code></td>
 *    <td>(a collection short options)</td>
 *  </tr>
 *  <tr>
 *    <td><code>-Dparameter</code></td>
 *    <td><code>"-D" "parameter"</code></td>
 *    <td>(when calling {@link #expectParameter()} after <code>"-D"</code>)</td>
 *  </tr>
 *  <tr>
 *    <td><code>--</code></td>
 *    <td><code>(none)</code></td>
 *    <td>(this causes the next arguments to be regarded as non-options by {@link #isOption(String)})</td>
 *  </tr>
 * </table>
 */
public class Lexer {
	private int index = 0;
	private int offset = 0;
	private boolean noMoreOptions = false;
	private String args[];
	private String previousToken = null;

	private static Pattern caseBregex = Pattern.compile("^--[-a-zA-Z0-9]+=");
	private static Pattern caseCregex = Pattern.compile("^--[-a-zA-Z0-9]+$");
	private static Pattern caseDregex = Pattern.compile("^-[a-zA-Z0-9]+");

	/**
	 * @param args          Command-line arguments to process.
	 */
	public Lexer(String args[]) {
		this.args = args;
	}

	/**
	 * Returns whether the given token is an option.
	 * <p>
	 * This depends on whether an "--" argument has been encountered, which
	 * in Unix convention marks the end of options. An "--" argument is not
	 * returned by the lexer, but all subsequent arguments are regarded as
	 * non-options by this method.
	 *
	 * @param token         String to check whether is an option.
	 *                      Typically a token returned from {@link #next()}.
	 *
	 * @return              Whether the given String appears to be an option.
	 */
	public boolean isOption(String token) {
		if(noMoreOptions)
			return false;
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


		// CASE B: Long option with parameter.
		//
		// args[index] = --long=parameter
		// offset      = ^
		if(caseBregex.matcher(args[index]).lookingAt() && offset == 0) {
			offset = args[index].indexOf("=");
			return args[index].substring(0, offset);
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
		//
		// args[index] = -Dparameter
		// offset      = ^
		//
		// args[index] = -o=parameter
		// offset      = ^
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
		// offset      = ~~~~~~^
		//
		// args[index] = -o=parameter
		// offset      = ~~^
		//
		// args[index] = -abc=parameter
		// offset      = ~~~~^
		if(offset == args[index].indexOf('=')) {
			String value = args[index].substring(offset + 1);
			index++;
			offset = 0;
			return value;
		}

		// CASE: Short option with parameter in same argument
		//
		// args[index] = -Dparameter
		// offset      = ~~^
		if(caseDregex.matcher(args[index]).lookingAt() && offset == 2) {
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
