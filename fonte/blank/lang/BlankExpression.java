package blank.lang;

import java.util.*;

class BlankExpression
{
	public static final String OP_SUM = "+";
	public static final String OP_SUB = "-";
	public static final String OP_DIV = "/";
	public static final String OP_MUL = "*";
	public static final String OP_GTR = ">";
	public static final String OP_LWR = "<";
	public static final String OP_GTE = ">=";
	public static final String OP_LWE = "<=";
	public static final String OP_EQL = "==";
	public static final String OP_DIF = "!=";

	private float param1, param2;
	private String op;
	private float result = null;

	public BlankExpression(String param1, String param2, String op)
	{
		this.param1 = Float.parseFloat(param1);
		this.param2 = Float.parseFloat(param2);
		this.op     = op;
	}

	public void setParam1(String p)
	{
		this.param1 = Float.parseFloat(p);
	}

	public void setParam2(String p)
	{
		this.param2 = Float.parseFloat(p);
	}

	public void setOp(String op)
	{
		this.op = op;
	}

	public float getParam1() { return this.param1; }
	public float getParam2() { return this.param2; }

	public float getOp() { return this.op; }

	public float calculate() throws Exception
	{
		switch (op) {
			case OP_SUM:
				result = param1 + param2;
				break;

			case OP_SUB:
				result = param1 - param2;
				break;

			case OP_DIV:
				result = param1 / param2;
				break;

			case OP_MUL:
				result = param1 * param2;
				break;

			case OP_GTR:
				boolean boolResult = param1 > param2;
				result = boolResult.toString() == "true" ? 1f : 0f;
				break;

			case OP_LWR:
				boolean boolResult = param1 < param2;
				result = boolResult.toString() == "true" ? 1f : 0f;
				break;

			case OP_GTE:
				boolean boolResult = param1 >= param2;
				result = boolResult.toString() == "true" ? 1f : 0f;
				break;

			case OP_LWE:
				boolean boolResult = param1 <= param2;
				result = boolResult.toString() == "true" ? 1f : 0f;
				break;

			case OP_EQL:
				boolean boolResult = param1 == param2;
				result = boolResult.toString() == "true" ? 1f : 0f;
				break;

			case OP_DIF:
				boolean boolResult = param1 != param2;
				result = boolResult.toString() == "true" ? 1f : 0f;
				break;

			default:
				throw new Exception("Cannot parse the operation \"" + op + "\"");
		}
	}

	public float result()
	{
		if (result == null) this.calculate();

		return this.result;
	}
}
