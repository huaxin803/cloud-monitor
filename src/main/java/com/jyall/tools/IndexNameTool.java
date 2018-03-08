package com.jyall.tools;

/**
 * 用于获取索引名
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
public class IndexNameTool {

    public static String getIndex(String index){

        return index + "-" + TimeTool.getTime();
    }

}
