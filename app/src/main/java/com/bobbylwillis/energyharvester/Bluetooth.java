package com.bobbylwillis.energyharvester;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Bobby on 3/3/2016.
 */
public class Bluetooth extends Activity {
    final BluetoothManager bluetoothManager =
            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    private BluetoothAdapter mBluetoothAdapter;

//    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//    }

}
