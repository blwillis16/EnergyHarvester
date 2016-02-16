package com.bobbylwillis.energyharvester;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button viewDatabase;
    EditText editPower, editVoltage, editCurrent;
    String tableType = "";
    Button addDataBtn;
    BarGraphSeries<DataPoint> thermalSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,0),
    });
    BarGraphSeries<DataPoint> totalSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,0),
    });
    BarGraphSeries<DataPoint> solarSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,0),
    });
    BarGraphSeries<DataPoint> piezoSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0,0),
    });
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
    }
    public void addData(){
        addDataBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

                        Cursor res = myDb.getTotalData();

                        if (res.getCount() == 0) {
                            //show message if database has no data
                            showMessage("error", "no data");
                            return;
                        }
                        StringBuffer buffer = new StringBuffer();

                        while (res.moveToNext()) {
                            buffer.append("ID: " + res.getString(0) + "\n");
                            buffer.append("DateTime: "+ res.getString(1)+ "\n");
                            buffer.append("Power: " + res.getString(2) + "\n");
                            buffer.append("Voltage: " + res.getString(3) + "\n");
                            buffer.append("Current: " + res.getString(4) + "\n\n");
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
                //makes home graph
                //resets graph
                graph.removeAllSeries();
                addEntry();
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
           //     addEntry();
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
               // addEntry
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
        double doublePower = 0.0;

        if(tableType.equals("total")) {
            Cursor resultTable = myDb.getTotalData();
            resultTable.moveToLast();
            doublePower = Integer.parseInt(resultTable.getString(3));
            totalSeries.appendData(new DataPoint(1, doublePower), true, 2);
        }
        if(tableType.equals("piezo")) {
            Cursor resultTable = myDb.getTotalData();
            resultTable.moveToLast();
            doublePower = Integer.parseInt(resultTable.getString(3));
            piezoSeries.appendData(new DataPoint(1, doublePower), true, 2);
        }
        if(tableType.equals("thermal")) {
            Cursor resultTable = myDb.getTotalData();
            resultTable.moveToLast();
            doublePower = Integer.parseInt(resultTable.getString(3));
            thermalSeries.appendData(new DataPoint(1, doublePower), true, 2);
        }
        if(tableType.equals("solar")) {
            Cursor resultTable = myDb.getTotalData();
            resultTable.moveToLast();
            doublePower = Integer.parseInt(resultTable.getString(3));
            solarSeries.appendData(new DataPoint(1, doublePower), true, 2);
        }
    }


//   @Override
//    protected void onResume() {
//        super.onResume();
//       //thread that appends data to chart
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //100 data points
//                for (int i = 0; i<100; i++){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//                    //Sleep added to slow down additions to graph
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//                       // e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//    }
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
