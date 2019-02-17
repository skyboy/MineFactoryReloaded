package powercrystals.minefactoryreloaded.net;

import cofh.core.render.IModelRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.render.IColorRegister;

public class ClientProxy extends CommonProxy {

	@Override
	public void addModelRegister(IModelRegister register) {

		MineFactoryReloadedClient.addModelRegister(register);
	}

	@Override
	public void addColorRegister(IColorRegister register) {

		MineFactoryReloadedClient.addColorRegister(register);
	}

	@Override
	public void preInit() {

		MineFactoryReloadedClient.preInit();
	}

	@Override
	public void init() {

		super.init();

		MineFactoryReloadedClient.init();
	}

	@Override
	public EntityPlayer getPlayer() {

		return Minecraft.getMinecraft().player;
	}

	@Override
	public void movePlayerToCoordinates(EntityLivingBase e, double x, double y, double z) {

		e.setPositionAndUpdate(x, y, z);
	}

}
