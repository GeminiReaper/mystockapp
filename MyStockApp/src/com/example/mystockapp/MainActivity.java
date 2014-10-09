package com.example.mystockapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	TextView company;
	TextView price;
	EditText symbol;
	Button search;
	String URL = "http://finance.yahoo.com/webservice/v1/symbols/";
	String urlEnd = "/quote?format=json";
	String stockCompany;
	double stockPrice;
	URL url = null;

	JSONObject jsonMain;
	JSONArray stockArray;

	Message msg;
	String jsonString = "";
	String tmpJSON = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		company = (TextView) findViewById(R.id.company);
		price = (TextView) findViewById(R.id.stockPrice);
		symbol = (EditText) findViewById(R.id.stockSymbol);
		search = (Button) findViewById(R.id.search);

		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Thread loadContent = new Thread() {
					@Override
					public void run(){

						try {
							String urlJSON = "";

							stockCompany = symbol.getText().toString();
							urlJSON = URL + stockCompany + urlEnd;
							Log.i("URL: ", urlJSON.toString());
							
							url = new URL(urlJSON);
							BufferedReader br = new BufferedReader(
									new InputStreamReader(url.openStream()));
							
							StringBuilder stringBuilder = new StringBuilder();
							
							while((tmpJSON = br.readLine()) != null) {
								stringBuilder.append(tmpJSON + "\n");
							}
							
							jsonString = stringBuilder.toString();
							
							Log.i("JSON STRING: ", jsonString);
							msg = Message.obtain();
							msg.obj = jsonString;

							displayURL.sendMessage(msg);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}//end run
				}; //end thread
				loadContent.start();

			}
		}); //end search OnclickListener
	}//end oncreate

	public boolean isNetworkActive(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	Handler displayURL = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			try {
				jsonMain = new JSONObject(msg.obj.toString());
				Log.i("JSON Obj1: ", jsonMain.toString());
				JSONObject obj2 = jsonMain.getJSONObject("list");
				Log.i("JSON Obj2: ", obj2.toString());
				stockArray = obj2.getJSONArray("resources");
				Log.i("JSON Obj2 Array: ", stockArray.toString());
				JSONObject obj3 = stockArray.getJSONObject(0);
				Log.i("JSON Obj3: ", obj3.toString());
				JSONObject obj4 = obj3.getJSONObject("resource");
				Log.i("JSON Obj4: ", obj4.toString());
				JSONObject obj5 = obj4.getJSONObject("fields");
				Log.i("JSON Obj5: ", obj5.toString());
				stockCompany = obj5.getString("name");
				Log.i("JSON symbol: ", stockCompany);
				stockPrice = obj5.getDouble("price");
				Log.i("JSON stockprice: ", String.valueOf(stockPrice));

				Log.i("Stock Symbol: ", stockCompany);
				Log.i("Stock Price: ", String.valueOf(stockPrice));

				price.setText(String.valueOf(stockPrice));
				company.setText(stockCompany);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return false;
		}
	});
}

