package powercrystals.minefactoryreloaded.modhelpers.vanilla;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IRandomMobProvider;
import powercrystals.minefactoryreloaded.api.RandomMob;
import powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper;
import powercrystals.minefactoryreloaded.core.MFRUtil;

public class VanillaMobProvider implements IRandomMobProvider
{
	@Override
	public List<RandomMob> getRandomMobs(World world)
	{
		List<RandomMob> mobs = new ArrayList<RandomMob>();
		
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityMooshroom.class, world), 20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntitySlime.class, world), 20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityCow.class, world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityChicken.class, world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntitySheep.class, world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityWitch.class, world), 10));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityGhast.class, world), 15));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityPig.class, world), 100));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityCreeper.class, world), 25));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntitySquid.class, world), 30));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityOcelot.class, world), 20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityWolf.class, world), 20));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityBat.class, world), 35));
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityHorse.class, world), 20));
		
		EntityXPOrb batJockey = prepareXPOrb(world);
		EntityBat invisibat = MFRUtil.prepareMob(EntityBat.class, world);
		invisibat.addPotionEffect(new PotionEffect(Potion.invisibility.id, Short.MAX_VALUE));
		batJockey.mountEntity(invisibat);
		mobs.add(new RandomMob(invisibat, 15));
		
		mobs.add(new RandomMob(MFRUtil.prepareMob(EntityMinecartHopper.class, world), 15));
		
		EntityCreeper chargedCreeper = MFRUtil.prepareMob(EntityCreeper.class, world);
		NBTTagCompound creeperNBT = new NBTTagCompound(); 
		chargedCreeper.writeToNBT(creeperNBT);
		creeperNBT.setBoolean("powered", true);
		creeperNBT.setShort("Fuse", (short)120);
		chargedCreeper.readFromNBT(creeperNBT);
		mobs.add(new RandomMob(chargedCreeper, 5));
		
		EntityTNTPrimed armedTNT = MFRUtil.prepareMob(EntityTNTPrimed.class, world);
		armedTNT.fuse = 120;
		mobs.add(new RandomMob(armedTNT, 5));
		
		EntitySlime invisislime = MFRUtil.prepareMob(EntitySlime.class, world);
		invisislime.addPotionEffect(new PotionEffect(Potion.invisibility.id, 120 * 20));
		mobs.add(new RandomMob(invisislime, 5));
		
		EntityMooshroom invisishroom = MFRUtil.prepareMob(EntityMooshroom.class, world);
		invisishroom.addPotionEffect(new PotionEffect(Potion.invisibility.id, 120 * 20));
		mobs.add(new RandomMob(invisishroom, 5));
		
		EntityWolf invisiwolf = MFRUtil.prepareMob(EntityWolf.class, world);
		invisiwolf.addPotionEffect(new PotionEffect(Potion.invisibility.id, 120 * 20));
		invisiwolf.setAngry(true);
		mobs.add(new RandomMob(invisiwolf, 5));

		EntityTNTPrimed tntJockey = MFRUtil.prepareMob(EntityTNTPrimed.class, world);
		EntityBat tntMount = MFRUtil.prepareMob(EntityBat.class, world);
		tntJockey.fuse = 120;
		tntJockey.mountEntity(tntMount);
		mobs.add(new RandomMob(tntMount, 2));
		
		EntitySkeleton skeleton1 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		EntitySkeleton skeleton2 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		EntitySkeleton skeleton3 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		EntitySkeleton skeleton4 = MFRUtil.prepareMob(EntitySkeleton.class, world);
		skeleton4.mountEntity(skeleton3);
		skeleton3.mountEntity(skeleton2);
		skeleton2.mountEntity(skeleton1);
		mobs.add(new RandomMob(skeleton1, 2));
		
		EntityBlaze blazeJockey = MFRUtil.prepareMob(EntityBlaze.class, world);
		EntityGhast blazeMount = MFRUtil.prepareMob(EntityGhast.class, world);
		blazeJockey.mountEntity(blazeMount);
		mobs.add(new RandomMob(blazeMount, 2));
		
		EntityCreeper creeperJockey = MFRUtil.prepareMob(EntityCreeper.class, world);
		EntityCaveSpider creeperMount = MFRUtil.prepareMob(EntityCaveSpider.class, world);
		creeperJockey.mountEntity(creeperMount);
		mobs.add(new RandomMob(creeperMount, 2));

		tntJockey = MFRUtil.prepareMob(EntityTNTPrimed.class, world);
		EntityXPOrb tntMount2 = prepareXPOrb(world);
		tntJockey.fuse = 120;
		tntJockey.mountEntity(tntMount2);
		mobs.add(new RandomMob(tntMount2, 2));
		
		creeperJockey = MFRUtil.prepareMob(EntityCreeper.class, world);
		EntityXPOrb creeperMount2 = prepareXPOrb(world);
		creeperJockey.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20));
		creeperJockey.onStruckByLightning(null);
		creeperJockey.mountEntity(creeperMount2);
		mobs.add(new RandomMob(creeperMount2, 1));

		EntityEnderman direBane = MFRUtil.prepareMob(EntityEnderman.class, world);
		direBane.addPotionEffect(new PotionEffect(Potion.regeneration.id, 120 * 20));
		direBane.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 120 * 20));
		direBane.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(120);
		direBane.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.7);
		direBane.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(15);
		direBane.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32);
		direBane.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(1);
		direBane.stepHeight = 2;
		EntityPlayer player = world.getPlayerEntityByName("direwolf20");
		if (player != null)
		{
			direBane.setCustomNameTag("Bane of direwolf");
			direBane.setAlwaysRenderNameTag(true);
			direBane.func_110163_bv();
			ItemStack armor = new ItemStack(Item.plateGold);
			AutoEnchantmentHelper.addRandomEnchantment(direBane.getRNG(), armor, 60);
			int i = EntityLiving.getArmorPosition(armor);
			direBane.setCurrentItemOrArmor(i, armor);
			direBane.setEquipmentDropChance(i, 2.0F);
		}
		mobs.add(new RandomMob(direBane, 1));
		
		return mobs;
	}
	
	private EntityXPOrb prepareXPOrb(World world)
	{
		EntityXPOrb orb = MFRUtil.prepareMob(EntityXPOrb.class, world);
		orb.xpValue = 1;
		orb.xpOrbAge = Short.MIN_VALUE + 6001;
		orb.field_70532_c = Short.MAX_VALUE;
		return orb;
	}
}
