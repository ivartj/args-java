package org.ivartj.args;

import java.io.PrintStream;
import java.util.ArrayList;

public class Help {

	ArrayList<Object> elements = new ArrayList<Object>();

	public Help() {
	}

	public void print(PrintStream out) {
		out.print(this);
	}

	public String toString() {
		Class lastClass = null;
		StringBuilder sb = new StringBuilder();

		for(Object el : elements) {
			if(lastClass != null)
			if(el.getClass() != lastClass && lastClass != Header.class)
				sb.append('\n');
			sb.append(el);
			lastClass = el.getClass();
		}

		sb.append('\n');

		return sb.toString();
	}

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

	public Help wrap(String text) {
		return wrap("", text);
	}

	public Help wrap(String indentation, String text) {
		elements.add(new Wrap(0, indentation, text));
		return this;
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
	
	private static String wrapText(int off, String indentation, String text) {
		String ind = indentation;
		final int MAX_LINE_LENGTH = 80;
		StringBuilder sb = new StringBuilder();

		
		if(off > ind.length()) {
			sb.append('\n');
			off = 0;
		}

		sb.append(ind.substring(off));
		off = ind.length();

		boolean newline = true;
		for(String word : text.split(" ")) {
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

		return sb.toString();
	}

	String commonOptionHelpIndent = "";

	public Help option(String usage, String description) {

		int helpOffset = ("  " + usage + " ").length();
		if(helpOffset > commonOptionHelpIndent.length())
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
