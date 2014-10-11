package blank.lang;

import java.util.*;
import java.util.regex.*;

class BlankInterpreter
{
	StringTokenizer tokenizer;

	/**
	 *	The main function of the Blank Language Interpreter
	 *	It gather the line, executes it's operations and return the next line to be read
	 *	@param String line        The current line content
	 *	@param int    lineNumber  The identification of current line
	 *	@return int Number of the next line to be read
	 */
	public int understand(String line, int lineNumber)
	{
		// Pattern tokenIdentifier;
		// System.out.println(tokenizer.nextToken("(?:\\bvar\\W)")); // Token var
		// System.out.println("(\\w+)|(\\d)(?=[\\W])");
		// System.out.println(tokenizer.nextToken("(\\w+)|(\\d)(?=[\\W])")); // prop var

		System.out.println("Reading line " + lineNumber + ": " + line);

		// tokenizer.dumpTokens();

		return ++lineNumber;
	}
}