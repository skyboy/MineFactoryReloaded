package powercrystals.minefactoryreloaded.circuits;


public class Debug extends Noop {

	@Override
	public byte getInputCount()
	{
		return 16;
	}

	@Override
	public byte getOutputCount()
	{
		return 16;
	}

	@Override
	public String getUnlocalizedName()
	{
		return "######### Debug #########";
	}

	@Override
	public String getInputPinLabel(int pin)
	{
		switch (pin) {
		case 0:
			return "MMM";
		case 1:
			return "QQQ";
		case 2:
			return "WWW";
		}
		return "P" + String.valueOf(pin);
	}

	@Override
	public String getOutputPinLabel(int pin)
	{
		return getInputPinLabel(pin);
	}

}
