package com.bobbylwillis.energyharvester;


        import android.app.Activity;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothGattCharacteristic;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.lang.Thread;
        import java.nio.charset.Charset;
        import java.util.ArrayList;

public class BlueUART extends Activity implements BluetoothLeUart.Callback {

    // UI elements
    private TextView messages;
    private EditText input;
    private Button   send;
    private CheckBox newline;

    // Bluetooth LE UART instance.  This is defined in BluetoothLeUart.java.
    private BluetoothLeUart uart;
    private volatile ArrayList<UartDataChunk> mDataBuffer = new ArrayList<UartDataChunk>();
    private volatile int mReceivedBytes;

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

    // Handler for mouse click on the send button.
    public void sendClick(View view) {
        StringBuilder stringBuilder = new StringBuilder();
        String message = input.getText().toString();

        // We can only send 20 bytes per packet, so break longer messages
        // up into 20 byte payloads
        int len = message.length();
        int pos = 0;
        while(len != 0) {
            stringBuilder.setLength(0);
            if (len>=20) {
                stringBuilder.append(message.toCharArray(), pos, 20 );
                len-=20;
                pos+=20;
            }
            else {
                stringBuilder.append(message.toCharArray(), pos, len);
                len = 0;
            }
            uart.send(stringBuilder.toString());
        }
        // Terminate with a newline character if requests
        newline = (CheckBox) findViewById(R.id.newline);
        if (newline.isChecked()) {
            stringBuilder.setLength(0);
            stringBuilder.append("\n");
            uart.send(stringBuilder.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le_uart);

        // Grab references to UI elements.
        messages = (TextView) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.input);

        // Initialize UART.
        uart = new BluetoothLeUart(getApplicationContext());

        // Disable the send button until we're connected.
//        send = (Button)findViewById(R.id.send);
//        send.setClickable(false);
//        send.setEnabled(false);

        // Enable auto-scroll in the TextView
        messages.setMovementMethod(new ScrollingMovementMethod());

        final Button gatherButton = (Button)findViewById(R.id.gatherDatabutton);
       gatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gatherData();
            }
        });
    }

    // OnCreate, called once to initialize the activity.
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    // OnResume, called right before UI is displayed.  Connect to the bluetooth device.
    @Override
    protected void onResume() {
        super.onResume();
        writeLine("Scanning for devices ...");
        uart.registerCallback(this);
        uart.connectFirstAvailable();
    }

    // OnStop, called right before the activity loses foreground focus.  Close the BTLE connection.
    @Override
    protected void onStop() {
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
        // Enable the send button
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send = (Button)findViewById(R.id.send);
                send.setClickable(true);
                send.setEnabled(true);
            }
        });
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart) {
        // Called when some error occured which prevented UART connection from completing.
        writeLine("Error connecting to device!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send = (Button)findViewById(R.id.send);
                send.setClickable(false);
                send.setEnabled(false);
            }
        });
    }

    @Override
    public void onDisconnected(BluetoothLeUart uart) {
        // Called when the UART device disconnected.
        writeLine("Disconnected!");
        // Disable the send button.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send = (Button) findViewById(R.id.send);
                send.setClickable(false);
                send.setEnabled(false);
            }
        });
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
 //       byte[] isAvailable = {1};
  //      isAvailable = rx.getValue();
 //       if (isAvailable[0] != 1) {
        //       }
//       final String data = new String(dataCollected, Charset.forName("UTF-8"));
//            UartDataChunk dataChunk = new UartDataChunk(System.currentTimeMillis(), UartDataChunk.TRANSFERMODE_TX, data);
 //          mDataBuffer.add(dataChunk);
//            writeLine("Received: " + data);
    }

//    private void gatherData(){
//        final int bufferSize = mDataBuffer.size();
//       for (int i = 0; i <bufferSize; i++){
//          final UartDataChunk storedBuffer = mDataBuffer.get(i);
//            final int[] storedData = storedBuffer.getData();
////            String hexData = asciiToHex(storedData);
//           writeLine("Received: " + storedData);
//        }
//
//    }
    private String asciiToHex(String text) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {

            String charString = String.format("0x%02X", (byte) text.charAt(i));
            stringBuffer.append(charString + " ");
        }
        return stringBuffer.toString();
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
