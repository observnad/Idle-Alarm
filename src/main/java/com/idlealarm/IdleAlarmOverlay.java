package com.idlealarm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@Singleton
public class IdleAlarmOverlay extends Overlay
{
	private final Client client;
	private final IdleAlarmPlugin plugin;
	private final IdleAlarmConfig config;

	@Inject
	private IdleAlarmOverlay(Client client, IdleAlarmPlugin plugin, IdleAlarmConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		IdleAlarmFlicker flicker = config.getIdleAlarmFlicker();
		if (flicker == IdleAlarmFlicker.OFF
			|| client.getGameState() != GameState.LOGGED_IN
			|| !plugin.isAlarming())
		{
			return null;
		}

		if (client.getGameCycle() % 40 >= 20)
		{
			return null;
		}

		Color color = config.getFlickerColor();
		if (color == null || color.getAlpha() == 0)
		{
			return null;
		}

		graphics.setColor(color);

		if (flicker == IdleAlarmFlicker.WHOLE_SCREEN)
		{
			graphics.fillRect(0, 0, client.getCanvasWidth(), client.getCanvasHeight());
		}
		else if (flicker == IdleAlarmFlicker.UI)
		{
			renderUi(graphics);
		}

		return null;
	}

	private void renderUi(Graphics2D graphics)
	{
		if (!renderWidgetOval(graphics, ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_MINIMAP_DRAW_AREA)
			&& !renderWidgetOval(graphics, ComponentID.RESIZABLE_VIEWPORT_MINIMAP_DRAW_AREA))
		{
			renderWidgetOval(graphics, ComponentID.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);
		}

		if (!renderWidget(graphics, ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_CHATBOX_PARENT)
			&& !renderWidget(graphics, ComponentID.RESIZABLE_VIEWPORT_CHATBOX_PARENT))
		{
			renderWidget(graphics, ComponentID.CHATBOX_PARENT);
		}

		if (renderWidget(graphics, ComponentID.RESIZABLE_VIEWPORT_INVENTORY_PARENT))
		{
			return;
		}

		boolean modernRendered = renderWidget(graphics, ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_PARENT);
		if (renderWidget(graphics, InterfaceID.ToplevelPreEoc.SIDE_STATIC_LAYER))
		{
			modernRendered = true;
		}
		if (renderWidget(graphics, InterfaceID.ToplevelPreEoc.SIDE_MOVABLE_LAYER))
		{
			modernRendered = true;
		}

		if (!modernRendered)
		{
			if (!renderWidget(graphics, InterfaceID.Toplevel.SIDE))
			{
				renderWidget(graphics, ComponentID.FIXED_VIEWPORT_INTERFACE_CONTAINER);
			}
		}
	}

	private boolean renderWidgetOval(Graphics2D graphics, int componentId)
	{
		Widget widget = client.getWidget(componentId);
		if (widget == null || widget.isHidden())
		{
			return false;
		}

		Rectangle bounds = widget.getBounds();
		if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
		{
			return false;
		}

		graphics.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
		return true;
	}

	private boolean renderWidget(Graphics2D graphics, int componentId)
	{
		Widget widget = client.getWidget(componentId);
		if (widget == null || widget.isHidden())
		{
			return false;
		}

		Rectangle bounds = widget.getBounds();
		if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
		{
			return false;
		}

		graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		return true;
	}
}
