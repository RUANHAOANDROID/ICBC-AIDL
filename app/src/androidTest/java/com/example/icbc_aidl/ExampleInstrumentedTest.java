package com.example.icbc_aidl;

import static org.junit.Assert.assertEquals;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.icbc.selfserviceticketing.deviceservice.DeviceService;
import com.icbc.selfserviceticketing.deviceservice.IDeviceService;
import com.icbc.selfserviceticketing.deviceservice.IPrinter;
import com.icbc.selfserviceticketing.deviceservice.IScanner;
import com.icbc.selfserviceticketing.deviceservice.ScannerListener;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ICBC AIDL Service 层单元测试
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";

    @Test
    public void useAppContext() throws InterruptedException {

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IDeviceService binder = IDeviceService.Stub.asInterface(service);
                try {
                    IScanner scanner = binder.getScanner(1);
                    IPrinter printer = binder.getPrinter("a");
                    testPrinterICBCTest(printer);
                    //testScanner(scanner);
                    Log.d("Scanner", "onServiceConnected: ");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onServiceDisconnected(ComponentName name) {
                // 服务连接断开时的处理逻辑
                Log.d("TEST", "onServiceDisconnected: ");
            }
        };
        Intent intent = new Intent(appContext, DeviceService.class);
        appContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE | Context.BIND_DEBUG_UNBIND);
        Thread.sleep(100000);
        assertEquals("com.icbc.selfserviceticketing.deviceservice", appContext.getPackageName());
    }


    private void testPrinterICBCTest(IPrinter printer) throws RemoteException {
        /**
         *    openDevice: device =  VID:0FE6 PID:811EVendorId=4070ProductId=33054
         *    OnOpen: 0
         *    setPageSize: pageW=155
         *    setPageSize: pageH=125
         *    setPageSize: direction=0
         *    setPageSize: OffsetX=0
         *    setPageSize: OffsetY=0
         *    setPageSize: Builder{pageW=576, pageH=125, direction=0, offsetX=0, offsetY=0}
         *    addText: text=票券名称
         *    addText: fontSize=44 rotation=0 iLeft=0 iTop=14 align=1 pageWidth=92
         *    addText: text=票券编号
         *    addText: fontSize=14 rotation=0 iLeft=3 iTop=35 align=1 pageWidth=29
         *    addText: text=订单号
         *    addText: fontSize=44 rotation=0 iLeft=3 iTop=56 align=1 pageWidth=69
         *    addQrCode: iLeft=98 iTop=77 expectedHeight=45
         *    addText: text=销售渠道
         *    addText: fontSize=14 rotation=0 iLeft=7 iTop=80 align=0 pageWidth=29
         *    addQrCode: iLeft=14 iTop=94 expectedHeight=24
         *    addText: text=0元门票
         *    addText: fontSize=44 rotation=0 iLeft=92 iTop=14 align=1 pageWidth=48
         *    addText: text=T2306020016500600001
         *    addText: fontSize=14 rotation=0 iLeft=32 iTop=35 align=1 pageWidth=54
         *    addText: text=MO202306020000080470
         *    addText: fontSize=44 rotation=0 iLeft=72 iTop=56 align=1 pageWidth=47
         *    addText: text=自助售票机
         *    addText: fontSize=14 rotation=0 iLeft=37 iTop=80 align=0 pageWidth=54
         */
        android.os.Bundle pageBundle = new Bundle();
        pageBundle.putInt("pageW", 155);
        pageBundle.putInt("pageH", 125);
        pageBundle.putInt("OffsetX", 0);
        pageBundle.putInt("OffsetY", 0);
        pageBundle.putInt("direction", 0);
        printer.setPageSize(pageBundle);


        TextBuilder textBuilder = new TextBuilder();
        String text = "票券名称";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 0;
        textBuilder.iTop = 14;
        textBuilder.align = 1;
        textBuilder.pageWidth = 92;
        printerText(printer, text, textBuilder);
        /**
         *          *    addText: text=票券编号
         *          *    addText: fontSize=14 rotation=0 iLeft=3 iTop=35 align=1 pageWidth=29
         */
        TextBuilder numberBuilder = new TextBuilder();
        String noTitle = "票券编号";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 3;
        textBuilder.iTop = 35;
        textBuilder.align = 1;
        textBuilder.pageWidth = 29;
        printerText(printer, noTitle, numberBuilder);
/**
 *          *    addText: text=订单号
 *          *    addText: fontSize=44 rotation=0 iLeft=3 iTop=56 align=1 pageWidth=69
 */
        TextBuilder dingdanBuilder = new TextBuilder();
        String dingdanTitle = "订单号";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 3;
        textBuilder.iTop = 56;
        textBuilder.align = 1;
        textBuilder.pageWidth = 69;
        printerText(printer, dingdanTitle, dingdanBuilder);
        /*
         *  addText: text=销售渠道
         *  addText: fontSize=14 rotation=0 iLeft=7 iTop=80 align=0 pageWidth=29
         */
        TextBuilder qdBuilder = new TextBuilder();
        String qdTitle = "订单号";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 7;
        textBuilder.iTop = 80;
        textBuilder.align = 0;
        textBuilder.pageWidth = 29;
        printerText(printer, qdTitle, qdBuilder);
        /**
         *   addQrCode: iLeft=14 iTop=94 expectedHeight=24
         */
        android.os.Bundle qrBundle = new Bundle();
        qrBundle.putInt("iLeft", 14);
        qrBundle.putInt("iTop", 95);
        qrBundle.putInt("expectedHeight", 24);
        printer.addQrCode(qrBundle, "草泥马");
        /*    addText: text=0元门票
         *    addText: fontSize=44 rotation=0 iLeft=92 iTop=14 align=1 pageWidth=48
         */
        TextBuilder moneyBuilder = new TextBuilder();
        String money = "0元门票";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 92;
        textBuilder.iTop = 14;
        textBuilder.align = 1;
        textBuilder.pageWidth = 48;
        printerText(printer, money, moneyBuilder);
        /*    addText: text=T2306020016500600001
         *    addText: fontSize=14 rotation=0 iLeft=32 iTop=35 align=1 pageWidth=54
         */
        TextBuilder tBuilder = new TextBuilder();
        String t = "T2306020016500600001";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 32;
        textBuilder.iTop = 35;
        textBuilder.align = 1;
        textBuilder.pageWidth = 54;
        printerText(printer, t, tBuilder);
        /*    addText: text=MO202306020000080470
         *    addText: fontSize=44 rotation=0 iLeft=72 iTop=56 align=1 pageWidth=47
         */
        TextBuilder mBuilder = new TextBuilder();
        String m = "MO202306020000080470";
        textBuilder.fontSize = 44;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 72;
        textBuilder.iTop = 56;
        textBuilder.align = 1;
        textBuilder.pageWidth = 47;
        printerText(printer, m, mBuilder);

        /*    addText: text=自助售票机
         *    addText: fontSize=14 rotation=0 iLeft=37 iTop=80 align=0 pageWidth=54
         */
        TextBuilder zzBuilder = new TextBuilder();
        String zz = "MO202306020000080470";
        textBuilder.fontSize = 14;
        textBuilder.rotation = 0;
        textBuilder.iLeft = 37;
        textBuilder.iTop = 80;
        textBuilder.align = 0;
        textBuilder.pageWidth = 54;
        printerText(printer, zz, zzBuilder);

        printer.startPrintDoc();
        printer.CloseDevice(1);
    }

    private void printerText(IPrinter printer, String text, TextBuilder textBuilder) throws RemoteException {
        android.os.Bundle textBuild = new Bundle();
        textBuild.putInt("fontSize", textBuilder.fontSize);
        textBuild.putInt("rotation", textBuilder.rotation);
        textBuild.putInt("iLeft", textBuilder.iLeft);
        textBuild.putInt("iTop", textBuilder.iTop);
        textBuild.putInt("pageWidth", textBuilder.pageWidth);
        printer.addText(textBuild, text);
    }

    private void testScanner(IScanner scanner) throws RemoteException {
        scanner.startScan(null, 1000, new ScannerListener() {
            @Override
            public IBinder asBinder() {
                return null;
            }

            @Override
            public void onSuccess(Bundle result) throws RemoteException {
                Log.d(TAG, "onSuccess: result" + result.toString());
            }

            @Override
            public void onError(int error, String message) throws RemoteException {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onTimeout() throws RemoteException {
                Log.d(TAG, "onTimeout: ");
            }

            @Override
            public void onCancel() throws RemoteException {
                Log.d(TAG, "onCancel: ");
            }
        });
    }

}