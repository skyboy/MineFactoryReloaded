package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ValuedItem
{
	public final int value;
	public final @Nonnull ItemStack item;
	public final String key;
	public final Object object;
	
	public ValuedItem(int v, @Nonnull ItemStack i)
	{
		value = v;
		item = i;
		key = null;
		object = null;
	}
	
	public ValuedItem(String v, Object i)
	{
		value = -1;
		item = ItemStack.EMPTY;
		key = v;
		object = i;
	}
	
	/**
	 * Presently unused but included so that if they do get used in the future,
	 * people including this in their jar and loading before MFR don't destroy everyone
	 */
	
	public ValuedItem(int v, Object i)
	{
		value = v;
		item = ItemStack.EMPTY;
		key = null;
		object = i;
	}
	
	public ValuedItem(String v, @Nonnull ItemStack i)
	{
		value = -1;
		item = i;
		key = v;
		object = null;
	}
	
	public ValuedItem(int v, String k, @Nonnull ItemStack i)
	{
		value = v;
		item = i;
		key = k;
		object = null;
	}
	
	public ValuedItem(int v, String k, Object i)
	{
		value = v;
		item = ItemStack.EMPTY;
		key = k;
		object = i;
	}
	
	public ValuedItem(int v, String k, @Nonnull ItemStack i, Object o)
	{
		value = v;
		item = i;
		key = k;
		object = o;
	}
}
