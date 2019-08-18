package com.hujingli.benchmark.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.nio.file.FileSystemException;
import java.util.zip.GZIPInputStream;

/**
 * 文件处理工具类
 */
public class FileUtils {

    /**
     * 解压缩 tar.gz 文件至指定目录
     * @param src tar.gz文件
     * @param dest 目标文件夹
     * @throws IOException I/O异常
     */
    public static void decompress(String src, String dest) throws IOException {
        File file = new File(src);
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new GZIPInputStream(
                new BufferedInputStream(new FileInputStream(file))),
                1024 * 2)) {

            createDirectory(dest, null);//创建输出目录

            ArchiveEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {//是目录
                    entry.getName();
                    createDirectory(dest, entry.getName());//创建空目录
                } else {//是文件
                    File tmpFile = new File(dest + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/", null);//创建输出目录
                    try (OutputStream out = new FileOutputStream(tmpFile)) {
                        int length;

                        byte[] b = new byte[2048];

                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建目录
     * @param outputDir 输出文件夹
     * @param subDir 子文件夹
     */
    private static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        if (!(subDir == null || subDir.trim().equals(""))) {//子目录不为空
            file = new File(outputDir + "/" + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                boolean mkParentDirs = file.getParentFile().mkdirs();
                if (!mkParentDirs) {
                    System.out.println("log1");
                }
            }
            boolean mkCurDirs = file.mkdirs();
            if (!mkCurDirs) {
                System.out.println("log2");
            }
        }
    }

    /**
     * 获取临时文件目录
     * @return 文件目录地址
     * @throws FileSystemException 无法创建文件夹
     */
    public static String getTmpFolderPath() throws FileSystemException {
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "/tmp";
        File tmpFolder = new File(path);
        if (!tmpFolder.exists()) {
            boolean mkTmpFolder = tmpFolder.mkdirs();
            if (!mkTmpFolder) {
                throw new FileSystemException("无法创建临时文件目录");
            }
        }

        return tmpFolder.getPath();
    }

}
