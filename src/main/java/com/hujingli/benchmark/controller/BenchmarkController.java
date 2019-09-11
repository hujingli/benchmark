package com.hujingli.benchmark.controller;

import com.hujingli.benchmark.service.BenchmarkService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class BenchmarkController {

    @Resource
    private BenchmarkService benchmarkService;

    /**
     * 根据sql文件,config文件,java源程序,自动进行benchmark
     * @param sqlFile sql文件
     * @param configFile config文件
     * @param javaTarGzFile java文件
     * @param className 类名
     * @param methodName 方法名
     * @return json
     */
    @RequestMapping(value="/benchmark",method= RequestMethod.POST)
    public String doBenchmark(@RequestParam("sql") MultipartFile sqlFile, @RequestParam("config") MultipartFile configFile,
                              @RequestParam("java") MultipartFile javaTarGzFile, String className, String methodName) {
        return benchmarkService.doBenchmark(sqlFile, configFile, javaTarGzFile, className, methodName);
    }

    /**
     * 根据id删除benchmark的遗留编译文件
     * @param id benchmark id
     * @return json
     */
    @RequestMapping(value = "/benchmark/{id}", method = RequestMethod.DELETE)
    public String removeBenchmark(@PathVariable(value = "id") long id) {
        return "done";
    }

}
