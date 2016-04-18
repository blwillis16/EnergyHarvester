package com.bobbylwillis.energyharvester;


        import android.app.Activity;
        import android.app.Fragment;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothGattCharacteristic;
        import android.content.Context;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.view.MenuItem;
        import android.widget.TextView;
        import com.bobbylwillis.energyharvester.BluetoothLeUart;
        import java.util.ArrayList;

public class BlueUART extends Activity implements BluetoothLeUart.Callback {

    // UI elements
    private TextView messages;

    // Bluetooth LE UART instance.  This is defined in BluetoothLeUart.java.
    private BluetoothLeUart uart;

    // Write some text to the messages text view.
    // Care is taken to do this on the main UI thread so writeLine can be called from any thread
    // (like the BTLE callback).
    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.append(text);
                messages.append("\n");
            }
        });
    }

    @Override
   public void onCreate(Bundle savedInstanceState) { //was protected
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le_uart);
        // Grab references to UI elements.
        messages = (TextView) findViewById(R.id.messages);
        // Initialize UART.
        uart = new BluetoothLeUart(getApplicationContext());
        // Enable auto-scroll in the TextView
        messages.setMovementMethod(new ScrollingMovementMethod());

    }

    // OnResume, called right before UI is displayed.  Connect to the bluetooth device.

    @Override
    protected void onResume() {  //was protected
        super.onResume();
        writeLine("Scanning for devices ...");
        uart.registerCallback(this);
        uart.connectFirstAvailable();
    }

    // OnStop, called right before the activity loses foreground focus.  Close the BTLE connection.
    @Override

   protected void onStop() { //was protected
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // UART Callback event handlers.
    @Override
    public void onConnected(BluetoothLeUart uart) {
        // Called when UART device is connected and ready to send/receive data.
        writeLine("Connected!");
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart) {
        // Called when some error occured which prevented UART connection from completing.
        writeLine("Error connecting to device!");
    }

    @Override
    public void onDisconnected(BluetoothLeUart uart) {
        // Called when the UART device disconnected.
        writeLine("Disconnected!");
    }

    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        // Called when data is received by the UART.
        byte[] dataCollected = rx.getValue();
        for (int i = 0; i < dataCollected.length; i += 2) {
            int  testResult = (dataCollected[i]) << 8 | (dataCollected[i + 1]) & 0xff;
            double actualVoltage = (6.144/32768)*testResult;
            writeLine("Received: " + actualVoltage);
        }

    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        // Called when a UART device is discovered (after calling startScan).
        writeLine("Found device : " + device.getAddress());
        writeLine("Waiting for a connection ...");
    }

    @Override
    public void onDeviceInfoAvailable() {
        writeLine(uart.getDeviceInfo());
    }


}
