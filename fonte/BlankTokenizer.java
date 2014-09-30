import java.util.*;

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
	protected boolean isToken(char c)
	{
		for (int i = 0; i < this.tokenDefinition.length; i++) {
			for (int j = 0; j < this.tokenDefinition[i].length(); j++) {
				if ((c + "") == this.tokenDefinition[i]) return true;
			}
		}

		return false;
	}

	/*protected void storeToken(String s)
	{
		if (this.tokenDefinition == null) {
			this.tokenDefinition = new String[1];
			this.tokenDefinition[1] = s;
			return;
		}

		try {
			this.tokenDefinition[this.tokenDefinition.length] = s;
		} catch (Exception e) {
			this.tokenDefinition = new String[this.tokenDefinition.length * 2];
		}
	}*/

	protected void storeParam(String line, int paramStart)
	{
		int i = paramStart;
		String param = "";

		while (!this.isToken(line.charAt(i))) {
			param += line.charAt(i);
		}

		this.storeToken(param);
	}

	protected void storeToken(String s)
	{
		if (this.tokenDefinition == null) {
			this.tokenDefinition = new String[1];
			this.tokenDefinition[1] = s;
			return;
		}

		try {
			this.tokenDefinition[this.tokenDefinition.length] = s;
		} catch (Exception e) {
			this.tokenDefinition = new String[this.tokenDefinition.length * 2];
		}
	}

	/** 
	 *	Provides to this.tokenList, in order, the identified tokens by interpreter
	 */
	public boolean tokenize(String line)
	{
		// for (int i = 0; i < line.length(); i++) {
		// 	if (this.isToken(line.charAt(i))) this.storeToken(line.charAt(i) + "");
		// 	else this.storeParam(line, i);
		// 	System.out.println("Loop " + i);
		// }

		// Var Tokenize: (?:\bvar\W)

		StringTokenizer st = new StringTokenizer(line, "\\(\\)\\/\\;\\,\\+\\-\"\\*\\=\\{\\}\\s|(print)|(if)|(var)");

		// while (st.hasMoreTokens()) {
		// 	System.out.println(st.nextToken());
		// }

		while (st.hasMoreElements()) {
			System.out.println(st.nextElement());
		}

		return true;

		// boolean readingToken = false, isToken = false;
		// int tokenReadingStart;

		// for (int i = 0; i < line.length(); i++) {
		// 	if (line.charAt(i) == ' ') continue;

		// 	for (int j = 0; j < this.tokenDefinition.length; j++) {
		// 		for (int k = 0; k < this.tokenDefinition[j].length(); k++) {
		// 			if (line.charAt(i) != this.tokenDefinition[j].charAt(k)) {
		// 				isToken = false;
		// 				break;
		// 			}

		// 			if (k == this.tokenDefinition[j].length() - 1) {
		// 				isToken = true;
		// 				this.storeToken(this.tokenDefinition[j]);
		// 			}
		// 		}
		// 	}
		// }

		// String[] tokens = line.split("\\s+");

		// System.out.println(tokens.length);

		// for (int i = 0; i < tokens.length; i++)	{
		// 	System.out.println("Token at [" + i + "]: " + tokens[i]);
		// }

		// return true;
	}

	public void dumpTokens()
	{
		for (int i = 0; i < this.tokenList.length; i++)	{
			System.out.println("Token at [" + i + "]: " + this.tokenList[i]);
		}
	}
}