package com.bobbylwillis.energyharvester;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button viewDatabase;
    EditText editPower, editVoltage, editCurrent;
    String tableType = "";
    Button addDataBtn;
    //series variables
    BarGraphSeries<DataPoint> thermalSeries = new BarGraphSeries<DataPoint>(new DataPoint[]{new DataPoint(0,0)});
    BarGraphSeries<DataPoint> totalSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,0)});
    BarGraphSeries<DataPoint> solarSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,0)});
    BarGraphSeries<DataPoint> piezoSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,0)});

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

        Button BTbutton = (Button)findViewById(R.id.bluetoothbutton);
        BTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateBluetooth();
            }
        });
    }
    public void activateBluetooth(){

        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);

    }
    public void addData(){
        addDataBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editPower.getText().toString() == ""|| editVoltage.getText().toString() == "" ||editCurrent.getText().toString() =="" ){
                            Toast.makeText(MainActivity.this, "Text fields left empty. Please enter a numerical value.", Toast.LENGTH_LONG).show();
                            editPower.setText("");
                            editVoltage.setText("");
                            editCurrent.setText("");
                            return;
                        }
                        try
                        {
                            // the String to int conversion happens here
                            double checkPowerNumber = Double.parseDouble(editPower.getText().toString().trim());
                            double checkVoltageNumber = Double.parseDouble(editVoltage.getText().toString().trim());
                            double checkCurrentNumber = Double.parseDouble(editCurrent.getText().toString().trim());
                        }
                        catch (NumberFormatException nfe)
                        {
                            Toast.makeText(MainActivity.this, "Inserted value is not a valid number. Please enter a number.", Toast.LENGTH_LONG).show();
                            editPower.setText("");
                            editVoltage.setText("");
                            editCurrent.setText("");
                            return;
                        }
                          boolean dataInserted =  myDb.insertData(tableType,editPower.getText().toString(),
                                  editVoltage.getText().toString(),
                                  editCurrent.getText().toString());
                        //sends message was inserted
                        if(dataInserted == true){
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
                                buffer.append("ID: " + res1.getString(0) + "\n");
                                buffer.append("DateTime: " + res1.getString(1) + "\n");
                                buffer.append("Power: " + res1.getString(2) + "\n");
                                buffer.append("Voltage: " + res1.getString(3) + "\n");
                                buffer.append("Current: " + res1.getString(4) + "\n\n");
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
                                buffer.append("ID: " + res2.getString(0) + "\n");
                                buffer.append("DateTime: " + res2.getString(1) + "\n");
                                buffer.append("Power: " + res2.getString(2) + "\n");
                                buffer.append("Voltage: " + res2.getString(3) + "\n");
                                buffer.append("Current: " + res2.getString(4) + "\n\n");
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
                                buffer.append("ID: " + res3.getString(0) + "\n");
                                buffer.append("DateTime: " + res3.getString(1) + "\n");
                                buffer.append("Power: " + res3.getString(2) + "\n");
                                buffer.append("Voltage: " + res3.getString(3) + "\n");
                                buffer.append("Current: " + res3.getString(4) + "\n\n");
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
                                buffer.append("ID: " + res4.getString(0) + "\n");
                                buffer.append("DateTime: " + res4.getString(1) + "\n");
                                buffer.append("Power: " + res4.getString(2) + "\n");
                                buffer.append("Voltage: " + res4.getString(3) + "\n");
                                buffer.append("Current: " + res4.getString(4) + "\n\n");
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
        graph.getGridLabelRenderer().setVerticalAxisTitle("Power(uW)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time(s)");

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
                graph.setTitle("Total Harvester Power vs Time");
                totalSeries.setColor(Color.GREEN);
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
                graph.setTitle("Thermal Harvester Power vs Time");
                thermalSeries.setColor(Color.RED);
                graph.addSeries(thermalSeries);

            }
        });
        piezoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableType = "piezo";
                graph.removeAllSeries();
              //  addEntry();
                graph.setTitle("Piezo Harvester Power vs Time");
                piezoSeries.setColor(Color.BLUE);
                piezoSeries.setSpacing(50);
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
                graph.setTitle("Solar Harvester Power vs Time");
                solarSeries.setColor(Color.YELLOW);
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


    }
    private void addEntry() {
        double doublePower;

        if(tableType.equals("total")) {
            Cursor resultTable = myDb.getTotalData();
            resultTable.moveToLast();
            doublePower = Double.parseDouble(resultTable.getString(2));
            totalSeries.resetData(new DataPoint[]{new DataPoint(0,0), new DataPoint(0,doublePower)});
            //totalSeries.appendData(new DataPoint(1, doublePower), true, 2);
        }
        if(tableType.equals("piezo")) {
            Cursor resultTable = myDb.getPiezoData();
            resultTable.moveToLast();
            doublePower = Double.parseDouble(resultTable.getString(2));
            piezoSeries.resetData(new DataPoint[]{new DataPoint(0,0), new DataPoint(0,doublePower)});
        }
        if(tableType.equals("thermal")) {
            Cursor resultTable = myDb.getThermalData();
            resultTable.moveToLast();
            doublePower = Double.parseDouble(resultTable.getString(2));
            thermalSeries.resetData(new DataPoint[]{new DataPoint(0,0), new DataPoint(0,doublePower)});
        }
        if(tableType.equals("solar")) {
            Cursor resultTable = myDb.getSolarData();
            resultTable.moveToLast();
            doublePower = Double.parseDouble(resultTable.getString(2));
            solarSeries.resetData(new DataPoint[]{new DataPoint(0,0), new DataPoint(0,doublePower)});
        }
    }
   @Override
    protected void onResume() {
        super.onResume();
       //thread that appends data to chart
        new Thread(new Runnable() {
            @Override
            public void run() {
                //100 data points
                for (int i = 0; i<100; i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    //Sleep added to slow down additions to graph
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                       // e.printStackTrace();
                    }
                }
            }
        }).start();

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
