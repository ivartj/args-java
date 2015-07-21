package org.ivartj.args;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.ivartj.args.Lexer;
import org.ivartj.args.ArgumentException;
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
		.option("-i, --input=FILE",   "Specifies input file.")
		.option("-c, --config=CONFIGURATION",
		                              "Specifies output file.")
		.option("-d, --output-directory=DIRECTORY",
		                              "Specifies output directory.")
		.option("-Ddefinition",       "Adds a definition.")

		.print(System.out);
	}

	public static void testLexerUsage() {
		String args[] = {
			"-h",
			"--version",
			"--output=file",
			"-i=input",
			"positional1",
			"--config", "settings.cfg",
			"-d", "output-directory",
			"-DPACKAGE_VERSION=\"1.0\"",
			"--",
			"positional2",
			"positional3"
		};

		int positional = 0;

		Lexer lex = new Lexer(args);

		boolean help = false,
			version = false,
			output = false,
			input = false,
			outputDirectory = false,
			config = false,
			definition = false;

		while(lex.hasNext()) {
		try {
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
			case "-o":
			case "--output":
				String file = lex.expectParameter();
				System.out.printf("file = %s\n", file);
				assert(file.equals("file"));
				output = true;
				break;
			case "-i":
			case "--input":
				String inputParameter = lex.expectParameter();
				System.out.printf("input = %s\n", inputParameter);
				assert(inputParameter.equals("input"));
				input = true;
				break;
			case "-c":
			case "--config":
				String settings = lex.expectParameter();
				System.out.printf("settings.cfg = %s\n", settings);
				assert(settings.equals("settings.cfg"));
				config = true;
				break;
			case "-d":
			case "--output-directory":
				String outputDirectoryParameter = lex.expectParameter();
				System.out.printf("output-directory = %s\n", outputDirectoryParameter);
				assert(outputDirectoryParameter.equals("output-directory"));
				outputDirectory = true;
				break;
			case "-D":
				String definitionParameter = lex.expectParameter();
				System.out.printf("PACKAGE_VERSION=\"1.0\" = %s\n", definitionParameter);
				assert(definitionParameter.equals("PACKAGE_VERSION=\"1.0\""));
				definition = true;
				break;
			default:
				throw new ArgumentException("Unexpected option " + token);
			} else {
				positional++;
				System.out.printf("positional%d = %s\n", positional, token);
				assert(token.equals("positional" + positional));
			}
		} catch(ArgumentException e) {
			System.err.println("Error occurred when processing arguments:\n\t" + e.getMessage() + "\n");
			assert(false);
		} /* try */ }

		assert(help);
		assert(version);
		assert(output);
		assert(input);
		assert(outputDirectory);
		assert(definition);
		assert(config);
		assert(positional == 3);
	}
}
