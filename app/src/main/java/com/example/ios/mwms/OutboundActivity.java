package com.example.ios.mwms;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OutboundActivity extends AppCompatActivity {

    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID=0;
    DatePicker mDatePicker;
    TextView mTxtDate;
    private View mProgressView;
    private FloatingGroupExpandableListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbound);

        mDatePicker = (DatePicker)findViewById(R.id.datePicker);
        mTxtDate = (TextView)findViewById(R.id.textView);
        mProgressView = findViewById(R.id.stock_progress);

        FloatingGroupExpandableListView myList = (FloatingGroupExpandableListView) findViewById(R.id.outbound_list);
        BaseExpandableListAdapter myAdapter = new SampleAdapter(this);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(myAdapter);
        myList.setAdapter(wrapperAdapter);

        mDatePicker.init(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener()  {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)  {
                mTxtDate.setText(String.format("%d/%d/%d", year, monthOfYear+1, dayOfMonth));
                //getOutboundList();
            }

        });

        /*
        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IOS log", "clicked");
            }
        });
        */


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    public void getOutboundList()  {
        // get mTxtDate to string
        // Async task call
        showProgress(true);
        NetworkStockTask NetworkStockTask = new NetworkStockTask();
        NetworkStockTask.execute("");
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            myList.setVisibility(show ? View.GONE : View.VISIBLE);
            myList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    myList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            myList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class NetworkStockTask extends AsyncTask<String, Void, StringBuilder> {
        String strUrl = null;
        String strCookie = null;
        String result = null;

        URL wmsUrl = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            strUrl = "http://192.168.30.35:9080/delegateAndroidDao/testSelect.jsp?command=LoadOutboundOrder";
        }

        @Override
        protected StringBuilder doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            StringBuilder builder = new StringBuilder();
            try {
                wmsUrl = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) wmsUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);


                OutputStream os = conn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));


                //result.append(URLEncoder.encode("brandNm", "UTF-8"));
                result.append("brandNm");
                result.append("=");
                //result.append(mBrandNm);
                bw.write(result.toString());
                bw.flush();
                bw.close();
                os.close();


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));//, conn.getContentLength());
                String line;

                while ((line = reader.readLine()) != null) {
                    //builder.append(line + "\n");
                    builder.append(line);
                }
                strCookie = conn.getHeaderField("Set-Cookie");

                //result = builder.toString();
                //Log.d("result is: ", result);
                Log.d("Builder is ", builder.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException io) {
                io.printStackTrace();
            }
            return builder;
        }

        @Override
        protected void onPostExecute(StringBuilder stb) {
            Log.d("onPostExecute ", stb.toString());
            showProgress(false);

            if (stb.toString().length() == 2)  { // no brand stock data from the server, '2' means the brackets in the JSON format
                //Toast.makeText(StockInfoActivity.this, "The retrieved data is zero", Toast.LENGTH_LONG);
                noDataDlg();
            } else {
                try {
                    JSONArray jsArray = new JSONArray(stb.toString());

                    for (int x = 0; x < jsArray.length(); x++) {
                        JSONObject tmpJsonObj = jsArray.getJSONObject(x);
                    }
                } catch (JSONException e) {

                }
            }
        }

        private void noDataDlg()  {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(OutboundActivity.this);
            alt_bld.setMessage("The retrieved data is zero").setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener()  {
                        public void onClick(DialogInterface dialog, int id)  {
                        }
                    });
            AlertDialog alert = alt_bld.create();
            alert.setTitle("Information");
            alert.setIcon(R.drawable.ic_account_box_black_36dp);
            alert.show();
        }

    }
}
