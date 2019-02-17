package powercrystals.minefactoryreloaded.tile.machine.processing;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.Area;
import powercrystals.minefactoryreloaded.core.WeightedRandomItemStack;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRFluids;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileEntitySludgeBoiler extends TileEntityFactoryPowered {

	private Random _rand;
	private int _tick;
	private Area _area;

	public TileEntitySludgeBoiler() {

		super(Machine.SludgeBoiler);
		setManageSolids(true);
		_activeSyncTimeout = 5;
		_rand = new Random();
		_tanks[0].setLock(MFRFluids.getFluid("sludge"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {

		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}

	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {

		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	@Override
	public void validate() {

		super.validate();
		_area = new Area(pos, 3, 3, 3);
	}

	@Override
	public int getWorkMax() {

		return 100;
	}

	@Override
	public int getIdleTicksMax() {

		return 1;
	}

	@Override
	protected boolean activateMachine() {

		if (drain(10, false, _tanks[0]) == 10) {
			if (!incrementWorkDone())
				return false;
			drain(10, true, _tanks[0]);
			_tick++;

			if (getWorkDone() >= getWorkMax()) {
				@Nonnull ItemStack s = ((WeightedRandomItemStack) WeightedRandom.getRandomItem(_rand, MFRRegistry.getSludgeDrops())).getStack();

				doDrop(s);

				setWorkDone(0);
			}

			if (_tick >= 23) {
				List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, _area.toAxisAlignedBB());
				for (EntityLivingBase ent : entities) {
					ent.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 * 20, 0));
					ent.addPotionEffect(new PotionEffect(MobEffects.POISON, 6 * 20, 0));
				}
				_tick = 0;
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean updateIsActive(boolean failedDrops) {

		return super.updateIsActive(failedDrops) && drain(10, false, _tanks[0]) == 10;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void machineDisplayTick() {

		int s = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (s < 2 && isActive()) {
			int color = MFRFluids.sludgeLiquid.color;
			for (int a = 8 >> s, i = 4 >> s;
				 i-- > 0; )
				world.spawnParticle(_rand.nextInt(a) == 0 ? EnumParticleTypes.SPELL_MOB : EnumParticleTypes.SPELL_MOB_AMBIENT,
						_area.xMin + _rand.nextFloat() * (_area.xMax - _area.xMin),
						_area.yMin + _rand.nextFloat() * (_area.yMax - _area.yMin),
						_area.zMin + _rand.nextFloat() * (_area.zMax - _area.zMin),
						((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f);
		}
	}

	@Override
	public EnumFacing getDropDirection() {

		return EnumFacing.DOWN;
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	@Override
	public boolean allowBucketFill(EnumFacing facing, @Nonnull ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canDrainTank(EnumFacing facing, int index) {

		return false;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(EnumFacing facing, int maxDrain, boolean doDrain) {

		return null;
	}

}
