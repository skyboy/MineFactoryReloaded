package powercrystals.minefactoryreloaded.block;

import cofh.api.block.IBlockInfo;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.item.ItemRedNetMeter;
import powercrystals.minefactoryreloaded.render.block.RedNetCableRenderer;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.rednet.RedstoneNetwork;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class BlockRedNetCable extends BlockContainer
								implements IRedNetNetworkContainer, IBlockInfo, IDismantleable
{
	private static float _wireSize = 0.25F;
	private static float _plateWidth = 14.0F / 16.0F;
	private static float _plateDepth = 1.0F / 16.0F;
	private static float _bandWidth = 5.0F / 16.0F;
	private static float _bandOffset = 2.0F / 16.0F;
	private static float _bandDepth = 1.0F / 16.0F;
	
	private static float _wireStart = 0.5F - _wireSize / 2.0F;
	private static float _wireEnd = 0.5F + _wireSize / 2.0F;
	private static float _plateStart = 0.5F - _plateWidth / 2.0F;
	private static float _plateEnd = 0.5F + _plateWidth / 2.0F;
	private static float _bandWidthStart = 0.5F - _bandWidth / 2.0F;
	private static float _bandWidthEnd = 0.5F + _bandWidth / 2.0F;
	
	private static float _bandDepthStart = _bandOffset;
	private static float _bandDepthEnd = _bandOffset + _bandDepth;
	
	private static int[] _partSideMappings = new int[] { -1, -1, -1, 4, 5, 0, 1, 2, 3, 4, 5, 0, 1, 2, 3 };
	
	public BlockRedNetCable(int id)
	{
		super(id, Machine.MATERIAL);
		
		setUnlocalizedName("mfr.cable.redstone");
		setHardness(0.8F);
		
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	private AxisAlignedBB[] getParts(TileEntityRedNetCable cable)
	{
		RedNetConnectionType csu = cable.getConnectionState(ForgeDirection.UP);
		RedNetConnectionType csd = cable.getConnectionState(ForgeDirection.DOWN);
		RedNetConnectionType csn = cable.getConnectionState(ForgeDirection.NORTH);
		RedNetConnectionType css = cable.getConnectionState(ForgeDirection.SOUTH);
		RedNetConnectionType csw = cable.getConnectionState(ForgeDirection.WEST);
		RedNetConnectionType cse = cable.getConnectionState(ForgeDirection.EAST); 
		
		AxisAlignedBB[] parts = new AxisAlignedBB[15];
		
		parts[0] = AxisAlignedBB.getBoundingBox(csw != RedNetConnectionType.None ? 0 : _wireStart, _wireStart, _wireStart, cse != RedNetConnectionType.None ? 1 : _wireEnd, _wireEnd, _wireEnd);
		parts[1] = AxisAlignedBB.getBoundingBox(_wireStart, csd != RedNetConnectionType.None ? 0 : _wireStart, _wireStart, _wireEnd, csu != RedNetConnectionType.None ? 1 : _wireEnd, _wireEnd);
		parts[2] = AxisAlignedBB.getBoundingBox(_wireStart, _wireStart, csn != RedNetConnectionType.None ? 0 : _wireStart, _wireEnd, _wireEnd, css != RedNetConnectionType.None ? 1 : _wireEnd);
		
		parts[3] = !csw.isPlate ? null : AxisAlignedBB.getBoundingBox(0, _plateStart, _plateStart, _plateDepth, _plateEnd, _plateEnd);
		parts[4] = !cse.isPlate ? null : AxisAlignedBB.getBoundingBox(1.0F - _plateDepth, _plateStart, _plateStart, 1.0F, _plateEnd, _plateEnd);
		parts[5] = !csd.isPlate ? null : AxisAlignedBB.getBoundingBox(_plateStart, 0 , _plateStart, _plateEnd, _plateDepth, _plateEnd);
		parts[6] = !csu.isPlate ? null : AxisAlignedBB.getBoundingBox(_plateStart, 1.0F - _plateDepth, _plateStart, _plateEnd, 1.0F, _plateEnd);
		parts[7] = !csn.isPlate ? null : AxisAlignedBB.getBoundingBox(_plateStart, _plateStart, 0, _plateEnd, _plateEnd, _plateDepth);
		parts[8] = !css.isPlate ? null : AxisAlignedBB.getBoundingBox(_plateStart, _plateStart, 1.0F - _plateDepth, _plateEnd, _plateEnd, 1.0F);
		
		parts[9]  = !csw.isSingleSubnet ? null : AxisAlignedBB.getBoundingBox(_bandDepthStart, _bandWidthStart, _bandWidthStart, _bandDepthEnd, _bandWidthEnd, _bandWidthEnd);
		parts[10] = !cse.isSingleSubnet ? null : AxisAlignedBB.getBoundingBox(1.0F - _bandDepthEnd, _bandWidthStart, _bandWidthStart, 1.0F - _bandDepthStart, _bandWidthEnd, _bandWidthEnd);
		parts[11] = !csd.isSingleSubnet ? null : AxisAlignedBB.getBoundingBox(_bandWidthStart, _bandDepthStart, _bandWidthStart, _bandWidthEnd, _bandDepthEnd, _bandWidthEnd);
		parts[12] = !csu.isSingleSubnet ? null : AxisAlignedBB.getBoundingBox(_bandWidthStart, 1.0F - _bandDepthEnd, _bandWidthStart, _bandWidthEnd, 1.0F - _bandDepthStart, _bandWidthEnd);
		parts[13] = !csn.isSingleSubnet ? null : AxisAlignedBB.getBoundingBox(_bandWidthStart, _bandWidthStart, _bandDepthStart, _bandWidthEnd, _bandWidthEnd, _bandDepthEnd);
		parts[14] = !css.isSingleSubnet ? null : AxisAlignedBB.getBoundingBox(_bandWidthStart, _bandWidthStart, 1.0F - _bandDepthEnd, _bandWidthEnd, _bandWidthEnd, 1.0F - _bandDepthStart);
		
		return parts;
	}
	
	private int getPartClicked(EntityPlayer player, double reachDistance, TileEntityRedNetCable cable)
	{
		AxisAlignedBB[] wireparts = getParts(cable);
		
		Vec3 playerPosition = player.worldObj.getWorldVec3Pool().getVecFromPool(player.posX - cable.xCoord, player.posY - cable.yCoord + player.getEyeHeight(), player.posZ - cable.zCoord);
		Vec3 playerLook = player.getLookVec();
		
		Vec3 playerViewOffset = player.worldObj.getWorldVec3Pool().getVecFromPool(playerPosition.xCoord + playerLook.xCoord * reachDistance, playerPosition.yCoord + playerLook.yCoord * reachDistance, playerPosition.zCoord + playerLook.zCoord * reachDistance);
		int closest = -1;
		double closestdistance = Double.MAX_VALUE;
		
		for(int i = 0; i < wireparts.length; i++)
		{
			AxisAlignedBB part = wireparts[i];
			if(part == null)
			{
				continue;
			}
			MovingObjectPosition hit = part.calculateIntercept(playerPosition, playerViewOffset);
			if(hit != null)
			{
				double distance = playerPosition.distanceTo(hit.hitVec);
				if(distance < closestdistance)
				{
					closestdistance = distance;
					closest = i;
				}
			}
		}
		return closest;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
	{
		PlayerInteractEvent e = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side);
		if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.useBlock == Result.DENY)
		{
			return false;
		}
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetCable)
		{
			TileEntityRedNetCable cable = (TileEntityRedNetCable)te;
			
			int subHit = getPartClicked(player, 3.0F, cable);
			
			if (subHit < 0)
			{
				return false;
			}
			side = _partSideMappings[subHit];

			ItemStack s = player.inventory.getCurrentItem();
			
			if (side >= 0)
			{
				if (MFRUtil.isHoldingUsableTool(player, x, y, z))
				{
					if (!world.isRemote)
					{
						int nextColor;
						if(!player.isSneaking())
						{
							nextColor = cable.getSideColor(ForgeDirection.getOrientation(side)) + 1;
							if(nextColor > 15) nextColor = 0;
						}
						else
						{
							nextColor = cable.getSideColor(ForgeDirection.getOrientation(side)) - 1;
							if(nextColor < 0) nextColor = 15;
						}
						cable.setSideColor(ForgeDirection.getOrientation(side), nextColor);
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
				else if (s != null && s.itemID == MineFactoryReloadedCore.rednetMeterItem.itemID)
				{
					// TODO: move to client-side when forge fixes player.getEyeHeight on client
					if (!world.isRemote)
					{
						// TODO: localize
						player.sendChatToPlayer(new ChatMessageComponent().addText("Side is " + 
								ItemRedNetMeter._colorNames[cable.getSideColor(ForgeDirection.getOrientation(side))]));
					}
				}
				else if (s != null && s.itemID == Item.dyePowder.itemID)
				{
					if (!world.isRemote)
					{
						cable.setSideColor(ForgeDirection.getOrientation(side), 15 - s.getItemDamage());
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
			}
			else if (MFRUtil.isHoldingUsableTool(player, x, y, z))
			{
				byte mode = cable.getMode();
				mode++;
				if (mode > 3)
				{
					mode = 0;
				}
				if (!world.isRemote)
				{
					cable.setMode(mode);
					world.markBlockForUpdate(x, y, z);
					switch (mode)
					{
					case 0:
						player.sendChatToPlayer(new ChatMessageComponent().addKey("chat.info.mfr.rednet.connection.standard"));
						break;
					case 1:
						player.sendChatToPlayer(new ChatMessageComponent().addKey("chat.info.mfr.rednet.connection.forced"));
						break;
					case 2:
						player.sendChatToPlayer(new ChatMessageComponent().addKey("chat.info.mfr.rednet.connection.forcedstrong"));
						break;
					case 3:
						player.sendChatToPlayer(new ChatMessageComponent().addKey("chat.info.mfr.rednet.connection.cableonly"));
						break;
					default:
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		float xMin = 1;
		float yMin = 1;
		float zMin = 1;
		float xMax = 0;
		float yMax = 0;
		float zMax = 0;
		
		TileEntity cable = world.getBlockTileEntity(x, y, z);
		if(cable instanceof TileEntityRedNetCable)
		{
			for(AxisAlignedBB aabb : getParts((TileEntityRedNetCable)cable))
			{
				if(aabb == null)
				{
					continue;
				}
				
				xMin = Math.min(xMin, (float)aabb.minX);
				yMin = Math.min(yMin, (float)aabb.minY);
				zMin = Math.min(zMin, (float)aabb.minZ);
				xMax = Math.max(xMax, (float)aabb.maxX);
				yMax = Math.max(yMax, (float)aabb.maxY);
				zMax = Math.max(zMax, (float)aabb.maxZ);
			}
			setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
		}
		else
		{
			super.setBlockBoundsBasedOnState(world, x, y, z);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB collisionTest, List collisionBoxList, Entity entity)
	{
		TileEntity cable = world.getBlockTileEntity(x, y, z);
		if(cable instanceof TileEntityRedNetCable)
		{
			for(AxisAlignedBB aabb : getParts((TileEntityRedNetCable)cable))
			{
				if(aabb == null)
				{
					continue;
				}
				aabb = AxisAlignedBB.getBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
				aabb.minX += x;
				aabb.maxX += x;
				aabb.minY += y;
				aabb.maxY += y;
				aabb.minZ += z;
				aabb.maxZ += z;
				if(collisionTest.intersectsWith(aabb))
				{
					collisionBoxList.add(aabb);
				}
			}
		}
		else
		{
			super.addCollisionBoxesToList(world, x, y, z, collisionTest, collisionBoxList, entity);
		}
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId)
	{
		super.onNeighborBlockChange(world, x, y, z, blockId);
		if(blockId == blockID || world.isRemote)
		{
			return;
		}
		RedstoneNetwork.log("Cable block at %d, %d, %d got update from ID %d", x, y, z, blockId);
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			((TileEntityRedNetCable)te).onNeighboorChanged();
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			if (((TileEntityRedNetCable)te).getNetwork() != null)
				((TileEntityRedNetCable)te).getNetwork().setInvalid();
			
			world.markTileEntityForDespawn(te);
			te.invalidate();
			world.removeBlockTileEntity(x, y, z);
		}
		for(ForgeDirection d : ForgeDirection.VALID_DIRECTIONS)
		{
			BlockPosition bp = new BlockPosition(x, y, z);
			bp.orientation = d;
			bp.moveForwards(1);
			world.notifyBlockOfNeighborChange(bp.x, bp.y, bp.z, MineFactoryReloadedCore.rednetCableBlock.blockID);
			world.notifyBlocksOfNeighborChange(bp.x, bp.y, bp.z, MineFactoryReloadedCore.rednetCableBlock.blockID);
		}
		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock)
	{
		ItemStack machine = new ItemStack(idDropped(blockID, world.rand, 0), 1,
				damageDropped(world.getBlockMetadata(x, y, z)));
		world.setBlockToAir(x, y, z);
		if (!returnBlock)
			dropBlockAsItem_do(world, x, y, z, machine);
		return machine;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z)
	{
		return true;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		int power = 0;
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			TileEntityRedNetCable cable = ((TileEntityRedNetCable)te);
			RedNetConnectionType state = cable.getConnectionState(ForgeDirection.getOrientation(side).getOpposite());
			if(cable.getNetwork() == null || !state.isConnected | !state.isSingleSubnet)
			{
				return 0;
			}
			
			int subnet = ((TileEntityRedNetCable)te).getSideColor(ForgeDirection.getOrientation(side).getOpposite());
			power = Math.min(Math.max(((TileEntityRedNetCable)te).getNetwork().getPowerLevelOutput(subnet), 0), 15);
			RedstoneNetwork.log("Asked for weak power at " + x + "," + y + "," + z + ";" + ForgeDirection.getOrientation(side).getOpposite() + " - got " + power + " from network " + ((TileEntityRedNetCable)te).getNetwork().getId() + ":" + subnet);
		}
		return power;
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		int power = 0;
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable)
		{
			TileEntityRedNetCable cable = ((TileEntityRedNetCable)te);
			RedNetConnectionType state = cable.getConnectionState(ForgeDirection.getOrientation(side).getOpposite());
			if(cable.getNetwork() == null || !state.isConnected | !state.isSingleSubnet)
			{
				return 0;
			}
			
			BlockPosition nodebp = new BlockPosition(x, y, z, ForgeDirection.getOrientation(side).getOpposite());
			nodebp.moveForwards(1);

			int subnet = cable.getSideColor(nodebp.orientation);
			
			if(cable.getNetwork().isWeakNode(nodebp))
			{
				power = 0;
				RedstoneNetwork.log("Asked for strong power at " + x + "," + y + "," + z + ";" + ForgeDirection.getOrientation(side).getOpposite() + " - weak node, power 0");
			}
			else
			{
				power = Math.min(Math.max(cable.getNetwork().getPowerLevelOutput(subnet), 0), 15);
				RedstoneNetwork.log("Asked for strong power at " + x + "," + y + "," + z + ";" + ForgeDirection.getOrientation(side).getOpposite() + " - got " + power + " from network " + ((TileEntityRedNetCable)te).getNetwork().getId() + ":" + subnet);
			}
		}
		return power;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
	
	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		switch (meta)
		{
		default:
		case 0:
			return new TileEntityRedNetCable();
		case 2:
			return new TileEntityRedNetEnergy();
		}
	}
	
	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdRedNet;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
		RedNetCableRenderer.updateUVT(blockIcon);
	}
	
	@Override
	public void updateNetwork(World world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable && ((TileEntityRedNetCable)te).getNetwork() != null)
		{
			//((TileEntityRedNetCable)te).getNetwork().updatePowerLevels();
			((TileEntityRedNetCable)te).updateNodes();
		}
	}
	
	@Override
	public void updateNetwork(World world, int x, int y, int z, int subnet)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetCable && ((TileEntityRedNetCable)te).getNetwork() != null)
		{
			//((TileEntityRedNetCable)te).getNetwork().updatePowerLevels(subnet);
			((TileEntityRedNetCable)te).updateNodes();
		}
	}

	@Override
	public void getBlockInfo(IBlockAccess world, int x, int y, int z,
			ForgeDirection side, EntityPlayer player, List<String> info, boolean debug)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityRedNetEnergy)
			((TileEntityRedNetEnergy)tile).getTileInfo(info, side, player, debug);
	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}
}
