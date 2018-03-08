package com.jyall.tools;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * @autor: Faizel Lannister(褚福影)
 * @date: 2017/8/9 8:53
 * @version:1.0.0
 * @jvm-version: jdk 1.8
 * @Email: chufuying3@163.com
 */
public class ThreadPool {
    /**
     * 初始化线程池
     * */
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR =

            new ThreadPoolExecutor(20, 50, 20, TimeUnit.SECONDS,

                    new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    /** 返回pool **/
    public static ThreadPoolExecutor getThreadPool(){

        return THREAD_POOL_EXECUTOR;
    }
}
