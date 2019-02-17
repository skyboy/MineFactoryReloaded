package powercrystals.minefactoryreloaded.block.decor;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.ItemBlockFactory;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.setup.MFRThings;

public class BlockPinkSlime extends BlockBreakable implements IInitializer, IModelRegister {
	
	public BlockPinkSlime() {

		super(Material.CLAY, false, MapColor.PINK);
		setCreativeTab(MFRCreativeTab.tab);
		setUnlocalizedName("mfr.pinkslime.block"); // FIXME: relocalize to pink_slime.block
		slipperiness = 0.8f;
		setHardness(0.5f);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.SLIME);
		MFRThings.registerInitializer(this);
		MineFactoryReloadedCore.proxy.addModelRegister(this);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	/**
	 * Block's chance to react to a living entity falling on it.
	 */
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {

		if (entityIn.isSneaking()) {
			super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
		} else {
			entityIn.fall(fallDistance, 0.0F);
		}
	}

	/**
	 * Called when an Entity lands on this Block. This method *must* update motionY because the entity will not do that
	 * on its own
	 */
	public void onLanded(World worldIn, Entity entityIn) {

		if (entityIn.isSneaking()) {
			super.onLanded(worldIn, entityIn);
		} else if (entityIn.motionY < 0.0D) {
			entityIn.motionY = -entityIn.motionY;

			if (!(entityIn instanceof EntityLivingBase)) {
				entityIn.motionY *= 0.8D;
			}
		}
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the block)
	 */
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {

		if (Math.abs(entityIn.motionY) < 0.1D && !entityIn.isSneaking()) {
			double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
			entityIn.motionX *= d0;
			entityIn.motionZ *= d0;
		}

		super.onEntityWalk(worldIn, pos, entityIn);
	}

	@Override public boolean preInit() {

		return false;
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactory(this));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelHelper.registerModel(this);
	}
}
