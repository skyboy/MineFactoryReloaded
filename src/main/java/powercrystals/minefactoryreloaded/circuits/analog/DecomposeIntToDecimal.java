package powercrystals.minefactoryreloaded.circuits.analog;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class DecomposeIntToDecimal extends StatelessCircuit
{
	@Override
	public byte getInputCount()
	{
		return 1;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 11;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		int[] returnArray = new int[11];
		//add a sign bit to the first 
		if(inputValues[0] >= 0)
		{
			returnArray[0] = 0;
		}
		else
		{
			returnArray[0] = 15;
		}
		char[] decimalChars = Integer.toString(Math.abs(inputValues[0])).toCharArray();
		// put the decimal digits in the return array, little endian.
		for(int i = decimalChars.length - 1 ; i >= 0 ; i--)
		{
			returnArray[decimalChars.length - i] = Character.digit(decimalChars[i], 10);
		}
		// fill the remainder of the array with zeroes.
		for(int i = decimalChars.length + 1 ; i < returnArray.length ; i++)
		{
			returnArray[i] = 0;
		}
		return returnArray;
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.decompose.decimal";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "I";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return pin == 0 ? "SN" : "D" + (pin - 1);
	}
}
