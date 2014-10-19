/**
 *	BlankInterpreter
 *
 *	O core do interpretador da Linguagem Blank
 *	Essa classe faz uso pesado de RegEx para a busca de tokens e
 *	da classe Matcher para identificação de tokens.
 */

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
	public int understand(String line, int lineNumber) throws Exception
	{
		// Set auxiliar variable to temporaly store variables
		BlankVar var = null;

		Pattern commentIdentifier = Pattern.compile("\\/\\/[\\s\\S]+");
		Matcher commentMatcher    = commentIdentifier.matcher(line);

		// Remove todos os comentários
		if (commentMatcher.find()) {
			String commentSection = commentMatcher.toMatchResult().group();
			// Substitui na linha a expressão pelo resultado
			line = line.replace(commentSection, "");
		}

		/**
		 *	Identifiers
		 *	São os RegEx identificadores de cada parametro da linguagem
		 */
		Pattern varIdentifier         = Pattern.compile("\\bvar\\W+"); // Identifica "var"
		Pattern paramIdentifier       = Pattern.compile("(\\w+|\\.+)+"); // Identifica um parametro
		Pattern attributionIdentifier = Pattern.compile("\\w+(\\s+|\\b)\\=(\\s+|\\b)\\w+"); // Identifica "="
		Pattern showIdentifier        = Pattern.compile("\\bshow\\W+"); // Identifica "show"
		Pattern sumSubIdentifier      = Pattern.compile("\\w+(\\s+|\\b)(\\+|\\-)(\\s+|\\b)\\w+"); // Identifica "+" ou "-"
		Pattern divMultIdentifier     = Pattern.compile("\\w+(\\s+|\\b)(\\*|\\/|\\%)(\\s+|\\b)\\w+"); // Identifica "*", "/" ou "%"
		Pattern logicOpIdentifier     = Pattern.compile("\\w+(\\s+|\\b)(\\<|\\>|\\>\\=|\\<\\=|\\=\\=|\\!\\=)(\\s+|\\b)\\w+"); // Identifica operações lógicas >, <, ==...
		Pattern operationIdentifier   = Pattern.compile("(\\s+|\\b)(\\*|\\/|\\%|\\+|\\-|\\<|\\>|\\>\\=|\\<\\=|\\=\\=|\\!\\=)(\\s+|\\b)"); // Identifica uma operação (+, -, /, *, %, >, <, >=, ==, ...)
		
		/**
		 *	Matchers
		 *	Declaração dos Matchers, serão eles que irão, de fato, identificar o se a string contem
		 *	o regex
		 */
		Matcher varMatcher;
		Matcher paramMatcher;
		Matcher attributionMatcher;
		Matcher showMatcher;
		Matcher sumSubMatcher;
		Matcher divMultMatcher;
		Matcher operationMatcher;

		varMatcher = varIdentifier.matcher(line); // Prepara a verificação se existe o token var
		if (varMatcher.find()) {
			String analysis = line.split(varIdentifier.toString(), 2)[1]; // subject is after the var token
			paramMatcher = paramIdentifier.matcher(analysis);

			if (paramMatcher.find()) {
				var = new BlankVar(paramMatcher.toMatchResult().group(), "");
				mainScope.storeVariable(var);
			}
		}

		divMultMatcher = divMultIdentifier.matcher(line); // prepara a busca para operações de *, / e %
		if (divMultMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = divMultMatcher.toMatchResult().group();
			String[] analysis = rawExpression.split(operationIdentifier.toString());

			// Identifica a operação realizada
			operationMatcher = operationIdentifier.matcher(rawExpression);
			operationMatcher.find();
			String op   = operationMatcher.toMatchResult().group().trim();
			String val1 = analysis[0].trim();
			String val2 = analysis[1].trim();

			// Pega o primeiro valor
			paramMatcher = paramIdentifier.matcher(val1);
			if (paramMatcher.find()) {
				String opValue1 = paramMatcher.toMatchResult().group();

				if (mainScope.hasVariable(opValue1) >= 0) { // verifica se é uma variavel
					var = mainScope.getVariable(opValue1);
					val1 = var.getValue();
				} else {
					val1 = opValue1;
				}
			}

			// Encontra o segundo valor
			paramMatcher = paramIdentifier.matcher(val2);
			if (paramMatcher.find()) {
				String opValue2 = paramMatcher.toMatchResult().group();

				if (mainScope.hasVariable(opValue2) >= 0) { // verifica se é uma variavel
					var = mainScope.getVariable(opValue2);
					val2 = var.getValue();
				} else {
					val2 = opValue2;
				}
			}

			// Cria um novo calculo de expressão
			BlankExpression exp = new BlankExpression(val1, val2, op);

			// Substitui na linha a expressão pelo resultado
			line = line.replace(rawExpression, exp.result().toString());
		}

		sumSubMatcher = sumSubIdentifier.matcher(line); // Prepara a busca para + e -
		if (sumSubMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = sumSubMatcher.toMatchResult().group();
			String[] analysis = rawExpression.split(operationIdentifier.toString());

			// Identifica a operação realizada
			operationMatcher = operationIdentifier.matcher(rawExpression);
			operationMatcher.find();
			String op   = operationMatcher.toMatchResult().group().trim();
			String val1 = analysis[0].trim();
			String val2 = analysis[1].trim();

			// Pega o primeiro valor
			paramMatcher = paramIdentifier.matcher(val1);
			if (paramMatcher.find()) {
				String opValue1 = paramMatcher.toMatchResult().group();

				if (mainScope.hasVariable(opValue1) >= 0) { // verifica se é uma variavel
					var = mainScope.getVariable(opValue1);
					val1 = var.getValue();
				} else {
					val1 = opValue1;
				}
			}

			// Encontra o segundo valor
			paramMatcher = paramIdentifier.matcher(val2);
			if (paramMatcher.find()) {
				String opValue2 = paramMatcher.toMatchResult().group();

				if (mainScope.hasVariable(opValue2) >= 0) { // verifica se é uma variavel
					var = mainScope.getVariable(opValue2);
					val2 = var.getValue();
				} else {
					val2 = opValue2;
				}
			}

			// Cria um novo calculo de expressão
			BlankExpression exp = new BlankExpression(val1, val2, op);

			// Substitui na linha a expressão pelo resultado
			line = line.replace(rawExpression, exp.result().toString());
		}

		attributionMatcher = attributionIdentifier.matcher(line); // Prepara a busca para token "="
		if (attributionMatcher.find()) {
			String rawAttribuition = attributionMatcher.toMatchResult().group();
			String[] analysis = rawAttribuition.split("(\\s+|\\b)(\\=)(\\s+|\\b)");
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
					var.setValue(mainScope.getVariable(varValue).getValue());
				} else {
					var.setValue(varValue); // Atribui o valor identificado
				}
			}
		}

		showMatcher = showIdentifier.matcher(line); // Prepara a busca para o token show
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