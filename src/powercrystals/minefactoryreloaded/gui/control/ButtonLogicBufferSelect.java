package powercrystals.minefactoryreloaded.gui.control;

import cofh.lib.gui.element.ElementButtonOption;
import net.minecraft.util.EnumFacing;
import powercrystals.minefactoryreloaded.gui.client.GuiRedNetLogic;

public class ButtonLogicBufferSelect extends ElementButtonOption
{
	private LogicButtonType _buttonType;
	private GuiRedNetLogic _logicScreen;
	private int _pinIndex;
	private boolean _ignoreChanges;
	private int _lastValue;
	
	public ButtonLogicBufferSelect(GuiRedNetLogic containerScreen, int x, int y, int pinIndex, LogicButtonType buttonType, EnumFacing rotation)
	{
		super(containerScreen, x, y, 30, 14);
		_logicScreen = containerScreen;
		_buttonType = buttonType;
		_pinIndex = pinIndex;
		
		char[] dir = {'L','B','R','F',};
		char[] dirMap = new char[4];
		for (int i = 0; i < 4; ++i)
			dirMap[ (i + rotation.getHorizontalIndex() + 1) & 3] = dir[i];
		//TODO needs a lot of refactoring - getHorizontalIndex() + 1 here is a hack to avoid having to go through that now
		
		_ignoreChanges = true;
		if(_buttonType == LogicButtonType.Input)
		{
			setValue(0,  "I/O D");
			setValue(1,  "I/O U");
			setValue(2,  "I/O " + dirMap[2]);
			setValue(3,  "I/O " + dirMap[0]);
			setValue(4,  "I/O " + dirMap[1]);
			setValue(5,  "I/O " + dirMap[3]);
			setValue(12, "CNST");
			setValue(13, "VARS");
			setSelectedIndex(0);
		}
		else
		{
			setValue(6,  "I/O D");
			setValue(7,  "I/O U");
			setValue(8,  "I/O " + dirMap[2]);
			setValue(9,  "I/O " + dirMap[0]);
			setValue(10, "I/O " + dirMap[1]);
			setValue(11, "I/O " + dirMap[3]);
			setValue(13, "VARS");
			setValue(14, "NULL");
			setSelectedIndex(6);
		}
		_ignoreChanges = false;
		setVisible(false);
	}
	
	public int getBuffer()
	{
		return getSelectedIndex();
	}
	
	public void setBuffer(int buffer)
	{
		_ignoreChanges = true;
		setSelectedIndex(buffer);
		if (getValue() == null)
			onClick();
		_lastValue = getSelectedIndex();
		_ignoreChanges = false;
	}
	
	@Override
	public void onValueChanged(int value, String label)
	{
		if(_ignoreChanges)
		{
			return;
		}
		if(_buttonType == LogicButtonType.Input)
		{
			if(value < 6 && _lastValue < 6)
			{
				_logicScreen.setInputPinMapping(_pinIndex, value, _logicScreen.getInputPin(_pinIndex).pin);
			}
			else
			{
				_logicScreen.setInputPinMapping(_pinIndex, value, 0);
			}
		}
		else
		{
			if(value < 6 && _lastValue < 6)
			{
				_logicScreen.setOutputPinMapping(_pinIndex, value, _logicScreen.getOutputPin(_pinIndex).pin);
			}
			else
			{
				_logicScreen.setOutputPinMapping(_pinIndex, value, 0);
			}
		}
		_lastValue = value;
	}
	
	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		if(getValue() == null)
		{
			System.out.println("Buffer selection of " + getSelectedIndex() + " on " + _buttonType + " has null value!");
		}
		super.drawForeground(mouseX, mouseY);
	}
}
