package org.xiaowu.xiaowu.spring.spring.util;

import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.List;

/**
 * @author 小五
 */
public class RecursiveFileUtils{

    public static List<String> recursiveFiles(String path,List<String> paths){
        // 创建 File对象
        File file = new File(path);
        // 取 文件/文件夹
        File files[] = file.listFiles();       
        // 对象为空 直接返回
        if(files == null){
            return null;
        }               
        // 目录下文件
        if(files.length == 0){
            System.out.println(path + "该文件夹下没有文件");
            return null;
        }       
        // 存在文件 遍历 判断
        for (File f : files) {           
            // 判断是否为 文件夹
            if(f.isDirectory()){               
                // 为 文件夹继续遍历
                recursiveFiles(f.getAbsolutePath(),paths);
            // 判断是否为 文件
            } else if(f.isFile()){
                String absolutePath = StrUtil.replace(f.getAbsolutePath(),"\\",".");
                paths.add(absolutePath);
            }
        }
        return paths;
    }   
}