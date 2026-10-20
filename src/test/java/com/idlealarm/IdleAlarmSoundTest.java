package com.idlealarm;

import net.runelite.api.SoundEffectID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdleAlarmSoundTest
{
	@Test
	public void testSoundIds()
	{
		assertEquals(SoundEffectID.PICK_PLANT_BLOOP, IdleAlarmSound.PLANT_BLOOP.getSoundId());
		assertEquals(SoundEffectID.GE_INCREMENT_PLOP, IdleAlarmSound.PLOP.getSoundId());
		assertEquals(2401, IdleAlarmSound.POTION.getSoundId());
		assertEquals(2496, IdleAlarmSound.WATER_SPLASH.getSoundId());
		assertEquals(SoundEffectID.UI_BOOP, IdleAlarmSound.UI_BOOP.getSoundId());
		assertEquals(2461, IdleAlarmSound.JUMP.getSoundId());
		assertEquals(SoundEffectID.TINDER_STRIKE, IdleAlarmSound.TINDER.getSoundId());
		assertEquals(SoundEffectID.TREE_CHOP, IdleAlarmSound.WOODCUT.getSoundId());
		assertEquals(SoundEffectID.MINING_TINK, IdleAlarmSound.MINING.getSoundId());
		assertEquals(98, IdleAlarmSound.LOW_ALCH.getSoundId());
		assertEquals(2249, IdleAlarmSound.WHIP.getSoundId());
		assertEquals(2277, IdleAlarmSound.BUZZER.getSoundId());
		assertEquals(2131, IdleAlarmSound.GLASS_CHINK.getSoundId());
	}

	@Test
	public void testCleanNames()
	{
		assertEquals("Plant Bloop", IdleAlarmSound.PLANT_BLOOP.toString());
		assertEquals("Plop", IdleAlarmSound.PLOP.toString());
		assertEquals("Glass Chink", IdleAlarmSound.GLASS_CHINK.toString());
		assertEquals("Custom", IdleAlarmSound.CUSTOM.toString());
	}
}
