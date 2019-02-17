package powercrystals.minefactoryreloaded.modcompat.thermalexpansion;

import cofh.api.util.ThermalExpansionHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator;
import powercrystals.minefactoryreloaded.api.mob.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.mob.RandomMobProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static powercrystals.minefactoryreloaded.api.integration.IMFRIntegrator.findItem;
import static powercrystals.minefactoryreloaded.api.integration.IMFRRecipeSet.stack;
import static powercrystals.minefactoryreloaded.modcompat.Compats.ModIds.*;

@IMFRIntegrator.DependsOn(THERMAL_EXPANSION)
@IMFRIntegrator.After("Minecraft")
public class ThermalExpansion implements IMFRIntegrator, IRandomMobProvider {


	public void load() {

		final Item sulfur = findItem(THERMAL_FOUNDATION, "material");
		final Item rawRubberItem = findItem(MFR, "rubber_raw");
		final Item rubberBarItem = findItem(MFR, "rubber_bar");
		final Item factoryDecorativeStoneBlock = findItem(MFR, "decorative_stone");

		REGISTRY.registerRandomMobProvider(this);

		ThermalExpansionHelper.addSmelterRecipe(2000,
				stack(rawRubberItem, 2), stack(sulfur, 1, 771),
				stack(rubberBarItem, 4),
				stack(rubberBarItem, 1), 15);

		// Smooth Blackstone -> Cobble
		ThermalExpansionHelper.addPulverizerRecipe(3200,
				stack(factoryDecorativeStoneBlock, 1, 0),
				stack(factoryDecorativeStoneBlock, 1, 2));
		// Smooth Whitestone -> Cobble
		ThermalExpansionHelper.addPulverizerRecipe(3200,
				stack(factoryDecorativeStoneBlock, 1, 1),
				stack(factoryDecorativeStoneBlock, 1, 3));
	}

	@Override
	public List<RandomMobProvider> getRandomMobs(World w) {

		final Item plasticBootsItem = findItem(MFR, "plastic_boots");

		ArrayList<RandomMobProvider> mobs = new ArrayList<>();

		mobs.add(new RandomMobProvider(20, (world, pos) -> {
			EntityCreeper creeper = IRandomMobProvider.spawnMob(EntityCreeper.class, world, pos);
			Objects.requireNonNull(creeper).setCustomNameTag("Exploding Zeldo");
			creeper.setAlwaysRenderNameTag(true);
			creeper.enablePersistence();
			ItemStack armor = stack(plasticBootsItem);
			armor.setStackDisplayName("Zeldo's Ruby Slippers");
			EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(armor);
			creeper.setItemStackToSlot(slot, armor);
			creeper.setDropChance(slot, 2);
			return creeper;
		}));

		return mobs;
	}

}
