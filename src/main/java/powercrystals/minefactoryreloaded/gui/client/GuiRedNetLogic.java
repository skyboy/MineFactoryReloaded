package powercrystals.minefactoryreloaded.gui.client;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButtonManaged;
import cofh.core.gui.element.ElementListBox;
import cofh.core.gui.element.ElementSlider;
import cofh.core.gui.element.listbox.IListBoxElement;
import cofh.core.gui.element.listbox.SliderHorizontal;
import cofh.core.gui.element.listbox.SliderVertical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;
import powercrystals.minefactoryreloaded.block.BlockRedNetLogic;
import powercrystals.minefactoryreloaded.circuits.Noop;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.client.font.PrcFontRenderer;
import powercrystals.minefactoryreloaded.gui.control.ButtonLogicBufferSelect;
import powercrystals.minefactoryreloaded.gui.control.ButtonLogicPinSelect;
import powercrystals.minefactoryreloaded.gui.control.ListBoxElementCircuit;
import powercrystals.minefactoryreloaded.gui.control.LogicButtonType;
import powercrystals.minefactoryreloaded.net.MFRPacket;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic.PinMapping;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GuiRedNetLogic extends GuiContainerCore {

	private class CircuitComparator implements Comparator<IRedNetLogicCircuit> {

		@Override
		public int compare(IRedNetLogicCircuit arg0, IRedNetLogicCircuit arg1) {

			boolean a = arg0 instanceof Noop, b = arg1 instanceof Noop;
			if (a != b)
				return a ? -1 : 1;
			return MFRUtil.localize(arg0.getUnlocalizedName()).compareTo(MFRUtil.localize(arg1.getUnlocalizedName()));
		}
	}

	private static final int pinOffset = 13;

	private TileEntityRedNetLogic _logic;

	private int _selectedCircuit;

	private ElementListBox _circuitList;

	private ElementSlider _circuitScrollV;
	private ElementSlider _circuitScrollH;

	private ButtonLogicBufferSelect[] _inputIOBufferButtons = new ButtonLogicBufferSelect[16];
	private ButtonLogicBufferSelect[] _outputIOBufferButtons = new ButtonLogicBufferSelect[16];

	private ButtonLogicPinSelect[] _inputIOPinButtons = new ButtonLogicPinSelect[16];
	private ButtonLogicPinSelect[] _outputIOPinButtons = new ButtonLogicPinSelect[16];

	private ElementButtonManaged _nextCircuit;
	private ElementButtonManaged _prevCircuit;

	private ElementButtonManaged _reinit;
	private ElementButtonManaged _reinitConfirm;

	private boolean _listNeedsUpdated = true;
	private int _reinitCountdown;

	private FontRenderer uFontRenderer, rFontRenderer;

	public GuiRedNetLogic(Container container, TileEntityRedNetLogic logic) {

		super(container, new ResourceLocation(MFRProps.GUI_FOLDER + "rednetlogic.png"));
		xSize = 384;
		ySize = 231;
		drawInventory = false;
		drawTitle = false;

		_logic = logic;
		name = logic.getBlockName();
	}

	@Override
	public FontRenderer getFontRenderer() {

		return rFontRenderer;
	}

	@Override
	public void initGui() {

		uFontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, true);
		uFontRenderer.onResourceManagerReload(mc.getResourceManager());

		rFontRenderer = new PrcFontRenderer(mc.gameSettings, mc.renderEngine, false);
		rFontRenderer.onResourceManagerReload(mc.getResourceManager());

		super.initGui();

		List<IRedNetLogicCircuit> circuits = new LinkedList<>(MFRRegistry.getRedNetLogicCircuits());
		// circuits.add(new Debug());
		circuits.sort(new CircuitComparator());
		boolean unicode = false;
		for (IRedNetLogicCircuit e : circuits) {
			if (MFRUtil.containsForcedUnicode(MFRUtil.localize(e.getUnlocalizedName()))) {
				unicode = true;
				break;
			}
		}
		final FontRenderer font = unicode ? uFontRenderer : fontRenderer;

		_circuitList = new ElementListBox(this, 88, 17, 131, 196) {

			@Override
			protected void onSelectionChanged(int newIndex, IListBoxElement newElement) {

			}

			@Override
			public FontRenderer getFontRenderer() {

				return font;
			}

			@Override
			protected void onElementClicked(IListBoxElement newElement) {

				MFRPacket.requestLogicSetCircuitFromServer(_logic, _selectedCircuit, newElement.getValue().getClass().getName());
			}

			@Override
			protected void onScrollV(int newStartIndex) {

				_circuitScrollV.setValue(newStartIndex);
			}

			@Override
			protected void onScrollH(int newStartIndex) {

				_circuitScrollH.setValue(newStartIndex);
			}
		};

		for (IRedNetLogicCircuit circuit : circuits) {
			_circuitList.add(new ListBoxElementCircuit(_circuitList, circuit));
		}

		addElement(_circuitList);

		_circuitScrollV = new SliderVertical(this, 220, 17, 10, 196, _circuitList.getLastScrollPosition()) {

			@Override
			public void onValueChanged(int value) {

				_circuitList.scrollToV(value);
			}
		};

		addElement(_circuitScrollV);

		final int maxScrollH = _circuitList.getLastScrollPositionH(), mult = 15;
		int scrollH = (maxScrollH + (mult - 1)) / mult, scrollW = 142;
		_circuitScrollH = new SliderHorizontal(this, 88, 214, scrollW, 10, scrollH) {

			@Override
			public void onValueChanged(int value) {

				value = value >= _valueMax ? maxScrollH : value * mult;
				_circuitList.scrollToH(value);
			}
		};

		addElement(_circuitScrollH);

		_prevCircuit = new ElementButtonManaged(this, 340, 76, 30, 30, "Prev") {

			@Override
			public void onClick() {

				_selectedCircuit--;
				if (_selectedCircuit < 0) {
					_selectedCircuit = _logic.getCircuitCount() - 1;
				}
				MineFactoryReloadedClient.prcPages.put(_logic.getPos(), _selectedCircuit);
				requestCircuit();
				_listNeedsUpdated = true;
			}
		};

		_nextCircuit = new ElementButtonManaged(this, 340, 16, 30, 30, "Next") {

			@Override
			public void onClick() {

				_selectedCircuit++;
				if (_selectedCircuit >= _logic.getCircuitCount()) {
					_selectedCircuit = 0;
				}
				MineFactoryReloadedClient.prcPages.put(_logic.getPos(), _selectedCircuit);
				requestCircuit();
				_listNeedsUpdated = true;
			}
		};

		addElement(_prevCircuit);
		addElement(_nextCircuit);

		_reinit = new ElementButtonManaged(this, 318, 205, 60, 20, "Reinitialize") {

			@Override
			public void onClick() {

				_reinitCountdown = 55;
			}
		};

		_reinitConfirm = new ElementButtonManaged(this, 318, 205, 60, 20, "Confirm") {

			@Override
			public boolean isEnabled() {

				return super.isEnabled() && _reinitCountdown < 25;
			}

			@Override
			public void onClick() {

				MFRPacket.sendLogicReinitializeToServer(_logic,	Minecraft.getMinecraft().player.getEntityId());
				_reinitCountdown = 0;
				_listNeedsUpdated = true;
			}
		};

		addElement(_reinit);
		addElement(_reinitConfirm);

		_reinitConfirm.setVisible(false);

		EnumFacing rotation = _logic.getWorld().getBlockState(_logic.getPos()).getValue(BlockRedNetLogic.FACING);

		for (int i = 0; i < _inputIOPinButtons.length; i++) {
			_inputIOBufferButtons[i] = new ButtonLogicBufferSelect(this, 25, 16 + i * pinOffset, i, LogicButtonType.Input,
				rotation);
			_inputIOPinButtons[i] = new ButtonLogicPinSelect(this, 54, 16 + i * pinOffset, i, LogicButtonType.Input);

			_outputIOBufferButtons[i] = new ButtonLogicBufferSelect(this, 254, 16 + i * pinOffset, i, LogicButtonType.Output,
				rotation);
			_outputIOPinButtons[i] = new ButtonLogicPinSelect(this, 283, 16 + i * pinOffset, i, LogicButtonType.Output);

			addElement(_inputIOBufferButtons[i].setVisible(false));
			addElement(_outputIOBufferButtons[i].setVisible(false));
			addElement(_inputIOPinButtons[i].setVisible(false));
			addElement(_outputIOPinButtons[i].setVisible(false));
		}

		Integer lastPage = MineFactoryReloadedClient.prcPages.get(_logic.getPos());
		if (lastPage != null && lastPage < _logic.getCircuitCount()) {
			_selectedCircuit = lastPage;
		}
		requestCircuit();
	}

	@Override
	public void updateElementInformation() {

		IRedNetLogicCircuit c = _logic.getLastCircuit(_selectedCircuit);
		if (c != null && c != _logic.getCircuit(_selectedCircuit)) {
			if (_listNeedsUpdated) {
				for (int i = 0; i < _circuitList.getElementCount(); i++)
					if (((IRedNetLogicCircuit) _circuitList.getElement(i).getValue()).getClass() == _logic.getCircuit(
						_selectedCircuit).getClass()) {
						_circuitList.setSelectedIndex(i);
						_circuitScrollV.setValue(Math.min(i, _circuitList.getLastScrollPosition()));
						break;
					}
				_listNeedsUpdated = false;
			}

			for (int i = 0; i < _inputIOPinButtons.length; i++) {
				if (i < _logic.getCircuit(_selectedCircuit).getInputCount()) {
					_inputIOPinButtons[i].setVisible(true);
					_inputIOBufferButtons[i].setVisible(true);
					_inputIOPinButtons[i].setPin(_logic.getInputPinMapping(_selectedCircuit, i).pin);
					_inputIOPinButtons[i].setBuffer(_logic.getInputPinMapping(_selectedCircuit, i).buffer);
					_inputIOBufferButtons[i].setBuffer(_logic.getInputPinMapping(_selectedCircuit, i).buffer);
				}
				else {
					_inputIOBufferButtons[i].setVisible(false);
					_inputIOPinButtons[i].setVisible(false);
				}
			}

			for (int i = 0; i < _outputIOPinButtons.length; i++) {
				if (i < _logic.getCircuit(_selectedCircuit).getOutputCount()) {
					_outputIOBufferButtons[i].setVisible(true);
					_outputIOPinButtons[i].setVisible(true);
					_outputIOPinButtons[i].setPin(_logic.getOutputPinMapping(_selectedCircuit, i).pin);
					_outputIOPinButtons[i].setBuffer(_logic.getOutputPinMapping(_selectedCircuit, i).buffer);
					_outputIOBufferButtons[i].setBuffer(_logic.getOutputPinMapping(_selectedCircuit, i).buffer);
				}
				else {
					_outputIOBufferButtons[i].setVisible(false);
					_outputIOPinButtons[i].setVisible(false);
				}
			}
		}

		if (_reinitCountdown > 0) {
			_reinitCountdown--;
		}

		_reinit.setVisible(_reinitCountdown == 0);
		_reinitConfirm.setVisible(_reinitCountdown > 0);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		String name = this.name;
		if (name == null)
			name = MFRUtil.localize("tile.mfr.rednet.logic");
		FontRenderer font = MFRUtil.containsForcedUnicode(name) ? uFontRenderer : fontRenderer;
		font.drawString(name, 8, 5, 4210752);
		String count = String.valueOf(_logic.getCircuitCount());
		int countP = 370 - (count.length() == 1 ? fontRenderer.getCharWidth('0') : 0);
		count = (_selectedCircuit + 1) + "/" + count;
		fontRenderer.drawString(count, countP - fontRenderer.getStringWidth(count), 58, 4210752);

		for (int i = 0; i < _inputIOPinButtons.length; i++) {
			if (i < _logic.getCircuit(_selectedCircuit).getInputCount()) {
				rFontRenderer.drawString(_logic.getCircuit(_selectedCircuit).getInputPinLabel(i), 5, 20 + i * pinOffset,
					4210752);
			}
		}

		for (int i = 0; i < _outputIOPinButtons.length; i++) {
			if (i < _logic.getCircuit(_selectedCircuit).getOutputCount()) {
				rFontRenderer.drawString(_logic.getCircuit(_selectedCircuit).getOutputPinLabel(i), 234, 20 + i * pinOffset,
					4210752);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int x, int y) {

		mouseX = x - guiLeft;
		mouseY = y - guiTop;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(texture);
		drawLargeTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0.0F);
		drawElements(gameTicks, false);
		drawTabs(gameTicks, false);
		GlStateManager.popMatrix();
	}

	public void drawLargeTexturedModalRect(int x, int y, int u, int v, int xSize, int ySize) {

		float uScale = 1.0F / 384.0F;
		float vScale = 1.0F / 256.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder BufferBuilder = tessellator.getBuffer();
		BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		BufferBuilder.pos(x + 0, y + ySize, this.zLevel).tex((u + 0) * uScale, (v + ySize) * vScale).endVertex();
		BufferBuilder.pos(x + xSize, y + ySize, this.zLevel).tex((u + xSize) * uScale, (v + ySize) * vScale).endVertex();
		BufferBuilder.pos(x + xSize, y + 0, this.zLevel).tex((u + xSize) * uScale, (v + 0) * vScale).endVertex();
		BufferBuilder.pos(x + 0, y + 0, this.zLevel).tex((u + 0) * uScale, (v + 0) * vScale).endVertex();
		tessellator.draw();
	}

	public PinMapping getInputPin(int pinIndex) {

		return _logic.getInputPinMapping(_selectedCircuit, pinIndex);
	}

	public PinMapping getOutputPin(int pinIndex) {

		return _logic.getOutputPinMapping(_selectedCircuit, pinIndex);
	}

	private void requestCircuit() {

		MFRPacket.requestCircuitDefinitionFromServer(_logic, _selectedCircuit);
	}

	public void setInputPinMapping(int index, int buffer, int pin) {

		MFRPacket.sendLogicSetPinToServer(_logic, (byte) 0, _selectedCircuit, index, buffer, pin);
	}

	public void setOutputPinMapping(int index, int buffer, int pin) {

		MFRPacket.sendLogicSetPinToServer(_logic, (byte) 1, _selectedCircuit, index, buffer, pin);
	}

	public int getVariableCount() {

		return _logic.getVariableBufferSize();
	}

}
