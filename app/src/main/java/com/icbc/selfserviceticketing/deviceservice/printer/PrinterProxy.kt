package com.icbc.selfserviceticketing.deviceservice.printer

import android.content.Context
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.icbc.selfserviceticketing.deviceservice.Config
import com.icbc.selfserviceticketing.deviceservice.IPrinter
import com.icbc.selfserviceticketing.deviceservice.PRINTER_CSN

class PrinterProxy(var context: Context,val config: Config) : IPrinter.Stub() {
    private var mProxyPrinter: IProxyPrinter? = null
    var TAG = "PrinterProxy"

    init {
        mProxyPrinter = if (config.printerType== PRINTER_CSN) {
            Log.d(TAG,"GSNPrinter")
            CSNPrinter(
                context,config
            )
        } else {
            TSCUsbPrinter(context,config)
        }
        Log.d(TAG, "PrinterProxy: " + mProxyPrinter!!.javaClass.simpleName)
        LogUtils.file(mProxyPrinter!!.javaClass.simpleName)
    }

    @Throws(RemoteException::class)
    override fun OpenDevice(
        DeviceID: Int,
        deviceFile: String,
        szPort: String,
        szParam: String
    ): Int {
        LogUtils.file("deviceId=${DeviceID},deviceFile=${deviceFile},szPort=${szPort},szParam=$szParam")
        return mProxyPrinter!!.OpenDevice(DeviceID, deviceFile, szPort, szParam)
    }

    @Throws(RemoteException::class)
    override fun CloseDevice(DeviceID: Int): Int {
        LogUtils.file("deviceId=${DeviceID}")
        return mProxyPrinter!!.CloseDevice(DeviceID)
    }

    @Throws(RemoteException::class)
    override fun getStatus(): Int {
        LogUtils.file("deviceId=${mProxyPrinter!!.status}")
        return mProxyPrinter!!.status
    }

    @Throws(RemoteException::class)
    override fun setPageSize(format: Bundle): Int {
        LogUtils.file("format=$format")
        return mProxyPrinter!!.setPageSize(format)
    }

    @Throws(RemoteException::class)
    override fun startPrintDoc(): Int {
        LogUtils.file("startPrintDoc")
        return mProxyPrinter!!.startPrintDoc()
    }

    @Throws(RemoteException::class)
    override fun addText(format: Bundle, text: String): Int {
        LogUtils.file("format=$format,text=${text}")
        return if (null == text || text.isEmpty()) 0 else mProxyPrinter!!.addText(format, text)
    }

    @Throws(RemoteException::class)
    override fun addQrCode(format: Bundle, qrCode: String): Int {
        LogUtils.file("format=$format,qrCode=${qrCode}")
        return mProxyPrinter!!.addQrCode(format, qrCode)
    }

    @Throws(RemoteException::class)
    override fun addImage(format: Bundle, imageData: String): Int {
        LogUtils.file("format=$format,imageData=${imageData}")
        return mProxyPrinter!!.addImage(format, imageData)
    }

    @Throws(RemoteException::class)
    override fun endPrintDoc(): Int {
        LogUtils.file("endPrintDoc")
        return mProxyPrinter!!.endPrintDoc()
    }
}