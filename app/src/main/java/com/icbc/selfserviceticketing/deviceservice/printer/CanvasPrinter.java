package com.icbc.selfserviceticketing.deviceservice.printer;

import static android.security.KeyStore.getApplicationContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.csnprintersdk.csnio.CSNCanvas;
import com.csnprintersdk.csnio.CSNPOS;
import com.csnprintersdk.csnio.CSNUSBPrinting;
import com.csnprintersdk.csnio.csnbase.CSNIOCallBack;
import com.icbc.selfserviceticketing.deviceservice.IPrinter;
import com.icbc.selfserviceticketing.deviceservice.Prints;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CanvasPrinter implements CSNIOCallBack, IProxyPrinter {
    public static int nPrintWidth = 800;//384
    public static boolean bCutter = false;
    public static boolean bDrawer = false;
    public static boolean bBeeper = true;
    public static int nPrintCount = 1;
    public static int nCompressMethod = 0;
    public static boolean bAutoPrint = false;
    public static int nPrintContent = 0;
    private static final String TAG = "PagePrinter";
    ExecutorService es = Executors.newScheduledThreadPool(4);
    CSNCanvas csnCanvas = new CSNCanvas();
    CSNUSBPrinting mUsb = new CSNUSBPrinting();
    Context context;
    public int printerStatus = 0;

    private Builder pBuilder = new Builder();

    public CanvasPrinter(Context applicationContext) {
        this.context = applicationContext;
        openDevice();
    }

    @Override
    public int OpenDevice(int DeviceID, String deviceFile, String szPort, String szParam) {
        return 0;
    }

    private void openDevice() {
        final UsbManager mUsbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        csnCanvas.Set(mUsb);
        mUsb.SetCallBack(this);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice device = null;
        if (deviceList.size() > 0) {
            while (deviceIterator.hasNext()) {
                device = deviceIterator.next();
                String deviceId = String.format(" VID:%04X PID:%04X", device.getVendorId(), device.getProductId());
                Log.d(TAG, "openDevice: device = " + deviceId + "VendorId=" + device.getVendorId() + "ProductId=" + device.getProductId());
                if (device.getVendorId() == 4070 && device.getProductId() == 33054) {
                    break;
                }
            }
        }
        if (device == null) return;
        es.submit(new TaskOpen(mUsb, mUsbManager, device, context));
    }

    @Override
    public int CloseDevice(int DeviceID) {
        es.submit(new TaskClose(mUsb));
        Log.d(TAG, "CloseDevice: ");
        return 0;
    }

    /**
     * 首先获取状态
     *
     * @return
     * @
     */
    @Override
    public int getStatus() {
        byte[] status = new byte[1];
        // boolean usable = csnCanvas.POS_QueryStatus(status, 2000, 2);
//        return usable ? 0 : 1;
        return 0;
    }

    /**
     * 设置页面大小
     *
     * @param format - 打印设置
     *               <ul>
     * @return
     * @
     */
    @Override
    public int setPageSize(Bundle format) {
        /**
         * format – 指定打印设置格式
         * pageW(int)：纸张宽度（毫米），不能大于门票纸，否则可能导致定位
         * 错误
         * pageH(int)：纸张高度（毫米），不能大于门票纸，否则可能导致定位错
         * 误
         * direction(int)：打印起始坐标方向，0－出纸方向右下角为坐标原点，1
         * －出纸方向左上角为坐标原点
         * OffsetX(int)：左偏移量（毫米）,设置后，打印内容全部往左偏移指定位
         * 置
         * OffsetY(int)：上偏移量（毫米）,设置后，打印内容全部往下偏移指定位
         * 置
         */
        int pageW = format.getInt("pageW");
        int pageH = format.getInt("pageH");
        int direction = format.getInt("direction");
        int offsetX = format.getInt("OffsetX");
        int offsetY = format.getInt("OffsetY");
        Log.d(TAG, "setPageSize: pageW=" + pageW);
        Log.d(TAG, "setPageSize: pageH=" + pageH);
        Log.d(TAG, "setPageSize: direction=" + direction);
        Log.d(TAG, "setPageSize: OffsetX=" + offsetX);
        Log.d(TAG, "setPageSize: OffsetY=" + offsetY);
        pBuilder
                .setPageW(pageW * 8)
                .setPageH(pageH * 8)
                .setDirection(direction)
                .setOffsetX(offsetX).setOffsetY(offsetY);
        csnCanvas.CanvasBegin(pBuilder.pageW, pBuilder.pageH);
        csnCanvas.SetPrintDirection(0);
        return 0;
    }

    /**
     * 首先是获取状态，其次是startPrintDoc打印
     *
     * @return
     * @
     */
    @Override
    public int startPrintDoc() {
        return 0;
    }

    @Override
    public int addText(Bundle format, String text) {
        /**
         * fontName(Sting)：字体名称，安卓下使用的字体文件必须放在
         * asset\font 目录下，例如填： FZLTXHJW.TTF ， 则该字体文件存
         * 放在项目的 asset\font\FZLTXHJW.TTF
         * fontSize(int)：字体大小，一般设置为 10~16 大小
         * rotation(int)：旋转角度，0，90，180，270 四个角度
         * iLeft(int): 距离左边距离,单位 mm
         * iTop(int): 距离顶部距离,单位 mm
         * align(int)：对齐方式，默认左对齐，0:left, 1:center, 2:right
         * pageWidth(int)：文本打印宽度，单位:MM，如果内容超过宽度会自动
         * 换行
         */
        String fontName = format.getString("fontName");
        int fontSize = format.getInt("fontSize");
        int rotation = format.getInt("rotation");
        int iLeft = format.getInt("iLeft");
        int iTop = format.getInt("iTop");
        int align = format.getInt("align");
        int pageWidth = format.getInt("pageWidth");
        /**
         * nLan 0-GBK 1-UTF8 3-BIG5 4-SHIFT-JIS 5-EUC-KR
         *
         */
//        mPos.POS_S_Align(align);
//        mPos.POS_TextOut(text, 0, iLeft, 1, fontSize/100, 0, 0);
//        mPos.POS_FeedLine();
        csnCanvas.DrawText(text, 50, 50, -1, null, 10, 0);
        es.submit(new Runnable() {
            @Override
            public void run() {
                csnCanvas.DrawText(text, getX(align, iLeft), getY(iTop), convertAlign(align), null, fontSize, 0);
            }
        });

        Log.d(TAG, "addText: text=" + text);
        Log.d(TAG, "addText: fontSize=" + fontSize + " rotation=" + rotation + " iLeft=" + iLeft + " iTop=" + iTop + " align=" + align + " pageWidth=" + pageWidth);
        return 0;
    }

    private int convertAlign(int align) {
        return align;
    }

    private int getX(int align, int iLeft) {
        int x = 1;
        switch (align) {
            case 0:
                x = -1;
                break;
            case 1:
                x = -2;
                break;
            case 2:
                x = -3;
                break;
        }
        if (iLeft > x)
            x = iLeft * 8;
        return x;
    }

    private int getY(int iTop) {
        int y = iTop * 8;
        return y;
    }

    @Override
    public int addQrCode(Bundle format, String qrCode) {
        /**
         * iLeft(int): 距离左边距离,单位 mm
         * iTop(int): 距离顶部距离,单位 mm
         * expectedHeight(int) - 期望高度，单位 mm
         * qrCode – 二维码内容
         */
        int iLeft = format.getInt("iLeft");
        int iTop = format.getInt("iTop");
        int expectedHeight = format.getInt("expectedHeight");
        Log.d(TAG, "addQrCode: iLeft=" + iLeft + " iTop=" + iTop + " expectedHeight=" + expectedHeight + " qrCode=" + qrCode);
        csnCanvas.DrawQRCode(qrCode, 100, 100, 0, 16, 0, 1);
        es.submit(new Runnable() {
            @Override
            public void run() {
                csnCanvas.DrawQRCode(qrCode, getX(0, iLeft), getY(iTop), 0, 8, 0, 1);

            }
        });
        return 0;
    }

    @Override
    public int addImage(Bundle format, String imageData) {
        byte[] bytes = Base64.decode(imageData, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //format –打印格式，可设置打印的位置、宽度、高度
        //rotation(int)：旋转角度，0，90，180，270 四个角度
        //iLeft(int): 距离左边距离,单位 mm
        //iTop(int): 距离顶部距离,单位 mm
        //iWidth(int): 图像宽度,单位 mm
        //iHeight(int): 图像高度,单位 mm
        int rotation = format.getInt("rotation");
        int iLeft = format.getInt("iLeft");
        int iTop = format.getInt("iTop");
        int iWidth = format.getInt("iWidth");
        int iHeight = format.getInt("iHeight");
        es.submit(new Runnable() {
            @Override
            public void run() {
                csnCanvas.DrawBitmap(bitmap, iLeft * 8, getY(iTop), rotation);
            }
        });

        return 0;
    }

    /**
     * 结束打印任务
     *
     * @return
     * @
     */
    @Override
    public int endPrintDoc() {
        Log.d(TAG, "endPrintDoc: 结束打印任务");

        csnCanvas.CanvasEnd();
        csnCanvas.CanvasPrint(1, 0);
        return 0;
    }

    @Override
    public void OnOpen() {
        printerStatus = 0;
        Log.d(TAG, "OnOpen: " + printerStatus);
    }

    @Override
    public void OnOpenFailed() {
        printerStatus = 1;
        Log.d(TAG, "OnOpenFailed: " + printerStatus);

    }

    @Override
    public void OnClose() {
        printerStatus = 2;
        Log.d(TAG, "OnClose: " + printerStatus);
    }

    public class TaskOpen implements Runnable {
        CSNUSBPrinting usb = null;
        UsbManager usbManager = null;
        UsbDevice usbDevice = null;
        Context context = null;

        public TaskOpen(CSNUSBPrinting usb, UsbManager usbManager, UsbDevice usbDevice, Context context) {
            this.usb = usb;
            this.usbManager = usbManager;
            this.usbDevice = usbDevice;
            this.context = context;
        }

        @Override
        public void run() {
            usb.Open(usbManager, usbDevice, context);
        }
    }

    public class TaskClose implements Runnable {
        CSNUSBPrinting usb = null;

        public TaskClose(CSNUSBPrinting usb) {
            this.usb = usb;
        }

        @Override
        public void run() {
            usb.Close();
        }
    }
}
