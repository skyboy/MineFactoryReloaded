package powercrystals.minefactoryreloaded.modhelpers.pam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HarvestablePamsFlower extends HarvestablePams
{
	public HarvestablePamsFlower( int sourceId ) throws ClassNotFoundException
	{
		super( sourceId );
		getGrowthStage = Pam.pamTEFlowerGetGrowthStage;
		getCrop = Pam.pamTEFlowerGetCropId;
	}
	@Override
	public List<ItemStack> getDrops( World world, Random rand, Map<String, Boolean> harvesterSettings, int x, int y, int z )
	{
		List<ItemStack> outStack = new ArrayList<ItemStack>();
		TileEntity te = world.getBlockTileEntity( x, y, z );
		int seedDrops = rand.nextInt(2) + 1;
		int cropDrops = rand.nextInt(3) + 2;
		if ( te != null )
		{
			try
			{
				int cropID = ( Integer ) ( getCrop.invoke( te, new Object[]
						{} ) );
				int seedID=cropID;
				switch ( cropID )
				{
				case 4:
					outStack.add( new ItemStack( Block.plantYellow, seedDrops, 0 ) );
					break;
				case 14:
					outStack.add( new ItemStack( Block.plantRed, seedDrops, 0 ) );
					break;
				default:
					if(cropID>14)
						cropID-=2;
					else if(cropID>4)
						cropID-=1;
					outStack.add( new ItemStack( Pam.flowerId, cropDrops, cropID ) );
				}
				if(harvesterSettings.get("playSounds"))
				{
					world.playAuxSFXAtEntity(null, 2001, x,y,z,
							_sourceId + (0 << 12));
				}
				outStack.add( new ItemStack( Pam.flowerSeeds[seedID], seedDrops, 0 ) );
			}
			catch ( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		return outStack;
	}
	@Override
	public void postHarvest( World world, int x, int y, int z )
	{
		world.removeBlockTileEntity(x,y,z);

		world.setBlockToAir(x,y,z);
	}
}
