package com.hujingli.benchmark.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class ReflectUtils {

    /**
     * 根据指定的类名(包名.类型)和方法名反射调用具体方法
     * @param clz 类
     * @param methodName 方法名
     * @throws IllegalAccessException illegal access
     * @throws InstantiationException instantiation
     * @throws InvocationTargetException invocation target
     */
    public static Object invokeMethod(Class<?> clz, String methodName) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object obj = clz.newInstance();

        Method[] methods = clz.getDeclaredMethods();

        for(Method method : methods) {
            if(methodName.equals(method.getName())) {
                return method.invoke(obj);
            }
        }

        return null;
    }

}
