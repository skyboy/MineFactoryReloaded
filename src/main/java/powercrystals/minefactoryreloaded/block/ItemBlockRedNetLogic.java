package powercrystals.minefactoryreloaded.block;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockRedNetLogic extends ItemBlock {

	@Nonnull
	public static ItemStack manual;

	static {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("author", "powercrystals");
		nbt.setString("title", "PRC Owner's Manual");

		NBTTagList pages = new NBTTagList();
		pages.appendTag(wrapText("     Programmable\n        RedNet\n      Controller\n\nOwner's Manual v1.0\n\n\n" +
				"WARNING: YOU MAY NEED TO ADJUST YOUR GUI SCALE SMALLER TO APPROPRIATELY USE THIS PRODUCT"));
		pages.appendTag(wrapText(
				"Congratulations on your purchase, assembly, or theft of your new Programmable RedNet Controller (PRC). " +
						"This manual will attempt to guide you through its features and operations."));
		pages.appendTag(wrapText(
				"Features\n\n* 16 I/O points on\n  each side\n* 16 internal\n  variables\n* 6 user-defined\n  circuits\n" +
						"* Expandable with\n  LX-100, 300, 500\n  expansion modules\n  (sold separately)"));
		pages.appendTag(wrapText(
				"Circuits\n\nThe PRC is composed of one or more circuits. Each circuit is executed sequentially on every " +
						"PRC clock cycle. Circuits are composed of inputs and outputs."));
		pages.appendTag(wrapText(
				"Circuits\n\nInputs can come from I/O, variables, or constant values. Outputs can be routed to I/O, " +
						"variables, or null (disconnected). These options are discussed separately."));
		pages.appendTag(wrapText("Circuits\n\nAll circuits default to No-Op when the PRC is initialized."));
		pages.appendTag(wrapText(
				"Constants\n\nThe PRC can use constant values for its inputs. It contains 16 constants, with values " +
						"0-15 in them."));
		pages.appendTag(wrapText(
				"I/O\n\nEach face of the PRC can be connected to up to 16 I/O points. The front face cannot be connected, " +
						"but the PRC can be rotated by using your standard sledgehammer."));
		pages.appendTag(wrapText(
				"I/O\n\nEach I/O point is indicated by a color, ranging from white to black as per the standard elements. " +
						"I/O inputs will update on the next PRC clock cycle."));
		pages.appendTag(wrapText(
				"Variables\n\nThe PRC contains variables that can be used by its circuits. Variables propagate between " +
						"circuits on the same clock cycle; unlike the I/O delay."));
		pages.appendTag(wrapText("Null\n\nRoute outputs to Null if you do not want to use the value."));
		pages.appendTag(wrapText(
				"GUI\n\nThe PRC GUI has four sections - from left to right they are input pins, circuit type, output pins, " +
						"and circuit selection."));
		pages.appendTag(wrapText(
				"GUI: Circuit Type\n\nSelect a circuit type from the list to change the selected circuit in the PRC to that " +
						"circuit type. Use the Next and Prev buttons to change which circuit is being edited."));
		pages.appendTag(wrapText(
				"GUI: Pin Mapping\n\nPRC pins are connected in two parts, buffer and pin number. Buffer selects I/O and face, " +
						"variables, constants, or null, as applicable. Pin chooses which I/O color, or which variable/constant."));
		pages.appendTag(wrapText(
				"GUI: Pin Mapping\n\nLeft click advances by one, and right click reverses by one. Middle click " +
						"advances by 16 when using variables or constants (one for I/O)."));
		pages.appendTag(wrapText(
				"RedNet Meter\n\nMuch like it can be used to read out values of RedNet cables, the RedNet Meter can " +
						"be used to read out the state of the PRC's variables."));
		pages.appendTag(wrapText(
				"Memory Cards\n\nIf you need to back up your PRC or copy it to another PRC, use a memory card. Using " +
						"a blank card on a PRC will copy its program, and using a programmed card will write its program."));
		pages.appendTag(wrapText(
				"Examples\n\nThe next sections cover some basic PRC scenarios. Face your PRC West with your sledgehammer " +
						"before beginning. These examples assume access to some supplies (next page)."));
		pages.appendTag(wrapText(
				"Examples\n\nYou will need:\n* A few dozen cables\n* Several levers\n* Several buttons\n* Several lamps\n" +
						"* A daylight sensor\n* A piston\n* A note block\n* A sledgehammer"));
		pages.appendTag(wrapText(
				"Example 1: AND gate\n\nConnect RedNet cabling to the North face of your PRC, and place two levers and a lamp on " +
						"the cable. Set the levers to Orange and Magenta with your sledgehammer."));
		pages.appendTag(wrapText(
				"Ex 1: AND gate\n\nOpen your PRC. Set circuit one to And (2 Input). Set the input I/Os to I/O N, Orange and " +
						"Magenta, and the output to I/O N, White."));
		pages.appendTag(wrapText(
				"Ex 1: AND gate\n\nManipulate the levers. Watch the lamp. It will light when both levers are on " +
						"and deactivate when they are turned off."));
		pages.appendTag(wrapText(
				"Ex 2: Timer\n\nConnect RedNet cabling to the South face of your PRC, and attach a lamp to it. " +
						"Open your PRC and set circuit 2 to Wave (Square). Set the input to CNST 40. Set the output to I/O S, White."));
		pages.appendTag(wrapText(
				"Ex 2: Timer\n\nThe lamp will blink about once per second. You can use the square wave generator for any device " +
						"that requires an alternating redstone signal."));
		pages.appendTag(wrapText(
				"Ex 2: Timer\n\nThe PRC runs at a clock rate of 20 cycles per second. Adjust the period (Pd) of the " +
						"timer to make it faster or slower. Values too fast (<5 t) may not work correctly with all devices."));
		pages.appendTag(wrapText(
				"Ex 3: Door Toggle\n\nConnect cabling to the East face of your PRC. Attach a button to White and a piston to Orange. " +
						"If you have extra parts, you may want to build a real door."));
		pages.appendTag(wrapText(
				"Ex 3: Door Toggle\n\nOpen your PRC and set circuit 3 to a T-FlipFlop. Connect I/O E, White to both inputs, " +
						"and I/O E Orange to Q. Press the button."));
		pages.appendTag(wrapText(
				"Ex 3: Door Toggle\n\nNote the piston extends and retracts on each button press. The T-FlipFlop samples the input " +
						"value on a rising edge of CLK - if you don't care, connect both to the same input as here."));
		pages.appendTag(wrapText(
				"Ex 4: Daylight Sensor/Variables\n\nConnect RedNet cabling to the top of your PRC. Attach a daylight sensor to White, " +
						"and a note block to Orange. Set a pleasing tone on the note block."));
		pages.appendTag(wrapText(
				"Ex 4: Daylight Sensor/Variables\n\nOpen the PRC. Set circuit 4 to Equals, with I/O U White and CNST 3 as inputs, and the " +
						"output to VARS 0. Set circuit 5 to a One-Shot Pulse, with input VARS 0 and output I/O U Orange."));
		pages.appendTag(wrapText(
				"Ex 4: Daylight Sensor/Variables\n\nWhen the sun rises and sets, the daylight sensor's output level will reach 3. As it " +
						"arrives at that value, the note block will be pulsed. Use it as an alarm clock!"));
		pages.appendTag(
				wrapText("Ex 5: Daylight Clock\n\nConstruct an \"8\" out of redstone lamps. It should look like this:\n" +
						" XX \nX  X\nX  X\n XX \nX  X\nX  X\n XX \nEnsure no blocks connect the lamps."));
		pages.appendTag(wrapText(
				"Ex 5: Daylight Clock\n\nStarting with the top section and proceeding clockwise, connect them as follows: " +
						" cyan, purple, blue, brown, green, red, black. Red should be the top-left pair and black the center pair."));
		pages.appendTag(wrapText(
				"Ex 5: Daylight Clock\n\nConnect those wires to the top face of the PRC (along with the daylight sensor). " +
						"Open the PRC, and set circuit 6 to Seven-Segment Encoder."));
		pages.appendTag(wrapText(
				"Ex 5: Daylight Clock\n\nConnect I to I/O U White, and A through G to cyan through black in order. If you " +
						"have wired everything correctly, the clock will display the current daylight value."));
		pages.appendTag(wrapText(
				"Ex 5: Daylight Clock\n\nNote that as the daylight sensor has outputs 0-15, these are represented as hex characters " +
						"as 0-9 and A-F."));
		pages.appendTag(wrapText(
				"Additional Suggestions\n\n* Try the various wave generators connected to redstone dust\n* Try using the counter - " +
						"PRE is the rollover target.\n* Try using weighted pressure plates combined with greater/less than."));
		pages.appendTag(wrapText(
				"Summary\n\nNote that the examples all function independently, as the PRC has multiple circuits and " +
						"can process many tasks at once. Amazing things can be accomplished using the PRC."));
		pages.appendTag(wrapText("We hope you enjoy your new Programmable RedNet Controller!"));

		nbt.setTag("pages", pages);
		manual = new ItemStack(Items.WRITTEN_BOOK);
		manual.setTagCompound(nbt);
	}

	private static NBTTagString wrapText(String text) {

		ITextComponent itextcomponent = new TextComponentString(text);
		text = ITextComponent.Serializer.componentToJson(itextcomponent);
		 return new NBTTagString(text);
	}

	public ItemBlockRedNetLogic(net.minecraft.block.Block id) {

		super(id);
		setContainerItem(this);
		setCreativeTab(MFRCreativeTab.tab);
		setMaxStackSize(64);
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {

		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("circuits"))
			return 1;
		return maxStackSize;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {

		tooltip.add(I18n.translateToLocal("tip.info.mfr.prc"));
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("circuits", 9)) {
				int c = stack.getTagCompound().getTagList("circuits", 10).tagCount();
				tooltip.add(MFRUtil.localize("tip.info.mfr.memorycard.programmed", c));
			}
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(manual);
		}
	}
}
