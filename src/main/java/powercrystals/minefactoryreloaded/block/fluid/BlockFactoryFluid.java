package powercrystals.minefactoryreloaded.block.fluid;

import cofh.core.fluid.BlockFluidCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.IRedNetDecorative;
import powercrystals.minefactoryreloaded.core.UtilInventory;
import powercrystals.minefactoryreloaded.core.WeightedRandomItemStack;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.MFRThings;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Random;

public class BlockFactoryFluid extends BlockFluidCore implements IRedNetDecorative { // TODO: convert to BlockFluidFinite

	private static DamageSource steam = new DamageSource("steam");
	static {
		steam.setDamageBypassesArmor().setFireDamage().setDifficultyScaled();
		steam.setDamageIsAbsolute();
	}
	public static final Material material = new MaterialLiquid(MapColor.WATER);

	public int color;

	private static Fluid ensureFluid(String name) {

		Fluid fluid = MFRFluids.getFluid(name);
		if (fluid.canBePlacedInWorld())
			ReflectionHelper.setPrivateValue(Fluid.class, fluid, null, "block");
		return fluid;
	}

	public BlockFactoryFluid(String liquidName) {

		this(liquidName, Material.WATER);
	}

	public BlockFactoryFluid(String liquidName, Material material) {

		super(ensureFluid(liquidName), material, liquidName);
		setHardness(100.0F);
		setLightOpacity(3);
		setDisplaceFluids(true);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {

		super.onEntityCollidedWithBlock(world, pos, state, entity);

		if (entity instanceof EntityLivingBase) {
			NBTTagCompound data = entity.getEntityData();
			if (world.isRemote || data.getLong("mfr:fluidTimer" + fluidName) > world.getTotalWorldTime()) {
				return;
			}
			data.setLong("mfr:fluidTimer" + fluidName, world.getTotalWorldTime() + 40);

			EntityLivingBase ent = (EntityLivingBase) entity;
			if (this == MFRFluids.milkLiquid) {
				ent.addPotionEffect(new PotionEffect(MobEffects.HASTE, 6 * 20, 0, true, false));
			}
			else if (this == MFRFluids.sludgeLiquid) {
				ent.addPotionEffect(new PotionEffect(MobEffects.WITHER, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 12 * 20, 0, true, false));
				ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 16 * 20, 0, true, false));
			}
			else if (this == MFRFluids.sewageLiquid) {
				ent.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 12 * 20, 0, true, false));
				ent.addPotionEffect(new PotionEffect(MobEffects.POISON, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 12 * 20, 0, true, false));
			}
			else if (this == MFRFluids.essenceLiquid) {
				ent.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 60 * 20, 0, true, false));
			}
			else if (this == MFRFluids.biofuelLiquid) {
				ent.addPotionEffect(new PotionEffect(MobEffects.SPEED, 12 * 20, 0, true, false));
			}
			else if (this == MFRFluids.steamFluid) {
				ent.attackEntityFrom(steam, 6);
			}
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

		int light = super.getLightValue(state, world, pos);
		if (maxScaledLight != 0)
			light = Math.max(light, 2);
		return light;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

		super.onBlockAdded(world, pos, state);
		checkCanStay(world, pos, world.rand);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

		checkCanStay(world, pos, rand);
		super.updateTick(world, pos, state, rand);
	}

	protected void checkCanStay(World world, BlockPos pos, Random rand) {

		if (this == MFRFluids.steamFluid)
			return;
		l: if (world.provider.doesWaterVaporize())
		{
			if (!isSourceBlock(world, pos)) {
				if (world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2))
					return;
				break l;
			}
			@Nonnull ItemStack drop = ItemStack.EMPTY;
			Block block = Blocks.AIR;
			if (this == MFRFluids.milkLiquid) {
				if (rand.nextInt(50) == 0)
					drop = new ItemStack(Items.DYE, rand.nextInt(2), 15);
			}
			else if (this == MFRFluids.sludgeLiquid) {
				drop = ((WeightedRandomItemStack) WeightedRandom.
						getRandomItem(rand, MFRRegistry.getSludgeDrops())).getStack();
			}
			else if (this == MFRFluids.sewageLiquid) {
				drop = new ItemStack(MFRThings.fertilizerItem, 1 + rand.nextInt(2));
			}
			else if (this == MFRFluids.essenceLiquid) {
				if (world.setBlockToAir(pos)) {
					int i = rand.nextInt(5) + 10;
					while (i > 0) {
						int j = EntityXPOrb.getXPSplit(i);
						i -= j;
						world.spawnEntity(new EntityXPOrb(world,
								pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), j));
					}
					fizz(world, pos, rand);
					return;
				}
				break l;
			}
			else if (this == MFRFluids.meatLiquid) {
				if (rand.nextInt(5) != 0)
					drop = new ItemStack(MFRThings.meatIngotRawItem, rand.nextInt(2));
				else
					drop = new ItemStack(MFRThings.meatIngotCookedItem, rand.nextInt(2));
			}
			else if (this == MFRFluids.pinkSlimeLiquid) {
				if (rand.nextBoolean())
					drop = new ItemStack(MFRThings.pinkSlimeItem, rand.nextInt(3));
				else if (rand.nextInt(5) != 0)
					drop = new ItemStack(MFRThings.meatNuggetRawItem, rand.nextInt(2));
				else
					drop = new ItemStack(MFRThings.meatNuggetCookedItem, rand.nextInt(2));
			}
			else if (this == MFRFluids.chocolateMilkLiquid) {
				if (rand.nextBoolean())
					drop = new ItemStack(Items.DYE, rand.nextInt(2), 3);
			}
			else if (this == MFRFluids.mushroomSoupLiquid) {
				if (rand.nextInt(5) == 0)
					block = (rand.nextBoolean() ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM);
				else if (rand.nextBoolean())
					drop = new ItemStack(Blocks.BROWN_MUSHROOM, rand.nextInt(2));
				else
					drop = new ItemStack(Blocks.RED_MUSHROOM, rand.nextInt(2));
			}
			if (world.setBlockState(pos, block.getDefaultState(), 3)) {
				if (!drop.isEmpty() && drop.getCount() > 0) {
					UtilInventory.dropStackInAir(world, pos, drop);
				}

				fizz(world, pos, rand);
				return;
			}
		}
	}

	protected void fizz(World world, BlockPos pos, Random rand) {

		world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
		for (int l = 0; l < 8; ++l) {
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
				pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(),
				0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public String getUnlocalizedName() {

		return "fluid.mfr." + fluidName.toLowerCase(Locale.ROOT) + ".fluid";
	}
}
