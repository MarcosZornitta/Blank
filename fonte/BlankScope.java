class BlankScope
{
	/**
	 *	@var BlankScope parent The parent scope of the current scope
	 */
	protected BlankScope parent;

	protected String name;

	protected BlankVar[] variables;

	public BlankScope(BlankScope parent)
	{
		this.parent = parent;
	}

	public BlankScope(String scopeName)
	{
		setName(scopeName);
	}

	public BlankScope(String scopeName, BlankScope parent)
	{
		this(scopeName);
		this(parent);
	}

	public BlankScope getParent()
	{
		return this.parent;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String n)
	{
		this.name = n;
	}

	protected BlankVar getVariable(String name)
	{
		for (int i = 0; i < this.variables.length; i++)
			if (this.variables[i].getName() == name) return this.variables[i];

		throw new Exception("Unreconigzed variable " + name);
	}

	protected void storeVar(BlankVar variable)
	{

	}
}