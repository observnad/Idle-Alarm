package com.idlealarm;

import java.awt.Color;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("Idle Alarm")
public interface IdleAlarmConfig extends Config
{
	int VOLUME_MAX = SoundEffectVolume.HIGH;

	@ConfigSection(
			name = "Sound",
			description = "Configuration for sound alarm.",
			position = 4
	)
	String soundSection = "soundSection";

	@ConfigSection(
			name = "Visual",
			description = "Configuration for visual flicker alarm.",
			position = 5
	)
	String visualSection = "visualSection";

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

	@ConfigItem(
			keyName = "enablesound",
			name = "Enable sound",
			description = "Configures whether to play a sound alarm while idle.",
			section = soundSection,
			position = 1
	)
	default boolean isSoundEnabled()
	{
		return true;
	}

	@Range(
			max = VOLUME_MAX
	)
	@ConfigItem(
			keyName = "alarmvolume",
			name = "Alarm volume",
			description = "Configures the volume of the alarm. A value of 0 will disable the alarm sound.",
			section = soundSection,
			position = 2
	)
	default int getIdleAlarmVolume()
	{
		return SoundEffectVolume.MEDIUM_HIGH;
	}

	@ConfigItem(
			keyName = "sound",
			name = "Alarm sound",
			description = "Configures the sound played while idle.",
			section = soundSection,
			position = 3
	)
	default IdleAlarmSound getIdleAlarmSound()
	{
		return IdleAlarmSound.PLOP;
	}

	@ConfigItem(
			keyName = "customsoundid",
			name = "Custom sound ID",
			description = "Sound effect ID to play when Custom sound is selected.",
			section = soundSection,
			position = 4
	)
	default int getCustomSoundId()
	{
		return 2131;
	}

	@ConfigItem(
			keyName = "flicker",
			name = "Flicker effect",
			description = "Configures the visual flickering effect while idle.",
			section = visualSection,
			position = 1
	)
	default IdleAlarmFlicker getIdleAlarmFlicker()
	{
		return IdleAlarmFlicker.OFF;
	}

	@Alpha
	@ConfigItem(
			keyName = "flickercolor",
			name = "Flicker color",
			description = "Configures the color of the flicker effect.",
			section = visualSection,
			position = 2
	)
	default Color getFlickerColor()
	{
		return new Color(255, 0, 0, 70);
	}
}
