package com.example.ios.mwms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Intent intent = null;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button mChooseBrandButton = (Button) findViewById(R.id.btn_Choose_Brand);
        mChooseBrandButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view)  {
                //getBrandList();
                //Toast.makeText(getApplicationContext(), "onClick clicked", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, ChooseBrandActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        Button mGetStockInfo = (Button) findViewById(R.id.btn_Stock_Info);
        mGetStockInfo.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view)  {
                TextView tvBrandNm = (TextView)findViewById(R.id.BrandNm);
                if (tvBrandNm.getText() == "") {
                    Toast.makeText(getApplicationContext(), "You should select brand name first", Toast.LENGTH_LONG).show();
                } else {
                    Intent stockIntent = new Intent(MainActivity.this, StockInfoActivity.class);
                    stockIntent.putExtra("brandNm", tvBrandNm.getText());
                    startActivity(stockIntent);
                }
            }
        });

        Button mLogOut = (Button) findViewById(R.id.btn_LogOut);
        mLogOut.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view)  {
                Intent logOutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logOutIntent);
            }
        });
        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)  {
            if (requestCode == 1)  {
                TextView tvBrandNm = (TextView)findViewById(R.id.BrandNm);
                tvBrandNm.setText(data.getStringExtra("strBrandNm"));
                //Toast.makeText(getApplicationContext(), data.getStringExtra("strBrandNm"), Toast.LENGTH_LONG).show();
            }

        }
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
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
