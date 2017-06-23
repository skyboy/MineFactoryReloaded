package powercrystals.minefactoryreloaded.tile.machine;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import cofh.core.fluid.FluidTankCore;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.MobDrop;
import powercrystals.minefactoryreloaded.core.GrindingDamage;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityGrinder extends TileEntityFactoryPowered {

	public static final float DAMAGE = 0x1.fffffeP+120f;
	
	protected Random _rand;
	protected GrindingDamage _damageSource;
	protected EntityEventController _entityEventController;
	
	protected TileEntityGrinder(Machine machine) {

		super(machine);
		createEntityHAM(this);
		_rand = new Random();
		setManageSolids(true);
		setCanRotate(true);
		_entityEventController = new EntityEventController(this);
		_entityEventController.bind();
	}

	public TileEntityGrinder() {

		this(Machine.Grinder);
		_damageSource = new GrindingDamage();
		_entityEventController.setAllowItemDrops(false);
		_entityEventController.setConsumeXP(true);
		_tanks[0].setLock(FluidRegistry.getFluid("mob_essence"));
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
	public void onChunkUnload() {
		
		super.onChunkUnload();

		_entityEventController.unbind();
		_entityEventController.clearReferences();
		_entityEventController = null;		
	}

	public Random getRandom() {

		return _rand;
	}

	@Override
	protected boolean shouldPumpLiquid() {

		return true;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 200;
	}
	
	@Override
	public boolean activateMachine() {
		
		List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());

		entityList:
		for (EntityLivingBase e : entities) {
			if (e instanceof EntityAgeable && ((EntityAgeable) e).getGrowingAge() < 0 || e.isEntityInvulnerable(_damageSource) || e.getHealth() <= 0) {
				continue;
			}

			processEntity:
			{
				if (MFRRegistry.getGrindables().containsKey(e.getClass())) {
					IFactoryGrindable r = MFRRegistry.getGrindables().get(e.getClass());
					List<MobDrop> drops = r.grind(e.worldObj, e, getRandom());
					if (drops != null && drops.size() > 0 && WeightedRandom.getTotalWeight(drops) > 0) {
						ItemStack drop = WeightedRandom.getRandomItem(_rand, drops).getStack();
						doDrop(drop);
					}
					if (r.processEntity(e)) {
						if (e.getHealth() <= 0) {
							setIdleTicks(20);
							return true;
						}
						break processEntity;
					}
				}

				for (Class<?> t : MFRRegistry.getGrinderBlacklist()) {
					if (t.isInstance(e)) {
						continue entityList;
					}
				}
			}

			if(e instanceof EntityZombie) {
				_entityEventController.addZombie((EntityZombie)e);
			}

			damageEntity(e);
			if (e.getHealth() <= 0) {
				//fillTank(_tanks[0], "mob_essence", 1);

				setIdleTicks(20);
			} else {
				setIdleTicks(10);
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	protected void setRecentlyHit(EntityLivingBase entity, int t) {

		entity.recentlyHit = t;
	}

	protected void damageEntity(EntityLivingBase entity) {

		setRecentlyHit(entity, 100);
		entity.attackEntityFrom(_damageSource, DAMAGE);
	}
	
	
	public int acceptXP(int XP) {
		return MFRLiquidMover.fillTankWithXP(_tanks[0], XP);
	}

	@Override
	public int getSizeInventory() {

		return 0;
	}

	protected void fillTank(FluidTankCore tank, String fluid, float amount) {

		tank.fill(FluidRegistry.getFluidStack(fluid, (int) (100 * amount)), true);
		markDirty();
	}

	@Override
	protected FluidTankCore[] createTanks() {

		return new FluidTankCore[] { new FluidTankCore(4 * BUCKET_VOLUME) };
	}

	@Override
	public boolean allowBucketDrain(EnumFacing facing, ItemStack stack) {

		return true;
	}

	@Override
	protected boolean canFillTank(EnumFacing facing, int index) {

		return false;
	}

	@Override
	public int fill(EnumFacing facing, FluidStack resource, boolean doFill) {

		return 0;
	}
	
	public static class EntityEventController {
		private TileEntityGrinder grinder;
		private boolean allowItemDrops = false;
		private boolean consumeXP = true;
		private Set <EntityZombie> zombieCache;

		public EntityEventController(TileEntityGrinder grinder){

			this.grinder = grinder;
			this.zombieCache = Sets.newHashSet();
		}
		
		public void setAllowItemDrops(boolean allow) {

			this.allowItemDrops = allow;
		}	
		
		public void setConsumeXP(boolean consume) {
			
			this.consumeXP = consume;
		}
		
		public void addZombie(EntityZombie e) {
			
			zombieCache.add(e);
		}
		
		public void flushZombieCache() {
			
			zombieCache.clear();
		}
		
		public void clearReferences() {
			
			this.zombieCache.clear();
			this.zombieCache = null;
			this.grinder = null;
		}
		
		public void bind(){
			
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		public void unbind(){
			
			MinecraftForge.EVENT_BUS.unregister(this);
		}
		
		// ZOMBIE AID HANDLING
		@SubscribeEvent(priority=EventPriority.HIGHEST)
		public void onSummonAidEvent(SummonAidEvent ev) {
			
			if(!zombieCache.isEmpty() && zombieCache.remove(ev.getSummoner())) {
				ev.setResult(Result.DENY);
			}
		}
		
		// XP HANDLING
		@SubscribeEvent
		public void onExperienceDropEvent(LivingExperienceDropEvent ev) {
			
			if(consumeXP == false)
				return;
						
			if(ev.getAttackingPlayer() != grinder._damageSource.getEntity())
				return;

			int XP = ev.getDroppedExperience();
			grinder.acceptXP(XP);

			ev.setCanceled(true);			
		}
		
		
		// ITEM DROP HANDLING
		@SubscribeEvent
		public void onLivingDropsEven(LivingDropsEvent ev) {
			
			if(ev.getSource() != grinder._damageSource)
				return;
			
			
			if (grinder.manageSolids()) {
				for(EntityItem item : ev.getDrops()) {
				
					ItemStack drop = ((EntityItem) item).getEntityItem();
					if (drop != null)
						grinder.doDrop(drop);
				}	
			}
	
			if (allowItemDrops)
				return;
	
			ev.setCanceled(true);
		}
	}

}
