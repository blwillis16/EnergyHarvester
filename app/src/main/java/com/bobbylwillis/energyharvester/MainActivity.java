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
    Button addDataBtn;
    BarGraphSeries<DataPoint> thermalSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
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
                          boolean dataInserted =  myDb.insertData(editPower.getText().toString(),
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
                        Cursor res = myDb.getData();
                        if (res.getCount() == 0) {
                            //show message if database has not buttons
                            showMessage("error", "no data");
                            return;
                        }
                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("ID: " + res.getString(0) + "\n");
                            //buffer.append("DateTime: "+ res.getString(1)+ "\n");
                            buffer.append("Power: " + res.getString(3) + "\n");
                            buffer.append("Voltage: " + res.getString(4) + "\n");
                            buffer.append("Current: " + res.getString(5) + "\n\n");
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
                //makes home graph
                //resets graph
                graph.removeAllSeries();
                //creates new data points
                BarGraphSeries<DataPoint> homeSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0,0),
                        new DataPoint(1,5),

                });
                graph.setTitle("Total Harvester Power vs Time");
                homeSeries.setColor(Color.GREEN);
                graph.addSeries(homeSeries);
            }
        });
        //adds onclick listener to thermal button
        thermalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when clicked creates current thermal graph
                //data points
                graph.removeAllSeries();
                //generate new points
                addEntry();

                graph.setTitle("Piezo Harvester Power vs Time");
                thermalSeries.setColor(Color.RED);
                graph.addSeries(thermalSeries);

            }
        });
        piezoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                graph.removeAllSeries();
                //generate new points
                BarGraphSeries<DataPoint> piezoSeries = new BarGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, 0),
                        new DataPoint(1, 5)
                });
                graph.setTitle("Piezo Harvester Power vs Time");
                piezoSeries.setColor(Color.BLUE);
                piezoSeries.setSpacing(50);
                graph.addSeries(piezoSeries);
            }
        });
        solarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resets graph
                graph.removeAllSeries();
                //adds new data points
                BarGraphSeries<DataPoint> solarSeries = new BarGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(0, 0),
                        new DataPoint(1, 5)
                });
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

        Cursor resultTable = myDb.getData();
        resultTable.moveToLast();
        int doublePower = Integer.parseInt(resultTable.getString(3));
        thermalSeries.appendData(new DataPoint(1, doublePower), true, 2);

    }

//        while(resultTable.moveToFirst()) {
//            int doublePower = Integer.parseInt(resultTable.getString(3));
//            thermalSeries.appendData(new DataPoint(1, doublePower), true, 1);
//
//        }
//    }
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
