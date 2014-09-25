import java.io.*;
import java.util.Scanner;

class Blank
{
	public static void main(String[] args) throws Exception
	{
		File f;
		Scanner s = null;
		BlankInterpreter interpreter = new BlankInterpreter();

		try {
			f = new File(args[0]);
			s = new Scanner(f);
		} catch (IOException e) {
			System.out.println("Não foi possível ler o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
		} catch (Exception e) {
			System.out.println("Não foi possível ler o arquivo: " + (args.length > 0 ? args[0] : "(desconhecido)"));
		}

		interpreter.printLine(s.nextLine());
	}
}