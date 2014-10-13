package blank.lang;

import java.util.*;
import java.util.regex.*;

public class BlankInterpreter
{
	StringTokenizer tokenizer;

	/**
	 *	A função principal, responsável por interpretar e executar as ações contidas na linha
	 *	@param  String line        A linha para ser interpretada
	 *	@param  int    lineNumber  A identificação da linha atual
	 *	@return int                O número da próxima linha a ser lida
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