package com.hao.scannerlibrary;

import android.content.Intent;

/**
 * 扫描器接口。
 *
 */
public interface IScanner {

    int SCAN_REQUEST = 16088;

    /**
     * 启动扫描。
     *
     * @param callback 扫描回调方法
     */
    void scan(ScannerCallback callback);

    /**
     * 两步模式下获取扫描结果。
     *
     * @param data 包含扫描结果的数据
     * @return 扫描字符串
     */
    String getScanResult(Intent data);

    /**
     * 关闭扫描。
     */
    void close();
}
