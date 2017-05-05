package com.shishkovictor.onlinephonebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private TextView tv;
    private EditText et_students_name;
    private EditText et_students_phone;
    private Button button_send_post_request;

    //private final String url = "http://10.0.2.2:1337"; // для дебага на компе с эмулятра
    private final String url = "http://iwanp.tintekko.com:1337";

    private Gson gson = new GsonBuilder().create();

    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(url)
            .build();

    private APIInterface apiInterface = retrofit.create(APIInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView_students);
        tv = (TextView) findViewById(R.id.textView_studentList);
        tv.setText("");
        et_students_name = (EditText) findViewById(R.id.editText_name);
        et_students_phone = (EditText) findViewById(R.id.editText_phone);
        button_send_post_request = (Button) findViewById(R.id.button_send);
        button_send_post_request.setOnClickListener(onClickListener);

        // TODO: move to the doInBackground method:
        Thread t = new Thread() {
            public void run() {
                try {
                    String json = apiInterface.getUsers().execute().body().toString();
                    Log.d("jsongetted", json);
                    updateLV(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private void updateLV(String json) {
        ArrayList<String> al = new ArrayList<String>();
        String tempstr = "";
        for (int i = 0; i < json.length() - 1; i++) {
            tempstr = tempstr + json.charAt(i);
            if (json.charAt(i) == '}' && json.charAt(i + 1) == ',') {
                al.add(tempstr);
                tempstr = "";
            }
        }
        al.add(tempstr);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.listview_item,
                al );

        lv.setAdapter(arrayAdapter);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_send:
                    Thread t = new Thread()  {
                        public void run() {
                            User user = new User(
                                    0,
                                    et_students_name.getText().toString(),
                                    et_students_phone.getText().toString());
                            Call<User> call = apiInterface.addUser(user);
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    fastrestart();
                                }
                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        }
                    };
                    t.start();

                    break;
            }
        }
    };


    private void fastrestart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    //private void doIngBackground() {

   // }


}








































