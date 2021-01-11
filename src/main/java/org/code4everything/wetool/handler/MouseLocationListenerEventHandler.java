package org.code4everything.wetool.handler;

import org.code4everything.wetool.plugin.support.event.EventCenter;
import org.code4everything.wetool.plugin.support.event.handler.BaseNoMessageEventHandler;
import org.code4everything.wetool.plugin.support.event.message.MouseListenerEventMessage;
import org.jnativehook.mouse.NativeMouseEvent;

import java.awt.*;
import java.util.Date;

/**
 * @author pantao
 * @since 2021/1/11
 */
public class MouseLocationListenerEventHandler extends BaseNoMessageEventHandler {

    private int lastPosX = 0;

    private int lastPosY = 0;

    @Override
    public void handleEvent0(String s, Date date) {
        Point location = MouseInfo.getPointerInfo().getLocation();
        int posX = (int) location.getX();
        int posY = (int) location.getY();

        if (lastPosX == posX && lastPosY == posY) {
            return;
        }

        lastPosX = posX;
        lastPosY = posY;

        NativeMouseEvent event = new NativeMouseEvent(NativeMouseEvent.NATIVE_MOUSE_MOVED, 0, posX, posY, 1);
        MouseListenerEventMessage message = MouseListenerEventMessage.of(event);
        EventCenter.publishEvent(EventCenter.EVENT_MOUSE_MOTION, date, message);
    }
}
