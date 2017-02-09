package com.example.ios.mwms;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChooseBrandActivity extends AppCompatActivity {
    static final String[] LIST_MENU = {"List1", "List2", "List3"};
    ListView listview;
    ListViewAdapter adapter;
    Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_brand);

        adapter = new ListViewAdapter();
        listview = (ListView) findViewById(R.id.listView1);
        listview.setAdapter(adapter);

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
        NetworkTask networkTask = new NetworkTask();
        networkTask.execute("");
    }

    public class NetworkTask extends AsyncTask<String, Void, StringBuilder> {
        String strUrl = null;
        String strCookie = null;
        String result = null;
        StringBuilder builder = null;

        java.net.URL Url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            strUrl = "http://192.168.30.35:9080/delegateAndroidDao/testSelect.jsp?command=LoadData";
            if (builder != null)
                builder.setLength(0);
        }

        @Override
        protected StringBuilder doInBackground(String... params) {
            try {
                Url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                strCookie = conn.getHeaderField("Set-Cookie");
                InputStream is = conn.getInputStream();

                builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;

                while ((line = reader.readLine()) != null) {
                    //builder.append(line + "\n");
                    builder.append(line);
                }
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

            try  {
                JSONArray jsArray = new JSONArray(stb.toString());

                for (int x = 0; x < jsArray.length(); x++)  {
                    JSONObject tmpJsonObj = jsArray.getJSONObject(x);
                    addList(tmpJsonObj);
                }
            } catch (JSONException e)  {

            }
            adapter.notifyDataSetChanged();

        }
    }

    public void addList(JSONObject jsonObj) throws JSONException {
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_box_black_36dp), jsonObj.getString("BRAND_NM"), "");
    }

    public void btnStart(View v) {
        NetworkTask networkTask = new NetworkTask();
        networkTask.execute("");
    }
}
