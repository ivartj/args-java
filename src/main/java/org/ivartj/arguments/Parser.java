package org.ivartj.arguments;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class Parser {
	private String args[];
	private ArrayList<Option> options;
	private int index;
	private int offset;
	private String argument;
	private String parameter;
	private boolean plainOnly = false;

	public final int END_OF_ARGUMENTS = -1;
	public final int PLAIN_ARGUMENT = -2;

	Parser(String args[]) {
		super();
		this.args = args;
		options = new ArrayList<Option>();
	}

	public Option addOption(String helpMessage) {
		Option option = new Option(options.length(), helpMessage);
		options.add(option);

		return option;
	}

	private enum State {
		// Prepared for a new argument
		INIT,

		// We are processing one or more short flags (-abc)
		SHORT,

		// We are processing a long flag (--version)
		LONG,

		// We are expecting a parameter to a flag (--output file)
		PARAM
	}

	State state = INIT;

	public int parse() throws InvalidOptionException, ExpectedParameterException {

		Option option;
		String flag;

		while(index < args.length) {
			String arg = args[index];

			switch(state) {
			case INIT:
				if(arg.equals("--")) {
					plainOnly = true;
					index++;
					break;
				}

				if(plainOnly == false) {
					if(arg.startsWith("--")) {
						state = LONG;
						break;
					}

					if(arg.startsWith("-")) {
						state = SHORT;
						break;
					}
				}

				argument = arg;
				index++;
				return PLAIN_ARGUMENT;

			case SHORT:
				if(offset == arg.length()) {
					index++;
					state = INIT;
					break;
				}

				if(offset == 0)
					offset++;
				char c = arg[offset++];
			
				flag = "-" + c;
				option = getOption(flag);

				if(option.hasParameter()) {
					if(offset == arg.length())
						throw ExpectedParameterException(flag);
					state = PARAM;
					index++;
					break;
				}

				return option.getIndex();

			case LONG:
				if(!arg.contains("="))
					flag = arg;
				else {
					int atEqualSign = arg.indexOf('=');
					flag = arg.substring(0, atEqualSign);
					parameter = 
				}

			case PARAM:
				parameter = arg;
				return option.getIndex();
			}
		}

		if(state == PARAM)
			throw ExpectedParameterEsception(flag);

		return END_OF_ARGUMENTS;
	}

	private Option getOption(String flag) throws InvalidOptionException {
		for(Option option : options) {
			for(String optflag : option.flags) {
				if(flag == optflag)
					return option;
			}
		}

		throw new InvalidOptionException(flag);
	}

	public String getArgument() {
		return argument;
	}

	public String getParameter() {
		return parameter;
	}

	public void printOptions(PrintStream out) {

	} 

	public static class InvalidOptionException extends Exception() {
		protected InvalidOptiionException(String flag) {
			super(flag + " is not a valid option");
		}
	}

	public static class ExpectedParameterException extends Exception() {
		protected ExpectedParamterException(String flag) {
			super("Expected parameter to " + flag);
		}
	}
}
