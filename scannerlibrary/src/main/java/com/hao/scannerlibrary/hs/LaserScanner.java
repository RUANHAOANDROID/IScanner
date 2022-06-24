package com.hao.scannerlibrary.hs;

import android.app.Activity;
import android.content.Intent;

import com.hao.scannerlibrary.IScanner;
import com.hao.scannerlibrary.ScannerCallback;
import com.hisense.pos.scanner.Scanner;
import com.hisense.pos.system.Sys;

/**
 * HI98 激光扫描器。
 */
public class LaserScanner implements IScanner {

    private static boolean IS_QUIT = false;

    private Activity mActivity;
    private Thread mScanThread;
    private Scanner mScanner;


    public LaserScanner(Activity activity) {
        this.mScanner = new Scanner();
        this.mActivity = activity;
    }

    /**
     * 启动扫描。
     *
     * @param callback 扫描回调方法
     */
    @Override
    public void scan(final ScannerCallback callback) {
        if (callback == null) {
            return;
        }

        IS_QUIT = false;

        mScanThread = new ScanThread(mScanner, mActivity, callback);
        mScanThread.start();
    }

    /**
     * 关闭扫描。
     */
    @Override
    public void close() {
        if (this.mScanner != null) {
            this.mScanner.Scanner_Close();
        }
        IS_QUIT = true;
        if (mScanThread != null) {
            IS_QUIT = true;
            try {
                mScanThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mScanThread = null;
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

    /**
     * 扫码线程
     */
    private static class ScanThread extends Thread {

        private ScannerCallback mCallback;
        private Activity mActivity;
        private Scanner mScanner;
        private Sys mSys;
        private byte[] mBuffer;
        private long beginTime;

        ScanThread(Scanner scanner, Activity activity, ScannerCallback callback) {
            this.mCallback = callback;
            this.mScanner = scanner;
            this.mActivity = activity;
            this.mSys = new Sys(activity);
            this.mBuffer = new byte[200];
            this.beginTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            int state = mScanner.Scanner_Open();
            if (state != mScanner.SCANNER_OK) {
                this.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onError(new Exception("无法打开扫描模块"));
                    }
                });
                IS_QUIT = true;
            }

            while (true) {
                if (IS_QUIT) {
                    break;
                }

                int len = mScanner.Scanner_ReadData(mBuffer, 100);
                if (len > 0) {
                    // 添加响声
                    this.mSys.Sys_Beep(3, 1);

                    this.mScanner.Scanner_Close();

                    final byte[] code = new byte[len];
                    System.arraycopy(mBuffer, 0, code, 0, len);

                    this.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallback != null) {
                                mCallback.onSuccess(new String(code));
                            }
                        }
                    });
                }

                // 超出 1 分钟后自动关闭
                if (System.currentTimeMillis() - beginTime > 60 * 1000) {
                    break;
                }

                try {
                    sleep(300);
                } catch (InterruptedException ex) {

                }
            }

            IS_QUIT = true;

            this.mScanner = null;
            this.mCallback = null;
            this.mActivity = null;
            this.mSys = null;
        }
    }
}
