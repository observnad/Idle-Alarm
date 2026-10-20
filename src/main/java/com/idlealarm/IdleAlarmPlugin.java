package com.idlealarm;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import static net.runelite.api.AnimationID.IDLE;

@Slf4j
@PluginDescriptor(
	name = "Idle Alarm",
	description = "Plays alarm while player is idle",
	tags = {"idle", "tick", "animation", "alarm"}
)
public class IdleAlarmPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private IdleAlarmConfig config;

	private long lastAnimating;
	private long lastAlarming;

	@Provides
	IdleAlarmConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IdleAlarmConfig.class);
	}

	@Override
	protected void shutDown() throws Exception
	{
		lastAnimating = 0;
		lastAlarming = 0;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			lastAnimating = 0;
			lastAlarming = 0;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick tick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return;
		}

		int idlePose = localPlayer.getIdlePoseAnimation();
		int pose = localPlayer.getPoseAnimation();
		int animation = localPlayer.getAnimation();
		long now = System.currentTimeMillis();

		if (!(animation == IDLE && pose == idlePose))
		{
			lastAnimating = now;
			return;
		}

		if (lastAnimating == 0)
		{
			lastAnimating = now;
			return;
		}

		final int alarmDelay = config.getIdleAlarmDelay();
		final int alarmSpeed = config.getIdleAlarmSpeed();
		final int maxDuration = config.getIdleAlarmDuration();

		if (now - lastAnimating >= alarmDelay
			&& now - lastAlarming >= alarmSpeed
			&& (maxDuration == 0 || now - lastAnimating <= alarmDelay + maxDuration))
		{
			// As playSoundEffect only uses the volume argument when the in-game volume isn't muted, sound effect volume
			// needs to be set to the value desired for the alarm and afterwards reset to the previous value.
			Preferences preferences = client.getPreferences();
			int previousVolume = preferences.getSoundEffectVolume();

			if (config.getIdleAlarmVolume() > 0)
			{
				IdleAlarmSound sound = config.getIdleAlarmSound();
				int soundId = sound == IdleAlarmSound.CUSTOM
					? config.getCustomSoundId()
					: (sound != null ? sound.getSoundId() : SoundEffectID.GE_INCREMENT_PLOP);

				if (soundId > 0)
				{
					preferences.setSoundEffectVolume(config.getIdleAlarmVolume());
					client.playSoundEffect(soundId, config.getIdleAlarmVolume());
				}
			}

			preferences.setSoundEffectVolume(previousVolume);
			lastAlarming = now;
		}
	}
}
