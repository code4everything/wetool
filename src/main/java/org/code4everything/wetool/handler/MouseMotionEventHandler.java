package org.code4everything.wetool.handler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.swing.ScreenUtil;
import org.code4everything.wetool.plugin.support.event.EventCenter;
import org.code4everything.wetool.plugin.support.event.handler.BaseMouseEventHandler;
import org.code4everything.wetool.plugin.support.event.message.MouseCornerEventMessage;
import org.code4everything.wetool.plugin.support.event.message.MouseListenerEventMessage;
import org.jnativehook.mouse.NativeMouseEvent;

import java.util.Date;
import java.util.Objects;

/**
 * @author pantao
 * @since 2020/11/28
 */
public class MouseMotionEventHandler extends BaseMouseEventHandler {

    private static final int HEIGHT = ScreenUtil.getHeight() - 2;

    private static final int WIDTH = ScreenUtil.getWidth() - 2;

    private static final int SECOND = 1000;

    private int lastPosX = -1;

    private int lastPosY = -1;

    private long lastEventTimestamp = 0;

    @Override
    public void handleEvent0(String s, Date date, MouseListenerEventMessage mouseListenerEventMessage) {
        DateTime now = DateUtil.date();
        long timestamp = now.getTime();
        if (timestamp - lastEventTimestamp < SECOND) {
            // 一秒钟之内不重复发布事件
            return;
        }

        NativeMouseEvent nativeMouseEvent = mouseListenerEventMessage.getMouseEvent();
        int posX = Math.max(0, nativeMouseEvent.getX());
        int posY = Math.max(0, nativeMouseEvent.getY());

        if (lastPosX == posX && lastPosY == posY) {
            return;
        }

        lastPosX = posX;
        lastPosY = posY;

        MouseCornerEventMessage message = null;
        if (posX == 0 && posY == 0) {
            message = MouseCornerEventMessage.of(MouseCornerEventMessage.LocationTypeEnum.LEFT_TOP, posX, posY);
        } else if (posX == 0 && posY >= HEIGHT) {
            message = MouseCornerEventMessage.of(MouseCornerEventMessage.LocationTypeEnum.LEFT_BOTTOM, posX, posY);
        } else if (posY == 0 && posX >= WIDTH) {
            message = MouseCornerEventMessage.of(MouseCornerEventMessage.LocationTypeEnum.RIGHT_TOP, posX, posY);
        } else if (posX >= WIDTH && posY >= HEIGHT) {
            message = MouseCornerEventMessage.of(MouseCornerEventMessage.LocationTypeEnum.RIGHT_BOTTOM, posX, posY);
        }

        if (Objects.nonNull(message)) {
            EventCenter.publishEvent(EventCenter.EVENT_MOUSE_CORNER_TRIGGER, now, message);
            lastEventTimestamp = timestamp;
        }
    }
}
