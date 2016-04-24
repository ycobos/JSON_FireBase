package com.example.yesid.json_firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String url = "http://api.randomuser.me/?results=1&format=jsaon";
    JSONArray usuarios = null;
    ArrayList<DataEntry> listaUsuarios;
    private ListView listView;
    private ProgressDialog pDialog;
    Firebase rootRef;
    Firebase userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        rootRef= new Firebase("https://jsonex.firebaseio.com/");
        userRef= rootRef.child("usuarios");
        listaUsuarios=new ArrayList<>();
        listView= (ListView) findViewById(R.id.listView);
        updateUsers();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("data", (DataEntry) view.getTag());
                MainActivity.this.startActivity(intent);
            }
        });

    }

    private void updateUsers() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<DataEntry> users = new ArrayList<DataEntry>();
                ArrayList<String> ids = new ArrayList<String>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    users.add(new DataEntry(postSnapshot.child("gender").getValue().toString(),
                            postSnapshot.child("firstName").getValue().toString(),
                            postSnapshot.child("lastName").getValue().toString(),
                            postSnapshot.child("picture").getValue().toString()
                    ));
                    ids.add(postSnapshot.getRef().getKey().toString());
                }
                listView.setAdapter(null);
                CustomAdapter adapter = new CustomAdapter(MainActivity.this, users, ids, userRef);
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void Load(View view) {
        new GetData().execute();
    }

    private class GetData extends AsyncTask<Void, Void, Void> {

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

                    usuarios = jsonObj.getJSONArray("results");
                    Log.d("Response length: ", "> " + usuarios.length());

                    for (int i = 0; i < usuarios.length(); i++) {
                        JSONObject c = usuarios.getJSONObject(i);

                        DataEntry dataEntry = new DataEntry();

                        dataEntry.setGender(c.getString("gender"));

                        JSONObject name = c.getJSONObject("name");

                        dataEntry.setFirstName(name.getString("first"));
                        dataEntry.setLastName(name.getString("last"));

                        JSONObject imageObject = c.getJSONObject("picture");

                        dataEntry.setPicture(imageObject.getString("large"));
                        userRef.push().setValue(dataEntry);
                        updateUsers();


                    }
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            updateUsers();
        }

    }


}
