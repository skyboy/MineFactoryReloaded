package powercrystals.minefactoryreloaded.tile.machine;

import com.google.common.collect.Lists;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

import java.util.List;

public class TileEntityRedNote extends TileEntityFactory
{
	private static final List<SoundEvent> INSTRUMENTS = Lists.newArrayList(SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT,
			SoundEvents.BLOCK_NOTE_BASS);

	public TileEntityRedNote()
	{
		super(Machine.RedNote);
	}

	@Override
	public void onRedNetChanged(EnumFacing side, int value)
	{
		if (value <= 0 || value > 120)
		{
			return;
		}

		value--;
		int instrument = value / 25;
		int note = value % 25;
		
		float f = (float)Math.pow(2.0D, (note - 12) / 12.0D);
		
		world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, INSTRUMENTS.get(instrument), SoundCategory.BLOCKS, 3.0F, f);
	}
}
