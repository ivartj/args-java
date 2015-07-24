package org.ivartj.args;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Convenience class for producing conventional help output given a --help or
 * -h argument option.
 */
public class Help {

	private ArrayList<Object> elements = new ArrayList<Object>();

	public Help() {
	}

	/**
	 * Prints the prepared help message.
	 *
	 * @param out  The output to which to print the help message.
	 */
	public void print(PrintStream out) {
		out.print(this);
	}

	/**
	 * The String representation of this class is also the prepared help
	 * message.
	 *
	 * @return The prepared help message.
	 */
	public String toString() {
		Class lastClass = null;
		StringBuilder sb = new StringBuilder();

		for(Object el : elements) {
			if(lastClass != null)
			if(lastClass == Wrap.class || (el.getClass() != lastClass && lastClass != Header.class))
				sb.append('\n');
			sb.append(el);
			lastClass = el.getClass();
		}

		sb.append('\n');

		return sb.toString();
	}

	/**
	 * Adds a usage line.
	 * <p>
	 * Consecutive calls causes the subsequent usage lines to take an
	 * alternate form:
	 * <pre>
	 *   help.usage("program INPUT");
	 *   help.usage("program INPUT OUTPUT");
	 *
	 *   Usage: program INPUT
	 *      or: program INPUT OUTPUT
	 * </pre>
	 *
	 * @param usage  Usage string.
	 * @return       The Help instance on which this was called.
	 */
	public Help usage(String usage) {
		if(elements.size() == 0 || elements.get(elements.size() - 1).getClass() != Usage.class)
			elements.add(new Usage("Usage: ", usage));
		else
			elements.add(new Usage("   or: ", usage));
		return this;
	}

	private static class Usage {
		String usage;
		String prefix;

		Usage(String prefix, String usage) {
			this.prefix = prefix;
			this.usage = usage;
		}

		public String toString() {
			return prefix + usage + '\n';
		}
	}

	/**
	 * Wraps text so that lines do not exceed 80 characters.
	 *
	 * @param text   Text to be wrapped.
	 * @return       The Help instance on which this was called.
	 */
	public Help wrap(String text) {
		return wrap("", text);
	}

	/**
	 * Wraps text so that lines do not exceed 80 characters, and with the
	 * given indentation.
	 *
	 * @param indentation Indentation (typically a series of spaces).
	 * @param text        Text to be wrapped.
	 * @return            The Help instance on which this was called.
	 */
	public Help wrap(String indentation, String text) {
		elements.add(new Wrap(0, indentation, text));
		return this;
	}

	/**
	 * Like {@link #wrap(String,String)}, except that indentation is given
	 * by spaces at the start of the text parameter.
	 *
	 * @param text        Text which optionally starts with some
	 *                    spaces, which will be used as indentation
	 *                    when wrapping it.
	 * @return            The Help instance on which this was called.
	 */
	public Help pg(String text) {
		int i;

		for(i = 0; i < text.length(); i++)
			if(text.charAt(i) != ' ')
				break;

		String indentation = text.substring(0, i);
		text = text.substring(i);
		return wrap(indentation, text);
	}

	private static class Wrap {
		String indentation;
		String text;
		int off;

		Wrap(int offset, String indentation, String text) {
			this.indentation = indentation;
			this.text = text;
			off = offset;
		}

		public String toString() {
			return wrapText(off, indentation, text);
		}
	}
	
	private static final int MAX_LINE_LENGTH = 80;

	private static String wrapText(int off, String indentation, String text) {
		String ind = indentation;
		StringBuilder sb = new StringBuilder();

		
		if(off > ind.length()) {
			sb.append('\n');
			off = 0;
		}


		boolean newline = true;
		for(String line : text.split("\n")) {
			sb.append(ind.substring(off));
			off = ind.length();
			for(String word : line.split(" ")) {
				String outword = word;

				if(!newline)
					outword = " " + word;

				off += outword.length();
				if(off > MAX_LINE_LENGTH) {
					sb.append('\n');
					sb.append(ind);
					outword = word;
					off = (ind + outword).length();
				} 

				sb.append(outword);
				newline = false;
			}
			sb.append('\n');
			off = 0;
			newline = true;
		}


		return sb.toString();
	}

	private final static int MAX_INDENTATION = 20;

	private String commonOptionHelpIndent = "";

	/**
	 * Adds documentation for an option in the help message. 
	 *
	 * @param usage        Typically in the form "-o, --output=FILE"
	 * @param description  Description of the option.
	 * @return             The Help instance on which this was called.
	 */
	public Help option(String usage, String description) {

		int helpOffset = ("  " + usage + " ").length();
		if(helpOffset > commonOptionHelpIndent.length() && helpOffset <= MAX_INDENTATION)
			for(int i = commonOptionHelpIndent.length(); i < helpOffset; i++)
				commonOptionHelpIndent += " ";

		elements.add(new Option(usage, description));
		return this;
	}

	private class Option {
		String usage;
		String description;

		Option(String usage, String description) {
			this.usage = usage;
			this.description = description;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			int off = ("  " + usage + " ").length();
			sb.append("  " + usage + " ");
			sb.append(wrapText(off, commonOptionHelpIndent, description));

			return sb.toString();
		}
	}

	/**
	 * Adds a header in the help message.
	 * <p>
	 * According to typographical convention, this element is followed by a
	 * only single line break.
	 *
	 * @param header       The header string.
	 * @return             The Help instance on which this was called.
	 */
	public Help header(String header) {
		elements.add(new Header(header));
		return this;
	}

	private static class Header {
		String header;

		Header(String header) {
			this.header = header;
		}

		public String toString() {
			return header + '\n';
		}
	}

}
