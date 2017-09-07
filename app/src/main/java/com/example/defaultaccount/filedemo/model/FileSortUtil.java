package com.example.defaultaccount.filedemo.model;


import java.io.File;
import java.util.List;

import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

/**
 * Created by DefaultAccount on 2017/9/4.
 */

public class FileSortUtil {
    public static List<File> sortByTime(List<File> files){
        return Stream.of(files).sorted((file, t1) -> {
            Long fileLastModified = file.lastModified();
            Long t1LastModified = t1.lastModified();
           return t1LastModified.compareTo(fileLastModified);
        }).collect(Collectors.toList());
    }
    public static List<File> sortByName(List<File> files){
        return Stream.of(files).sorted((file, t1) -> file.getName().compareTo(t1.getName()))
                .collect(Collectors.toList());
    }
}
