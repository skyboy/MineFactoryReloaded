
package powercrystals.minefactoryreloaded.modcompat.twilightforest;

import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.mob.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.mob.RandomMobProvider;

import java.util.ArrayList;
import java.util.List;

public class TwilightForestMobProvider implements IRandomMobProvider {

	@Override
	@SuppressWarnings("unchecked")
	public List<RandomMobProvider> getRandomMobs(World world) {

		List<RandomMobProvider> mobs = new ArrayList<>();

		try {
			Class tfBoar = Class.forName("twilightforest.entity.passive.EntityTFBoar");
			Class tfDeathTome = Class.forName("twilightforest.entity.EntityTFDeathTome");
			Class tfDeer = Class.forName("twilightforest.entity.passive.EntityTFDeer");
			Class tfMazeSlime = Class.forName("twilightforest.entity.EntityTFMazeSlime");
			Class tfPenguin = Class.forName("twilightforest.entity.passive.EntityTFPenguin");
			Class tfPinchBeetle = Class.forName("twilightforest.entity.EntityTFPinchBeetle");
			Class tfRaven = Class.forName("twilightforest.entity.passive.EntityTFRaven");
			Class tfSquirrel = Class.forName("twilightforest.entity.passive.EntityTFSquirrel");
			Class tfTinyBird = Class.forName("twilightforest.entity.passive.EntityTFTinyBird");
			Class tfWraith = Class.forName("twilightforest.entity.EntityTFWraith");

			mobs.add(new RandomMobProvider(80, IRandomMobProvider.prepareMob(tfBoar)));
			mobs.add(new RandomMobProvider(80, IRandomMobProvider.prepareMob(tfDeer)));
			mobs.add(new RandomMobProvider(50, IRandomMobProvider.prepareMob(tfRaven)));
			mobs.add(new RandomMobProvider(25, IRandomMobProvider.prepareMob(tfPenguin)));
			mobs.add(new RandomMobProvider(25, IRandomMobProvider.prepareMob(tfSquirrel)));
			mobs.add(new RandomMobProvider(75, IRandomMobProvider.prepareMob(tfTinyBird)));
			mobs.add(new RandomMobProvider(15, IRandomMobProvider.prepareMob(tfMazeSlime)));
			mobs.add(new RandomMobProvider(15, IRandomMobProvider.prepareMob(tfPinchBeetle)));
			mobs.add(new RandomMobProvider(5, IRandomMobProvider.prepareMob(tfWraith)));
			mobs.add(new RandomMobProvider(10, IRandomMobProvider.prepareMob(tfDeathTome)));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return mobs;
	}

}
