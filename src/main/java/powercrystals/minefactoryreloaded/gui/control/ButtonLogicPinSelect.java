package powercrystals.minefactoryreloaded.gui.control;

import cofh.core.gui.GuiColor;
import cofh.core.gui.element.ElementButtonManaged;
import cofh.core.init.CoreProps;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.client.GuiRedNetLogic;

public class ButtonLogicPinSelect extends ElementButtonManaged
{
	private static GuiColor[] _pinColors = new GuiColor[]
			{
		new GuiColor(223, 223, 223), // white
		new GuiColor(219, 125,  63), // orange
		new GuiColor(180,  81, 188), // magenta
		new GuiColor(107, 138, 207), // light blue
		new GuiColor(177, 166,  39), // yellow
		new GuiColor( 66, 174,  57), // lime
		new GuiColor(208, 132, 153), // pink
		new GuiColor( 65,  65,  65), // dark gray
		new GuiColor(155, 155, 155), // light gray
		new GuiColor( 47, 111, 137), // cyan
		new GuiColor(127,  62, 182), // purple
		new GuiColor( 46,  57, 141), // blue
		new GuiColor( 79,  50,  31), // brown
		new GuiColor( 53,  71,  28), // green
		new GuiColor(151,  52,  49), // red
		new GuiColor( 22,  22,  26), // black
			};

	@SuppressWarnings("SpellCheckingInspection")
	private static String[] _pinColorNames = new String[]
			{
		"WHIT",
		"ORNG",
		"MGTA",
		"L_BL",
		"YLLW",
		"LIME",
		"PINK",
		"GRAY",
		"SILV",
		"CYAN",
		"PURP",
		"BLUE",
		"BRWN",
		"GRN",
		"RED",
		"BLK",
			};

	private int _pinIndex;
	private LogicButtonType _buttonType;
	private GuiRedNetLogic _containerScreen;

	private int _pin;
	private int _buffer;

	public ButtonLogicPinSelect(GuiRedNetLogic containerScreen, int x, int y, int pinIndex, LogicButtonType buttonType)
	{
		super(containerScreen, x, y, 30, 14, "");
		_pinIndex = pinIndex;
		_buttonType = buttonType;
		_containerScreen = containerScreen;
		setVisible(false);
	}

	public int getBuffer()
	{
		return _buffer;
	}

	public void setBuffer(int buffer)
	{
		_buffer = buffer;
	}

	public int getPin()
	{
		return _pin;
	}

	public void setPin(int pin)
	{
		_pin = pin;
		setText(((Integer)_pin).toString());
	}

	@Override
	public void onClick()
	{
		int mult = getMult();
		_pin += mult;
		switch (_buffer)
		{
		case 14:
			_pin = 0;
			break;
		case 13:
			_pin %= _containerScreen.getVariableCount();
			break;
		case 12:
			_pin %= 10000;
			break;
		default:
			_pin %= 16;
			break;
		}

		updatePin();
	}

	@Override
	public void onRightClick()
	{
		int mult = getMult();
		_pin -= mult;

		if (_pin < 0)
		{
			switch (_buffer)
			{
			case 14:
				_pin = 0;
				break;
			case 13:
				int t = _containerScreen.getVariableCount();
				_pin = t + (_pin % t);
				break;
			case 12:
				_pin = 10000 + _pin;
			break;
			default:
				_pin = 16 + (_pin % 16);
				break;
			}
		}

		updatePin();
	}

	@Override
	public void onMiddleClick()
	{
		// TODO: replace text display with text input instead
		int mult = getMult();
		if (_buffer == 13)
		{
			_pin += 16 * mult;
			_pin %= _containerScreen.getVariableCount();
			updatePin();
		}
		else if (_buffer == 12)
		{
			_pin += 16 * mult;
			_pin %= 10000;
			updatePin();
		}
		else
		{
			onClick();
		}
	}

	private static final int getMult()
	{
		// ctrl = *2; alt = *4; shift = *8;
		if (MFRUtil.isAltKeyDown())
			if (MFRUtil.isShiftKeyDown())
				if (MFRUtil.isCtrlKeyDown())
					return 64;
				else
					return 32;
			else
				if (MFRUtil.isCtrlKeyDown())
					return 6; // break from multiplying so there's not two 8s
				else
					return 4;
		else if (MFRUtil.isShiftKeyDown())
			if (MFRUtil.isCtrlKeyDown())
				return 16;
			else
				return 8;
		else if (MFRUtil.isCtrlKeyDown())
			return 2;
		else
			return 1;
	}

	private void updatePin()
	{
		setText(String.valueOf(_pin));
		if (_buttonType == LogicButtonType.Input)
		{
			_containerScreen.setInputPinMapping(_pinIndex, _buffer, _pin);
		}
		else
		{
			_containerScreen.setOutputPinMapping(_pinIndex, _buffer, _pin);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		if (_buffer < 12)
		{
			if (!CoreProps.enableColorBlindTextures)
			{
				drawModalRect(posX + 3, posY + 3, posX + sizeX - 3, posY + sizeY - 3, _pinColors[_pin].getColor());
			}
			else
			{

				drawCenteredString(gui.getFontRenderer(), _pinColorNames[_pin], posX + sizeX / 2, posY + sizeY / 2 - 4, getTextColor(mouseX, mouseY));
			}
		}
		else if (_buffer < 14)
		{
			super.drawForeground(mouseX, mouseY);
		}
	}
}
