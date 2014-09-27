class BlankVar
{
	protected String name;
	protected String value;

	public BlankVar(String name, String value)
	{
		this.name  = name;
		this.value = value;
	}

	public void setName(String n)
	{
		this.name = n;
	}

	public String getName()
	{
		return this.name;
	}

	public void setValue(String v)
	{
		this.value = v;
	}

	public String getValue()
	{
		return this.value;
	}
}