/**
 *	Blank Language Interpreter
 *	
 *	Interpretador da linguagem "Blank"
 *
 *	@see https://github.com/MarcosZornitta/Blank
 *	@author Gabriel Henrique Rudey (@whothey) | Marcos Alexandre Zornitta Ferreira
 *	@version Alpha
 */

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import blank.lang.*;

class Blank
{
	public static void main(String[] args) throws Exception
	{
		int lineNumber = 0;
		BlankInterpreter interpreter = new BlankInterpreter();
		List<String> lines; // Buffer de Linhas

		try {
			// Cria um buffer das linhas em uma lista
			lines = Files.readAllLines(Paths.get(args[0]), Charset.defaultCharset());
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Não foi possível ler o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
		}

		/**
		 *	O interpretador e todas as suas mecânicas.
		 *	Basicamente o interpretador retornará a próxima linha á ser lida.
		 *	Se for retornado um valor menor que 0 ou chegar no fim das linhas do buffer "lines"
		 *	então o loop de leitura é parado.
		 */
		while (lineNumber >= 0 && lineNumber < lines.size()) {
			lineNumber = interpreter.understand(lines.get(lineNumber), lineNumber);
		}
	}
}