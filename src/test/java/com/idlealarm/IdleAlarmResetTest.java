package com.idlealarm;

import java.awt.Button;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdleAlarmResetTest
{
	private IdleAlarmPlugin plugin;
	private TestConfig config;
	private Component dummyComponent;

	private static class TestConfig implements IdleAlarmConfig
	{
		boolean resetOnMouseMovement = false;
		boolean resetOnClicksAndKeystrokes = false;

		@Override
		public boolean resetOnMouseMovement()
		{
			return resetOnMouseMovement;
		}

		@Override
		public boolean resetOnClicksAndKeystrokes()
		{
			return resetOnClicksAndKeystrokes;
		}
	}

	@Before
	public void setUp() throws Exception
	{
		plugin = new IdleAlarmPlugin();
		config = new TestConfig();
		dummyComponent = new Button();

		Client client = (Client) Proxy.newProxyInstance(
			Client.class.getClassLoader(),
			new Class<?>[]{Client.class},
			(proxy, method, args) -> {
				if ("getGameState".equals(method.getName()))
				{
					return GameState.LOGGED_IN;
				}
				return null;
			}
		);

		setField(plugin, "client", client);
		setField(plugin, "config", config);
	}

	private void setField(Object target, String fieldName, Object value) throws Exception
	{
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	private long getLastAnimating(IdleAlarmPlugin target) throws Exception
	{
		Field field = target.getClass().getDeclaredField("lastAnimating");
		field.setAccessible(true);
		return (long) field.get(target);
	}

	private void setLastAnimating(IdleAlarmPlugin target, long value) throws Exception
	{
		Field field = target.getClass().getDeclaredField("lastAnimating");
		field.setAccessible(true);
		field.set(target, value);
	}

	@Test
	public void testConfigDefaults()
	{
		IdleAlarmConfig defaultConfig = new IdleAlarmConfig() {};
		assertFalse(defaultConfig.resetOnMouseMovement());
		assertFalse(defaultConfig.resetOnClicksAndKeystrokes());
	}

	@Test
	public void testMouseMovementDisabled() throws Exception
	{
		config.resetOnMouseMovement = false;
		setLastAnimating(plugin, 1000L);

		MouseEvent event = new MouseEvent(dummyComponent, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 10, 10, 0, false);
		plugin.mouseMoved(event);

		assertEquals(1000L, getLastAnimating(plugin));
	}

	@Test
	public void testMouseMovementEnabled() throws Exception
	{
		config.resetOnMouseMovement = true;
		setLastAnimating(plugin, 1000L);

		MouseEvent event = new MouseEvent(dummyComponent, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 10, 10, 0, false);
		plugin.mouseMoved(event);

		assertTrue(getLastAnimating(plugin) > 1000L);
	}

	@Test
	public void testMouseDraggedEnabled() throws Exception
	{
		config.resetOnMouseMovement = true;
		setLastAnimating(plugin, 1000L);

		MouseEvent event = new MouseEvent(dummyComponent, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 10, 10, 0, false);
		plugin.mouseDragged(event);

		assertTrue(getLastAnimating(plugin) > 1000L);
	}

	@Test
	public void testMouseInputDisabled() throws Exception
	{
		config.resetOnClicksAndKeystrokes = false;
		setLastAnimating(plugin, 1000L);

		MouseEvent event = new MouseEvent(dummyComponent, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);
		plugin.mousePressed(event);

		assertEquals(1000L, getLastAnimating(plugin));
	}

	@Test
	public void testMouseInputEnabled() throws Exception
	{
		config.resetOnClicksAndKeystrokes = true;
		setLastAnimating(plugin, 1000L);

		MouseEvent event = new MouseEvent(dummyComponent, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);
		plugin.mousePressed(event);

		assertTrue(getLastAnimating(plugin) > 1000L);
	}

	@Test
	public void testMouseWheelInputEnabled() throws Exception
	{
		config.resetOnClicksAndKeystrokes = true;
		setLastAnimating(plugin, 1000L);

		MouseWheelEvent event = new MouseWheelEvent(dummyComponent, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, 10, 10, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1);
		plugin.mouseWheelMoved(event);

		assertTrue(getLastAnimating(plugin) > 1000L);
	}

	@Test
	public void testKeyboardInputDisabled() throws Exception
	{
		config.resetOnClicksAndKeystrokes = false;
		setLastAnimating(plugin, 1000L);

		KeyEvent event = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');
		plugin.keyPressed(event);

		assertEquals(1000L, getLastAnimating(plugin));
	}

	@Test
	public void testKeyboardInputEnabled() throws Exception
	{
		config.resetOnClicksAndKeystrokes = true;
		setLastAnimating(plugin, 1000L);

		KeyEvent event = new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');
		plugin.keyPressed(event);

		assertTrue(getLastAnimating(plugin) > 1000L);
	}
}
