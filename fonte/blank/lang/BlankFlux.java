/**
 *	BlankFlux é uma classe de controle para os seletore de fluxo usados na BlankIterpreter
 *	Ela pode guardar as linhas de início, de final e o resultado de uma expressão.
 *	Por ser uma estrutura de controle apenas, ela não pode interagir ou controlar a 
 *  estrutura em questão.
 */

package blank.lang;
import java.util.*;
import java.util.regex.*;

class BlankFlux
{
	private int     startingLine;
	private int     endingLine;
	private boolean expressionResult;
	private Pattern endFluxPattern;

	public BlankFlux(int sl, Pattern p, boolean result)
	{
		this.startingLine = sl;
		this.endFluxPattern = p;
		this.expressionResult = result;
	}

	public int getStartingLine()
	{
		return this.startingLine;
	}

	public int getEndingLine()
	{
		return this.endingLine;
	}

	public void setEndingLine(int l)
	{
		this.endingLine = l;
	}

	public boolean result()
	{
		return this.expressionResult;
	}

	public void setResult(boolean r)
	{
		this.expressionResult = r;
	}

	public Pattern getEndFluxPattern()
	{
		return this.endFluxPattern;
	}
}