package hu.ekf.stopperapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends ListActivity {

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    private Button pauseButton;
    private Button startButton;
    private Button newLapButton;
    private TextView stopperValue;
    private long startTime = 0L;

    private String currentTime;
    private Handler customHandler = new Handler();

    long timeMillisec = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.clockpackage");
                startActivity(launchIntent);
            }
        });
        stopperValue = (TextView) findViewById(R.id.stopperValue);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                startButton.setEnabled(false);
                startButton.setText("Start");
                pauseButton.setEnabled(true);
                newLapButton.setEnabled(true);
        }
        });

        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeSwapBuff += timeMillisec;
                customHandler.removeCallbacks(updateTimerThread);
                startButton.setEnabled(true);
                startButton.setText("Folytatás");
                pauseButton.setEnabled(false);
                newLapButton.setEnabled(false);
            }
        });

        pauseButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {

                customHandler.removeCallbacks(updateTimerThread);
                timeSwapBuff = 0;
                timeMillisec = 0;
                stopperValue.setText("00:00:00");

                startButton.setEnabled(true);
                newLapButton.setEnabled(false);
                pauseButton.setEnabled(false);
                listItems.clear();
                return true;
            }
        });



        newLapButton = (Button) findViewById(R.id.newLap);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);

        newLapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                currentTime = stopperValue.getText().toString();
                addItems(view);
            }
        });

    }
    public void addItems(View v) {
        listItems.add(currentTime);
        adapter.notifyDataSetChanged();
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeMillisec = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeMillisec;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            stopperValue.setText("" + mins + ":"
                            + String.format("%02d", secs) + ":"
                            + String.format("%02d", milliseconds));
            customHandler.postDelayed(this, 0);
        }
    };
}
