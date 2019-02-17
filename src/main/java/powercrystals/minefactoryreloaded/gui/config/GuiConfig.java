package powercrystals.minefactoryreloaded.gui.config;

import cofh.CoFHCore;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {

	public GuiConfig(GuiScreen parentScreen) {

		super(parentScreen, getConfigElements(parentScreen), CoFHCore.MOD_ID, false, false, CoFHCore.MOD_NAME);
	}

	private static List<IConfigElement> getConfigElements(GuiScreen parent) {

		List<IConfigElement> list = new ArrayList<>();

		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (server == null || !server.isServerRunning()) {
				list.add(new DummyCategoryElement("Child Mods", "config.childMods", getClientConfigElements()));
			}
		}

		return list;
	}

	private static List<IConfigElement> getClientConfigElements() {

		List<IConfigElement> list = new ArrayList<>();

		//ModContainer container = Loader.instance().getIndexedModList().get(MineFactoryReloadedCore.modId);
		//container.get
		//for (int i = 0; i < CATEGORIES_CLIENT.length; i++) {
		//	list.add(new DummyConfigElement<ConfigCategory>();
		//}
		return list;
	}

}
