package com.idlealarm;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.ClientTick;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

	@Test
	public void testSoundPlayedDirectlyWithoutModifyingPreferences() throws Exception
	{
		IdleAlarmPlugin plugin = new IdleAlarmPlugin();
		AtomicInteger playedSoundId = new AtomicInteger(-1);
		AtomicInteger playedVolume = new AtomicInteger(-1);
		AtomicBoolean preferencesAccessed = new AtomicBoolean(false);

		Client client = createMockClient(createMockPlayer(), playedSoundId, playedVolume, preferencesAccessed);
		IdleAlarmConfig config = new IdleAlarmConfig()
		{
			@Override
			public boolean isSoundEnabled()
			{
				return true;
			}

			@Override
			public int getIdleAlarmVolume()
			{
				return 100;
			}

			@Override
			public IdleAlarmSound getIdleAlarmSound()
			{
				return IdleAlarmSound.UI_BOOP;
			}
		};

		setField(plugin, "client", client);
		setField(plugin, "config", config);
		setField(plugin, "lastAnimating", 1L);
		setField(plugin, "lastAlarming", 0L);

		plugin.onClientTick(new ClientTick());

		assertEquals(SoundEffectID.UI_BOOP, playedSoundId.get());
		assertEquals(100, playedVolume.get());
		assertFalse(preferencesAccessed.get());
	}

	@Test
	public void testCustomSoundPlayed() throws Exception
	{
		IdleAlarmPlugin plugin = new IdleAlarmPlugin();
		AtomicInteger playedSoundId = new AtomicInteger(-1);
		AtomicInteger playedVolume = new AtomicInteger(-1);
		AtomicBoolean preferencesAccessed = new AtomicBoolean(false);

		Client client = createMockClient(createMockPlayer(), playedSoundId, playedVolume, preferencesAccessed);
		IdleAlarmConfig config = new IdleAlarmConfig()
		{
			@Override
			public boolean isSoundEnabled()
			{
				return true;
			}

			@Override
			public int getIdleAlarmVolume()
			{
				return 80;
			}

			@Override
			public IdleAlarmSound getIdleAlarmSound()
			{
				return IdleAlarmSound.CUSTOM;
			}

			@Override
			public int getCustomSoundId()
			{
				return 1234;
			}
		};

		setField(plugin, "client", client);
		setField(plugin, "config", config);
		setField(plugin, "lastAnimating", 1L);
		setField(plugin, "lastAlarming", 0L);

		plugin.onClientTick(new ClientTick());

		assertEquals(1234, playedSoundId.get());
		assertEquals(80, playedVolume.get());
		assertFalse(preferencesAccessed.get());
	}

	private Client createMockClient(Player player, AtomicInteger playedSoundId, AtomicInteger playedVolume, AtomicBoolean preferencesAccessed)
	{
		return (Client) Proxy.newProxyInstance(
			Client.class.getClassLoader(),
			new Class<?>[]{Client.class},
			(proxy, method, args) -> {
				switch (method.getName())
				{
					case "getGameState":
						return GameState.LOGGED_IN;
					case "getLocalPlayer":
						return player;
					case "playSoundEffect":
						playedSoundId.set((int) args[0]);
						playedVolume.set((int) args[1]);
						return null;
					case "getPreferences":
						preferencesAccessed.set(true);
						return null;
					default:
						return null;
				}
			}
		);
	}

	private Player createMockPlayer()
	{
		return (Player) Proxy.newProxyInstance(
			Player.class.getClassLoader(),
			new Class<?>[]{Player.class},
			(proxy, method, args) -> {
				switch (method.getName())
				{
					case "getAnimation":
					case "getIdlePoseAnimation":
					case "getPoseAnimation":
						return AnimationID.IDLE;
					default:
						return null;
				}
			}
		);
	}

	private void setField(Object target, String fieldName, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}
