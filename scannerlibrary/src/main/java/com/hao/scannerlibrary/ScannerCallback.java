package com.hao.scannerlibrary;

/**
 * 扫描器回调接口。
 *
 */
public interface ScannerCallback {
    /**
     * 成功时调用的方法。
     *
     * @param s 扫描字符串
     */
    void onSuccess(String s);

    /**
     * 扫描过程中发生异常时调用的方法。
     *
     * @param t 扫描异常
     */
    void onError(Throwable t);
}
