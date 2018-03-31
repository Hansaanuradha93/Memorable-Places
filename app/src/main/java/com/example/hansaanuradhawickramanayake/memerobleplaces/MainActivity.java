package com.example.hansaanuradhawickramanayake.memerobleplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView placesListView;

    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locationList = new ArrayList<>();

    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.hansaanuradhawickramanayake.memerobleplaces", Context.MODE_PRIVATE);


        ArrayList<String> latitudesList = new ArrayList<>();
        ArrayList<String> longitudesList = new ArrayList<>();

        
        places.clear();
        latitudesList.clear();
        longitudesList.clear();
        locationList.clear();

        try{

            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));

            latitudesList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats", ObjectSerializer.serialize(new ArrayList<String>())));

            longitudesList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs", ObjectSerializer.serialize(new ArrayList<String>())));


        } catch (Exception e){

            e.printStackTrace();
        }

        if (places.size() > 0 && latitudesList.size() > 0  && longitudesList.size() > 0){

            if (places.size() == latitudesList.size() && places.size() == longitudesList.size()){
                for (int i = 0; i < latitudesList.size(); i++){

                    locationList.add(new LatLng(Double.parseDouble(latitudesList.get(i)), Double.parseDouble(longitudesList.get(i))));
                }
            }

        } else {


            places.add("Add a Place ...");
            locationList.add(new LatLng(0 , 0));

        }

        placesListView = findViewById(R.id.placesListView);


        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, places){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv =  view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE);

                // Generate ListView Item using TextView
                return view;
            }
        };

        // DataBind ListView with items from ArrayAdapter
        placesListView.setAdapter(arrayAdapter);

        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeIndex", position);
                startActivity(intent);
            }
        });


    }


}
