package com.hujingli.benchmark.controller;

import com.hujingli.benchmark.service.BenchmarkService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@RestController
public class BenchmarkController {

    @Resource
    private BenchmarkService benchmarkService;

    /**
     * 根据sql文件,config文件,java源程序,自动进行benchmark
     * @param sqlFile
     * @param configFile
     * @param javaTarGzFile
     * @param className
     * @param methodName
     * @return
     */
    @RequestMapping(value="/benchmark",method= RequestMethod.POST)
    public String doBenchmark(@RequestParam("sql") MultipartFile sqlFile, @RequestParam("config") MultipartFile configFile,
                              @RequestParam("java") MultipartFile javaTarGzFile, String className, String methodName) {
        return benchmarkService.doBenchmark(sqlFile, configFile, javaTarGzFile, className, methodName);
    }

    /**
     * 根据id删除benchmark的遗留编译文件
     * @param id
     * @return
     */
    @RequestMapping(value = "/benchmark/{id}", method = RequestMethod.DELETE)
    public String removeBenchmark(@PathVariable(value = "id") long id) {
        return "done";
    }

}
