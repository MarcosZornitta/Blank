class BlankTokenizer
{
	/**
	 *	@var BlankTokenizer Singleton Instance
	 */
	private static BlankTokenizer instance = null;

	/**
	 *	@var String[] Token List of the current line analysis
	 */
	protected String[] tokenList;

	/**
	 *	@var String[] Available tokens
	 */
	protected String[] tokenDefinition;

	protected BlankTokenizer()
	{
		this.tokenDefinition = new String[12];
		this.tokenDefinition[0]  = "var";
		this.tokenDefinition[1]  = "=";
		this.tokenDefinition[2]  = "+";
		this.tokenDefinition[3]  = "-";
		this.tokenDefinition[4]  = "*";
		this.tokenDefinition[5]  = "/";
		this.tokenDefinition[6]  = "(";
		this.tokenDefinition[7]  = ")";
		this.tokenDefinition[8]  = "if";
		this.tokenDefinition[9]  = "while";
		this.tokenDefinition[10] = "for";
		this.tokenDefinition[11] = ";";
	}

	/**
	 *	Singleton getInstance
	 *
	 *	@return BlankTokenizer instance
	 */
	public static BlankTokenizer getInstance()
	{
		if (instance == null) {
			instance = new BlankTokenizer();
		}
		
		return instance;
	}

	/**
	 *	Checks if given character is recognized as a token.
	 *	Tokens are defined in tokenDefinition;
	 *
	 *	@see    this.tokenDefinition
	 *	@return boolean true if a token is identified, false otherwise;
	 */
	private boolean isToken(char c)
	{
		for (int i = 0; i < this.tokenDefinition.length; i++)
			if (c == this.tokenDefinition[i]) return true;

		return false;
	}

	private void storeToken(char c)
	{
		if (this.tokenDefinition == null) {
			this.tokenDefinition = new String[1];
			this.tokenDefinition[1] = c;
			return;
		}

		try {
			this.tokenDefinition[this.tokenDefinition.length] = c;
		} catch (Exception e) {
			this.tokenDefinition = new String[this.tokenDefinition.length * 2];
		}
	}

	/** 
	 *	Provides to this.tokenList, in order, the identified tokens by interpretor
	 */
	public boolean tokenize(String line)
	{
		for (int i = 0; i < line.length(); i++) {
			if (this.isToken(line[i])) this.storeToken(line[i]);
			else this.
		}

		return true;
	}
}