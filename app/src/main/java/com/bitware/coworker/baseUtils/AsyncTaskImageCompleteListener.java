package com.bitware.coworker.baseUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;


/**
 * Created by KrishnaDev on 1/10/17.
 */
public interface AsyncTaskImageCompleteListener {
    void onImageUpload(TransferObserver transferObserver, int serviceCode);
}
