package com.idlealarm;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("Idle Alarm")
public interface IdleAlarmConfig extends Config
{
	int VOLUME_MAX = SoundEffectVolume.HIGH;

	@ConfigItem(
			keyName = "delay",
			name = "Alarm delay",
			description = "Number of milliseconds before alarming",
			position = 1
	)
	default int getIdleAlarmDelay()
	{
		return 600;
	}

	@ConfigItem(
			keyName = "speed",
			name = "Alarm speed",
			description = "Number of milliseconds between sounds",
			position = 2
	)
	default int getIdleAlarmSpeed() { return 300; }

	@ConfigItem(
			keyName = "duration",
			name = "Max duration",
			description = "Max duration of alarm in milliseconds. 0 will play alarm endlessly",
			position = 3
	)
	default int getIdleAlarmDuration()
	{
		return 0;
	}

	@Range(
			max = VOLUME_MAX
	)
	@ConfigItem(
			keyName = "alarmvolume",
			name = "Alarm volume",
			description = "Configures the volume of the alarm. A value of 0 will disable the alarm sound.",
			position = 4
	)
	default int getIdleAlarmVolume()
	{
		return SoundEffectVolume.MEDIUM_HIGH;
	}
}
