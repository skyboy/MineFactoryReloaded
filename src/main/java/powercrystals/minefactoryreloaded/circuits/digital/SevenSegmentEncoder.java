package powercrystals.minefactoryreloaded.circuits.digital;

import powercrystals.minefactoryreloaded.circuits.base.StatelessCircuit;

public class SevenSegmentEncoder extends StatelessCircuit
{
	private static String[] _outputPinNames = new String[] { "A", "B", "C", "D", "E", "F", "G" };
	
	private static int[][] _outputMatrix = new int[][]
			{
		//A   B   C   D   E   F   G
		{ 15, 15, 15, 15, 15, 15, 0  }, // 0
		{ 0,  15, 15, 0,  0,  0,  0  }, // 1
		{ 15, 15, 0,  15, 15, 0,  15 }, // 2
		{ 15, 15, 15, 15, 0,  0,  15 }, // 3
		{ 0,  15, 15, 0,  0,  15, 15 }, // 4
		{ 15, 0,  15, 15, 0,  15, 15 }, // 5
		{ 15, 0,  15, 15, 15, 15, 15 }, // 6
		{ 15, 15, 15, 0,  0,  0,  0  }, // 7
		{ 15, 15, 15, 15, 15, 15, 15 }, // 8
		{ 15, 15, 15, 15, 0,  15, 15 }, // 9
		{ 15, 15, 15, 0,  15, 15, 15 }, // A,
		{ 0,  0,  15, 15, 15, 15, 15 }, // B
		{ 15, 0,  0,  15, 15, 15, 0  }, // C
		{ 0,  15, 15, 15, 15, 0,  15 }, // D
		{ 15, 0,  0,  15, 15, 15, 15 }, // E
		{ 15, 0,  0,  0,  15, 15, 15 }  // F
			};
	
	@Override
	public byte getInputCount()
	{
		return 1;
	}
	
	@Override
	public byte getOutputCount()
	{
		return 7;
	}
	
	@Override
	public int[] recalculateOutputValues(long worldTime, int[] inputValues)
	{
		if(inputValues[0] < 0 || inputValues[0] > 15)
		{
			return new int[] { 0, 0, 0, 0, 0, 0, 0 };
		}
		
		return new int[]
				{
				_outputMatrix[inputValues[0]][0],
				_outputMatrix[inputValues[0]][1],
				_outputMatrix[inputValues[0]][2],
				_outputMatrix[inputValues[0]][3],
				_outputMatrix[inputValues[0]][4],
				_outputMatrix[inputValues[0]][5],
				_outputMatrix[inputValues[0]][6],
				};
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return "circuit.mfr.sevensegmentencoder";
	}
	
	@Override
	public String getInputPinLabel(int pin)
	{
		return "I";
	}
	
	@Override
	public String getOutputPinLabel(int pin)
	{
		return _outputPinNames[pin];
	}
}
