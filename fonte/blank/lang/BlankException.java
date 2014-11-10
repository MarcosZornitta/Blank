/**
 *	Estendendo a classe Exception do java para melhor mostrar
 *	um erro ocorrido dentro da Blank.
 *	Por enquanto apenas mostra uma mensagem melhor formatada.
 */

package blank.lang;

public class BlankException extends Exception
{
	private int errorLineNumber;
	private String errorLine;
	private String errorMessage;

	public BlankException(String msg, int lineNumber, String line)
	{
		super(msg + " on line [" + (lineNumber + 1) + "]: " + line);

		this.errorMessage    = msg;
		this.errorLine       = line;
		this.errorLineNumber = lineNumber;
	}

	public int getErrorLineNumber()
	{
		return this.errorLineNumber;
	}

	public String getErrorLine()
	{
		return this.errorLine;
	}

	public String getErrorMessage()
	{
		return this.errorMessage;
	}
}
