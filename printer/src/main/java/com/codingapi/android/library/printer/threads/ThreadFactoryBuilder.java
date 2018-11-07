package com.codingapi.android.library.printer.threads;

import android.support.annotation.NonNull;
import java.util.concurrent.ThreadFactory;

/**
 * Created by iCong
 */
public class ThreadFactoryBuilder implements ThreadFactory {

    private String name;

    public ThreadFactoryBuilder(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        Thread thread = new Thread(runnable, name);
        thread.setName("ThreadFactoryBuilder_" + name);
        return thread;
    }
}
