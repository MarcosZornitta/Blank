package blank.lang;

import java.util.*;

class BlankScope
{
	/**
	 *	@var BlankScope parent The parent scope of the current scope
	 */
	protected BlankScope parent;

	protected String name;

	protected ArrayList<BlankVar> variables = new ArrayList<BlankVar>();

	public BlankScope(String scopeName)
	{
		setName(scopeName);
	}

	public BlankScope(String scopeName, BlankScope parent)
	{
		setName(scopeName);
		this.parent = parent;
	}

	public boolean hasParent()
	{
		return this.parent == null;
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

	public BlankVar getVariable(String name) throws Exception
	{
		int exists = this.hasVariable(name);

		if (exists == -1)
			throw new Exception ("Unreconigzed variable " + name);

		return this.variables.get(exists);
	}

	/**
	 *	Verify if has an variable with the given name in the Variable List.
	 *
	 *	@return Integer The variable index on list or -1 if none was found
	 */
	public int hasVariable(String name)
	{
		for (int i = 0; i < this.variables.size(); i++)
			if (this.variables.get(i).equals(name)) return i;

		if (this.hasParent())
			return this.getParent().hasVariable(name);

		return -1;
	}

	protected void storeVariable(BlankVar variable)
	{
		int has = this.hasVariable(variable.getName());

		if (has == -1)
			this.variables.add(variable);
		else
			this.variables.set(has, variable);
	}
}
