package com.hujingli.benchmark.service.impl;

import com.hujingli.benchmark.service.BenchmarkService;
import com.hujingli.benchmark.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BenchmarkServiceImpl implements BenchmarkService {

    /**
     * 采取类似热部署的方式，即时加载jar文件并“部署”相关功能
     * @param sqlFile sql文件
     * @param configFile config文件
     * @param jarFile jar文件
     * @param className 类名
     * @param methodName 方法名
     * @return View
     */
    @Override
    public String doBenchmark(MultipartFile sqlFile, MultipartFile configFile, MultipartFile jarFile, String className, String methodName) {
        // 暂存文件
        String[] filePaths = new String[3];
        try {
            filePaths = storeFiles(sqlFile, configFile, jarFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加载jar文件
        ClassLoader jarClassLoader = null;
        try {
            jarClassLoader = loadJarFile(filePaths[2]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // 进行类加载
        Class<?> clz = null;
        try {
            clz = loadNewComeClass(jarClassLoader, className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(clz + "=============");

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
     * @param tarFile java文件
     * @return 存储后的路径数组(sql->config->java)
     * @throws IOException I/O异常
     */
    private String[] storeFiles(MultipartFile sqlFile, MultipartFile configFile, MultipartFile tarFile) throws IOException {
        String tmpFolderPath = FileUtils.getTmpFolder().getPath();
        String[] paths = new String[3];

        // 存储sql文件
        paths[0] = storeFile(sqlFile, tmpFolderPath);

        // 存储config文件
        paths[1] = storeFile(configFile, tmpFolderPath);

        // 存储tar文件
        paths[2]  = storeFile(tarFile, tmpFolderPath);

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
     * 加载tar文件
     * @param jarFilePaths jar包地址数组
     */
    private ClassLoader loadJarFile(String... jarFilePaths) throws MalformedURLException {
        return FileUtils.jarClassLoader(jarFilePaths);
    }

    /**
     * 加载逻辑主类
     * @param className 类名
     * @throws ClassNotFoundException 类
     */
    private Class<?> loadNewComeClass(ClassLoader loader, String className) throws ClassNotFoundException {
        String classNameWithOutSuffix;
        String regex = ".";
        String replacement = "\\\\";
        if (className.endsWith(".class") || className.endsWith(".java")) {
            int lastDotIdx = className.lastIndexOf(".");
            classNameWithOutSuffix = className.substring(0, lastDotIdx).replaceAll(regex, replacement);
        } else {
            classNameWithOutSuffix = className;
        }

        return loader.loadClass(classNameWithOutSuffix);
    }

}
