package com.hao.scannerlibrary.sunmi

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.hao.scannerlibrary.IScanner
import com.hao.scannerlibrary.ScannerCallback
import com.sunmi.scanner.IScanInterface


/**
 * sunmi scanner
 * @date 2022/6/24
 * @author 锅得铁
 * @since v1.0
 */
class SunmiScanner(activity: Activity) : IScanner {

    companion object {
        var scanInterface: IScanInterface? = null
        private val conn: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                scanInterface = IScanInterface.Stub.asInterface(service)
                Log.i("setting", "Scanner Service Connected!")
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.e("setting", "Scanner Service Disconnected!")
                scanInterface = null
            }
        }
    }

    init {
        val intent = Intent()
        intent.setPackage("com.sunmi.scanner")
        intent.setAction("com.sunmi.scanner.IScanInterface");
        activity.bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }


    override fun scan(callback: ScannerCallback?) {
        scanInterface!!.scan()
    }


    override fun getScanResult(data: Intent?): String {
        return ""
    }

    override fun close() {
        scanInterface!!.stop()
    }


}