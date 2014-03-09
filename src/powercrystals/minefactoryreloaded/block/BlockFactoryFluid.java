package powercrystals.minefactoryreloaded.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;

import powercrystals.core.random.WeightedRandomItemStack;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNoConnection;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class BlockFactoryFluid extends BlockFluidClassic implements IRedNetNoConnection
{ // TODO: convert to BlockFluidFinite
	private Icon _iconFlowing;
	private Icon _iconStill;
	protected String fluidName;

	public BlockFactoryFluid(int id, String liquidName)
	{
		super(id, FluidRegistry.getFluid(liquidName), Material.water);
		setUnlocalizedName("mfr.liquid." + liquidName + ".still");
		setHardness(100.0F);
		setLightOpacity(3);
		fluidName = liquidName;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		super.onEntityCollidedWithBlock(world, x, y, z, entity);

		if(entity instanceof EntityLivingBase)
		{
			NBTTagCompound data = entity.getEntityData();
			if (world.isRemote | data.getLong("mfr:fluidTimer" + blockID) > world.getTotalWorldTime())
			{
				return;
			}
			data.setLong("mfr:fluidTimer" + blockID, world.getTotalWorldTime() + 40);
			
			EntityLivingBase ent = (EntityLivingBase)entity;
			if(blockID == MineFactoryReloadedCore.milkLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 6 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.sludgeLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.wither.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.weakness.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.confusion.id, 12 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.sewageLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.hunger.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.poison.id, 12 * 20, 0));
				ent.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 12 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.essenceLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.nightVision.id, 60 * 20, 0));
			}
			else if(blockID == MineFactoryReloadedCore.biofuelLiquid.blockID)
			{
				ent.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 12 * 20, 0));
			}
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		/*BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		l: if (biome != null && biome.getFloatTemperature() > 1.9f)//*/
		l: if (world.provider.isHellWorld)
		{
			if (!isSourceBlock(world, x, y, z))
			{
				if (world.setBlockToAir(x, y, z))
					return;
				break l;
			}
			ItemStack drop = null;
			int block = 0;
			if(blockID == MineFactoryReloadedCore.milkLiquid.blockID)
			{
				if (rand.nextInt(50) == 0)
					drop = new ItemStack(Item.dyePowder, rand.nextInt(2), 15);
			}
			else if(blockID == MineFactoryReloadedCore.sludgeLiquid.blockID)
			{
				drop = ((WeightedRandomItemStack)WeightedRandom.
						getRandomItem(rand, MFRRegistry.getSludgeDrops())).getStack();
			}
			else if(blockID == MineFactoryReloadedCore.sewageLiquid.blockID)
			{
				drop = new ItemStack(MineFactoryReloadedCore.fertilizerItem, 1 + rand.nextInt(2));
			}
			else if(blockID == MineFactoryReloadedCore.essenceLiquid.blockID)
			{
				if (world.setBlockToAir(x, y, z))
				{
					int i = rand.nextInt(5) + 10;
					while (i > 0)
					{
						int j = EntityXPOrb.getXPSplit(i);
						i -= j;
						world.spawnEntityInWorld(new EntityXPOrb(world,
								x + rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble(), j));
					}
					fizz(world, x, y, z, rand);
					return;
				}
				break l;
			}
			else if(blockID == MineFactoryReloadedCore.biofuelLiquid.blockID)
			{
				if (world.setBlockToAir(x, y, z))
				{
					if (MFRConfig.enableFuelExploding.getBoolean(true))
						world.createExplosion(null, x, y, z, 4, true);
					fizz(world, x, y, z, rand);
					return;
				}
				break l;
			}
			else if(blockID == MineFactoryReloadedCore.meatLiquid.blockID)
			{
				if (rand.nextInt(5) != 0)
					drop = new ItemStack(MineFactoryReloadedCore.meatIngotRawItem, rand.nextInt(2));
				else
					drop = new ItemStack(MineFactoryReloadedCore.meatIngotCookedItem, rand.nextInt(2));
			}
			else if (blockID == MineFactoryReloadedCore.pinkSlimeLiquid.blockID)
			{
				if (rand.nextBoolean())
					drop = new ItemStack(MineFactoryReloadedCore.pinkSlimeballItem, rand.nextInt(3));
				else
					if (rand.nextInt(5) != 0)
						drop = new ItemStack(MineFactoryReloadedCore.meatNuggetRawItem, rand.nextInt(2));
					else
						drop = new ItemStack(MineFactoryReloadedCore.meatNuggetCookedItem, rand.nextInt(2));
			}
			else if(blockID == MineFactoryReloadedCore.chocolateMilkLiquid.blockID)
			{
				if (rand.nextBoolean())
					drop = new ItemStack(Item.dyePowder, rand.nextInt(2), 3);
			}
			else if(blockID == MineFactoryReloadedCore.mushroomSoupLiquid.blockID)
			{
				if (rand.nextInt(5) == 0)
					block = (rand.nextBoolean() ? Block.mushroomBrown : Block.mushroomRed).blockID;
				else
					if (rand.nextBoolean())
						drop = new ItemStack(Block.mushroomBrown, rand.nextInt(2));
					else
						drop = new ItemStack(Block.mushroomRed, rand.nextInt(2));
			}
			if (world.setBlock(x, y, z, block))
			{
				if (drop != null && drop.stackSize > 0)
					this.dropBlockAsItem_do(world, x, y, z, drop);
				
				fizz(world, x, y, z, rand);
				return;
			}
		}
		super.updateTick(world, x, y, z, rand);
	}
	
	protected void fizz(World world, int x, int y, int z, Random rand)
	{
		world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
				"random.fizz", 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F);
		for (int l = 0; l < 8; ++l)
		{
			world.spawnParticle("largesmoke",
					x + rand.nextDouble(), y + rand.nextDouble(), z + rand.nextDouble(),
					0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public String getUnlocalizedName()
	{
		return "fluid." + this.unlocalizedName;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		_iconStill = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		_iconFlowing = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName().replace(".still", ".flowing"));
	}

	@Override
	public Icon getIcon(int side, int meta)
	{
		return side <= 1 ? _iconStill : _iconFlowing;
	}
}
