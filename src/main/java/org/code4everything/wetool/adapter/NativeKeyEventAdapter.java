package org.code4everything.wetool.adapter;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jnativehook.keyboard.NativeKeyEvent;

/**
 * @author pantao
 * @since 2021/1/11
 */
public class NativeKeyEventAdapter extends NativeKeyEvent {

    private NativeKeyEventAdapter(int id, int modifiers, int rawCode, int keyCode, char keyChar) {
        super(id, modifiers, rawCode, keyCode, keyChar);
    }

    public static NativeKeyEventAdapter of(int id, KeyEvent keyEvent) {
        int rawCode = parseRawCode(keyEvent.getCode());
        int keycode = parseKeyCode(keyEvent.getCode());
        return new NativeKeyEventAdapter(id, 0, rawCode, keycode, '0');
    }

    private static int parseKeyCode(KeyCode keyCode) {
        switch (keyCode) {
            case UNDEFINED:
                return VC_UNDEFINED;
            case ESCAPE:
                return VC_ESCAPE;
            case DIGIT1:
            case NUMPAD1:
                return VC_1;
            case DIGIT2:
            case NUMPAD2:
                return VC_2;
            case DIGIT3:
            case NUMPAD3:
                return VC_3;
            case DIGIT4:
            case NUMPAD4:
                return VC_4;
            case DIGIT5:
            case NUMPAD5:
                return VC_5;
            case DIGIT6:
            case NUMPAD6:
                return VC_6;
            case DIGIT7:
            case NUMPAD7:
                return VC_7;
            case DIGIT8:
            case NUMPAD8:
                return VC_8;
            case DIGIT9:
            case NUMPAD9:
                return VC_9;
            case DIGIT0:
            case NUMPAD0:
                return VC_0;
            case MINUS:
                return VC_MINUS;
            case EQUALS:
                return VC_EQUALS;
            case BACK_SPACE:
                return VC_BACKSPACE;
            case TAB:
                return VC_TAB;
            case Q:
                return VC_Q;
            case W:
                return VC_W;
            case E:
                return VC_E;
            case R:
                return VC_R;
            case T:
                return VC_T;
            case Y:
                return VC_Y;
            case U:
                return VC_U;
            case I:
                return VC_I;
            case O:
                return VC_O;
            case P:
                return VC_P;
            case OPEN_BRACKET:
                return VC_OPEN_BRACKET;
            case CLOSE_BRACKET:
                return VC_CLOSE_BRACKET;
            case ENTER:
                return VC_ENTER;
            case CONTROL:
                return VC_CONTROL;
            case A:
                return VC_A;
            case S:
                return VC_S;
            case D:
                return VC_D;
            case F:
                return VC_F;
            case G:
                return VC_G;
            case H:
                return VC_H;
            case J:
                return VC_J;
            case K:
                return VC_K;
            case L:
                return VC_L;
            case SEMICOLON:
                return VC_SEMICOLON;
            case QUOTE:
                return VC_QUOTE;
            case BACK_QUOTE:
                return VC_BACKQUOTE;
            case SHIFT:
                return VC_SHIFT;
            case BACK_SLASH:
                return VC_BACK_SLASH;
            case Z:
                return VC_Z;
            case X:
                return VC_X;
            case C:
                return VC_C;
            case V:
                return VC_V;
            case B:
                return VC_B;
            case N:
                return VC_N;
            case M:
                return VC_M;
            case COMMA:
                return VC_COMMA;
            case PERIOD:
                return VC_PERIOD;
            case SLASH:
                return VC_SLASH;
            case ALT:
                return VC_ALT;
            case SPACE:
                return VC_SPACE;
            case CAPS:
                return VC_CAPS_LOCK;
            case F1:
                return VC_F1;
            case F2:
                return VC_F2;
            case F3:
                return VC_F3;
            case F4:
                return VC_F4;
            case F5:
                return VC_F5;
            case F6:
                return VC_F6;
            case F7:
                return VC_F7;
            case F8:
                return VC_F8;
            case F9:
                return VC_F9;
            case F10:
                return VC_F10;
            case NUM_LOCK:
                return VC_NUM_LOCK;
            case SCROLL_LOCK:
                return VC_SCROLL_LOCK;
            case SEPARATOR:
                return VC_SEPARATOR;
            case F11:
                return VC_F11;
            case F12:
                return VC_F12;
            case F13:
                return VC_F13;
            case F14:
                return VC_F14;
            case F15:
                return VC_F15;
            case F16:
                return VC_F16;
            case F17:
                return VC_F17;
            case F18:
                return VC_F18;
            case F19:
                return VC_F19;
            case F20:
                return VC_F20;
            case F21:
                return VC_F21;
            case F22:
                return VC_F22;
            case F23:
                return VC_F23;
            case F24:
                return VC_F24;
            case KATAKANA:
                return VC_KATAKANA;
            case UNDERSCORE:
                return VC_UNDERSCORE;
            // case 119:
            // return VC_FURIGANA;
            case KANJI:
                return VC_KANJI;
            case HIRAGANA:
                return VC_HIRAGANA;
            // case 125:
            // return VC_YEN;
            case PRINTSCREEN:
                return VC_PRINTSCREEN;
            case PAUSE:
                return VC_PAUSE;
            case HOME:
                return VC_HOME;
            case PAGE_UP:
                return VC_PAGE_UP;
            case END:
                return VC_END;
            case PAGE_DOWN:
                return VC_PAGE_DOWN;
            case INSERT:
                return VC_INSERT;
            case DELETE:
                return VC_DELETE;
            case META:
                return VC_META;
            case CONTEXT_MENU:
                return VC_CONTEXT_MENU;
            case PREVIOUS_CANDIDATE:
                return VC_MEDIA_PREVIOUS;
            case TRACK_NEXT:
                return VC_MEDIA_NEXT;
            case MUTE:
                return VC_VOLUME_MUTE;
            // case 57377:
            // return VC_APP_CALCULATOR;
            case PLAY:
                return VC_MEDIA_PLAY;
            case STOP:
                return VC_MEDIA_STOP;
            case EJECT_TOGGLE:
                return VC_MEDIA_EJECT;
            case VOLUME_DOWN:
                return VC_VOLUME_DOWN;
            case VOLUME_UP:
                return VC_VOLUME_UP;
            // case 57394:
            // return VC_BROWSER_HOME;
            // case 57404:
            // return VC_APP_MUSIC;
            case UP:
                return VC_UP;
            case LEFT:
                return VC_LEFT;
            case CLEAR:
                return VC_CLEAR;
            case RIGHT:
                return VC_RIGHT;
            case DOWN:
                return VC_DOWN;
            case POWER:
                return VC_POWER;
            // case 57439:
            // return VC_SLEEP;
            // case 57443:
            // return VC_WAKE;
            // case 57444:
            // return VC_APP_PICTURES;
            // case 57445:
            // return VC_BROWSER_SEARCH;
            // case 57446:
            // return VC_BROWSER_FAVORITES;
            // case 57447:
            // return VC_BROWSER_REFRESH;
            // case 57448:
            // return VC_BROWSER_STOP;
            // case 57449:
            // return VC_BROWSER_FORWARD;
            // case 57450:
            // return VC_BROWSER_BACK;
            // case 57452:
            // return VC_APP_MAIL;
            // case 57453:
            // return VC_MEDIA_SELECT;
            // case 65396:
            // return VC_SUN_OPEN;
            // case 65397:
            // return VC_SUN_HELP;
            // case 65398:
            // return VC_SUN_PROPS;
            // case 65399:
            // return VC_SUN_FRONT;
            // case 65400:
            // return VC_SUN_STOP;
            // case 65401:
            // return VC_SUN_AGAIN;
            // case 65403:
            // return VC_SUN_CUT;
            // case 65404:
            // return VC_SUN_COPY;
            // case 65405:
            // return VC_SUN_INSERT;
            // case 65406:
            // return VC_SUN_FIND;
            default:
                return 0;
        }
    }

    private static int parseRawCode(KeyCode keyCode) {
        switch (keyCode) {
            case UNDEFINED:
                return java.awt.event.KeyEvent.VK_UNDEFINED;
            case ESCAPE:
                return java.awt.event.KeyEvent.VK_ESCAPE;
            case DIGIT1:
            case NUMPAD1:
                return java.awt.event.KeyEvent.VK_1;
            case DIGIT2:
            case NUMPAD2:
                return java.awt.event.KeyEvent.VK_2;
            case DIGIT3:
            case NUMPAD3:
                return java.awt.event.KeyEvent.VK_3;
            case DIGIT4:
            case NUMPAD4:
                return java.awt.event.KeyEvent.VK_4;
            case DIGIT5:
            case NUMPAD5:
                return java.awt.event.KeyEvent.VK_5;
            case DIGIT6:
            case NUMPAD6:
                return java.awt.event.KeyEvent.VK_6;
            case DIGIT7:
            case NUMPAD7:
                return java.awt.event.KeyEvent.VK_7;
            case DIGIT8:
            case NUMPAD8:
                return java.awt.event.KeyEvent.VK_8;
            case DIGIT9:
            case NUMPAD9:
                return java.awt.event.KeyEvent.VK_9;
            case DIGIT0:
            case NUMPAD0:
                return java.awt.event.KeyEvent.VK_0;
            case MINUS:
                return java.awt.event.KeyEvent.VK_MINUS;
            case EQUALS:
                return java.awt.event.KeyEvent.VK_EQUALS;
            case BACK_SPACE:
                return java.awt.event.KeyEvent.VK_BACK_SPACE;
            case TAB:
                return java.awt.event.KeyEvent.VK_TAB;
            case Q:
                return java.awt.event.KeyEvent.VK_Q;
            case W:
                return java.awt.event.KeyEvent.VK_W;
            case E:
                return java.awt.event.KeyEvent.VK_E;
            case R:
                return java.awt.event.KeyEvent.VK_R;
            case T:
                return java.awt.event.KeyEvent.VK_T;
            case Y:
                return java.awt.event.KeyEvent.VK_Y;
            case U:
                return java.awt.event.KeyEvent.VK_U;
            case I:
                return java.awt.event.KeyEvent.VK_I;
            case O:
                return java.awt.event.KeyEvent.VK_O;
            case P:
                return java.awt.event.KeyEvent.VK_P;
            case OPEN_BRACKET:
                return java.awt.event.KeyEvent.VK_OPEN_BRACKET;
            case CLOSE_BRACKET:
                return java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
            case ENTER:
                return java.awt.event.KeyEvent.VK_ENTER;
            case CONTROL:
                return java.awt.event.KeyEvent.VK_CONTROL;
            case A:
                return java.awt.event.KeyEvent.VK_A;
            case S:
                return java.awt.event.KeyEvent.VK_S;
            case D:
                return java.awt.event.KeyEvent.VK_D;
            case F:
                return java.awt.event.KeyEvent.VK_F;
            case G:
                return java.awt.event.KeyEvent.VK_G;
            case H:
                return java.awt.event.KeyEvent.VK_H;
            case J:
                return java.awt.event.KeyEvent.VK_J;
            case K:
                return java.awt.event.KeyEvent.VK_K;
            case L:
                return java.awt.event.KeyEvent.VK_L;
            case SEMICOLON:
                return java.awt.event.KeyEvent.VK_SEMICOLON;
            case QUOTE:
                return java.awt.event.KeyEvent.VK_QUOTE;
            case BACK_QUOTE:
                return java.awt.event.KeyEvent.VK_BACK_QUOTE;
            case SHIFT:
                return java.awt.event.KeyEvent.VK_SHIFT;
            case BACK_SLASH:
                return java.awt.event.KeyEvent.VK_BACK_SLASH;
            case Z:
                return java.awt.event.KeyEvent.VK_Z;
            case X:
                return java.awt.event.KeyEvent.VK_X;
            case C:
                return java.awt.event.KeyEvent.VK_C;
            case V:
                return java.awt.event.KeyEvent.VK_V;
            case B:
                return java.awt.event.KeyEvent.VK_B;
            case N:
                return java.awt.event.KeyEvent.VK_N;
            case M:
                return java.awt.event.KeyEvent.VK_M;
            case COMMA:
                return java.awt.event.KeyEvent.VK_COMMA;
            case PERIOD:
                return java.awt.event.KeyEvent.VK_PERIOD;
            case SLASH:
                return java.awt.event.KeyEvent.VK_SLASH;
            case ALT:
                return java.awt.event.KeyEvent.VK_ALT;
            case SPACE:
                return java.awt.event.KeyEvent.VK_SPACE;
            case CAPS:
                return java.awt.event.KeyEvent.VK_CAPS_LOCK;
            case F1:
                return java.awt.event.KeyEvent.VK_F1;
            case F2:
                return java.awt.event.KeyEvent.VK_F2;
            case F3:
                return java.awt.event.KeyEvent.VK_F3;
            case F4:
                return java.awt.event.KeyEvent.VK_F4;
            case F5:
                return java.awt.event.KeyEvent.VK_F5;
            case F6:
                return java.awt.event.KeyEvent.VK_F6;
            case F7:
                return java.awt.event.KeyEvent.VK_F7;
            case F8:
                return java.awt.event.KeyEvent.VK_F8;
            case F9:
                return java.awt.event.KeyEvent.VK_F9;
            case F10:
                return java.awt.event.KeyEvent.VK_F10;
            case NUM_LOCK:
                return java.awt.event.KeyEvent.VK_NUM_LOCK;
            case SCROLL_LOCK:
                return java.awt.event.KeyEvent.VK_SCROLL_LOCK;
            case SEPARATOR:
                return java.awt.event.KeyEvent.VK_SEPARATOR;
            case F11:
                return java.awt.event.KeyEvent.VK_F11;
            case F12:
                return java.awt.event.KeyEvent.VK_F12;
            case F13:
                return java.awt.event.KeyEvent.VK_F13;
            case F14:
                return java.awt.event.KeyEvent.VK_F14;
            case F15:
                return java.awt.event.KeyEvent.VK_F15;
            case F16:
                return java.awt.event.KeyEvent.VK_F16;
            case F17:
                return java.awt.event.KeyEvent.VK_F17;
            case F18:
                return java.awt.event.KeyEvent.VK_F18;
            case F19:
                return java.awt.event.KeyEvent.VK_F19;
            case F20:
                return java.awt.event.KeyEvent.VK_F20;
            case F21:
                return java.awt.event.KeyEvent.VK_F21;
            case F22:
                return java.awt.event.KeyEvent.VK_F22;
            case F23:
                return java.awt.event.KeyEvent.VK_F23;
            case F24:
                return java.awt.event.KeyEvent.VK_F24;
            case KATAKANA:
                return java.awt.event.KeyEvent.VK_KATAKANA;
            case UNDERSCORE:
                return java.awt.event.KeyEvent.VK_UNDERSCORE;
            case FULL_WIDTH:
                return java.awt.event.KeyEvent.VK_FULL_WIDTH;
            case KANJI:
                return java.awt.event.KeyEvent.VK_KANJI;
            case HIRAGANA:
                return java.awt.event.KeyEvent.VK_HIRAGANA;
            // case 125:
            // return VC_YEN;
            case PRINTSCREEN:
                return java.awt.event.KeyEvent.VK_PRINTSCREEN;
            case PAUSE:
                return java.awt.event.KeyEvent.VK_PAUSE;
            case HOME:
                return java.awt.event.KeyEvent.VK_HOME;
            case PAGE_UP:
                return java.awt.event.KeyEvent.VK_PAGE_UP;
            case END:
                return java.awt.event.KeyEvent.VK_END;
            case PAGE_DOWN:
                return java.awt.event.KeyEvent.VK_PAGE_DOWN;
            case INSERT:
                return java.awt.event.KeyEvent.VK_INSERT;
            case DELETE:
                return java.awt.event.KeyEvent.VK_DELETE;
            case META:
                return java.awt.event.KeyEvent.VK_META;
            case CONTEXT_MENU:
                return java.awt.event.KeyEvent.VK_CONTEXT_MENU;
            case PREVIOUS_CANDIDATE:
                return java.awt.event.KeyEvent.VK_PREVIOUS_CANDIDATE;
            // case TRACK_NEXT:
            // return VC_MEDIA_NEXT;
            // case MUTE:
            // return VC_VOLUME_MUTE;
            // case 57377:
            // return VC_APP_CALCULATOR;
            // case PLAY:
            // return VC_MEDIA_PLAY;
            case STOP:
                return java.awt.event.KeyEvent.VK_STOP;
            // case EJECT_TOGGLE:
            // return VC_MEDIA_EJECT;
            // case VOLUME_DOWN:
            // return VC_VOLUME_DOWN;
            // case VOLUME_UP:
            // return VC_VOLUME_UP;
            // case 57394:
            // return VC_BROWSER_HOME;
            // case 57404:
            // return VC_APP_MUSIC;
            case UP:
                return java.awt.event.KeyEvent.VK_UP;
            case LEFT:
                return java.awt.event.KeyEvent.VK_LEFT;
            case CLEAR:
                return java.awt.event.KeyEvent.VK_CLEAR;
            case RIGHT:
                return java.awt.event.KeyEvent.VK_RIGHT;
            case DOWN:
                return java.awt.event.KeyEvent.VK_DOWN;
            // case POWER:
            // return VC_POWER;
            // case 57439:
            // return VC_SLEEP;
            // case 57443:
            // return VC_WAKE;
            // case 57444:
            // return VC_APP_PICTURES;
            // case 57445:
            // return VC_BROWSER_SEARCH;
            // case 57446:
            // return VC_BROWSER_FAVORITES;
            // case 57447:
            // return VC_BROWSER_REFRESH;
            // case 57448:
            // return VC_BROWSER_STOP;
            // case 57449:
            // return VC_BROWSER_FORWARD;
            // case 57450:
            // return VC_BROWSER_BACK;
            // case 57452:
            // return VC_APP_MAIL;
            // case 57453:
            // return VC_MEDIA_SELECT;
            // case 65396:
            // return VC_SUN_OPEN;
            // case 65397:
            // return VC_SUN_HELP;
            // case 65398:
            // return VC_SUN_PROPS;
            // case 65399:
            // return VC_SUN_FRONT;
            // case 65400:
            // return VC_SUN_STOP;
            // case 65401:
            // return VC_SUN_AGAIN;
            // case 65403:
            // return VC_SUN_CUT;
            // case 65404:
            // return VC_SUN_COPY;
            // case 65405:
            // return VC_SUN_INSERT;
            // case 65406:
            // return VC_SUN_FIND;
            default:
                return 0;
        }
    }
}
