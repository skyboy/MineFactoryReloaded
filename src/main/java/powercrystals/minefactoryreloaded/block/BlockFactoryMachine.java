package powercrystals.minefactoryreloaded.block;

import cofh.core.init.CoreProps;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MFRProps;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.IRotatableTile;
import powercrystals.minefactoryreloaded.render.MachineStateMapper;
import powercrystals.minefactoryreloaded.render.ModelHelper;
import powercrystals.minefactoryreloaded.render.tileentity.LaserDrillPrechargerRenderer;
import powercrystals.minefactoryreloaded.render.tileentity.LaserDrillRenderer;
import powercrystals.minefactoryreloaded.setup.MFRThings;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityBase;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.processing.TileEntityLaserDrill;
import powercrystals.minefactoryreloaded.tile.machine.power.TileEntityLaserDrillPrecharger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlockFactoryMachine extends BlockFactory implements IRedNetOmniNode {

	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyBool CB = PropertyBool.create("cb");

	private int _mfrMachineBlockIndex;

	public BlockFactoryMachine(int index) {

		super(1.5F);
		setUnlocalizedName("mfr.machine." + index);
		_mfrMachineBlockIndex = index;
		providesPower = true;
		setHarvestLevel("wrench", 0);
	}

	public int getBlockIndex() {

		return _mfrMachineBlockIndex;
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer(this, TYPE, FACING, ACTIVE, CB);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(TYPE, Type.byMetadata(_mfrMachineBlockIndex, meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(TYPE).getMeta();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {

		if (world.getTileEntity(pos) instanceof TileEntityFactory) {
			TileEntityFactory te = (TileEntityFactory) world.getTileEntity(pos);

			state = state.withProperty(FACING, EnumFacing.getHorizontal(te.getDirectionFacing().getHorizontalIndex()))
					.withProperty(CB, CoreProps.enableColorBlindTextures);

			if (te instanceof TileEntityFactoryInventory) {
				state = state.withProperty(ACTIVE, ((TileEntityFactoryInventory) te).isActive());
			}

			return state;
		}

		return state;
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {

		if (world.getTileEntity(pos) instanceof TileEntityLaserDrill) {
			return 0;
		}
		return super.getLightOpacity(state, world, pos);
	}

	private void dropContents(TileEntity te, ArrayList<ItemStack> list) {

		if (te instanceof IInventory) {
			World world = te.getWorld();
			IInventory inventory = ((IInventory) te);
			TileEntityFactoryInventory factoryInv = null;
			if (te instanceof TileEntityFactoryInventory)
				factoryInv = (TileEntityFactoryInventory) te;

			for (int i = inventory.getSizeInventory(); i-- > 0; ) {
				if (factoryInv != null)
					if (!factoryInv.shouldDropSlotWhenBroken(i))
						continue;

				@Nonnull ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack.isEmpty())
					continue;
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				if (list != null) {
					list.add(itemstack);
				} else
					dropStack(world, te.getPos(), itemstack);
			}
		}
	}

	private void dropStack(World world, BlockPos pos, @Nonnull ItemStack itemstack) {

		do {
			if (itemstack.getCount() <= 0)
				break;

			float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
			float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
			float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;

			int amountToDrop = Math.min(world.rand.nextInt(21) + 10, itemstack.getCount());

			EntityItem entityitem = new EntityItem(world,
					pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset,
					itemstack.splitStack(amountToDrop));

			float motionMultiplier = 0.05F;
			entityitem.motionX = (float) world.rand.nextGaussian() * motionMultiplier;
			entityitem.motionY = (float) world.rand.nextGaussian() * motionMultiplier + 0.2F;
			entityitem.motionZ = (float) world.rand.nextGaussian() * motionMultiplier;

			world.spawnEntity(entityitem);
		} while (true);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity te = getTile(world, pos);
		if (te != null) {
			dropContents(te, null); // TODO: rewrite drop logic

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory) te).onBlockBroken();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player,
			boolean returnBlock) {

		ArrayList<ItemStack> list = new ArrayList<>(1);
		@Nonnull ItemStack machine = new ItemStack(getItemDropped(state, world.rand, 0), 1, damageDropped(state));
		list.add(machine);
		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityBase) {
			dropContents(te, list);

			if (te instanceof TileEntityFactoryInventory)
				((TileEntityFactoryInventory) te).onDisassembled();

			NBTTagCompound tag = new NBTTagCompound();
			((TileEntityBase) te).writeItemNBT(tag);
			if (!tag.hasNoTags())
				machine.setTagCompound(tag);
		}
		world.setBlockToAir(pos);
		if (!returnBlock)
			for (@Nonnull ItemStack stack : list)
				dropStack(world, pos, stack);
		return list;
	}

	@Override
	public boolean canDismantle(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

		return getTile(world, pos) instanceof TileEntityFactory;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, @Nonnull ItemStack stack) {

		super.onBlockPlacedBy(world, pos, state, entity, stack);
		if (entity != null) {
			TileEntity te = getTile(world, pos);
			if (te instanceof IRotatableTile)
				if (((IRotatableTile) te).canRotate())
					switch (MathHelper.floor((entity.rotationYaw * 4F) / 360F + 0.5D) & 3) {
					case 0:
						((IRotatableTile) te).rotateDirectlyTo(3);
						break;
					case 1:
						((IRotatableTile) te).rotateDirectlyTo(4);
						break;
					case 2:
						((IRotatableTile) te).rotateDirectlyTo(2);
						break;
					case 3:
						((IRotatableTile) te).rotateDirectlyTo(5);
						break;
					}

			if (te instanceof TileEntityFactory) {
				if (entity.addedToChunk)
					((TileEntityFactory) te).setOwner(entity.getName());
				else
					((TileEntityFactory) te).setOwner(null);
			}
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return Machine.getMachineFromIndex(_mfrMachineBlockIndex, state.getValue(TYPE).getMeta()).getNewTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactoryInventory)
			return ((TileEntityFactoryInventory) te).getComparatorOutput();
		return 0;
	}

	@Override
	public boolean activated(World world, BlockPos pos, EntityPlayer entityplayer, EnumFacing side, EnumHand hand,
			@Nonnull ItemStack heldItem) {

		if (super.activated(world, pos, entityplayer, side, hand, heldItem))
			return true;
		TileEntity te = getTile(world, pos);
		if (te == null) {
			return false;
		}

		if (te instanceof TileEntityFactoryInventory) {
			if (((TileEntityFactoryInventory) te).acceptUpgrade(heldItem)) {
				if (entityplayer.capabilities.isCreativeMode) {
					heldItem.grow(1);
				}
				if (heldItem.getCount() <= 0) {
					EntityEquipmentSlot slot =
							hand == EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
					entityplayer.setItemStackToSlot(slot, ItemStack.EMPTY);
				}
				return true;
			}
		}

		if (te instanceof TileEntityFactory &&
				((TileEntityFactory) te).getContainer(entityplayer.inventory) != null) {
			if (!world.isRemote) {
				entityplayer.openGui(MineFactoryReloadedCore.instance(), 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFactory) {
			return ((TileEntityFactory) te).getRedNetOutput(side);
		}
		return 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return getWeakPower(state, world, pos, side);
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, BlockPos pos, EnumFacing side) {

		return RedNetConnectionType.DecorativeSingle;
	}

	@Override
	public int[] getOutputValues(World world, BlockPos pos, EnumFacing side) {

		return null;
	}

	@Override
	public void onInputsChanged(World world, BlockPos pos, EnumFacing side, int[] inputValues) {

	}

	@Override
	public int getOutputValue(World world, BlockPos pos, EnumFacing side, int subnet) {

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactory) {
			return ((TileEntityFactory) te).getRedNetOutput(side);
		}
		return 0;
	}

	@Override
	public void onInputChanged(World world, BlockPos pos, EnumFacing side, int inputValue) {

		TileEntity te = getTile(world, pos);
		if (te instanceof TileEntityFactory) {
			((TileEntityFactory) te).onRedNetChanged(side, inputValue);
			neighborChanged(world.getBlockState(pos), world, pos, MFRThings.rednetCableBlock, pos);
		}
	}

	@Override
	public boolean initialize() {

		MFRRegistry.registerBlock(this, new ItemBlockFactoryMachine(this));
		//TODO look into replacing the meta in here with state or at least enum
		for (Type type : Type.GROUP_TYPES[_mfrMachineBlockIndex]) {
			Machine machine = Machine.getMachineFromIndex(_mfrMachineBlockIndex, type.getMeta());
			GameRegistry.registerTileEntity(machine.getTileEntityClass(), machine.getTileEntityName());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomStateMapper(this, MachineStateMapper.getInstance());

		Item item = MFRRegistry.getItemBlock(this);

		for (Type type : Type.GROUP_TYPES[_mfrMachineBlockIndex]) {
			if (type == Type.ITEM_COLLECTOR) {
				ModelResourceLocation regular = new ModelResourceLocation(
						MFRProps.PREFIX + MachineStateMapper.getModelName(type), "type=" + type.getName());
				ModelResourceLocation tinker = new ModelResourceLocation(regular.toString() + ",active=true");
				ModelLoader.registerItemVariants(item, regular, tinker);
				ModelLoader.setCustomMeshDefinition(item,
						stack -> stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasTinkerStuff") ? tinker : regular);
			} else {
				ModelHelper.registerModel(item, type.getMeta(), MachineStateMapper.getModelName(type), "type=" + type.getName());
			}
		}

		if (_mfrMachineBlockIndex == 2) {
			//slightly hacky but probably best way to handle just two TESRs needed to get registered
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserDrill.class, new LaserDrillRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserDrillPrecharger.class,
					new LaserDrillPrechargerRenderer());
		}
	}

	public enum Type implements IStringSerializable {
		PLANTER(0, 0, "planter"),
		FISHER(0, 1, "fisher"),
		HARVESTER(0, 2, "harvester"),
		RANCHER(0, 3, "rancher"),
		FERTILIZER(0, 4, "fertilizer"),
		VET(0, 5, "vet"),
		ITEM_COLLECTOR(0, 6, "item_collector"),
		BLOCK_BREAKER(0, 7, "block_breaker"),
		WEATHER_COLLECTOR(0, 8, "weather_collector"),
		SLUDGE_BOILER(0, 9, "sludge_boiler"),
		SEWER(0, 10, "sewer"),
		COMPOSTER(0, 11, "composter"),
		BREEDER(0, 12, "breeder"),
		GRINDER(0, 13, "grinder"),
		AUTO_ENCHANTER(0, 14, "auto_enchanter"),
		CHRONOTYPER(0, 15, "chronotyper"),

		EJECTOR(1, 0, "ejector"),
		ITEM_ROUTER(1, 1, "item_router"),
		LIQUID_ROUTER(1, 2, "liquid_router"),
		DEEP_STORAGE_UNIT(1, 3, "deep_storage_unit"),
		LIQUI_CRAFTER(1, 4, "liqui_crafter"),
		LAVA_FABRICATOR(1, 5, "lava_fabricator"),
		STEAM_BOILER(1, 6, "steam_boiler"),
		AUTO_JUKEBOX(1, 7, "auto_jukebox"),
		UNIFIER(1, 8, "unifier"),
		AUTO_SPAWNER(1, 9, "auto_spawner"),
		BIO_REACTOR(1, 10, "bio_reactor"),
		BIO_FUEL_GENERATOR(1, 11, "bio_fuel_generator"),
		AUTO_DISENCHANTER(1, 12, "auto_disenchanter"),
		SLAUGHTER_HOUSE(1, 13, "slaughter_house"),
		MEAT_PACKER(1, 14, "meat_packer"),
		ENCHANTMENT_ROUTER(1, 15, "enchantment_router"),

		LASER_DRILL(2, 0, "laser_drill"),
		LASER_DRILL_PRECHARGER(2, 1, "laser_drill_precharger"),
		AUTO_ANVIL(2, 2, "auto_anvil"),
		BLOCK_SMASHER(2, 3, "block_smasher"),
		RED_NOTE(2, 4, "red_note"),
		AUTO_BREWER(2, 5, "auto_brewer"),
		FRUIT_PICKER(2, 6, "fruit_picker"),
		BLOCK_PLACER(2, 7, "block_placer"),
		MOB_COUNTER(2, 8, "mob_counter"),
		STEAM_TURBINE(2, 9, "steam_turbine"),
		CHUNK_LOADER(2, 10, "chunk_loader"),
		FOUNTAIN(2, 11, "fountain"),
		MOB_ROUTER(2, 12, "mob_router");

		private final int groupIndex;
		private final int meta;
		private final String name;

		public static List<Type>[] GROUP_TYPES = new List[3];

		Type(int groupIndex, int meta, String name) {

			this.groupIndex = groupIndex;
			this.meta = meta;
			this.name = name;
		}

		public int getGroupIndex() {

			return groupIndex;
		}

		public int getMeta() {

			return meta;
		}

		@Override
		public String getName() {

			return name;
		}

		public static Type byMetadata(int groupIndex, int meta) {

			int index = groupIndex * 16 + meta;
			return index >= values().length ? PLANTER : values()[index];
		}

		static {
			for (int i = 0; i < 3; i++)
				GROUP_TYPES[i] = new ArrayList<>();

			for (Type type : Type.values()) {
				GROUP_TYPES[type.getGroupIndex()].add(type);
			}
		}
	}
}
