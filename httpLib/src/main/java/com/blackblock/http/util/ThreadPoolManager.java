package com.blackblock.http.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 16:52
 * @desc 简易线程池
 */
public class ThreadPoolManager {
    private static ThreadPoolManager instance;
    //把任务添加到请求队列中
    private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    //把队列中的人物放入到线程池
    private ThreadPoolExecutor threadPoolExecutor;


    private ThreadPoolManager() {
        //核心线程数4个，最大线程数20，线程最久的存活时间15秒
        //拒绝策略（线程存活超过指定时间并被销毁时的处理事件）
        //r就是超时的线程（被丢出线程池的）
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                //r就是超时的线程（被丢出线程池的）
                try {
                    queue.put(r);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadPoolExecutor = new ThreadPoolExecutor(
                4,
                20,
                15,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4),
                rejectedExecutionHandler
        );
        //阻塞队列
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Runnable runnable = null;
                    try {
                        runnable = queue.take();//阻塞队列
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (runnable != null) {
                        threadPoolExecutor.execute(runnable);
                    }
                }
            }
        };
        threadPoolExecutor.execute(runnable);
    }

    public synchronized static ThreadPoolManager getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolManager();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable runnable) {
        if (runnable != null) {
            try {
                queue.put(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
