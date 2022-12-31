package com.erebus.cactus.lock;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lock.LockResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EtcdLock {

    private Client client;
    private Lock lockClient;
    private Lease leaseClient;

    private String lockKey;
    private long leaseTTL;

    private EtcdLock() {
        super();
    }

    public EtcdLock(Client client) {
        this.client = client;
        //1.准备客户端
        this.lockClient = client.getLockClient();
        this.leaseClient = client.getLeaseClient();
        this.lockKey = "/defaultLock";
        this.leaseTTL = 30L;
    }

    public LockResult tryLock(String lockName, long TTL) {
        LockResult lockResult = new LockResult(false);
        /*1.准备阶段*/
        // 记录租约ID，初始值设为 0L
        Long leaseId = 0L;
        /*2.创建租约*/
        // 创建一个租约，租约有效期为TTL，实际应用中根据具体业务确定。
        try {
            leaseId = leaseClient.grant(TTL).get().getID();
            lockResult.setLeaseId(leaseId);
        }
        catch (InterruptedException | ExecutionException e) {
            System.out.println("[error]: Create lease failed:" + e);
            lockResult.setErrorMsg("[error]: Create lease failed:" + e);
            return lockResult;
        }
        //自动续约
        StreamObserver<LeaseKeepAliveResponse> observer = new StreamObserver<LeaseKeepAliveResponse>() {
            @Override
            public void onNext(LeaseKeepAliveResponse value) {
                log.info("cluster node lease remaining ttl: {}, lease id: {}", value.getTTL(), value.getID());
            }
            @Override
            public void onError(Throwable t) {
                log.error("cluster node lease keep alive failed. exception info: ",t);
            }
            @Override
            public void onCompleted() {
                log.trace("cluster node lease completed");
            }
        };
        // 设置自动续约
        leaseClient.keepAlive(leaseId, observer);
        System.out.println("[lock]: start to lock." + Thread.currentThread().getName());
        /*3.加锁操作*/
        // 执行加锁操作，并为锁对应的Key绑定租约
        try {
            LockResponse lockResponse = lockClient.lock(ByteSequence.from(lockName, StandardCharsets.UTF_8), leaseId).get();
            if (lockResponse != null) {
                String lockPath = lockResponse.getKey().toString(StandardCharsets.UTF_8);
                lockResult.setLockPath(lockPath);
                log.info("线程：{} 加锁成功，锁路径：{}", Thread.currentThread().getName(), lockPath);
                lockResult.setLockSuccess(true);
            }
        }
        catch (InterruptedException | ExecutionException e1) {
            System.out.println("[error]: lock failed:" + e1);
            lockResult.setErrorMsg("[error]: lock failed:" + e1);
            return lockResult;
        }
        System.out.println("[lock]: lock successfully." + Thread.currentThread().getName());
        return lockResult;

    }
    public void unLock(String lockName, LockResult lockResult) {
        System.out.println("[unlock]: start to unlock." + Thread.currentThread().getName());
        // 释放锁
        lockClient.unlock(ByteSequence.from(lockName, StandardCharsets.UTF_8));
        // 删除租约
        if (lockResult.getLeaseId() != 0L) {
            leaseClient.revoke(lockResult.getLeaseId());
        }
        System.out.println("[unlock]: unlock successfully." + Thread.currentThread().getName());
    }

}
