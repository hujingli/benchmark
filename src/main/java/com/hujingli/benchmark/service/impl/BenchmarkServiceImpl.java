package com.hujingli.benchmark.service.impl;

import com.hujingli.benchmark.service.BenchmarkService;
import com.hujingli.benchmark.utils.ClassUtils;
import com.hujingli.benchmark.utils.FileUtils;
import com.hujingli.benchmark.utils.RuntimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

    @Override
    public String doBenchmark(MultipartFile sqlFile, MultipartFile configFile, MultipartFile javaTarGzFile, String className, String methodName) {
        // 暂存文件
        String[] filePaths = new String[3];
        String classPath = null;
        try {
            classPath = new File(ResourceUtils.getURL("classpath:").getPath()).getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            filePaths = storeFiles(sqlFile, configFile, javaTarGzFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将javaTarGzFile解压至classpath
        try {
            decompressJava(filePaths[2], classPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 运行时编译用户传入的java文件(根据入口类编译)
        runtimeCompile(classPath, className);

        // 进行类加载
        try {
            loadNewComeClass(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 根据sqlFile文件建库建表插入数据

        // 根据configFile初始化线程池以及benchmark参数

        // 开始进行数据库压测(记录数据)

        // 记录此次压测记录，记录解压后的文件地址

        // 删除暂存文件
        return null;
    }

    /**
     * 存储sql,config,java文件至指定文件夹下
     * @param sqlFile sql文件
     * @param configFile config文件
     * @param javaTarGzFile java文件
     * @return 存储后的路径数组(sql->config->java)
     * @throws IOException I/O异常
     */
    private String[] storeFiles(MultipartFile sqlFile, MultipartFile configFile, MultipartFile javaTarGzFile) throws IOException {
        String tmpFolderPath = FileUtils.getTmpFolderPath();
        String[] paths = new String[3];

        // 存储sql文件
        paths[0] = storeFile(sqlFile, tmpFolderPath);

        // 存储config文件
        paths[1] = storeFile(configFile, tmpFolderPath);

        // 存储javaTarGz文件
        paths[2]  = storeFile(javaTarGzFile, tmpFolderPath);

        return paths;
    }

    /**
     * 存储文件至指定文件夹下
     * @param file multipart file
     * @param basePath 文件夹地址
     * @return 存储后路径
     * @throws IOException I/O异常
     */
    private String storeFile(MultipartFile file, String basePath) throws IOException {
        if (file.isEmpty()) {
            throw  new FileNotFoundException("文件不存在");
        }

        String fileName = file.getOriginalFilename();
        Path dest = Paths.get(basePath, fileName);
        file.transferTo(dest);

        return dest.toString();
    }

    /**
     * 将java代码解压至classpath下
     * @param filePath java代码压缩文件地址
     * @param classPath classpath
     * @throws IOException I/O异常
     */
    private void decompressJava(String filePath, String classPath) throws IOException {
        FileUtils.decompress(filePath, classPath);
    }

    /**
     * 运行时编译java文件
     * @param classPath classpath
     * @param className 入口文件名称(包名.类名)
     */
    private void runtimeCompile(String classPath, String className) {
        String classNameWithoutSuffix = null;
        String regex = ".";
        String replacement = "\\\\";
        if (className.endsWith(".class") || className.endsWith(".java")) {
            int lastDotIdx = className.lastIndexOf(".");
            classNameWithoutSuffix = className.substring(0, lastDotIdx).replaceAll(regex, replacement);
        }

        String filePath = Paths.get(classPath, classNameWithoutSuffix).toString();

        RuntimeUtils.compile(filePath);
    }

    /**
     * 加载逻辑主类
     * @param className 类名
     * @throws IOException I/O异常
     */
    private void loadNewComeClass(String className) throws IOException {
        String classNameWithOutSuffix;
        String regex = ".";
        String replacement = "\\\\";
        if (className.endsWith(".class") || className.endsWith(".java")) {
            int lastDotIdx = className.lastIndexOf(".");
            classNameWithOutSuffix = className.substring(0, lastDotIdx).replaceAll(regex, replacement);
        } else {
            classNameWithOutSuffix = className;
        }
        int lastDotIdx = className.lastIndexOf(".");

        String packageName = classNameWithOutSuffix.substring(0, lastDotIdx).replaceAll(regex, replacement);

        ClassUtils.scanByPackage(packageName);
    }

}
