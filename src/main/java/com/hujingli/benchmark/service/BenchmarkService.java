package com.hujingli.benchmark.service;

import org.springframework.web.multipart.MultipartFile;

public interface BenchmarkService {

    String doBenchmark(MultipartFile sqlFile, MultipartFile configFile, MultipartFile javaTarGzFile, String className, String methodName);

}
