package com.idlealarm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdleAlarmFlickerTest
{
	@Test
	public void testFlickerNames()
	{
		assertEquals("Off", IdleAlarmFlicker.OFF.toString());
		assertEquals("Whole screen", IdleAlarmFlicker.WHOLE_SCREEN.toString());
		assertEquals("UI", IdleAlarmFlicker.UI.toString());
	}
}
