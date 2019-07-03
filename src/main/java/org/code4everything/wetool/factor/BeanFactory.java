package org.code4everything.wetool.factor;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存打开的窗口控制器对象
 *
 * @author pantao
 * @since 2018/3/31
 */
@UtilityClass
public class BeanFactory {

    private static final Map<Class<?>, Object> BEANS = new HashMap<>(16);

    public static <T> void register(T bean) {
        BEANS.put(bean.getClass(), bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return (T) BEANS.get(clazz);
    }
}