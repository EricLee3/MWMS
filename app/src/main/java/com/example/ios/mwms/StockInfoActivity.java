package com.example.ios.mwms;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class StockInfoActivity extends AppCompatActivity {
    ListView listview;
    ListViewAdapter adapter;
    Intent intent = null;
    String mBrandNm = null;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);

        mProgressView = findViewById(R.id.stock_progress);
        adapter = new ListViewAdapter();
        listview = (ListView) findViewById(R.id.ListViewStockInfo);
        listview.setAdapter(adapter);

        intent = getIntent();
        mBrandNm = intent.getStringExtra("brandNm");
        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id)   {
                //String toastMessage = (String)((adapterView.getAdapter().getItem(pos)).getClass().getName());
                // get brand_nm from the view IOS
                String strBrandNm = ((ListViewItem)((ListView) adapterView).getAdapter().getItem(pos)).getTitle();
                //Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                intent = getIntent();
                intent.putExtra("strBrandNm", strBrandNm);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        */
        showProgress(true);
        NetworkStockTask NetworkStockTask = new NetworkStockTask();
        NetworkStockTask.execute("");
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

            listview.setVisibility(show ? View.GONE : View.VISIBLE);
            listview.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listview.setVisibility(show ? View.GONE : View.VISIBLE);
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
            listview.setVisibility(show ? View.GONE : View.VISIBLE);
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
            strUrl = "http://192.168.30.35:9080/delegateAndroidDao/testSelect.jsp?command=LoadStock";
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
                result.append(mBrandNm);
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
                        addList(tmpJsonObj);
                    }
                } catch (JSONException e) {

                }
                adapter.notifyDataSetChanged();
            }
        }

        private void noDataDlg()  {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(StockInfoActivity.this);
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

    public void addList(JSONObject jsonObj) throws JSONException {
        String desc="Warehouse : " + jsonObj.getString("CENTER_CD") + "\n" + "item state : " + jsonObj.getString("ITEM_STATE") + "\n" + "stock qty : " + jsonObj.getString("STOCK_QTY");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_box_black_36dp), jsonObj.getString("ITEM_CD"), desc);
    }

    public void btnStart(View v) {
        NetworkStockTask NetworkStockTask = new NetworkStockTask();
        NetworkStockTask.execute("");
    }
}
