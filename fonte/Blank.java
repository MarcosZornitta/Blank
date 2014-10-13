import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import blank.lang.*;

class Blank
{
	public static void main(String[] args) throws Exception
	{
		File f = null;
		Scanner s = null;
		int lineNumber = 0;
		BlankInterpreter interpreter = new BlankInterpreter();

		try {
			f = new File(args[0]);
			s = new Scanner(f);
		} catch (IOException e) {
			System.out.println("Não foi possível ler o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
		} catch (Exception e) {
			System.out.println("Não foi possível ler o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
		}

		// Cria um buffer das linhas em uma lista
		List<String> lines = Files.readAllLines(Paths.get(args[0]), Charset.defaultCharset());

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