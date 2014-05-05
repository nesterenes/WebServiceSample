package com.oneupapps.jsonsample;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;


public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://api.icndb.com/jokes/random?limitTo=[nerdy]";

    // JSON Node names
    private static final String TAG_VALUES = "value";
    private static final String TAG_ID = "id";
    private static final String TAG_JOKE = "joke";

    // jokes JSONArray
    JSONArray jokes = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> jokeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jokeList = new ArrayList<HashMap<String, String>>();

        // Calling async task to get json
        new GetJokes().execute();

        // Define the Button
        Button yesButton = (Button) findViewById(R.id.btnNewJoke);

        //Function Associated with the Button press
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                jokeList = new ArrayList<HashMap<String, String>>();

                // Calling async task to get json
                new GetJokes().execute();

            } //END onClick
        }); //END setOnClickListener

    } // END onCreate




    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetJokes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONObject jsonItem = jsonObj.getJSONObject(TAG_VALUES);

                    String id = jsonItem.getString(TAG_ID);
                    String joke = jsonItem.getString(TAG_JOKE);

                    // tmp hashmap for single joke
                    HashMap<String, String> jokeHash = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    jokeHash.put(TAG_ID, id);
                    jokeHash.put(TAG_JOKE, joke);

                    // adding joke to joke list
                    jokeList.add(jokeHash);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, jokeList,
                    R.layout.list_item, new String[] { TAG_JOKE }, new int[] { R.id.joke });

            setListAdapter(adapter);
        }

    }

}
