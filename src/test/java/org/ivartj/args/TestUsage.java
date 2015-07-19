package org.ivartj.arguments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.ivartj.args.Lexer;
import org.ivartj.args.Help;

/**
 * Unit test for simple App.
 */
public class TestUsage 
    extends TestCase
{
	public TestUsage( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{
		return new TestSuite( TestUsage.class );
	}

	public static void testHelpUsage() {
		new Help()
		.usage("inviteBot [OPTION]...")
		.usage("inviteBot [OPTION]... CONFIGURATION-FILE")

		.header("DESCRIPTION")
		.wrap("  ", ""
		+ 	"Handles invitations to channels. By default it reads "
		+ 	"settings from ./settings.properties."
		)

		.header("OPTIONS")
		.option("-h, --help",         "Prints help message.")
		.option("--version",          "Prints version.")
		.option("-o, --output=FILE",  "Specifies output file.")

		.print(System.out);
	}

	public static void testLexerUsage() throws Exception {
		String args[] = { "-h", "--version", "--output=file", "positional", "--config", "settings.cfg" };
		Lexer lex = new Lexer(args);

		boolean help = false,
			version = false,
			output = false,
			config = false,
			positional = false;

		while(lex.hasNext()) {
			String token = lex.next();

			if(lex.isOption(token))
			switch(token) {
			case "-h":
			case "--help":
				help = true;
				break;
			case "--version":
				version = true;
				break;
			case "--output":
				String filename = lex.expectParameterTo("--output");
				assert(filename.equals("file"));
				output = true;
				break;
			case "--config":
				String settings = lex.expectParameterTo("--config");
				assert(settings.equals("settings.cfg"));
				config = true;
				break;
			default:
				throw new Exception("Unexpected option " + token);
			} else {
				assert(token.equals("positional"));
				positional = true;
			}
		}

		assert(help);
		assert(version);
		assert(output);
		assert(positional);
		assert(config);

	}
}
