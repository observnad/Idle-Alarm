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

import java.time.Duration;
import java.time.Instant;

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

	private Instant lastAnimating;
	private Instant lastAlarming = Instant.now();

	@Provides
	IdleAlarmConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IdleAlarmConfig.class);
	}

	@Subscribe
	public void onClientTick(ClientTick tick) {
		Player localPlayer = client.getLocalPlayer();
		int idlePose = localPlayer.getIdlePoseAnimation();
		int pose = localPlayer.getPoseAnimation();
		int animation = localPlayer.getAnimation();

		if (!(animation == IDLE && pose == idlePose)) {
			lastAnimating = Instant.now();
			return;
		}

		final Duration alarmDelay = Duration.ofMillis(config.getIdleAlarmDelay());
		final Duration alarmSpeed = Duration.ofMillis(config.getIdleAlarmSpeed());
		final int maxDuration = config.getIdleAlarmDuration();
		final Duration alarmEnd = alarmDelay.plus(Duration.ofMillis(maxDuration));

		// When player is idle and alarm should sound
		if (Instant.now().compareTo(lastAnimating.plus(alarmDelay)) >= 0
				&& Instant.now().compareTo(lastAlarming.plus(alarmSpeed)) >= 0
				&& (maxDuration == 0 || Instant.now().compareTo(lastAnimating.plus(alarmEnd)) <= 0 )) {
			// As playSoundEffect only uses the volume argument when the in-game volume isn't muted, sound effect volume
			// needs to be set to the value desired for the alarm and afterwards reset to the previous value.
			Preferences preferences = client.getPreferences();
			int previousVolume = preferences.getSoundEffectVolume();

			if (config.getIdleAlarmVolume() > 0)
			{
				preferences.setSoundEffectVolume(config.getIdleAlarmVolume());
				client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, config.getIdleAlarmVolume());
			}

			preferences.setSoundEffectVolume(previousVolume);
			lastAlarming = Instant.now();
		}
	}
}
