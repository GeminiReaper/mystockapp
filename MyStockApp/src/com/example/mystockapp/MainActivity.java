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
	
	//Initial Declarations/initialization
	TextView company;
	TextView price;
	EditText symbol;
	Button search;
	String URL = "http://finance.yahoo.com/webservice/v1/symbols/";
	String urlEnd = "/quote?format=json";
	String stockCompany;
	double stockPrice;
	URL url = null;

	Message msg;
	


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
							String jsonString = "";
							String tmpJSON = "";

							stockCompany = symbol.getText().toString();
							urlJSON = URL + stockCompany + urlEnd;
							Log.i("URL: ", urlJSON.toString());
							
							//Read in JSON String
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

							getJSONString.sendMessage(msg);

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

	Handler getJSONString = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			try {
				JSONObject jsonMain;
				JSONArray resourcesArray;
				JSONObject jsonList;
				JSONObject jsonArray;
				JSONObject jsonResource;
				JSONObject jsonAttribute;

				//Each getJSONObject goes deeper into the json string hierarchy
				jsonMain = new JSONObject(msg.obj.toString());
					jsonList = jsonMain.getJSONObject("list");
						resourcesArray = jsonList.getJSONArray("resources");
						jsonArray = resourcesArray.getJSONObject(0);
							jsonResource = jsonArray.getJSONObject("resource");
								jsonAttribute = jsonResource.getJSONObject("fields");
								stockCompany = jsonAttribute.getString("name");
								stockPrice = jsonAttribute.getDouble("price");

				//Testing and Debugging purposes
				Log.i("JSON Obj1: ", jsonMain.toString());
				Log.i("JSON Obj2: ", jsonList.toString());
				Log.i("JSON Obj2 Array: ", resourcesArray.toString());
				Log.i("JSON Obj3: ", jsonArray.toString());
				Log.i("JSON Obj4: ", jsonResource.toString());
				Log.i("JSON Obj5: ", jsonAttribute.toString());
				Log.i("JSON symbol: ", stockCompany);
				Log.i("JSON stockprice: ", String.valueOf(stockPrice));

				// Set textviews to the Results
				price.setText(String.valueOf(stockPrice));
				company.setText(stockCompany);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return false;
		}//end handleMessage
	});//end of Handler getJSONString
}//end of 

