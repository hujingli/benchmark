package com.hujingli.benchmark.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理工具类
 */
public class FileUtils {

    /**
     * 获取临时文件目录
     * @return 文件目录地址
     * @throws FileSystemException 无法创建文件夹
     */
    public static File getTmpFolder() throws FileSystemException {
        File dir = new File("src/main/resources/jars/"+ UUID.randomUUID().toString());

        System.out.println(dir.getPath());

        if (!dir.exists()) {
            boolean isMkDirs = dir.mkdirs();
            if (!isMkDirs) {
                throw new FileSystemException("无法创建临时目录");
            }
        }

        return dir;
    }

    /**
     * 创建jar类加载器
     * @param jarPaths jar包地址数组
     * @return 类加载器
     * @throws MalformedURLException jarFile转换URI错误
     */
    public static ClassLoader jarClassLoader(String... jarPaths) throws MalformedURLException {
        List<URL> jarsToLoad = new ArrayList<URL>();
        for (String path : jarPaths) {
            File jarFile = new File(path);
            jarsToLoad.add(jarFile.toURI().toURL());
        }

        URL[] urls = new URL[jarsToLoad.size()];
        jarsToLoad.toArray(urls);

        return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

}
