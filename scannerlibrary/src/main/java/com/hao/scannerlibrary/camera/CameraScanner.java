package com.hao.scannerlibrary.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hao.scannerlibrary.IScanner;
import com.hao.scannerlibrary.ScannerCallback;

/**
 * 使用摄像头的扫描器。
 *
 * @author zhangsai    2019-01-18 HIPOSAPP-1002
 * @author wangyaogang 2019-02-28 HIPOSAPP-1031 优化扫描结果传值逻辑
 */
public class CameraScanner implements IScanner {

    private static final String DEFAULT_SCAN_RESULT = "";

    public static final String SCAN_RESULT_KEY = "SCAN_RESULT";

    private Activity activity;

    public CameraScanner(Activity activity) {
        this.activity = activity;
    }

    /**
     * 启动扫描。
     *
     * @param callback 扫描回调方法
     */
    @Override
    public void scan(ScannerCallback callback) {
        Intent intent = new Intent();
        intent.setClass(this.activity, MipcaActivityCapture.class);
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
                result = bundle.getString(CameraScanner.SCAN_RESULT_KEY, DEFAULT_SCAN_RESULT);
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
