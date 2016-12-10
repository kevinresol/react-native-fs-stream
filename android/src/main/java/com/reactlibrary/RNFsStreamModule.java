
package com.reactlibrary;

import android.os.Environment;
import android.util.Base64;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RNFsStreamModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private final HashMap<Integer, FileInputStream> reads;
  private final HashMap<Integer, FileOutputStream> writes;

  public RNFsStreamModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reads = new HashMap<Integer, FileInputStream>();
    writes = new HashMap<Integer, FileOutputStream>();
  }

  @Override
  public String getName() {
    return "RNFsStream";
  }

  @ReactMethod
  public void openForRead(String path, Promise promise) {
    try {
      Integer i = getNextAvailableIndex(reads);
      FileInputStream f = new FileInputStream(path);
      reads.put(i, f);
      promise.resolve(i);
    } catch(Exception ex) {
      promise.reject(ex);
    }
  }

  @ReactMethod
  public void openForWrite(String path, Promise promise) {
    try {
      Integer i = getNextAvailableIndex(reads);
      FileOutputStream f = new FileOutputStream(path);
      writes.put(i, f);
      promise.resolve(i);
    } catch(Exception ex) {
      promise.reject(ex);
    }
  }

  @ReactMethod
  public void read(final Integer fd, final Integer size, final Promise promise) {
    if(!reads.containsKey(fd)) {
      promise.reject("Not Opened", "The provided file descriptor is not opened for read. Call openForRead first.");
      return;
    }

    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          FileInputStream f = reads.get(fd);
          byte[] buffer = new byte[size];

          int offset = 0;
          int numRead = 0;

          while (offset < size && (numRead = f.read(buffer, offset, size - offset)) > 0) {
            offset += numRead;
          }

          if (numRead == -1) {
            f.close();
            reads.remove(fd);
          }

          if (offset < size) {
            byte[] trunc = new byte[offset];
            System.arraycopy(buffer, 0, trunc, 0, offset);
            buffer = trunc;
          }

          WritableMap result = Arguments.createMap();
          result.putString("data", Base64.encodeToString(buffer, Base64.NO_WRAP));
          result.putInt("bytesRead", offset);
          result.putBoolean("ended", numRead == -1);
          promise.resolve(result);

        } catch (Exception ex) {
          promise.reject(ex);
        }
      }
    };
    thread.start();
  }

  @ReactMethod
  public void write(final Integer fd, final String data, final Promise promise) {
    if(!writes.containsKey(fd)) {
      promise.reject("Not Opened", "The provided file descriptor is not opened for write. Call openForWrite first.");
      return;
    }
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          FileOutputStream f = writes.get(fd);
          byte[] buffer = Base64.decode(data, Base64.DEFAULT);

          f.write(buffer);

          promise.resolve(null);

        } catch(Exception ex) {
          promise.reject(ex);
        }
      }
    };
    thread.start();
  }

  @ReactMethod
  public void closeRead(Integer fd, Promise promise) {
    if(reads.containsKey(fd)) {
      try {
        reads.get(fd).close();
        reads.remove(fd);
      } catch(Exception ex) {
        promise.reject(ex);
      }
    }
    promise.resolve(null);
  }

  @ReactMethod
  public void closeWrite(Integer fd, Promise promise) {
    if(writes.containsKey(fd)) {
      try {
        writes.get(fd).close();
        writes.remove(fd);
      } catch(Exception ex) {
        promise.reject(ex);
      }
    }
    promise.resolve(null);
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    constants.put("DocumentDirectory", 0);
    constants.put("DocumentDirectoryPath", this.getReactApplicationContext().getFilesDir().getAbsolutePath());
    constants.put("TemporaryDirectoryPath", null);
    constants.put("PicturesDirectoryPath", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
    constants.put("CachesDirectoryPath", this.getReactApplicationContext().getCacheDir().getAbsolutePath());
    constants.put("FileTypeRegular", 0);
    constants.put("FileTypeDirectory", 1);

    return constants;
  }

  private <T> Integer getNextAvailableIndex(HashMap<Integer, T> map) {
    Integer i = 3;
    while(true) {
      if(!map.containsKey(i)) return i;
      i++;
    }
  }
}