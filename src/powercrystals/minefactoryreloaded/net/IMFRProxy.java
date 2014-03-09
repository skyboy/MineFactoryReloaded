package powercrystals.minefactoryreloaded.net;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.TextureStitchEvent;

public interface IMFRProxy
{
	public void init();
	
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z);
	
	public void onPostTextureStitch(TextureStitchEvent.Post e);
	
	public void addRelightChunk(Chunk chunk);
}
