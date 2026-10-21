package com.idlealarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdleAlarmFlicker
{
	OFF("Off"),
	WHOLE_SCREEN("Whole screen"),
	UI("UI");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
