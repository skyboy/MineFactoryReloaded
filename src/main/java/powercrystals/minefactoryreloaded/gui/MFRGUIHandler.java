package powercrystals.minefactoryreloaded.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import powercrystals.minefactoryreloaded.gui.client.GUIBag;
import powercrystals.minefactoryreloaded.gui.client.GuiNeedlegun;
import powercrystals.minefactoryreloaded.gui.client.GuiRedNetLogic;
import powercrystals.minefactoryreloaded.gui.container.ContainerBag;
import powercrystals.minefactoryreloaded.gui.container.ContainerNeedlegun;
import powercrystals.minefactoryreloaded.gui.container.ContainerRedNetLogic;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

import javax.annotation.Nonnull;

public class MFRGUIHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if(te instanceof TileEntityFactory)
			{
				return ((TileEntityFactory)te).getContainer(player.inventory);
			}
			else if(te instanceof TileEntityRedNetLogic)
			{
				return new ContainerRedNetLogic((TileEntityRedNetLogic)te);
			}
		}
		else if(ID == 1)
		{
			@Nonnull ItemStack stack = getCorrectStackFromEitherHand(MFRThings.needlegunItem, player);
			if(!stack.isEmpty())
			{
				return new ContainerNeedlegun(new NeedlegunContainerWrapper(stack), player.inventory);
			}
		}
		else if(ID == 2)
		{
			@Nonnull ItemStack stack = getCorrectStackFromEitherHand(MFRThings.plasticBagItem, player);
			if(!stack.isEmpty())
			{
				return new ContainerBag(stack, player.inventory);
			}
		}
		return null;
	}

	@Nonnull
	private ItemStack getCorrectStackFromEitherHand(Item item, EntityPlayer player) {
		
		for(EnumHand hand : EnumHand.values()) {
			
			@Nonnull ItemStack stack = player.getHeldItem(hand);
			
			if (!stack.isEmpty() && stack.getItem() == item) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if(te instanceof TileEntityFactory)
			{
				return ((TileEntityFactory)te).getGui(player.inventory);
			}
			else if(te instanceof TileEntityRedNetLogic)
			{
				return new GuiRedNetLogic(new ContainerRedNetLogic((TileEntityRedNetLogic)te), (TileEntityRedNetLogic)te);
			}
		}
		else if(ID == 1)
		{
			@Nonnull ItemStack stack = getCorrectStackFromEitherHand(MFRThings.needlegunItem, player);
			if(!stack.isEmpty())
			{
				return new GuiNeedlegun(new ContainerNeedlegun(new NeedlegunContainerWrapper(stack), player.inventory), stack);
			}
		}
		else if(ID == 2)
		{
			@Nonnull ItemStack stack = getCorrectStackFromEitherHand(MFRThings.plasticBagItem, player);
			if(!stack.isEmpty())
			{
				return new GUIBag(new ContainerBag(stack, player.inventory));
			}
		}
		return null;
	}
}
