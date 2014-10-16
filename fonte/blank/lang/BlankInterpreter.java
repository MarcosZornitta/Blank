package blank.lang;

import java.util.*;
import java.util.regex.*;

public class BlankInterpreter
{
	BlankScope mainScope = new BlankScope("main");

	/**
	 *	A função principal, responsável por interpretar e executar as ações contidas na linha
	 *	@param  String line        A linha para ser interpretada
	 *	@param  int    lineNumber  A identificação da linha atual
	 *	@return int                O número da próxima linha a ser lida
	 */
	public int understand(String line, int lineNumber)
	{
		Pattern varIdentifier = Pattern.compile("\\bvar\\W+");
		Matcher varMatcher    = varIdentifier.matcher(line);

		Pattern paramIdentifier = Pattern.compile("\\w+");
		Matcher paramMatcher    = paramIdentifier.matcher(line);

		if (varMatcher.find()) {
			String analysis = line.split(varIdentifier.toString(), 2)[1];
			System.out.println(analysis);
			paramMatcher = paramIdentifier.matcher(analysis);
			if (paramMatcher.find()) {
				BlankVar var = new BlankVar(paramMatcher.toMatchResult().group(), "");
				mainScope.storeVariable(var);
			}
		}

		return ++lineNumber;
	}
}