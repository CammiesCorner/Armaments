package dev.cammiescorner.armaments;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;

@Config(value = Armaments.MOD_ID, categories = {
	ArmamentsConfig.SeaCrown.class,
	ArmamentsConfig.EchoDagger.class,
	ArmamentsConfig.CrystalSpear.class,
	ArmamentsConfig.GoatHorn.class
})
public final class ArmamentsConfig {
	@Category(
		value = "Sea Crown"
	)
	public static final class SeaCrown {
		@ConfigEntry(
			id = "enablesRiptide",
			translation = "config." + Armaments.MOD_ID + ".sea_crown.enables_riptide"
		)
		public static boolean enablesRiptide = true;

		@ConfigEntry(
			id = "riptideCoolDown",
			translation = "config." + Armaments.MOD_ID + ".sea_crown.riptide_cool_down"
		)
		public static int riptideCoolDown = 60;

		@ConfigEntry(
			id = "potionAmplifier",
			translation = "config." + Armaments.MOD_ID + ".sea_crown.potion_amplifier"
		)
		public static int potionAmplifier = 1;

		@ConfigEntry(
			id = "healthModifier",
			translation = "config." + Armaments.MOD_ID + ".sea_crown.health_modifier"
		)
		public static int healthModifier = -2;

		@ConfigEntry(
			id = "armorPoints",
			translation = "config." + Armaments.MOD_ID + ".sea_crown.armor_points"
		)
		public static int armorPoints = 0;
	}

	@Category(
		value = "Echo Dagger"
	)
	public static final class EchoDagger {
		@ConfigEntry(
			id = "echoMultiplier",
			translation = "config." + Armaments.MOD_ID + ".echo_dagger.echo_multiplier"
		)
		@ConfigOption.Range(min = 0f, max = 0.9f)
		public static float echoMultiplier = 0.5f;

		@ConfigEntry(
			id = "echoDelay",
			translation = "config." + Armaments.MOD_ID + ".echo_dagger.echo_delay"
		)
		public static int echoDelay = 20;

		@ConfigEntry(
			id = "potionDuration",
			translation = "config." + Armaments.MOD_ID + ".echo_dagger.potion_duration"
		)
		public static int potionDuration = 300;
	}

	@Category(
		value = "Crystal Spear"
	)
	public static final class CrystalSpear {
		@ConfigEntry(
			id = "chargeInterval",
			translation = "config." + Armaments.MOD_ID + ".crystal_spear.charge_interval"
		)
		public static int chargeInterval = 40;

		@ConfigEntry(
			id = "joustingCoolDown",
			translation = "config." + Armaments.MOD_ID + ".crystal_spear.jousting_cool_down"
		)
		public static int joustingCoolDown = 60;
	}

	@Category(
		value = "Goat Horn"
	)
	public static final class GoatHorn {
		@ConfigEntry(
			id = "speedDuration",
			translation = "config." + Armaments.MOD_ID + ".goat_horn.speed_duration"
		)
		public static int speedDuration = 600;

		@ConfigEntry(
			id = "speedAmplifier",
			translation = "config." + Armaments.MOD_ID + ".goat_horn.speed_amplifier"
		)
		@ConfigOption.Range(min = 0, max = 255)
		public static int speedAmplifier = 1;

		@ConfigEntry(
			id = "resistanceDuration",
			translation = "config." + Armaments.MOD_ID + ".goat_horn.resistance_duration"
		)
		public static int resistanceDuration = 300;

		@ConfigEntry(
			id = "speedAmplifier",
			translation = "config." + Armaments.MOD_ID + ".goat_horn.resistance_amplifier"
		)
		@ConfigOption.Range(min = 0, max = 255)
		public static int resistanceAmplifier = 0;
	}
}
