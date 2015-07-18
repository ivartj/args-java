package org.ivartj.arguments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.ivartj.arguments.Option;
import org.ivartj.arguments.Parser;

/**
 * Unit test for simple App.
 */
public class ArgumentParserTest 
    extends TestCase
{
	/**
	* Create the test case
	*
	* @param testName name of the test case
	*/
	public AppTest( String testName )
	{
		super( testName );
	}

	/**
	* @return the suite of tests being tested
	*/
	public static Test suite()
	{
		return new TestSuite( AppTest.class );
	}


	public static void testBasicUsage {
		String args[] = { "-h", "--help", "--output=file", "plainArgument" };
		Parser parser = new Parser(args);

		int help = parser.addOption("Prints help message.")
			.flags("-h", "--help")
			.index();

		int version = parser.addOption("Prints version string.")
			.flags("--version");
			.index();

		int output = parser.addOption("Specifies output file.")
			.flags("-o", "--output")
			.takes("FILE");
			.index();

		int plain = parser.plainArgument;

		switch(parser.parse()) {
		case help:
			parser.printUsage(System.out);
			break;
		case version:
			System.out.println("test version 0.1");
			break;
		case output:
			System.out.println(parser.optionArgument());
			break;
		case plain:
			System.out.println(plain);
			break;
		}
	}

	
}
