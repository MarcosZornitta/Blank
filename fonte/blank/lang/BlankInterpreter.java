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
	 *	Caso algum resultado do if tenha dado falso essa variavel
	 *	deve ser setada como verdadeira, entao o intepretador
	 *	ignorará as linhas até encontrar o final da instrução do if
	 */
	private boolean ignoreUntilNextEndIf = false;
	private boolean ignoreUntilNextElse  = false;
	private boolean ignoreNextElse       = false;

	/**
	 *	Identifiers
	 *	São os RegEx identificadores de cada parametro da linguagem
	 */
	private final Pattern varIdentifier         = Pattern.compile("\\bvar\\W+"); // Identifica "var"
	private final Pattern paramIdentifier       = Pattern.compile("(\\w+|\\.+)+"); // Identifica um parametro
	private final Pattern attributionIdentifier = Pattern.compile("\\w+(\\s+|\\b)\\=(\\s+|\\b)\\w+"); // Identifica "="
	private final Pattern showIdentifier        = Pattern.compile("\\bshow\\W+"); // Identifica "show"
	private final Pattern sumSubIdentifier      = Pattern.compile("\\w+(\\s+|\\b)(\\+|\\-)(\\s+|\\b)\\w+"); // Identifica "+" ou "-"
	private final Pattern divMultIdentifier     = Pattern.compile("\\w+(\\s+|\\b)(\\*|\\/|\\%)(\\s+|\\b)\\w+"); // Identifica "*", "/" ou "%"
	private final Pattern logicOpIdentifier     = Pattern.compile("\\w+(\\s+|\\b)(\\<|\\>|\\>\\=|\\<\\=|\\=\\=|\\!\\=)(\\s+|\\b)\\w+"); // Identifica operações lógicas >, <
	private final Pattern operationIdentifier   = Pattern.compile("(\\s+|\\b)(\\*|\\/|\\%|\\+|\\-|\\<|\\>|\\>\\=|\\<\\=|\\=\\=|\\!\\=)(\\s+|\\b)"); // Identifica uma operação (+, -, /, *, %, >, <, >=, ==, ...)
	private final Pattern ifIdentifier          = Pattern.compile("if(\\s+|\\b)\\((\\s+|\\b)(\\d|\\.)+(\\s+|\\b)\\)"); // Identifica "if ()"
	private final Pattern elseIdentifier        = Pattern.compile("(\\W|\\b)else(\\W|\\b)"); // Identifica "else"
	private final Pattern endifIdentifier       = Pattern.compile("(\\W|\\b)endif(\\W|\\b)"); // Identifica "endif"
	private final Pattern loopIdentifier        = Pattern.compile("(\\W|\\b)loop(\\s+|\\b)\\((\\s+|\\b)\\d(\\s+|\\b)\\)"); // Identifica "loop ()"
	private final Pattern parenthesisIdentifier = Pattern.compile("\\([\\s\\S]+\\)"); // Identifica "if ()"

	private boolean shouldIgnoreLine(String line)
	{
		Matcher elseMatcher  = elseIdentifier.matcher(line);
		Matcher endIfMatcher = endifIdentifier.matcher(line);

		if (this.ignoreUntilNextElse) {
			if (elseMatcher.find()) {
				this.ignoreUntilNextElse = false;
				return false;
			} else {
				return true;
			}
		}

		if (this.ignoreNextElse) {
			if (elseMatcher.find()) {
				this.ignoreNextElse       = false;
				this.ignoreUntilNextEndIf = true;
				return true;
			} else {
				return false;
			}
		}

		if (this.ignoreUntilNextEndIf) {
			if (endIfMatcher.find()) {
				this.ignoreUntilNextEndIf = false;
				return true;
			} else {
				return true;
			}
		}

		return false;
	}

	private BlankExpression evalExpression(String rawExpression) throws Exception
	{
		BlankVar var; // variavel auxiliar para guardar uma BlankVar
		String[] analysis = rawExpression.split(operationIdentifier.toString());

		// Identifica a operação realizada
		Matcher operationMatcher = operationIdentifier.matcher(rawExpression);
		operationMatcher.find();
		String op   = operationMatcher.toMatchResult().group().trim();
		String val1 = analysis[0].trim();
		String val2 = analysis[1].trim();

		// Pega o primeiro valor
		Matcher paramMatcher = paramIdentifier.matcher(val1);
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
		return (new BlankExpression(val1, val2, op));
	}

	/**
	 *	A função principal, responsável por interpretar e executar as ações contidas na linha
	 *	@param  String line        A linha para ser interpretada
	 *	@param  int    lineNumber  A identificação da linha atual
	 *	@return int                O número da próxima linha a ser lida
	 */
	public int understand(String line, int lineNumber) throws Exception
	{
		if (this.shouldIgnoreLine(line)) return ++lineNumber;

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
		Matcher logicOpMatcher;
		Matcher operationMatcher;
		Matcher ifMatcher;
		Matcher parenthesisMatcher;

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

			BlankExpression exp = this.evalExpression(rawExpression);

			// Substitui na linha a expressão pelo resultado
			line = line.replace(rawExpression, exp.result().toString());
		}

		sumSubMatcher = sumSubIdentifier.matcher(line); // Prepara a busca para + e -
		if (sumSubMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = sumSubMatcher.toMatchResult().group();

			BlankExpression exp = this.evalExpression(rawExpression);

			// Substitui na linha a expressão pelo resultado
			line = line.replace(rawExpression, exp.result().toString());
		}

		logicOpMatcher = logicOpIdentifier.matcher(line); // Prepara a busca para + e -
		if (logicOpMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = logicOpMatcher.toMatchResult().group();
			
			BlankExpression exp = this.evalExpression(rawExpression);

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

		ifMatcher = ifIdentifier.matcher(line);
		if (ifMatcher.find()) {
			String rawIf = ifMatcher.toMatchResult().group();

			parenthesisMatcher   = parenthesisIdentifier.matcher(rawIf);
			parenthesisMatcher.find();
			String ifParenthesis = parenthesisMatcher.toMatchResult().group();

			paramMatcher = paramIdentifier.matcher(ifParenthesis);
			paramMatcher.find();
			String ifResult = paramMatcher.toMatchResult().group();

			Float ifResultValue;

			/**
			 *	Á esse ponto o interpretador já deve ter substituído na linha
			 *	o valor-resultado da operação dentro do if, então é somente um digito que está
			 *	ali presente.
			 */
			if (mainScope.hasVariable(ifResult) >= 0) { // Checa se o valor é uma variavel já definida
				// encontra a variavel e atribui o valor da outra variavel á esta
				ifResultValue = Float.parseFloat(mainScope.getVariable(ifResult).getValue());
			} else {
				ifResultValue = Float.parseFloat(ifResult); // Atribui o valor identificado
			}

			// Se o resultado for falso
			if (ifResultValue == 0)
				this.ignoreUntilNextElse = true;
			else
				this.ignoreNextElse = true;
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