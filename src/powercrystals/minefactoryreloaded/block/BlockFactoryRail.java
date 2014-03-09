package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public class BlockFactoryRail extends BlockRailBase
{
	protected boolean canSlope;

	public BlockFactoryRail(int par1, boolean par2, boolean slopes)
	{
		super(par1, par2);
		setHardness(0.5F);
		setStepSound(Block.soundMetalFootstep);
		setCreativeTab(MFRCreativeTab.tab);
		canSlope = slopes;
	}

	@Override
	public boolean canMakeSlopes(World world, int x, int y, int z)
	{
		return canSlope;
	}

	public boolean isPowered(World world, int x, int y, int z)
	{
		return (world.getBlockMetadata(x, y, z) & 8) != 0;
	}

	@Override
	public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z)
	{
		return 0.4f;
	}

	@Override // correct argument naming
	public void onMinecartPass(World world, EntityMinecart minecart, int x, int y, int z){}

	@Override // onRailNeighborChange
	protected void func_94358_a(World world, int x, int y, int z, int oldMeta, int newMeta, int neighorID)
	{
		boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);

		if (flag & (oldMeta & 8) == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, newMeta | 8, 3);
		}
		else if (!flag & (oldMeta & 8) != 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		blockIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
}
