package com.erebus.cactus.lock;

import lombok.Data;

@Data
public class LockResult {

    private boolean isLockSuccess;
    private long leaseId;
    private String lockPath;
    private String errorMsg;

    public LockResult(){
        super();
    }

    public LockResult(boolean isLockSuccess){
        this.isLockSuccess = isLockSuccess;
    }

}
