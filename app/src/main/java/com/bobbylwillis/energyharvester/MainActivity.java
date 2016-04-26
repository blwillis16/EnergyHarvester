package com.bobbylwillis.energyharvester;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class MainActivity extends AppCompatActivity implements BluetoothLeUart.Callback {
    DatabaseHelper  myDb;
    Button viewDatabase;
    EditText editPower, editVoltage, editCurrent;
    String tableType = "";
    Button addDataBtn;
    Button clearDataBtn;
    //series variables
    LineGraphSeries<DataPoint> thermalSeries = new LineGraphSeries<DataPoint>(new DataPoint[]{new DataPoint(0,0)});
    LineGraphSeries<DataPoint> totalSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,0)});
    LineGraphSeries<DataPoint> solarSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,0)});
    LineGraphSeries<DataPoint> piezoSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,0)});


    private static final int kActivityRequestCode_EnableBluetooth = 1;


    String receiveTableType ="";
    private BluetoothLeUart uart;
    private TextView messages;
    boolean addingDataStatus = true;
    boolean dataconnectionStatus =false;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //sets app orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //creates all harvester buttons
        createButtons();
        //creates database
        myDb = new DatabaseHelper(this);
        //creates method for add data button
        addData();
        //creates method for view data button
        viewData();
        //set initial values for database
        myDb.insertData("total", "0", "0", "0");
        myDb.insertData("piezo", "0", "0", "0");
        myDb.insertData("thermal", "0", "0", "0");
        myDb.insertData("solar", "0", "0", "0");
        //blue initilization for window
        messages = (TextView) findViewById(R.id.messages2);
        uart = new BluetoothLeUart(getApplicationContext());
        messages.setMovementMethod(new ScrollingMovementMethod());

        final boolean wasBluetoothEnabled = manageBluetoothAvailability();
        if(wasBluetoothEnabled){
            ToggleButton BluetoothUART = (ToggleButton)findViewById(R.id.collectDataButton);
            BluetoothUART.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        dataconnectionStatus = true;
                    } else {
                        // The toggle is disabled
                        dataconnectionStatus = false;
                    }
                }
            });


//                    BluetoothUART.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dataconnectionStatus = !dataconnectionStatus;
//                        }
//                    });
                }

    }

    public void addData(){

        addDataBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tableType ==""){
                            Toast.makeText(MainActivity.this, "Please select a graph", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (editPower.getText().toString() == "" || editVoltage.getText().toString() == "" || editCurrent.getText().toString() == "") {
                            Toast.makeText(MainActivity.this, "Text fields left empty. Please enter a numerical value.", Toast.LENGTH_LONG).show();
                            editPower.setText("");
                            editVoltage.setText("");
                            editCurrent.setText("");
                            return;
                        }
                        try {
                            // the String to int conversion happens here

                        } catch (NumberFormatException nfe) {
                            Toast.makeText(MainActivity.this, "Inserted value is not a valid number. Please enter a number.", Toast.LENGTH_LONG).show();
                            editPower.setText("");
                            editVoltage.setText("");
                            editCurrent.setText("");
                            return;
                        }

                        boolean dataInserted = myDb.insertData(tableType, editPower.getText().toString(),
                                editVoltage.getText().toString(),
                                editCurrent.getText().toString());
                        //sends message was inserted
                        if (dataInserted == true) {
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        }
                        //if not data was inserted, send error message
                        else
                            Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                        editPower.setText("");
                        editVoltage.setText("");
                        editCurrent.setText("");


                    }
                }
        );

        clearDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingDataStatus = false;
                myDb.clearTable(tableType);
            }
        });
    }

    public void viewData(){
        //method to create a view database button
        //used to troubleshoot database

        viewDatabase.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StringBuffer buffer = new StringBuffer();
                        if (tableType.equals("total")) {
                            Cursor res1 = myDb.getTotalData();

                            if (res1.getCount() == 0) {
                                //show message if database has no data
                                showMessage("error", "no data");
                                return;
                            }

                            while (res1.moveToNext()) {
                                buffer.append("ID: " + res1.getString(0) + " Date: "
                                        + res1.getString(1)+ " Power: "
                                        + res1.getString(2) + " Voltage: "
                                        + res1.getString(3)+ " Current: "
                                        + res1.getString(4)+"\n\n");
//                                buffer.append("DateTime: " + res1.getString(1) + "\n");
//                                buffer.append("Power: " + res1.getString(2) + "\n");
//                                buffer.append("Voltage: " + res1.getString(3) + "\n");
//                                buffer.append("Current: " + res1.getString(4) + "\n\n");
                            }
                        }
                        if (tableType.equals("piezo")) {
                            Cursor res2 = myDb.getPiezoData();

                            if (res2.getCount() == 0) {
                                //show message if database has no data
                                showMessage("error", "no data");
                                return;
                            }

                            while (res2.moveToNext()) {
                                buffer.append("ID: " + res2.getString(0) + " Date: "
                                        + res2.getString(1)+ " Power: "
                                        + res2.getString(2) + " Voltage: "
                                        + res2.getString(3)+ " Current: "
                                        + res2.getString(4)+"\n\n");

//                                buffer.append("ID: " + res2.getString(0) + "\n");
//                                buffer.append("DateTime: " + res2.getString(1) + "\n");
//                                buffer.append("Power: " + res2.getString(2) + "\n");
//                                buffer.append("Voltage: " + res2.getString(3) + "\n");
//                                buffer.append("Current: " + res2.getString(4) + "\n\n");
                            }


                        }
                        if (tableType.equals("thermal")) {
                            Cursor res3 = myDb.getThermalData();

                            if (res3.getCount() == 0) {
                                //show message if database has no data
                                showMessage("error", "no data");
                                return;
                            }

                            while (res3.moveToNext()) {
                                buffer.append("ID: " + res3.getString(0) + " Date: "
                                        + res3.getString(1)+ " Power: "
                                        + res3.getString(2) + " Voltage: "
                                        + res3.getString(3)+ " Current: "
                                        + res3.getString(4)+"\n\n");
//                                buffer.append("ID: " + res3.getString(0) + "\n");
//                                buffer.append("DateTime: " + res3.getString(1) + "\n");
//                                buffer.append("Power: " + res3.getString(2) + "\n");
//                                buffer.append("Voltage: " + res3.getString(3) + "\n");
//                                buffer.append("Current: " + res3.getString(4) + "\n\n");
                            }

                        }
                        if (tableType.equals("solar")) {
                            Cursor res4 = myDb.getSolarData();

                            if (res4.getCount() == 0) {
                                //show message if database has no data
                                showMessage("error", "no data");
                                return;
                            }

                            while (res4.moveToNext()) {
                                buffer.append("ID: " + res4.getString(0) + " Date: "
                                        + res4.getString(1)+ " Power: "
                                        + res4.getString(2) + " Voltage: "
                                        + res4.getString(3)+ " Current: "
                                        + res4.getString(4)+"\n\n");
//                                buffer.append("ID: " + res4.getString(0) + "\n");
//                                buffer.append("DateTime: " + res4.getString(1) + "\n");
//                                buffer.append("Power: " + res4.getString(2) + "\n");
//                                buffer.append("Voltage: " + res4.getString(3) + "\n");
//                                buffer.append("Current: " + res4.getString(4) + "\n\n");
                            }
                        }
                        //show all data
                        showMessage("Data", buffer.toString());

                    }
                }
        );
    }
    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();


    }
    private void createButtons(){
        //creates graph variable connected with graph object
        final GraphView graph = (GraphView) findViewById(R.id.graph);

        //labels vertical and horizontal axis of graph
        graph.getGridLabelRenderer().setVerticalAxisTitle("Voltage(V)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time(s)");

        //  Custom Viewport
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(false);
     //   viewport.setYAxisBoundsManual(true);
     //   viewport.setMinX(0);
        viewport.setMinY(0);

        //creates instance for each respective button
        ImageButton homeButton = (ImageButton) findViewById(R.id.homeBtn);
        ImageButton thermalButton = (ImageButton) findViewById(R.id.thermalButton);
        ImageButton piezoButton = (ImageButton) findViewById(R.id.piezoButton);
        ImageButton solarButton = (ImageButton) findViewById(R.id.solarButton);

        //adds onclick listener to each button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableType = "total";
                //resets graph
                graph.removeAllSeries();
                //addEntry();
                graph.setTitle("Total Harvester Voltage vs Time");
                totalSeries.setColor(Color.GREEN);
                totalSeries.setDrawBackground(true);
                graph.addSeries(totalSeries);
            }
        });


        //adds onclick listener to thermal button
        thermalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableType = "thermal";
                //when clicked creates current thermal graph
                //data points
                graph.removeAllSeries();
                //generate new points
               // addEntry();
                graph.setTitle("Thermal Harvester Voltage vs Time");
                thermalSeries.setColor(Color.RED);
                thermalSeries.setDrawBackground(true);
                graph.addSeries(thermalSeries);

            }
        });
        piezoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableType = "piezo";
                graph.removeAllSeries();
              //  addEntry();
                graph.setTitle("Piezo Harvester Voltage vs Time");
                piezoSeries.setColor(Color.BLUE); piezoSeries.setDrawBackground(true);
                //piezoSeries.setSpacing(50);
                graph.addSeries(piezoSeries);
            }
        });
        solarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableType = "solar";
                //resets graph
                graph.removeAllSeries();
               // addEntry();
                //adds new data points
                graph.setTitle("Solar Harvester Voltage vs Time");
                solarSeries.setColor(Color.YELLOW);
                solarSeries.setDrawBackground(true);
                graph.addSeries(solarSeries);
            }
        });

        //edit text fields for testing
        editPower   = (EditText)findViewById(R.id.PowerField);
        editVoltage = (EditText)findViewById(R.id.VoltageField);
        editCurrent = (EditText)findViewById(R.id.CurrentField);

        //add data and view database buttons
        addDataBtn = (Button)findViewById(R.id.addDataButton);
        viewDatabase = (Button)findViewById(R.id.viewDatabasebutton);
        clearDataBtn = (Button)findViewById(R.id.clearButton);


    }
    private void addEntry() {
//        double doublePower;
//        int currentID;
//        if(tableType.equals("total")) {
//            Cursor resultTable = myDb.getTotalData();
//            resultTable.moveToLast();
//            doublePower = Double.parseDouble(resultTable.getString(2));
//            currentID = Integer.parseInt(resultTable.getString(0));
//           //totalSeries.resetData(new DataPoint[]{new DataPoint(0,0), new DataPoint(0,doublePower)});
//            totalSeries.appendData(new DataPoint(currentID, doublePower), true, 512);
//        }
//        if(tableType.equals("piezo")) {
//            Cursor resultTable = myDb.getPiezoData();
//            resultTable.moveToLast();
//            doublePower = Double.parseDouble(resultTable.getString(2));
//            currentID = Integer.parseInt(resultTable.getString(0));
//           // piezoSeries.resetData(new DataPoint[]{new DataPoint(0, 0), new DataPoint(0, doublePower)});
//            piezoSeries.appendData(new DataPoint(currentID, doublePower), true, 512);
//        }
//        if(tableType.equals("thermal")) {
//            Cursor resultTable = myDb.getThermalData();
//            resultTable.moveToLast();
//            doublePower = Double.parseDouble(resultTable.getString(2));
//            currentID = Integer.parseInt(resultTable.getString(0));
//           // thermalSeries.resetData(new DataPoint[]{new DataPoint(0,0), new DataPoint(0,doublePower)});
//            thermalSeries.appendData(new DataPoint(currentID, doublePower), true, 512);
//        }
//        if(tableType.equals("solar")) {
//            Cursor resultTable = myDb.getSolarData();
//            resultTable.moveToLast();
//            doublePower = Double.parseDouble(resultTable.getString(2));
//            currentID = Integer.parseInt(resultTable.getString(0));
//            //solarSeries.resetData(new DataPoint[]{new DataPoint(0, 0), new DataPoint(0, doublePower)});
//            solarSeries.appendData(new DataPoint(currentID, doublePower), true, 512);
//        }
    }
   @Override
    protected void onResume() {
        super.onResume();
       //thread that appends data to chart

        new Thread(new Runnable() {
            @Override
            public void run() {
                //100 data points
                for (int i = 0; i<100001; i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(addingDataStatus ==true){
                            addEntry();}
                        }
                    });
                    //Sleep added to slow down additions to graph
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                       // e.printStackTrace();
                    }
                }
            }
        }).start();

       writeLine("Scanning for devices ...");
       uart.registerCallback(this);
       uart.connectFirstAvailable();

    }
    private boolean manageBluetoothAvailability() {
        boolean isEnabled = true;

        // Check Bluetooth HW status
        int errorMessageId = 0;
        final int bleStatus = BleUtils.getBleStatus(getBaseContext());
        switch (bleStatus) {
            case BleUtils.STATUS_BLE_NOT_AVAILABLE:
                errorMessageId = R.string.dialog_error_no_ble;
                isEnabled = false;
                break;
            case BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE: {
                errorMessageId = R.string.dialog_error_no_bluetooth;
                isEnabled = false;      // it was already off
                break;
            }
            case BleUtils.STATUS_BLUETOOTH_DISABLED: {
                isEnabled = false;      // it was already off
                // if no enabled, launch settings dialog to enable it (user should always be prompted before automatically enabling bluetooth)
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, kActivityRequestCode_EnableBluetooth);
                // execution will continue at onActivityResult()
                break;
            }
        }
        if (errorMessageId > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setMessage(errorMessageId)
                    .setPositiveButton(R.string.dialog_ok, null)
                    .show();
            //DialogUtils.keepDialogOnOrientationChanges(dialog);
        }

        return isEnabled;
    }

    @Override
    protected void onStop() {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
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
        int flagMask = 0xc0;
        int flagCheck = 0;
        int gainCheck =0;
        double actualGain =0;
        int actualChannel =8;
        int channelCheck =0;
        boolean isVoltage = true;
        double voltageResult =0;
        double currentResult = 0;
        double powerResult = 0;
        byte[] dataCollected = rx.getValue();



        for (int i = 0; i < dataCollected.length; i += 3) {
            Log.w("DIS", "Packet length: " + dataCollected.length);
            Log.w("DIS", "Flag Byte: " + dataCollected[i]);
            flagCheck = dataCollected[i] & flagMask;
            if(flagCheck == 0xC0){
                gainCheck = (dataCollected[i] & 0x38) >>3;
                switch (gainCheck){
                    case 0: actualGain = 6.144; break;
                    case 1: actualGain = 4.069; break;
                    case 2: actualGain = 2.048; break;
                    case 3: actualGain = 1.024; break;
                    case 4: actualGain = 0.512; break;
                    case 5: actualGain = 0.256; break;
                    default:actualGain = 0; break;
                }
                if(actualGain !=0){
                    actualChannel = dataCollected[i] & 0x7;
                    Log.w("DIS", "CHANNEL# : " + actualChannel);
//                    switch (channelCheck){
//                        case 0: actualChannel = 0; break;
//                        case 1: actualChannel = 1; break;
//                        case 2: actualChannel = 2; break;
//                        case 3: actualChannel = 3; break;
//                        case 4: actualChannel = 4; break;
//                        case 5: actualChannel = 5; break;
//                        case 6: actualChannel = 6; break;
//                        case 7: actualChannel = 7; break;
//                        default:actualChannel = 8; break;
//                    }
                 }else{
                    return;
                }
            }else{
                return;
            }

                switch (actualChannel) {
                case 0:
                    voltageResult = (actualGain / 32768) * ((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff);

                    break;
                case 1:
                    voltageResult = (actualGain / 32768) * ((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff);

                    break;
                case 2:
                    voltageResult = (actualGain / 32768) * ((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff);

                    break;
                case 3:
                    voltageResult = (actualGain / 32768) * ((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff);

                    break;
                case 4:
                    currentResult = 1 ;
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
                    receiveTableType = "solar";
                    break;
                case 5:
                    currentResult = 1 ;
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
                    receiveTableType = "thermal";
                    break;
                case 6:
                    currentResult = 1 ;
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
                    receiveTableType = "piezo";
                    break;
                case 7:
                    currentResult = 1 ;
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
                    receiveTableType = "total";
                    break;
                default:
                    break;
            }
//            if(isVoltage ==false){
//            switch (actualChannel) {
//                case 0:
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
//                    receiveTableType = "solar";
//                    isVoltage = true;
//                    break;
//                case 1:
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
//                    receiveTableType = "thermal";
//                    isVoltage = true;
//                    break;
//                case 2:
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
//                    receiveTableType = "piezo";
//                    isVoltage = true;
//                    break;
//                case 3:
//                    currentResult = (actualGain / 32768) * (((dataCollected[i + 1]) << 8 | (dataCollected[i + 2]) & 0xff) / (0.5));
//                    receiveTableType = "total";
//                    break;
//                default:
//                    break;
//            }}
           // && disconnect ==0

            if(dataconnectionStatus && i %3  == 0 && i!=0 ) {
                powerResult = voltageResult * currentResult;
                Log.w("DIS", "Channel: " +actualChannel+" Voltage: " + voltageResult + " Current: "+ currentResult+ " power: " +powerResult);
                myDb.insertData(receiveTableType, String.valueOf(powerResult), String.valueOf(voltageResult), String.valueOf(currentResult));
                double doublePower;
                int currentID;

                if(addingDataStatus ==true){

                switch(receiveTableType){
                    case"total":
                        Cursor resultTable1 = myDb.getTotalData();
                        resultTable1.moveToLast();
                        doublePower = Double.parseDouble(resultTable1.getString(2));
                        currentID = Integer.parseInt(resultTable1.getString(0));
                        totalSeries.appendData(new DataPoint(currentID, doublePower), true, 512);break;
                    case"solar": Cursor resultTable2 = myDb.getSolarData();
                        resultTable2.moveToLast();
                        doublePower = Double.parseDouble(resultTable2.getString(2));
                        currentID = Integer.parseInt(resultTable2.getString(0));

                        solarSeries.appendData(new DataPoint(currentID, doublePower), true, 512);break;
                    case"piezo":
                        Cursor resultTable3 = myDb.getPiezoData();
                        resultTable3.moveToLast();
                        doublePower = Double.parseDouble(resultTable3.getString(2));
                        currentID = Integer.parseInt(resultTable3.getString(0));piezoSeries.appendData(new DataPoint(currentID, doublePower), true, 512);break;
                    case"thermal":
                        Cursor resultTable4 = myDb.getThermalData();
                        resultTable4.moveToLast();
                        doublePower = Double.parseDouble(resultTable4.getString(2));
                        currentID = Integer.parseInt(resultTable4.getString(0));
                        thermalSeries.appendData(new DataPoint(currentID, doublePower), true, 512);break;
                    default: break;

                }}
                voltageResult =0;currentResult =0;


            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
