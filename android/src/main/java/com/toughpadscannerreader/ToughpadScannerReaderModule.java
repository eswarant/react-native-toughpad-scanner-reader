package com.toughpadscannerreader;

import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import android.util.Log;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Callback;

import com.panasonic.toughpad.android.api.ToughpadApi;
import com.panasonic.toughpad.android.api.ToughpadApiListener;
import com.panasonic.toughpad.android.api.barcode.BarcodeData;
import com.panasonic.toughpad.android.api.barcode.BarcodeException;
import com.panasonic.toughpad.android.api.barcode.BarcodeListener;
import com.panasonic.toughpad.android.api.barcode.BarcodeReader;
import com.panasonic.toughpad.android.api.barcode.BarcodeReaderManager;

@ReactModule(name = ToughpadScannerReaderModule.NAME)
public class ToughpadScannerReaderModule extends ReactContextBaseJavaModule
    implements ToughpadApiListener, BarcodeListener {
  public static final String NAME = "ToughpadScannerReader";

  EnableReaderTask enableReaderTask;
  private List<BarcodeReader> readers;
  private BarcodeReader selectedReader;
  private Callback onReadCallback;

  public ToughpadScannerReaderModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void initAPI(Promise promise) {
    printDebugLog("init");
    if (ToughpadApi.isAlreadyInitialized()) {
      printDebugLog("init Already Initialized");
      return;
    }
    readers = null;
    selectedReader = null;
    try {
      ToughpadApi.initialize(getCurrentActivity(), this);
    } catch (RuntimeException ex) {
      printDebugLog(ex.getMessage());
      promise.reject("Error in init", ex);
    }
  }

  @ReactMethod
  public boolean hasReader() {
    if (readers == null || readers.size() == 0) {
      printDebugLog("There is no readers");
      return false;
    }

    boolean foundReader = false;
    for (int i = 0; i < readers.size(); i++) {
      if (readers.get(i).getBarcodeType() != BarcodeReader.BARCODE_TYPE_CAMERA) {
        foundReader = true;
        break;
      }
    }
    printDebugLog("Found readers " + Boolean.toString(foundReader));
    return foundReader;
  }

  @ReactMethod
  public void activateReader(Callback readCallBack) {
    printDebugLog("activateReader");
    onReadCallback = readCallBack;
    try {
      if (isSelectedReaderDeviceEnabled()) {
        pressSoftwareTrigger();
      } else if (hasReader()) {
        selectLaserDevice();
        printSelectedReaderDeviceInfo();
        enableSelectedReaderDevice(true);
        if (isSelectedReaderDeviceEnabled()) {
          pressSoftwareTrigger();
        }
      }
    } catch (Exception ex) {
      printDebugLog(ex.getMessage());
    }
  }

  @ReactMethod
  public void deactivateReader() {
    printDebugLog("deactivateReader");
    try {
      if (selectedReader != null) {
        selectedReader.pressSoftwareTrigger(false);
        releaseBarcodeReader();
      }
    } catch (BarcodeException ex) {
      printDebugLog(ex.getMessage());
    }
  }

  @Override
  public void onApiConnected(int version) {
    printDebugLog("onApiConnected version " + Integer.toString(version));
    readers = BarcodeReaderManager.getBarcodeReaders();
    printDebugLog("onApiConnected readers size " + Integer.toString(readers.size()));
    for (BarcodeReader reader : readers) {
      printDebugLog("onApiConnected" + " => " + reader.getDeviceName() + ", " + reader.getBarcodeType());
    }
  }

  @Override
  public void onApiDisconnected() {
    printDebugLog("onApiDisconnected");
    releaseBarcodeReader();
  }

  @Override
  public void onRead(BarcodeReader paramBarcodeReader, BarcodeData paramBarcodeData) {
    printDebugLog("onRead");
    String strDeviceName = paramBarcodeReader.getDeviceName();
    String strBarcodeData = paramBarcodeData.getTextData();
    String strSymbologyId = paramBarcodeData.getSymbology();
    printDebugLog("onRead DeviceName " + strDeviceName);
    printDebugLog("onRead Barcode " + strBarcodeData);
    printDebugLog("onRead Symbology " + strSymbologyId);
    onReadCallback.invoke(strBarcodeData);
  }

  private void releaseBarcodeReader() {
    printDebugLog("releaseBarcodeReader");
    try {
      selectedReader.disable();
      selectedReader.clearBarcodeListener();
      printDebugLog(selectedReader.getDeviceName());
    } catch (BarcodeException ex) {
      printDebugLog(ex.getMessage());
    }
  }

  public void printDebugLog(String text) {
    Log.d(this.getClass().getSimpleName(), text);
  }

  public void pressSoftwareTrigger() {
    printDebugLog("pressSoftwareTrigger");
    try {
      selectedReader.pressSoftwareTrigger(true);
    } catch (BarcodeException ex) {
      printDebugLog(ex.getMessage());
    }
  }

  public boolean isSelectedReaderDeviceEnabled() {
    boolean isDeviceEnabled = (selectedReader != null && selectedReader.isEnabled());
    printDebugLog("isSelectedReaderDeviceEnabled " + Boolean.toString(isDeviceEnabled));

    return isDeviceEnabled;
  }

  public void selectLaserDevice() {
    printDebugLog("selectLaserDevice readers size " + Integer.toString(readers.size()));
    for (int i = 0; i < readers.size(); i++) {
      if (readers.get(i).getBarcodeType() != BarcodeReader.BARCODE_TYPE_CAMERA) {
        selectReader(i);
        break;
      }
    }
  }

  public void selectReader(int position) {
    selectedReader = readers.get(position);
    printDebugLog("selectReader: " + selectedReader.getDeviceName());
  }

  public String getDeviceTypeString(BarcodeReader reader) {
    String deviceType = "Unknown";
    switch (reader.getBarcodeType()) {
      case BarcodeReader.BARCODE_TYPE_CAMERA:
        deviceType = "BARCODE_TYPE_CAMERA";
        break;
      case BarcodeReader.BARCODE_TYPE_ONE_DIMENSIONAL:
        deviceType = "BARCODE_TYPE_ONE_DIMENSIONAL";
        break;
      case BarcodeReader.BARCODE_TYPE_TWO_DIMENSIONAL:
        deviceType = "BARCODE_TYPE_TWO_DIMENSIONAL";
        break;
    }
    return deviceType;
  }

  public void printSelectedReaderDeviceInfo() {
    printDebugLog("printSelectedReaderDeviceInfo");
    if (selectedReader != null) {

      printDebugLog("printSelectedReaderDeviceInfo" + " => HardwareTriggerAvailable : "
          + selectedReader.isHardwareTriggerAvailable());
      if (selectedReader.isHardwareTriggerAvailable()) {
        printDebugLog("printSelectedReaderDeviceInfo" + " => HardwareTriggerEnabled : "
            + selectedReader.isHardwareTriggerEnabled());
      }

      printDebugLog(
          "printSelectedReaderDeviceInfo" + " => Selected reader is enabled/disabled : " + selectedReader.isEnabled());
      printDebugLog(
          "printSelectedReaderDeviceInfo" + " => Selected Device Type : " + getDeviceTypeString(selectedReader));

      if (selectedReader.isExternal()) {
        printDebugLog("printSelectedReaderDeviceInfo" + " => This is external device");
      }
    }
  }

  public void enableReaderDevice(BarcodeReader reader) {
    printDebugLog("enableReaderDevice");
    enableReaderTask = new EnableReaderTask();
    enableReaderTask.execute(reader);
  }

  public void disableReaderDevice(BarcodeReader reader) {
    printDebugLog("disableReaderDevice");
    try {
      selectedReader.disable();
      selectedReader.clearBarcodeListener();
    } catch (BarcodeException ex) {
      printDebugLog(ex.getMessage());
    }
  }

  public void enableSelectedReaderDevice(boolean flag) {
    printDebugLog("enableSelectedReaderDevice");

    if (selectedReader.isEnabled() && !flag) {
      disableReaderDevice(selectedReader);
    } else if (!selectedReader.isEnabled() && flag) {
      enableReaderDevice(selectedReader);
    } else {
      printDebugLog("Wrong logic/state for enabling reader device");
      printDebugLog(" => selectedReader.isEnabled() : " + selectedReader.isEnabled());
      printDebugLog(" => flag : " + flag);
    }
  }

  private class EnableReaderTask extends AsyncTask<BarcodeReader, Void, Boolean> {
    private ProgressDialog dialog;

    @Override
    protected void onPreExecute() {
      Log.d("EnableReaderTask", "onPreExecute");
      if (dialog == null) {
        Log.d("EnableReaderTask", "dialog");
        dialog = new ProgressDialog(getCurrentActivity());
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.setMessage("Connecting to barcode reader…");
      }
      dialog.show();
    }

    @Override
    protected Boolean doInBackground(BarcodeReader... params) {
      Log.d("EnableReaderTask", "doInBackground");
      try {
        params[0].enable(3000);
        params[0].addBarcodeListener(ToughpadScannerReaderModule.this);
        return true;
      } catch (BarcodeException ex) {
        Log.d("EnableReaderTask", ex.getMessage());
        return false;
      } catch (TimeoutException ex) {
        Log.d("EnableReaderTask", ex.getMessage());
        return false;
      } catch (Exception ex) {
        Log.d("EnableReaderTask", ex.getMessage());
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean result) {
      Log.d("EnableReaderTask ", "onPostExecute");
      if (dialog != null && dialog.isShowing()) {
        dialog.dismiss();
      }
      if (result) {
        ToughpadScannerReaderModule.this.pressSoftwareTrigger();
        Log.d("EnableReaderTask", "onPostExecute => " + selectedReader.getDeviceName() + "is enabled.");
      }
    }
  }

}
