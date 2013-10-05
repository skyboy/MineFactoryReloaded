package powercrystals.minefactoryreloaded.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;

public class RedstoneCableModel extends ModelBase
{
	private ModelRenderer _base;
	private ModelRenderer _cableConn;
	private ModelRenderer _plate;
	private ModelRenderer _interfaceConn;
	private ModelRenderer _bandWhite;
	
	private Vector3f[] _bandColors = new Vector3f[16];
	
	public RedstoneCableModel()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		_base = new ModelRenderer(this, 32, 0);
		_base.addBox(-2F, -2F, -2F, 4, 4, 4);
		_base.setRotationPoint(0F, 0F, 0F);
		_base.setTextureSize(64, 32);
		_base.mirror = true;
		setRotation(_base, 0F, 0F, 0F);
		_cableConn = new ModelRenderer(this, 32, 8);
		_cableConn.addBox(2F, -2F, -2F, 6, 4, 4);
		_cableConn.setRotationPoint(0F, 0F, 0F);
		_cableConn.setTextureSize(64, 32);
		_cableConn.mirror = true;
		setRotation(_cableConn, 0F, 0F, 0F);
		_plate = new ModelRenderer(this, 0, 0);
		_plate.addBox(6, -7, -7, 2, 14, 14);
		_plate.setRotationPoint(0,0,0);
		_plate.setTextureSize(64, 32);
		_plate.mirror = true;
		setRotation(_plate, 0F, 0F, 0F);
		_interfaceConn = new ModelRenderer(this, 48, 0);
		_interfaceConn.addBox(2, -2, -2, 4, 4, 4);
		_interfaceConn.setRotationPoint(0,0,0);
		_interfaceConn.setTextureSize(64, 32);
		_interfaceConn.mirror = true;
		setRotation(_interfaceConn, 0F, 0F, 0F);
		_bandWhite = new ModelRenderer(this, 32, 16);
		_bandWhite.addBox(5, -2.5f, -2.5f, 1, 5, 5);
		_bandWhite.setRotationPoint(0,0,0);
		_bandWhite.setTextureSize(64, 32);
		_bandWhite.mirror = true;
		setRotation(_bandWhite, 0F, 0F, 0F);
		
		_bandColors[0]  = new Vector3f(255.0f/255.0f, 255.0f/255.0f, 255.0f/255.0f); // white
		_bandColors[1]  = new Vector3f(255.0f/255.0f, 128.0f/255.0f,  64.0f/255.0f); // orange
		_bandColors[2]  = new Vector3f(255.0f/255.0f,  64.0f/255.0f, 255.0f/255.0f); // magenta
		_bandColors[3]  = new Vector3f(128.0f/255.0f, 128.0f/255.0f, 255.0f/255.0f); // light blue
		_bandColors[4]  = new Vector3f(255.0f/255.0f, 255.0f/255.0f,   0.0f/255.0f); // yellow
		_bandColors[5]  = new Vector3f( 64.0f/255.0f, 255.0f/255.0f,  64.0f/255.0f); // lime
		_bandColors[6]  = new Vector3f(255.0f/255.0f, 196.0f/255.0f, 255.0f/255.0f); // pink
		_bandColors[7]  = new Vector3f( 96.0f/255.0f,  96.0f/255.0f,  96.0f/255.0f); // gray
		_bandColors[8]  = new Vector3f(168.0f/255.0f, 168.0f/255.0f, 168.0f/255.0f); // light gray
		_bandColors[9]  = new Vector3f( 64.0f/255.0f, 128.0f/255.0f, 128.0f/255.0f); // cyan
		_bandColors[10] = new Vector3f(128.0f/255.0f,   0.0f/255.0f, 192.0f/255.0f); // purple
		_bandColors[11] = new Vector3f(  0.0f/255.0f,   0.0f/255.0f, 255.0f/255.0f); // blue
		_bandColors[12] = new Vector3f( 96.0f/255.0f,  64.0f/255.0f,  32.0f/255.0f); // brown
		_bandColors[13] = new Vector3f(  0.0f/255.0f, 128.0f/255.0f,   0.0f/255.0f); // green
		_bandColors[14] = new Vector3f(255.0f/255.0f,   0.0f/255.0f,   0.0f/255.0f); // red
		_bandColors[15] = new Vector3f( 32.0f/255.0f,  32.0f/255.0f,  32.0f/255.0f); // black
	}
	
	public void render(float f5)
	{
		_base.render(f5);
		renderSide(RedNetConnectionType.CableAll, 0, 0, 0, f5);
		renderSide(RedNetConnectionType.CableAll, 0, (float)Math.PI, 0, f5);
	}
	
	public void render(TileEntityRedNetCable entity, float f5)
	{
		_base.render(f5);
		
		renderSide(entity.getConnectionState(ForgeDirection.EAST), entity.getSideColor(ForgeDirection.EAST),0,0,f5);
		renderSide(entity.getConnectionState(ForgeDirection.SOUTH), entity.getSideColor(ForgeDirection.SOUTH),(float)-Math.PI/2, 0, f5);
		renderSide(entity.getConnectionState(ForgeDirection.WEST), entity.getSideColor(ForgeDirection.WEST),(float)Math.PI, 0, f5);
		renderSide(entity.getConnectionState(ForgeDirection.NORTH), entity.getSideColor(ForgeDirection.NORTH),(float)Math.PI/2, 0, f5);
		renderSide(entity.getConnectionState(ForgeDirection.UP), entity.getSideColor(ForgeDirection.UP),0,(float)Math.PI/2, f5);
		renderSide(entity.getConnectionState(ForgeDirection.DOWN), entity.getSideColor(ForgeDirection.DOWN),0,(float)-Math.PI/2, f5);
	}
	
	private void renderModelState(RedNetConnectionType state, float yRot, float zRot, float scale)
	{
		switch (state.flags & 31)
		{
		case 19: // isCable, isAllSubnets
			_cableConn.rotateAngleY = yRot;
			_cableConn.rotateAngleZ = zRot;
			_cableConn.render(scale);
			break;
		case 11: // isCable, isSingleSubnet
			_cableConn.rotateAngleY = yRot;
			_cableConn.rotateAngleZ = zRot;
			_cableConn.render(scale);
			
			_bandWhite.rotateAngleY = yRot;
			_bandWhite.rotateAngleZ = zRot;
			_bandWhite.render(scale);
			break;
		case 21: // isPlate, isAllSubnets
			_interfaceConn.rotateAngleY = yRot;
			_interfaceConn.rotateAngleZ = zRot;
			_interfaceConn.render(scale);
			
			_plate.rotateAngleY = yRot;
			_plate.rotateAngleZ = zRot;
			_plate.render(scale);
			break;
		case 13: // isPlate, isSingleSubnet
			_interfaceConn.rotateAngleY = yRot;
			_interfaceConn.rotateAngleZ = zRot;
			_interfaceConn.render(scale);
			
			_plate.rotateAngleY = yRot;
			_plate.rotateAngleZ = zRot;
			_plate.render(scale);
			
			_bandWhite.rotateAngleY = yRot;
			_bandWhite.rotateAngleZ = zRot;
			_bandWhite.render(scale);
			break;
		default:
			break;
		}
	}
	
	private void renderSide(RedNetConnectionType state, int color, float yRot, float zRot, float scale)
	{
		if (state.isSingleSubnet)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor3f(_bandColors[color].x, _bandColors[color].y, _bandColors[color].z);
			renderModelState(state, yRot, zRot, scale);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glPolygonOffset(0, -1);
		}
		
		renderModelState(state, yRot, zRot, scale);
		
		if (state.isSingleSubnet)
		{
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		}
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
	{
	}
}