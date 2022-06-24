package com.hao.scannerlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 使用扫码枪等的扫描器。
 *
 */
public class StdScanner implements IScanner {

    private static final String DEFAULT_SCAN_RESULT = "";

    public static final String SCAN_RESULT_KEY = "SCAN_RESULT";

    private Activity activity;
    /**
     * TODO 目前需要自己实现一个扫描页面
     */
    private Class scannerActivity;

    public StdScanner(Activity activity,Class scannerActivity) {
        this.activity = activity;
        this.scannerActivity =scannerActivity;
    }

    /**
     * 启动扫描。
     *
     * @param callback 扫描回调方法
     */
    @Override
    public void scan(ScannerCallback callback) {
        Intent intent = new Intent();
        intent.setClass(this.activity, scannerActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.activity.startActivityForResult(intent, IScanner.SCAN_REQUEST);
    }

    /**
     * 两步模式下获取扫描结果。
     *
     * @param data 包含扫描结果的数据
     * @return 扫描字符串
     */
    @Override
    public String getScanResult(Intent data) {
        String result = DEFAULT_SCAN_RESULT;
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                result = bundle.getString(StdScanner.SCAN_RESULT_KEY, DEFAULT_SCAN_RESULT);
            }
        }
        return result;
    }

    /**
     * 关闭扫描。
     */
    @Override
    public void close() {
        // 使用摄像头时无需关闭。
    }
}
