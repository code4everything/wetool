package org.code4everything.wetool.listener;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.code4everything.wetool.plugin.support.event.EventCenter;
import org.code4everything.wetool.plugin.support.event.message.KeyboardListenerEventMessage;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * @author pantao
 * @since 2020/11/26
 */
@Slf4j
public class KeyboardListener implements NativeKeyListener {

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        log.debug("key typed: {}", NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        KeyboardListenerEventMessage message = KeyboardListenerEventMessage.of(e);
        EventCenter.publishEvent(EventCenter.EVENT_KEYBOARD_PRESSED, DateUtil.date(), message);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        KeyboardListenerEventMessage message = KeyboardListenerEventMessage.of(e);
        EventCenter.publishEvent(EventCenter.EVENT_KEYBOARD_RELEASED, DateUtil.date(), message);
    }
}
