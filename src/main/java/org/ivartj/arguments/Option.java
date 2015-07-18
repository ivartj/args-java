package org.ivartj.arguments;

public class Option {

	private String helpMessage;
	private String flags[] = {};
	private String parameterName = null;
	private int index;

	protected Option(int index, String helpMessage) {
		this.helpMessage = helpMessage;
		this.index = index;
	}
	
	public Option flags(String... flags) {
		int fstlen = this.flags.length;
		int seclen = flags.length;

		String newFlags[] = new String[fstlen + seclen];

		System.arraycopy(this.flags, 0, newFlags, 0,      fstlen);
		System.arraycopy(flags,      0, newFlags, fstlen, seclen);

		this.flags = newFlags;

		return this;
	}

	protected String[] getFlags() {
		return flags;
	}

	protected boolean hasParameter() {
		return parameterName != null;
	}

	public Option takes(String parameterName) {
		this.parameterName = parameterName;
		return this;
	}

	protected int getIndex() {
		return index;
	}

}
