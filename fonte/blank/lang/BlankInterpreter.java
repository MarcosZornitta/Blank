package blank.lang;

import java.util.*;
import java.util.regex.*;

public class BlankInterpreter
{
	BlankScope mainScope = new BlankScope("main");

	private boolean isNotReservedWord(String subject)
	{
		return false;
	}

	/**
	 *	A função principal, responsável por interpretar e executar as ações contidas na linha
	 *	@param  String line        A linha para ser interpretada
	 *	@param  int    lineNumber  A identificação da linha atual
	 *	@return int                O número da próxima linha a ser lida
	 */
	public int understand(String line, int lineNumber) throws Exception
	{
		// Set auxiliar variable to temporaly store variables
		BlankVar var = null;

		// Match the "var" token
		Pattern varIdentifier = Pattern.compile("\\bvar\\W+");
		Matcher varMatcher    = varIdentifier.matcher(line);

		// Match any word
		Pattern paramIdentifier = Pattern.compile("\\w+");
		Matcher paramMatcher    = paramIdentifier.matcher(line);

		// Match a "word=word"
		Pattern attributionIdentifier = Pattern.compile("\\w+(\\s+|\\b)\\=(\\s+|\\b)\\w+");
		Matcher attributionMatcher    = attributionIdentifier.matcher(line);

		// Match "show"
		Pattern showIdentifier = Pattern.compile("\\bshow\\W+");
		Matcher showMatcher    = showIdentifier.matcher(line);

		// Match + and - expressions
		Pattern sumSubIdentifier = Pattern.compile("\\w+(\\s+|\\b)(\\+|\\-)(\\s+|\\b)\\w+");
		Matcher sumSubMatcher    = sumSubIdentifier.matcher(line);

		// Match + and - expressions
		Pattern divMultIdentifier = Pattern.compile("\\w+(\\s+|\\b)(\\*|\\/|\\%)(\\s+|\\b)\\w+");
		Matcher divMultMatcher    = divMultIdentifier.matcher(line);

		// Find "var"
		if (varMatcher.find()) {
			String analysis = line.split(varIdentifier.toString(), 2)[1]; // subject is after the var token
			paramMatcher = paramIdentifier.matcher(analysis);

			if (paramMatcher.find()) {
				var = new BlankVar(paramMatcher.toMatchResult().group(), "");
				mainScope.storeVariable(var);
			}
		}

		// Encontra "="
		if (attributionMatcher.find()) {
			String[] analysis = line.split("\\=");
			String item  = analysis[0].trim();
			String value = analysis[1].trim();

			// Pega o nome da variável
			paramMatcher = paramIdentifier.matcher(item);
			if (paramMatcher.find()) {
				String varName = paramMatcher.toMatchResult().group();

				if (mainScope.hasVariable(varName) >= 0) {
					var = mainScope.getVariable(varName);
				} else {
					throw new Exception("Variable " + varName + " is not set.");
				}
			}

			// Pega o valor proposto para atribuição
			paramMatcher = paramIdentifier.matcher(value);
			if (paramMatcher.find()) {
				String varValue = paramMatcher.toMatchResult().group(); // Pega o valor após o =

				if (mainScope.hasVariable(varValue) >= 0) { // Checa se o valor é uma variavel já definida
					// encontra a variavel e atribui o valor da outra variavel á esta
					var.setValue(mainScope.getVariable(varName).getValue());
				} else {
					var.setValue(varValue); // Atribui o valor identificado
				}
			}
		}

		// Encontra a keyword "show"
		if (showMatcher.find()) {
			String analysis = line.split(showIdentifier.toString(), 2)[1]; // pega o que vem após "show"
			paramMatcher    = paramIdentifier.matcher(analysis);

			if (paramMatcher.find()) {
				String varName = paramMatcher.toMatchResult().group();

				if (mainScope.hasVariable(varName) >= 0) {
					var = mainScope.getVariable(varName);
				} else {
					throw new Exception("Variable " + varName + " is not set.");
				}

				System.out.println(var.getValue());
			}
		}		

		return ++lineNumber;
	}
}