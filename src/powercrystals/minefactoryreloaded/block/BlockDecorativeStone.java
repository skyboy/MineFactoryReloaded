package powercrystals.minefactoryreloaded.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecorativeStone extends Block implements IConnectableRedNet
{
	private String[] _names = new String [] { "black.smooth", "white.smooth", "black.cobble", "white.cobble", "black.brick.large", "white.brick.large",
			"black.brick.small", "white.brick.small", "black.gravel", "white.gravel", "black.paved", "white.paved" };
	private Icon[] _icons = new Icon[_names.length];
	
	public BlockDecorativeStone(int blockId)
	{
		super(blockId, Material.rock);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundStoneFootstep);
		setUnlocalizedName("mfr.decorativestone");
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		for(int i = 0; i < _icons.length; i++)
		{
			_icons[i] = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName() + "." + _names[i]);
		}
	}
	
	@Override
	public Icon getIcon(int side, int meta)
	{
		return _icons[Math.min(meta, _icons.length)];
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
