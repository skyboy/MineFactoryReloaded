package powercrystals.minefactoryreloaded.core;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class GrindingDamage extends DamageSource {

	private static final String FAKE_PLAYER_NAME = "MinefactoryReloadedGrindingPlayer";
	private static Map<Integer, WeakReference<FakePlayer>> fakePlayerRefs = Maps.newHashMap();

	protected int _msgCount;
	protected Random _rand;
	private WeakReference<FakePlayer> fakePlayerRef;

	public GrindingDamage() {

		this(null, 1);
	}

	public GrindingDamage(String type) {

		this(type, 1);
	}

	public GrindingDamage(String type, int deathMessages) {

		super(type == null ? "mfr.grinder" : type);
		setDamageIsAbsolute();
		setDamageBypassesArmor();
		setDamageAllowedInCreativeMode();
		_msgCount = Math.max(deathMessages, 1);
		_rand = new Random();
	}

	public void setupGrindingPlayer(WorldServer world) {

		int dimId = world.provider.getDimension();
		if (!fakePlayerRefs.containsKey(dimId)) {
			String name = FAKE_PLAYER_NAME + "_" + dimId;
			fakePlayerRefs.put(dimId, new WeakReference<>(FakePlayerFactory.get(
					world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name))));
		}
		fakePlayerRef = fakePlayerRefs.get(dimId);
	}

	@Override
	public ITextComponent getDeathMessage(EntityLivingBase entity) {

		EntityLivingBase attackingEntity = entity.getAttackingEntity();
		String s = "death.attack." + this.damageType;
		if (_msgCount > 1) {
			int msg = _rand.nextInt(_msgCount);
			if (msg != 0) {
				s += "." + msg;
			}
		}
		String s1 = s + ".player";
		if (attackingEntity != null && I18n.canTranslate(s1))
			return new TextComponentTranslation(s1, entity.getName(), attackingEntity.getName());
		return new TextComponentTranslation(s, entity.getName());
	}

	@Nullable
	@Override
	public Entity getTrueSource() {

		return fakePlayerRef != null ? fakePlayerRef.get() : null;
	}
}
