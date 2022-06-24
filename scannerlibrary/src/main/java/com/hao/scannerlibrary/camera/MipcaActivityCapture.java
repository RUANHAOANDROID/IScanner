/***************************************************************************************************
 * Copyright (c) 2017 青岛海信智能商用系统股份有限公司 版权所有
 **************************************************************************************************/
package com.hao.scannerlibrary.camera;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.hao.scannerlibrary.R;
import com.hao.scannerlibrary.camera.CameraScanner;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 * @author wangyaogang 2019-01-11 HIPOSAPP-938 优化商品条形码及二维码扫描速度
 * @author wangyaogang 2019-02-28 HIPOSAPP-1031 优化扫描结果传值逻辑
 * @author wangyaogang 2019-03-01 HIPOSAPP-1034 更新扫码库，支持ITF格式一维条码
 * @author wangyaogang 2019-06-13 HIPOSAPP-1167 优化摄像头使用体验,打开摄像头时,若环境过暗,则自动打开闪光灯
 * @author wangyaogang 2019-06-13 HIPOSAPP-1168 优化摄像头扫描识别速度,仅识别高频格式
 */
public class MipcaActivityCapture extends AppCompatActivity implements QRCodeView.Delegate {
    private ZXingView mQRCodeView;

    private static final String TAG  = "MipcaActivityCapture";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);

        this.findViewById(R.id.btnGoBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFormat();

        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
    }

    /**
     * 设置识别格式
     */
    private void setFormat() {
        // 识别指定类型的编码
        //高频率格式，包括 QR_CODE、ISBN13、UPC_A、EAN_13、CODE_128、ITF
        Map<DecodeHintType, Object> hintMap = new EnumMap<>(DecodeHintType.class);
        List<BarcodeFormat> formatList = new ArrayList<>();
        formatList.add(BarcodeFormat.QR_CODE);
        formatList.add(BarcodeFormat.UPC_A);
        formatList.add(BarcodeFormat.EAN_13);
        formatList.add(BarcodeFormat.CODE_128);
        formatList.add(BarcodeFormat.ITF);
        hintMap.put(DecodeHintType.POSSIBLE_FORMATS, formatList); // 可能的编码格式
        hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
        hintMap.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 编码字符集
        mQRCodeView.setType(BarcodeType.CUSTOM, hintMap); // 自定义识别的类型
    }

    @Override
    protected void onStop() {
        mQRCodeView.closeFlashlight();
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.closeFlashlight();
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200L);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.d(TAG,"onScanQRCodeSuccess:" + result);
        vibrate();
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(CameraScanner.SCAN_RESULT_KEY, result);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        //isDark 为true 环境过暗
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mQRCodeView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mQRCodeView.openFlashlight();
                mQRCodeView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mQRCodeView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.d(TAG,"打开相机出错:");
    }

}