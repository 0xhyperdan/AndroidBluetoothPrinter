package com.codingapi.android.library.printer.threads;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iCong
 */
public class ThreadPool {

    private static ThreadPool threadPool;
    /**
     * java线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 最大线程数
     */
    private final static int MAX_POOL_COUNTS = 20;

    /**
     * 线程存活时间
     */
    private final static long ALIVETIME = 200L;

    /**
     * 核心线程数
     */
    private final static int CORE_POOL_SIZE = 20;

    /**
     * 线程池缓存队列
     */
    private static BlockingQueue<Runnable> mWorkQueue = new ArrayBlockingQueue<>(CORE_POOL_SIZE);

    private ThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder(ThreadPool.class.getSimpleName());
        threadPoolExecutor =
            new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_COUNTS, ALIVETIME, TimeUnit.SECONDS,
                mWorkQueue, threadFactory);
    }

    public static ThreadPool getInstance() {
        if (threadPool == null) {
            threadPool = new ThreadPool();
        }
        return threadPool;
    }

    public void addTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("addTask(Runnable runnable)传入参数为空");
        }
        if (threadPoolExecutor != null && threadPoolExecutor.getActiveCount() < MAX_POOL_COUNTS) {
            threadPoolExecutor.execute(runnable);
        }
    }

    public void stopThreadPool() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
            threadPoolExecutor = null;
        }
        if (mWorkQueue != null) {
            mWorkQueue.clear();
            mWorkQueue = null;
        }
    }
}
