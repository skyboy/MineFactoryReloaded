package powercrystals.minefactoryreloaded.modcompat.forestry;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizable;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.plant.IReplacementBlock;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings;
import powercrystals.minefactoryreloaded.api.util.IFactorySettings.SettingNames;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ForestryLeaf extends HarvestableTreeLeaves implements IFactoryFruit, IFactoryFertilizable {

	private ITreeRoot root;
	private IReplacementBlock repl;
	private Item _item;

	ForestryLeaf(Block block) {

		super(block);
		root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		_item = Item.getItemFromBlock(block);
		repl = (world, pos, stack) -> {

			TileEntity te = world.getTileEntity(pos);
			if (te instanceof IFruitBearer) {
				IFruitBearer fruit = (IFruitBearer) te;
				fruit.addRipeness(-fruit.getRipeness());
			}
			return true;
		};
	}

	@Override
	public boolean canBePicked(World world, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IFruitBearer) {
			IFruitBearer fruit = (IFruitBearer) te;
			return fruit.getRipeness() > 0.99f;
		}
		return false;
	}

	@Override
	public boolean canFertilize(World world, BlockPos pos, FertilizerType fertilizerType) {

		return !canBePicked(world, pos);
	}

	@Override
	public boolean fertilize(World world, Random rand, BlockPos pos, FertilizerType fertilizerType) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IFruitBearer) {
			IFruitBearer fruit = (IFruitBearer) te;
			fruit.addRipeness(0.15f + rand.nextFloat() * 0.35f);
			return true;
		}
		return false;
	}

	@Override
	public IReplacementBlock getReplacementBlock(World world, BlockPos pos) {

		return repl;
	}

	@Override // HARVESTER
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState harvestState, Random rand, IFactorySettings settings) {

		ITree tree = getTree(world, pos);
		if (tree == null)
			return null;

		ArrayList<ItemStack> prod = new ArrayList<ItemStack>();

		float modifier = 1f;
		if (settings.getBoolean(SettingNames.SHEARS_MODE)) {
			ItemStack item = new ItemStack(_item);
			NBTTagCompound tag = new NBTTagCompound();
			tree.writeToNBT(tag);
			item.setTagCompound(tag);
			prod.add(item);
		} else {
			boolean hasMate = tree.getMate() != null;
			for (ITree s : getSaplings(tree, world, pos, modifier))
				if (s != null) {
					if ((hasMate && !s.isGeneticEqual(tree)) || rand.nextInt(32) == 0)
						if (rand.nextBoolean())
							prod.add(root.getMemberStack(s, EnumGermlingType.POLLEN));

					prod.add(root.getMemberStack(s, EnumGermlingType.SAPLING));
				}

			getFruits(world, pos, tree, prod);
		}

		return prod;
	}

	private static List<ITree> getSaplings(ITree tree, World world, BlockPos pos, float modifier) {

		return tree.getSaplings(world, null, pos, modifier);
	}

	@Override // FRUIT PICKER
	public List<ItemStack> getDrops(World world, Random rand, BlockPos pos) {

		ITree tree = getTree(world, pos);
		if (tree == null)
			return null;

		ArrayList<ItemStack> prod = new ArrayList<>();
		getFruits(world, pos, tree, prod);
		return prod;
	}

	private ITree getTree(World world, BlockPos pos) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IPollinatable) {
			IIndividual t = ((IPollinatable) te).getPollen();
			if (t instanceof ITree)
				return (ITree) t;
		}
		return null;
	}

	private void getFruits(World world, BlockPos pos, ITree tree, ArrayList<ItemStack> prod) {

		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IFruitBearer) {
			IFruitBearer fruit = (IFruitBearer) te;
			if (fruit.hasFruit()) {
				//int period = tree.getGenome().getFruitProvider().getRipeningPeriod();
				//ItemStack[] o = tree.produceStacks(world, pos, (int)(fruit.getRipeness() * period + 0.1f));
				prod.addAll(fruit.pickFruit(ItemStack.EMPTY));
			}
		}
	}

}
