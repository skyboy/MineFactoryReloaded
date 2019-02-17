package powercrystals.minefactoryreloaded.tile.machine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import javax.annotation.Nonnull;

public class TileEntityBlockPlacer extends TileEntityFactoryPowered {

	public TileEntityBlockPlacer() {

		super(Machine.BlockPlacer);
		setManageSolids(true);
		setCanRotate(true);
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
	public int getSizeInventory() {

		return 9;
	}

	@Override
	protected boolean activateMachine() {

		for (int i = 0; i < getSizeInventory(); i++) {
			@Nonnull ItemStack stack = _inventory.get(i);
			if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlock))
				continue;

			ItemBlock item = (ItemBlock) stack.getItem();
			Block block = item.getBlock();

			BlockPos bp = pos.offset(getDirectionFacing());
			if (world.isAirBlock(bp) &&
					block.canPlaceBlockOnSide(world, bp, EnumFacing.DOWN)) {
				int j1 = item.getMetadata(stack.getItemDamage());
				FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) world);
				fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);
				IBlockState placementState = block.getStateForPlacement(world, bp, EnumFacing.DOWN, 0, 0, 0, j1, fakePlayer, EnumHand.MAIN_HAND);
				if (item.placeBlockAt(stack, fakePlayer, world, bp, EnumFacing.DOWN, 0, 0, 0, placementState)) {
					if (MFRConfig.playSounds.getBoolean(true)) {
						SoundType soundType = block.getSoundType(placementState, world, bp, null);
						world.playSound(null, bp, soundType.getStepSound(), SoundCategory.BLOCKS,
							(soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
					}
					decrStackSize(i, 1);
					return true;
				}
			}
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}

	@Override
	public int getWorkMax() {

		return 1;
	}

	@Override
	public int getIdleTicksMax() {

		return 20;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, EnumFacing side) {

		return !itemstack.isEmpty() && itemstack.getItem() instanceof ItemBlock;
	}

}
