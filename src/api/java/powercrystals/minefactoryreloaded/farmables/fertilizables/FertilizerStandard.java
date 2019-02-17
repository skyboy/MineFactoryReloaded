package powercrystals.minefactoryreloaded.farmables.fertilizables;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.api.plant.FertilizerType;
import powercrystals.minefactoryreloaded.api.plant.IFactoryFertilizer;

import javax.annotation.Nonnull;

public class FertilizerStandard implements IFactoryFertilizer
{
	private Item _item;
	private int _meta;
	private FertilizerType _type;
	
	public FertilizerStandard(Item item, int meta)
	{
		this(item, meta, FertilizerType.GrowPlant);
	}
	
	public FertilizerStandard(Item item, int meta, FertilizerType type)
	{
		_item = item;
		_meta = meta;
		_type = type;
	}
	
	@Override
	public Item getFertilizer()
	{
		return _item;
	}
	
	@Override
	public FertilizerType getFertilizerType(@Nonnull ItemStack stack)
	{
		if (stack.getItemDamage() == _meta)
			return _type;
		return FertilizerType.None;
	}
	
	@Override
	public void consume(@Nonnull ItemStack fertilizer)
	{
		fertilizer.shrink(1);
	}
}
