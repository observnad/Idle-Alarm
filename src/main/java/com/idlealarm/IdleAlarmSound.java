package com.idlealarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.SoundEffectID;

@Getter
@RequiredArgsConstructor
public enum IdleAlarmSound
{
	PLANT_BLOOP("Plant Bloop", SoundEffectID.PICK_PLANT_BLOOP),
	PLOP("Plop", SoundEffectID.GE_INCREMENT_PLOP),
	POTION("Drink Potion", 2401),
	WATER_SPLASH("Water Splash", 2496),
	UI_BOOP("UI Boop", SoundEffectID.UI_BOOP),
	JUMP("Jump", 2461),
	TINDER("Tinder Strike", SoundEffectID.TINDER_STRIKE),
	WOODCUT("Tree Chop", SoundEffectID.TREE_CHOP),
	MINING("Mining Tink", SoundEffectID.MINING_TINK),
	LOW_ALCH("Low Alchemy", 98),
	WHIP("Whip Crack", 2249),
	BUZZER("Pillory Buzzer", 2277),
	GLASS_CHINK("Glass Chink", 2131),
	CUSTOM("Custom", -1);

	private final String name;
	private final int soundId;

	@Override
	public String toString()
	{
		return name;
	}
}
