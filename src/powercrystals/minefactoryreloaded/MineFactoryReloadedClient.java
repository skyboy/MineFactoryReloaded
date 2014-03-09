package powercrystals.minefactoryreloaded;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderPlayerEvent.SetArmorModel;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;

import powercrystals.core.position.BlockPosition;
import powercrystals.core.render.RenderBlockFluidClassic;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.entity.EntityFishingRod;
import powercrystals.minefactoryreloaded.entity.EntityNeedle;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;
import powercrystals.minefactoryreloaded.entity.EntityRocket;
import powercrystals.minefactoryreloaded.entity.EntitySafariNet;
import powercrystals.minefactoryreloaded.item.ItemRocketLauncher;
import powercrystals.minefactoryreloaded.render.RenderTickHandler;
import powercrystals.minefactoryreloaded.render.block.ConveyorRenderer;
import powercrystals.minefactoryreloaded.render.block.DetCordRenderer;
import powercrystals.minefactoryreloaded.render.block.FactoryGlassPaneRenderer;
import powercrystals.minefactoryreloaded.render.block.FactoryGlassRenderer;
import powercrystals.minefactoryreloaded.render.block.RedNetCableRenderer;
import powercrystals.minefactoryreloaded.render.block.VineScaffoldRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityNeedleRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityPinkSlimeRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntityRocketRenderer;
import powercrystals.minefactoryreloaded.render.entity.EntitySafariNetRenderer;
import powercrystals.minefactoryreloaded.render.item.FactoryFluidOverlayRenderer;
import powercrystals.minefactoryreloaded.render.item.FactoryGlassPaneItemRenderer;
import powercrystals.minefactoryreloaded.render.item.NeedleGunItemRenderer;
import powercrystals.minefactoryreloaded.render.item.RocketItemRenderer;
import powercrystals.minefactoryreloaded.render.item.RocketLauncherItemRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.LaserDrillPrechargerRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.LaserDrillRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetCardItemRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetHistorianRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.RedNetLogicRenderer;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrillPrecharger;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetHistorian;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

@SideOnly(Side.CLIENT)
public class MineFactoryReloadedClient implements IScheduledTickHandler
{
	public static MineFactoryReloadedClient instance;
	
	private static final int _lockonMax = 30;
	private static final int _lockonLostMax = 60;
	private int _lockonTicks = 0;
	private int _lockonLostTicks = 0;
	private Entity _lastEntityOver = null;
	
	public static HashMap<BlockPosition, Integer> prcPages = new HashMap<BlockPosition, Integer>();
	
	private static List<IHarvestAreaContainer> _areaTileEntities = new LinkedList<IHarvestAreaContainer>();
	
	public static void init()
	{
		instance = new MineFactoryReloadedClient();
		
		// IDs
		MineFactoryReloadedCore.renderIdConveyor = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdFactoryGlassPane = RenderingRegistry.getNextAvailableRenderId();
		//MineFactoryReloadedCore.renderIdUnused = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdFluidClassic = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdRedNetLogic = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdVineScaffold = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdFactoryGlass = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdDetCord = RenderingRegistry.getNextAvailableRenderId();
		MineFactoryReloadedCore.renderIdRedNet = RenderingRegistry.getNextAvailableRenderId();
		
		// Blocks
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdConveyor,
				new ConveyorRenderer());
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdFactoryGlassPane,
				new FactoryGlassPaneRenderer());
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdFluidClassic,
				new RenderBlockFluidClassic(MineFactoryReloadedCore.renderIdFluidClassic));
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdVineScaffold,
				new VineScaffoldRenderer());
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdFactoryGlass,
				new FactoryGlassRenderer());
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdDetCord,
				new DetCordRenderer());
		RenderingRegistry.registerBlockHandler(MineFactoryReloadedCore.renderIdRedNet,
				new RedNetCableRenderer());
		
		// Items
		MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.factoryGlassPaneBlock.blockID,
				new FactoryGlassPaneItemRenderer());
		MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.logicCardItem.itemID,
				new RedNetCardItemRenderer());
		MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.plasticCupItem.itemID,
				new FactoryFluidOverlayRenderer());
		MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.needlegunItem.itemID,
				new NeedleGunItemRenderer());
		MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.rocketItem.itemID,
				new RocketItemRenderer());
		MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.rocketLauncherItem.itemID,
				new RocketLauncherItemRenderer());
		if (MineFactoryReloadedCore.syringeEmptyItem instanceof IFluidContainerItem)
			MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.syringeEmptyItem.itemID,
					new FactoryFluidOverlayRenderer(false));
		//MinecraftForgeClient.registerItemRenderer(MineFactoryReloadedCore.plasticCellItem.itemID,
		//		new FactoryFluidOverlayRenderer());
		if (MFRConfig.vanillaOverrideGlassPane.getBoolean(true))
		{
			MinecraftForgeClient.registerItemRenderer(Block.thinGlass.blockID,
					new FactoryGlassPaneItemRenderer());			
		}
		
		// TileEntities
		RedNetLogicRenderer logicRender = new RedNetLogicRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetLogic.class, logicRender);
		RenderingRegistry.registerBlockHandler(logicRender);
		
		RedNetHistorianRenderer panelRenderer = new RedNetHistorianRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedNetHistorian.class, panelRenderer);
		RenderingRegistry.registerBlockHandler(panelRenderer);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserDrill.class, new LaserDrillRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserDrillPrecharger.class,
				new LaserDrillPrechargerRenderer());

		// Entities
		RenderingRegistry.registerEntityRenderingHandler(EntitySafariNet.class, new EntitySafariNetRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityPinkSlime.class,
				new EntityPinkSlimeRenderer(new ModelSlime(16), new ModelSlime(0), 0.25F));
		RenderingRegistry.registerEntityRenderingHandler(EntityNeedle.class, new EntityNeedleRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new EntityRocketRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityFishingRod.class,
				new RenderSnowball(MineFactoryReloadedCore.fishingRodItem));
		
		// Handlers
		TickRegistry.registerScheduledTickHandler(instance, Side.CLIENT);
		TickRegistry.registerTickHandler(new RenderTickHandler(), Side.CLIENT);
		
		VillagerRegistry.instance().registerVillagerSkin(MFRConfig.zoolologistEntityId.getInt(),
				new ResourceLocation(MineFactoryReloadedCore.villagerFolder + "zoologist.png"));
		
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if(!(tickData[0] instanceof EntityPlayerSP))
		{
			return;
		}
		EntityPlayerSP player = (EntityPlayerSP)tickData[0];
		ItemStack equipped = player.inventory.getCurrentItem();
		if(equipped != null && equipped.getItem() instanceof ItemRocketLauncher)
		{
			Entity e = rayTrace();
			if(_lastEntityOver != null && _lastEntityOver.isDead)
			{
				_lastEntityOver = null;
				_lockonTicks = 0;
			}
			else if((e == null || e != _lastEntityOver) && _lockonLostTicks > 0)
			{
				_lockonLostTicks--;
			}
			else if(e == null && _lockonLostTicks == 0)
			{
				if(_lockonTicks > 0)
				{
					_lockonTicks--;
				}
				_lastEntityOver = null;
			}
			else if(_lastEntityOver == null)
			{
				_lastEntityOver = e;
			}
			else if(_lockonTicks < _lockonMax)
			{
				_lockonTicks++;
				if(_lockonTicks >= _lockonMax)
				{
					_lockonLostTicks = _lockonLostMax;
				}
			}
			else if(e != null && e == _lastEntityOver)
			{
				_lockonLostTicks = _lockonLostMax;
			}
		}
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}
	
	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}
	
	@Override
	public String getLabel()
	{
		return MineFactoryReloadedCore.modId + ".client";
	}
	
	@Override
	public int nextTickSpacing()
	{
		return 0;
	}
	
	@ForgeSubscribe
	public void setArmorModel(SetArmorModel e)
	{
		ItemStack itemstack = e.stack;
		
        if (itemstack != null)
        {
            Item item = itemstack.getItem();
            int par2 = 3 - e.slot;
            //if (item.isValidArmor(itemstack, e.slot, e.entity))
            if (item == MineFactoryReloadedCore.plasticCupItem)
            {
            	Minecraft.getMinecraft().renderEngine.
            		bindTexture(new ResourceLocation(item.getArmorTexture(itemstack, e.entity, par2, null)));
                ModelBiped modelbiped = new ModelBiped(1.0F);
                modelbiped.bipedHead.showModel = par2 == 0;
                modelbiped.bipedHeadwear.showModel = par2 == 0;
                modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
                modelbiped.bipedRightArm.showModel = par2 == 1;
                modelbiped.bipedLeftArm.showModel = par2 == 1;
                modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                e.renderer.setRenderPassModel(modelbiped);
                modelbiped.onGround = e.entityLiving.getSwingProgress(e.partialRenderTick);
                modelbiped.isRiding = e.entity.isRiding();
                modelbiped.isChild = e.entityLiving.isChild();
                float f1 = 1.0F;
                GL11.glColor3f(f1, f1, f1);

                if (itemstack.isItemEnchanted())
                {
                    e.result = 15;
                    return;
                }

                e.result = 1;
            }
        }
	}
	
	@ForgeSubscribe(priority=EventPriority.HIGHEST) // first to render, so everything else is overlayed
	public void renderWorldLast(RenderWorldLastEvent e)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player.inventory.getCurrentItem() == null ||
				player.inventory.getCurrentItem().itemID != MineFactoryReloadedCore.factoryHammerItem.itemID)
		{
			return;
		}
		
		float playerOffsetX = -(float)(player.lastTickPosX + (player.posX - player.lastTickPosX) * e.partialTicks);
		float playerOffsetY = -(float)(player.lastTickPosY + (player.posY - player.lastTickPosY) * e.partialTicks);
		float playerOffsetZ = -(float)(player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * e.partialTicks);
		
        GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
    	GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
			
		for(IHarvestAreaContainer c : _areaTileEntities)
		{
			if(((TileEntity)c).isInvalid())
				continue;
			
			float r = (c.getHAM().getOriginX() & 15) / 16.0F;
			float g = (c.getHAM().getOriginY() & 15) / 16.0F;
			float b = (c.getHAM().getOriginZ() & 15) / 16.0F;

			GL11.glPushMatrix();
			GL11.glColor4f(r, g, b, 0.4F);
			GL11.glTranslatef(playerOffsetX, playerOffsetY, playerOffsetZ);
			renderAABB(c.getHAM().getHarvestArea().toAxisAlignedBB());
			GL11.glPopMatrix();
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void addTileToAreaList(IHarvestAreaContainer tile)
	{
		_areaTileEntities.add(tile);
	}
	
	public static void removeTileFromAreaList(IHarvestAreaContainer tile)
	{
		_areaTileEntities.remove(tile);
	}
	
	public int getLockedEntity()
	{
		if(_lastEntityOver != null && _lockonTicks >= _lockonMax)
		{
			return _lastEntityOver.entityId;
		}
		
		return Integer.MIN_VALUE;
	}
	
	public int getLockTimeRemaining()
	{
		if(_lastEntityOver != null && _lockonTicks >= _lockonMax)
		{
			return _lockonLostMax - _lockonLostTicks;
		}
		else
		{
			return (_lockonMax - _lockonTicks) * 2;
		}
	}
	
	private Entity rayTrace()
	{
		if(Minecraft.getMinecraft().renderViewEntity == null || Minecraft.getMinecraft().theWorld == null)
		{
			return null;
		}
		
		double range = 64;
		Vec3 playerPos = Minecraft.getMinecraft().renderViewEntity.getPosition(1.0F);
		
		Vec3 playerLook = Minecraft.getMinecraft().renderViewEntity.getLook(1.0F);
		Vec3 playerLookRel = playerPos.addVector(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range);
		List<?> list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().renderViewEntity,
				Minecraft.getMinecraft().renderViewEntity.boundingBox.addCoord(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range)
						.expand(1, 1, 1));
		
		double entityDistTotal = range;
		Entity pointedEntity = null;
		for(int i = 0; i < list.size(); ++i)
		{
			Entity entity = (Entity)list.get(i);
			
			if(entity.canBeCollidedWith())
			{
				double entitySize = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(entitySize, entitySize, entitySize);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(playerPos, playerLookRel);
				
				if(axisalignedbb.isVecInside(playerPos))
				{
					if(0.0D < entityDistTotal || entityDistTotal == 0.0D)
					{
						pointedEntity = entity;
						entityDistTotal = 0.0D;
					}
				}
				else if(movingobjectposition != null)
				{
					double entityDist = playerPos.distanceTo(movingobjectposition.hitVec);
					
					if(entityDist < entityDistTotal || entityDistTotal == 0.0D)
					{
						pointedEntity = entity;
						entityDistTotal = entityDist;
					}
				}
			}
		}
		
		if(pointedEntity != null)
		{
			return pointedEntity;
		}
		return null;
	}
	
	private static void renderAABB(AxisAlignedBB par0AxisAlignedBB)
	{
		double eps = 0.01;
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.minX + eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.minZ + eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.maxY - eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.addVertex(par0AxisAlignedBB.maxX - eps, par0AxisAlignedBB.minY + eps, par0AxisAlignedBB.maxZ - eps);
		tessellator.draw();
	}
}
