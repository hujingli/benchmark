package com.hujingli.benchmark.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

    private static Set<Class<?>> CLASS_SET = null;

    /**
     * 获取类加载器
     * @return 当前线程的类加载器
     */
    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 扫描获取包下的所有类
     * @param packageName 包名(例如：org.apache.tools)
     */
    public static void scanByPackage(String packageName) throws IOException {
        Set<Class<?>> classSet = new HashSet<>();
        // TODO(cyvan): getResources扫描classpath下的类，需要找一方法可以扫描指定目录下的类
        Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null) {
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) { // 文件
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    addClass(classSet, packagePath, packageName);
                } else if ("jar".equals(protocol)) { // jar包
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    if (jarURLConnection != null) {
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if (jarFile != null) {
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while (jarEntries.hasMoreElements()) {
                                JarEntry jarEntry = jarEntries.nextElement();
                                String jarEntryName = jarEntry.getName();
                                if (".class".equals(jarEntryName)) {
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                            .replaceAll("/", ".");
                                    doAddClass(classSet, className);
                                }
                            }
                        }
                    }
                }
            }
        }

        CLASS_SET = classSet;
    }

    /**
     * 将包下的class文件加入到Set集合中
     * @param classSet Class集合
     * @param packagePath 包路径
     * @param packageName 包名
     */
    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
//        File[] files = new File(packagePath).listFiles(() -> {
//            @Override
//            public boolean accept(File file) {
//                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
//            }
//        });
        if (files == null) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (!className.isEmpty()) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else {
                String subPackagePath = fileName;
                if (!subPackagePath.isEmpty()) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (!subPackageName.isEmpty()) {
                    subPackageName = packageName + "." + subPackageName;
                }

                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className);
        classSet.add(cls);
    }

    /**
     * 加载类
     * @param className 类名(包名.类名)
     * @return Class对象
     */
    private static Class<?> loadClass(String className) {
        Class<?> clz = null;
        try {
            clz = Class.forName(className, false, getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clz;
    }

    /**
     * 根据类名(包名.类名)获取类的Class对象
     * @param className 类名(包名.类名)
     * @return Class对象
     */
    static Class<?> getByName(String className) {
        Optional<Class<?>> t = CLASS_SET.stream().filter(clz -> className.equals(clz.getName())).findFirst();
        return t.orElse(null);
    }

}
