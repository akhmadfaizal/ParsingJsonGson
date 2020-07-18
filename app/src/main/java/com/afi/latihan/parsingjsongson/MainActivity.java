package com.afi.latihan.parsingjsongson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    ListUserAdapter listUserAdapter;

    // Url Endpoint for get data JSON
    public static String BASE_URL = "https://reqres.in/api/users?page=1";

    // get data array from model
    private ArrayList<User> list = new ArrayList<>();

    // Variable
    RecyclerView rvUser;
    ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the recyclerview in activity layout
        rvUser = findViewById(R.id.rv_user);
        // if true, to optimize size of recyclerview
        rvUser.setHasFixedSize(true);

        pbLoading = findViewById(R.id.pb_loading);

        // get data json from AsyncHttpClint
        getUser();
    }

    // LoopJ
    // Asyncronus melakukannya dibalik layar(background) kalo sudah mendapatkan nya baru ditampilkan.
    private void getUser(){
        pbLoading.setVisibility(View.VISIBLE);
        // AsyncHttpClient, berarti kita akan menggunakan client yang bertanggung jawab untuk koneksi data dan sifatnya adalah asynchronous.
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BASE_URL, new AsyncHttpResponseHandler() {
            // Callback onSuccess() dipanggil ketika response memberikan kode status 200, yang artinya koneksi berhasil.
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Jika koneksi berhasil
                pbLoading.setVisibility(View.INVISIBLE);
                String response = new String(responseBody);
                parseJson(response);
            }

            // Callback onFailure() dipanggil ketika response memberikan kode status 4xx (seperti 401, 403, 404, dll), yang artinya koneksi gagal.
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Jika koneksi gagal
                pbLoading.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void parseJson(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++){
                Gson gson = new Gson();
                User data = gson.fromJson(dataArray.getJSONObject(i).toString(), User.class);

                // init User
                list.add(data);
            }
            showRecyclerList();
        } catch (Exception e){
            Log.e("Gson", "parseJson: ",e);
        }
    }

    public void showRecyclerList(){
        // Set layout manager to position the items
        rvUser.setLayoutManager(new LinearLayoutManager(this));
        // Create adapter passing in the sample user data
        listUserAdapter = new ListUserAdapter(list);
        // Attach the adapter to the recyclerview to populate items
        rvUser.setAdapter(listUserAdapter);
        pbLoading.setVisibility(View.GONE);
    }
}
