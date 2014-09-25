class BlankInterpreter
{
	protected BlankTokenizer tokenizer = BlankTokenizer.getInstance();
	// protected BlankGrammar   grammar   = BlankGrammar.getInstance();
	// protected BlankAction    action    = BlankAction.getInstance();

	/*public boolean doTheMagic(String line)
	{
		tokens = tokenizer.tokenize(line);

		if (!grammar.check(tokens))
			throw new BlankInterpreterException("Error on Line X " + grammar.lastErrorMessage());

		action.eval(tokens);

	}*/

	public void printLine(String line)
	{
		System.out.println(line);
	}
}