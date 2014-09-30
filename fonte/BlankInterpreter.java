import java.util.*;

class BlankInterpreter
{
	//protected BlankTokenizer tokenizer = BlankTokenizer.getInstance();
	StringTokenizer tokenizer;

	// protected BlankGrammar   grammar   = BlankGrammar.getInstance();
	// protected BlankAction    action    = BlankAction.getInstance();

	/*public boolean doTheMagic(String line)
	{
		tokens = tokenizer.tokenize(line);

		if (!grammar.check(tokens))
			throw new BlankInterpreterException("Error on Line X " + grammar.lastErrorMessage());

		action.eval(tokens);

	}*/

	public boolean understand(String line)
	{
		Pattern tokenIdentifier;
		System.out.println(tokenizer.nextToken("(?:\\bvar\\W)")); // Token var
		System.out.println("(\\w+)|(\\d)(?=[\\W])");
		System.out.println(tokenizer.nextToken("(\\w+)|(\\d)(?=[\\W])")); // prop var

		// tokenizer.dumpTokens();

		return true;
	}
}