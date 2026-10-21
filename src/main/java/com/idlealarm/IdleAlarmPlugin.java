package com.idlealarm;

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import static net.runelite.api.AnimationID.IDLE;

@Slf4j
@PluginDescriptor(
	name = "Idle Alarm",
	description = "Plays alarm while player is idle",
	tags = {"idle", "tick", "animation", "alarm"}
)
public class IdleAlarmPlugin extends Plugin implements KeyListener, MouseListener, MouseWheelListener
{
	@Inject
	private Client client;

	@Inject
	private IdleAlarmConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private IdleAlarmOverlay overlay;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	private volatile long lastAnimating;
	private long lastAlarming;

	@Provides
	IdleAlarmConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IdleAlarmConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(this);
		mouseManager.registerMouseWheelListener(this);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		keyManager.unregisterKeyListener(this);
		mouseManager.unregisterMouseListener(this);
		mouseManager.unregisterMouseWheelListener(this);
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
			if (config.isSoundEnabled() && config.getIdleAlarmVolume() > 0)
			{
				IdleAlarmSound sound = config.getIdleAlarmSound();
				int soundId = sound == IdleAlarmSound.CUSTOM
					? config.getCustomSoundId()
					: (sound != null ? sound.getSoundId() : SoundEffectID.GE_INCREMENT_PLOP);

				if (soundId > 0)
				{
					client.playSoundEffect(soundId, config.getIdleAlarmVolume());
				}
			}

			lastAlarming = now;
		}
	}

	boolean isAlarming()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return false;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null || lastAnimating == 0)
		{
			return false;
		}

		int idlePose = localPlayer.getIdlePoseAnimation();
		int pose = localPlayer.getPoseAnimation();
		int animation = localPlayer.getAnimation();

		if (!(animation == IDLE && pose == idlePose))
		{
			return false;
		}

		long now = System.currentTimeMillis();
		int alarmDelay = config.getIdleAlarmDelay();
		int maxDuration = config.getIdleAlarmDuration();

		return now - lastAnimating >= alarmDelay
			&& (maxDuration == 0 || now - lastAnimating <= alarmDelay + maxDuration);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (config.resetOnClicksAndKeystrokes())
		{
			resetDelay();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		if (config.resetOnClicksAndKeystrokes())
		{
			resetDelay();
		}
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		if (config.resetOnMouseMovement())
		{
			resetDelay();
		}
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		if (config.resetOnMouseMovement())
		{
			resetDelay();
		}
		return mouseEvent;
	}

	@Override
	public MouseWheelEvent mouseWheelMoved(MouseWheelEvent mouseWheelEvent)
	{
		if (config.resetOnClicksAndKeystrokes())
		{
			resetDelay();
		}
		return mouseWheelEvent;
	}

	private void resetDelay()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			lastAnimating = System.currentTimeMillis();
		}
	}
}
