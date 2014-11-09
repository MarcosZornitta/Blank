/**
 *	Extendendo a classe Exception do java para melhor mostrar
 *	um erro ocorrido dentro da Blank.
 *	Por enquanto apenas mostra uma mensagem melhor formatada.
 */

package blank.lang;

class BlankException extends Exception
{
	public BlankException(String msg, int lineNumber, String line)
	{
		super(msg + " on line [" + (lineNumber + 1) + "]: " + line);
	}
}