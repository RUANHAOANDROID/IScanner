/***************************************************************************************************
 * Copyright (c) 2003-2019 青岛海信智能商用系统股份有限公司 版权所有
 **************************************************************************************************/
package com.hao.scannerlibrary.ums;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hao.scannerlibrary.IScanner;
import com.hao.scannerlibrary.ScannerCallback;
import com.ums.upos.sdk.exception.CallServiceException;
import com.ums.upos.sdk.exception.SdkException;
import com.ums.upos.sdk.scanner.OnScanListener;
import com.ums.upos.sdk.scanner.ScannerConfig;
import com.ums.upos.sdk.scanner.ScannerManager;
import com.ums.upos.sdk.system.BaseSystemManager;
import com.ums.upos.sdk.system.OnServiceStatusListener;

/**
 * ums的摄像头扫描器
 *
 * 银联商务专用扫描器
 */
public class UmsCameraScanner implements IScanner {

    private Activity mActivity;
    private ScannerManager scannerManager;
    private Bundle bundle;

    public UmsCameraScanner(Activity activity) {
        this.mActivity = activity;
        this.scannerManager = new ScannerManager();
        this.bundle = new Bundle();
    }

    /**
     * 启动扫描。
     *
     * @param callback 扫描回调方法
     */
    @Override
    public void scan(final ScannerCallback callback) {
        if (callback == null || mActivity == null) {
            return;
        }
        try {
            BaseSystemManager.getInstance().deviceServiceLogin(
                    mActivity, null, "99999998",
                    new OnServiceStatusListener() {
                        @Override
                        public void onStatus(int arg0) {
                            if (0 == arg0 || 2 == arg0 || 100 == arg0) {
                                bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, 1);
                                bundle.putBoolean(ScannerConfig.COMM_ISCONTINUOUS_SCAN, false);
                                try {
                                    scannerManager.stopScan();
                                    scannerManager.initScanner(bundle);
                                    scannerManager.startScan(60000, new OnScanListener() {
                                        @Override
                                        public void onScanResult(int i, byte[] bytes) {
                                            if (bytes != null) {
                                                callback.onSuccess(new String(bytes));
                                            }
                                        }
                                    });

                                } catch (SdkException | CallServiceException e) {
                                    e.printStackTrace();
                                    callback.onError(new Throwable(e.getMessage()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    callback.onError(new Throwable(e.getMessage()));
                                }
                            } else {
                                callback.onError(new Throwable("银联商务设备登录失败"));
                            }
                        }
                    });
        } catch (SdkException e) {
            e.printStackTrace();
            callback.onError(new Throwable(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(new Throwable(e.getMessage()));
        }

    }

    /**
     * 关闭扫描。
     */
    @Override
    public void close() {
        if (scannerManager != null) {
            try {
                scannerManager.stopScan();
            } catch (SdkException | CallServiceException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TradeLog", "银联关闭扫码异常:" + e.getMessage());
            }
        }
    }

    /**
     * 两步模式下获取扫描结果。
     *
     * @param data 包含扫描结果的数据
     * @return 扫描字符串
     */
    @Override
    public String getScanResult(Intent data) {
        return "";
    }


}
