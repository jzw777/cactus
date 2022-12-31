package com.erebus.cactus.lock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.etcd.jetcd.Client;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EtcdLockTest {

    static EtcdLock lock = new EtcdLock(Client.builder().endpoints("http://localhost:2379").build());

    public static void main(String[] args) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(logger -> logger.setLevel(Level.INFO));

        //Client client = Client.builder().endpoints("http://localhost:2379").build();


        // 模拟分布式场景下，多个进程 “抢锁”
        for (int i = 0; i < 1; i++)
        {
            new EtcdLockTest.MyThread().start();
        }
    }

    public static class MyThread extends Thread
    {
        @Override
        public void run()
        {
            String lockName = "/lock/mylock";
            // 1. 加锁
            LockResult lockResult = lock.tryLock(lockName, 30);

            // 2. 执行业务
            if (lockResult.isLockSuccess())
            {
                // 获得锁后，执行业务，用sleep方法模拟.
                try
                {
                    Thread.sleep(50000);
                    System.out.println("hello "+ Thread.currentThread().getName());
                    System.out.println("hello "+ Thread.currentThread().getId());
                }
                catch (InterruptedException e)
                {
                    System.out.println("[error]:" + e);
                }
            }

            // 3. 解锁
            lock.unLock(lockName, lockResult);
        }
    }
}
