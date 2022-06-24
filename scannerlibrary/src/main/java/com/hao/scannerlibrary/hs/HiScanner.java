package com.hao.scannerlibrary.hs;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;

import com.hao.scannerlibrary.IScanner;

import com.hao.scannerlibrary.ScannerCallback;
import com.histonepos.npsdk.api.IServiceManager;
import com.histonepos.npsdk.bind.Const;
import com.histonepos.npsdk.scanner.IScanEventListener;
import com.histonepos.npsdk.scanner.IScannerService;

public class HiScanner implements IScanner {

    private Activity mActivity;
    private IScannerService mScanner;
    public static IServiceManager sm;

    private Thread mScanThread;
    private boolean IS_QUIT = false;

    public HiScanner(Activity activity) {
        mActivity = activity;
        initScanner();
    }

    public void setContext(Activity activity) {
        mActivity = activity;
    }

    public static void setSm(IServiceManager sm) {
        HiScanner.sm = sm;
    }
    private void initScanner() {
        mScanner = getScanner();
        try {
            mScanner.initScanner();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private IScannerService getScanner(){
        try {
            return IScannerService.Stub.asInterface(sm.getService(Const.SCANNER));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public String getScanResult(Intent data) {
        return "";
    }

    /**
     * 关闭扫描。
     */
    @Override
    public void close() {
        if (this.mScanner != null) {
            try {
                this.mScanner.removeScanListener();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
     * 扫码线程
     */
    private class ScanThread extends Thread {

        private ScannerCallback mCallback;
        private Activity mActivity;
        private IScannerService mScanner;

        ScanThread(IScannerService scanner, Activity activity, ScannerCallback callback) {
            this.mCallback = callback;
            this.mScanner = scanner;
            this.mActivity = activity;
        }

        @Override
        public void run() {
            if (mScanner == null) {
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

                try {
                    this.mScanner.setScanListener(new IScanEventListener.Stub() {

                        @Override
                        public void onScan(int i, final String s) throws RemoteException {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mCallback != null) {
                                        mCallback.onSuccess(s);
                                    }
                                }
                            });
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                    IS_QUIT = true;
                    this.mScanner = null;
                    this.mCallback = null;
                    this.mActivity = null;
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
        }
    }

}
