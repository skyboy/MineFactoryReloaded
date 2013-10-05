package powercrystals.minefactoryreloaded.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRubberWood extends BlockLog implements IConnectableRedNet
{
	private Icon _iconLogTop;
	private Icon _iconLogSide;
	
	public BlockRubberWood(int id)
	{
		super(id);
		setHardness(2.0F);
		setStepSound(soundWoodFootstep);
		setUnlocalizedName("mfr.rubberwood.log");
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		_iconLogSide = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".side");
		_iconLogTop = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + ".top");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta)
	{
		int logDirection = meta & 12;
		return logDirection == 0 && (side == 1 || side == 0) ? _iconLogTop : (logDirection == 4 && (side == 5 || side == 4) ? _iconLogTop : (logDirection == 8 && (side == 2 || side == 3) ? _iconLogTop : _iconLogSide));
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		
		drops.add(new ItemStack(blockID, 1, 0));
		if((metadata & 3) > 0)
		{
			drops.add(new ItemStack(MineFactoryReloadedCore.rawRubberItem, fortune <= 0 ? 1 : 1 + world.rand.nextInt(fortune)));
		}
		
		return drops;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(int blockId, CreativeTabs tab, List subBlocks)
	{
		subBlocks.add(new ItemStack(blockId, 1, 0));
	}
	
	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}
	
	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		return RedNetConnectionType.ForcedPlateSingle;
	}
	
	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side)
	{
		return null;
	}
	
	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet)
	{
		return 0;
	}
	
	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues)
	{
	}
	
	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue)
	{
	}
}
