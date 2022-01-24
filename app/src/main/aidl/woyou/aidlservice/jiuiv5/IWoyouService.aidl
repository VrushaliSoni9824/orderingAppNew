

package woyou.aidlservice.jiuiv5;

import woyou.aidlservice.jiuiv5.ICallback;
import android.graphics.Bitmap;
import woyou.aidlservice.jiuiv5.ITax;

interface IWoyouService
{

	void updateFirmware();


	int getFirmwareStatus();


	String getServiceVersion();


	void printerInit(in ICallback callback);


	void printerSelfChecking(in ICallback callback);


	String getPrinterSerialNo();


	String getPrinterVersion();


	String getPrinterModal();


	int getPrintedLength();


	void lineWrap(int n, in ICallback callback);

	void sendRAWData(in byte[] data, in ICallback callback);


	void setAlignment(int alignment, in ICallback callback);

	void setFontName(String typeface, in ICallback callback);


	void setFontSize(float fontsize, in ICallback callback);

	void printText(in String text, in ICallback callback);

	void printTextWithFont(in String text, in String typeface, in float fontsize, in ICallback callback);

	void printColumnsText(in String[] colsTextArr, in int[] colsWidthArr, in int[] colsAlign, in ICallback callback);


	void printBitmap(in Bitmap bitmap, in ICallback callback);


	void printBarCode(in String data, in int symbology, in int height, in int width, in int textposition,  in ICallback callback);

	void printQRCode(in String data, in int modulesize, in int errorlevel, in ICallback callback);

	void printOriginalText(in String text, in ICallback callback);

	void commitPrinterBuffer();


	void cutPaper(in ICallback callback);

	int getCutPaperTimes();

	void openDrawer(in ICallback callback);

	int getOpenDrawerTimes();


	void enterPrinterBuffer(in boolean clean);

	void exitPrinterBuffer(in boolean commit);

	void tax(in byte [] data,in ITax callback);


	int getPrinterMode();


	int getPrinterBBMDistance();


	void printColumnsString(in String[] colsTextArr, in int[] colsWidthArr, in int[] colsAlign, in ICallback callback);


	int updatePrinterState();


	void commitPrinterBufferWithCallback(in ICallback callback);

	void exitPrinterBufferWithCallback(in boolean commit, in ICallback callback);

    void printBitmapCustom(in Bitmap bitmap, in int type, in ICallback callback);
}