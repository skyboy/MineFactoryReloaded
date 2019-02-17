package powercrystals.minefactoryreloaded.gui.control;

import cofh.core.gui.element.ElementListBox;
import cofh.core.gui.element.listbox.IListBoxElement;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import powercrystals.minefactoryreloaded.core.MFRUtil;

public class ListBoxElementCircuit implements IListBoxElement {

	private IRedNetLogicCircuit _circuit;
	private ElementListBox gui;

	public ListBoxElementCircuit(ElementListBox gui, IRedNetLogicCircuit circuit) {

		_circuit = circuit;
		this.gui = gui;
	}

	@Override
	public Object getValue() {

		return _circuit;
	}

	@Override
	public int getHeight() {

		return 10;
	}

	@Override
	public int getWidth() {

		return gui.getFontRenderer().getStringWidth(MFRUtil.localize(_circuit.getUnlocalizedName()));
	}

	@Override
	public void draw(ElementListBox listBox, int x, int y, int backColor, int textColor) {

		listBox.getFontRenderer().drawString(MFRUtil.localize(_circuit.getUnlocalizedName()), x, y, textColor);
	}
}
