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
	 *	Flags para ignorar baseado em IF
	 */
	private boolean ignoreUntilNextEndIf   = false; // Ignora o bloco até o próximo endif
	private boolean ignoreUntilNextElse    = false; // Ignora o bloco até o próximo else
	private boolean ignoreNextElse         = false; // Ignora o próximo bloco else
	private boolean ignoreNextLoop         = false; // Ignora o próximo bloco de loop
	private boolean insideLoop             = false; // Verifica se está dentro de um loop
	private boolean ignoreUntilNextEndloop = false; // Ignora até encontrar um endloop

	private Stack<Integer> loopStack = new Stack<Integer>();
	private Stack<Integer> ifStack   = new Stack<Integer>();
	private Stack<BlankFlux> fluxStack = new Stack<BlankFlux>();

	/**
	 *	Identifiers
	 *	São os RegEx identificadores de cada parametro da linguagem
	 */
	private final Pattern varIdentifier         = Pattern.compile("\\bvar\\W+"); // Identifica "var"
	private final Pattern numberIdentifier      = Pattern.compile("\\d*[\\S\\.]?\\d+");
	private final Pattern paramIdentifier       = Pattern.compile("(\\w+|\\.+)+"); // Identifica um parametro
	private final Pattern attributionIdentifier = Pattern.compile("\\w+(\\s+|\\b)\\=(\\s+|\\b)\\w+"); // Identifica "="
	private final Pattern showIdentifier        = Pattern.compile("\\bshow\\s+"); // Identifica "show"
	private final Pattern sumSubIdentifier      = Pattern.compile("((\\w+|\\.+)+)(\\s+|\\b)(\\+|\\-)(\\s+|\\b)((\\w+|\\.+)+)"); // Identifica "+" ou "-"
	private final Pattern divMultIdentifier     = Pattern.compile("((\\w+|\\.+)+)(\\s+|\\b)(\\*|\\/|\\%)(\\s+|\\b)((\\w+|\\.+)+)"); // Identifica "*", "/" ou "%"
	private final Pattern logicOpIdentifier     = Pattern.compile("((\\w+|\\.+)+)(\\s+|\\b)(\\<|\\>|\\>\\=|\\<\\=|\\=\\=|\\!\\=|\\&\\&|\\|\\|)(\\s+|\\b)((\\w+|\\.+)+)"); // Identifica operações lógicas >, <
	private final Pattern operationIdentifier   = Pattern.compile("(\\s+|\\b)(\\*|\\/|\\%|\\+|\\-|\\<|\\>|\\>\\=|\\<\\=|\\=\\=|\\!\\=|\\&\\&|\\|\\|)(\\s+|\\b)"); // Identifica uma operação (+, -, /, *, %, >, <, >=, ==, ...)
	private final Pattern ifIdentifier          = Pattern.compile("if(\\s+|\\b)\\((\\s+|\\b)(\\d*[\\S\\.]?\\d+)(\\s+|\\b)\\)"); // Identifica "if ()"
	private final Pattern elseIdentifier        = Pattern.compile("(\\W|\\b)else(\\W|\\b)"); // Identifica "else"
	private final Pattern endifIdentifier       = Pattern.compile("(\\W|\\b)endif(\\W|\\b)"); // Identifica "endif"
	// private final Pattern loopIdentifier        = Pattern.compile("(\\W|\\b)loop(\\s+|\\b)\\((\\s+|\\b)(\\d*[\\S\\.]?\\d+|((\\w+|\\.+)+))(\\s+|\\b)\\)"); // Identifica "loop ()"
	private final Pattern loopIdentifier        = Pattern.compile("(\\W|\\b)loop(\\s+|\\b)\\((\\s+|\\b)([\\s\\S]+)(\\s+|\\b)\\)"); // Identifica "loop ()"
	private final Pattern endloopIdentifier     = Pattern.compile("(\\W|\\b)endloop(\\W|\\b)"); // Identifica "endif"
	private final Pattern parenthesisIdentifier = Pattern.compile("\\([\\s\\S]+\\)"); // Identifica "(conteudo)"
	private final Pattern strIdentifier         = Pattern.compile("\\\"[\\s\\S]+\\\"");
	private final Pattern commentIdentifier     = Pattern.compile("\\/\\/[\\s\\S]+");

	/**
	 *	Verifica se deve ignorar a linha, trabalhando com seu conteúdo.
	 */
	private boolean shouldIgnoreLine(String line)
	{
		if (fluxStack.empty()) return false;

		Matcher loopMatcher    = loopIdentifier.matcher(line);
		Matcher endFluxMatcher = (fluxStack.peek().getEndFluxPattern().matcher(line));

		// Enquanto o último resultado de Loop for falso
		if (fluxStack.peek().result() == false) {
			if (loopMatcher.find()) {
				fluxStack.push(new BlankFlux(0, endloopIdentifier, false));
			}

			if (endFluxMatcher.find()) {
				fluxStack.pop();
			}

			return true;
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
		
		/**
		 *	Matchers
		 *	Declaração dos Matchers, serão eles que irão, de fato, identificar o se a string contem
		 *	o regex
		 */
		Matcher varMatcher;
		Matcher paramMatcher;
		Matcher numberMatcher;
		Matcher attributionMatcher;
		Matcher showMatcher;
		Matcher sumSubMatcher;
		Matcher divMultMatcher;
		Matcher logicOpMatcher;
		Matcher operationMatcher;
		Matcher ifMatcher;
		Matcher endIfMatcher;
		Matcher parenthesisMatcher;
		Matcher loopMatcher;
		Matcher endloopMatcher;
		Matcher strMatcher;
		Matcher commentMatcher;

		// Variável auxiliar para guardar variaveis
		BlankVar var = null;

		// Procura por comentários
		commentMatcher = commentIdentifier.matcher(line);

		// Remove todos os comentários
		if (commentMatcher.find()) { // Se encontrar comentários
			String commentSection = commentMatcher.toMatchResult().group();
			// Substitui na linha a expressão pelo resultado
			line = line.replace(commentSection, "");
		}

		varMatcher = varIdentifier.matcher(line); // Prepara a verificação se existe o token var
		if (varMatcher.find()) {
			// Nome da variavél sempre está após o token, então por isso pega a posição [1]
			String analysis = line.split(varIdentifier.toString(), 2)[1];
			paramMatcher = paramIdentifier.matcher(analysis);

			if (paramMatcher.find()) {
				var = new BlankVar(paramMatcher.toMatchResult().group(), "");
				mainScope.storeVariable(var);
			}
		}

		divMultMatcher = divMultIdentifier.matcher(line); // prepara a busca para operações de *, / e %
		while (divMultMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = divMultMatcher.toMatchResult().group();

			BlankExpression exp = this.evalExpression(rawExpression);

			// Substitui na linha a expressão pelo resultado
			line = line.replace(rawExpression, " " + exp.result().toString() + " ");

			// Atualiza a busca para resolver mais calculos na linha
			divMultMatcher = divMultIdentifier.matcher(line);
		}

		sumSubMatcher = sumSubIdentifier.matcher(line); // Prepara a busca para + e -
		while (sumSubMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = sumSubMatcher.toMatchResult().group();

			BlankExpression exp = this.evalExpression(rawExpression);

			// Substitui na linha a expressão pelo resultado
			// Também adiciona espações em volta do número para que não se una com outros números
			line = line.replace(rawExpression, " " + exp.result().toString() + " ");

			// Atualiza a busca para encontrar mais calculos na linha
			sumSubMatcher = sumSubIdentifier.matcher(line);
		}

		logicOpMatcher = logicOpIdentifier.matcher(line); // Prepara a busca para + e -
		while (logicOpMatcher.find()) {
			// Guarda a expressão encontrada para ser substituída mais tarde
			String rawExpression = logicOpMatcher.toMatchResult().group();
			
			BlankExpression exp = this.evalExpression(rawExpression);

			// Substitui na linha a expressão pelo resultado
			line = line.replace(rawExpression, " " + exp.result().toString() + " ");

			// Atualiza a busca para resolver mais expressões na linha
			logicOpMatcher = logicOpIdentifier.matcher(line);
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

			// Depois de todas as operações, identifica a linha inicial do if na pilha de ifs

			// Se o resultado for falso
			if (ifResultValue == 0) {
				this.ignoreUntilNextElse = true;
			} else {
				if (ifStack.empty()) {
					ifStack.push(lineNumber);
				} else {
					if (ifStack.peek() != lineNumber) ifStack.push(lineNumber);
				}
				this.ignoreNextElse = true;
			}
		}

		endIfMatcher = endifIdentifier.matcher(line);
		if (endIfMatcher.find()) {
			if (ifStack.empty()) {
				throw new Exception("There is no if for this endif");
			} else {
				ifStack.pop();
			}
		}

		loopMatcher = loopIdentifier.matcher(line);
		if (loopMatcher.find()) {
			String rawLoop = loopMatcher.toMatchResult().group();

			parenthesisMatcher = parenthesisIdentifier.matcher(rawLoop);
			parenthesisMatcher.find();
			String loopParenthesis = parenthesisMatcher.toMatchResult().group();

			paramMatcher = paramIdentifier.matcher(loopParenthesis);
			paramMatcher.find();
			String loopResult = paramMatcher.toMatchResult().group();

			Float loopResultValue;

			/**
			 *	Á esse ponto o interpretador já deve ter substituído na linha
			 *	o valor-resultado da operação dentro da expressão do loop, então é somente um digito que está
			 *	ali presente.
			 */
			if (mainScope.hasVariable(loopResult) >= 0) { // Checa se o valor é uma variavel já definida
				// encontra a variavel e atribui o valor da outra variavel á esta
				loopResultValue = Float.parseFloat(mainScope.getVariable(loopResult).getValue());
			} else {
				loopResultValue = Float.parseFloat(loopResult); // Atribui o valor identificado
			}

			// Se o loop que está no topo, não é o mesmo da linha atual
			if (fluxStack.empty() || fluxStack.peek().getStartingLine() != lineNumber) {
				fluxStack.push(new BlankFlux(lineNumber, endloopIdentifier, loopResultValue != 0)); // Empilha novo fluxo
			}

			if (loopResultValue == 0) {
				if (fluxStack.peek().getEndingLine() != 0) {
					return (fluxStack.pop().getEndingLine() + 1);
				}
			}
		}

		endloopMatcher = endloopIdentifier.matcher(line);
		if (endloopMatcher.find()) {
			if (! fluxStack.empty()) {
				fluxStack.peek().setEndingLine(lineNumber);
				return fluxStack.peek().getStartingLine();
			} else {
				throw new Exception("endloop without being in a loop.");
			}
		}

		showMatcher = showIdentifier.matcher(line); // Prepara a busca para o token show
		if (showMatcher.find()) {
			String printResult = "";
			String analysis = line.split(showIdentifier.toString(), 2)[1]; // pega o que vem após "show"
			strMatcher      = strIdentifier.matcher(analysis);
			paramMatcher    = paramIdentifier.matcher(analysis);
			numberMatcher   = numberIdentifier.matcher(analysis);

			if (strMatcher.find()) {
				printResult = strMatcher.toMatchResult().group().replace("\"", "");
			} else if (numberMatcher.find()) {
				printResult = numberMatcher.toMatchResult().group();
			} else {
				if (paramMatcher.find()) {
					String varName = paramMatcher.toMatchResult().group();

					if (mainScope.hasVariable(varName) >= 0) {
						var = mainScope.getVariable(varName);
					} else {
						throw new Exception("Variable " + varName + " is not set.");
					}

					printResult = var.getValue();
				}
			}

			System.out.println(printResult);
		}

		return ++lineNumber;
	}
}