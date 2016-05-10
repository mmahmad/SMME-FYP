package com.smarteye.example.smarteye;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SeekBar seekBarHorizontal;
    SeekBar seekBarVertical;
    ViewGroup viewGroup;
    RelativeLayout relativeLayout;

    TextView textViewVerticalVal;
    TextView textViewHorizontalVal;

    String serverIP = "192.168.8.100";
    int serverPort = 10014;

    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        relativeLayout = (RelativeLayout) findViewById(R.id.relateLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new connectToServer().execute();
                // TODO: Change fab image resource to unlocked padlock.


            }
        });

        seekBarHorizontal = (SeekBar) findViewById(R.id.seekBarHorizontal);
        seekBarVertical = (SeekBar) findViewById(R.id.seekBarVertical);

        seekBarHorizontal.setProgress(0);
        seekBarHorizontal.incrementProgressBy(1);
        seekBarHorizontal.setMax(100);


        seekBarVertical.setProgress(0);
        seekBarVertical.incrementProgressBy(1);
        seekBarVertical.setMax(100);

        textViewHorizontalVal = (TextView) findViewById(R.id.textViewHorizontalVal);
        textViewVerticalVal = (TextView) findViewById(R.id.textViewVerticalVal);

        seekBarVertical.setOnSeekBarChangeListener(this);
        seekBarHorizontal.setOnSeekBarChangeListener(this);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        // for continuous changes. Each value will be received in the progress var.

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        // when start dragging the bar, or touched.

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int progress = seekBar.getProgress();


        if (seekBar == seekBarVertical) {
            if (connectionAvailable()) {

                new sendToServer().execute("v" + progress);
                textViewVerticalVal.setText("" + progress);

            }


        } else if (seekBar == seekBarHorizontal) {

            if (connectionAvailable()) {

                new sendToServer().execute("h" + progress);
                textViewHorizontalVal.setText("" + progress);

            }
        }


    }

    public boolean connectionAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // connection available
            return true;
        } else {
            return false;
        }
    }

    private class connectToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                clientSocket = new Socket(serverIP, serverPort);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {

                e.printStackTrace();
                // TODO: Alert Dialog on runOnUiThread(), compatible with Theme.AppCompat, to give error

            }


            return null;
        }
    }

    private class sendToServer extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {

            try {
                out.println(params[0]);
                Log.d("Eye", "sent data" + params[0]);
            } catch (Exception ex){
                ex.printStackTrace();

                // TODO: Alert Dialog on runOnUiThread(), compatible with Theme.AppCompat, to give error



            }


            return null;
        }


    }

    @Override
    public void onStop() {
        super.onStop();


        if (in != null)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (out != null)
            out.close();
        if (clientSocket != null)
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onPause(){
        super.onPause();


        if (in != null)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (out != null)
            out.close();
        if (clientSocket != null)
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onResume(){
        super.onResume();

        new connectToServer().execute();

    }


}
