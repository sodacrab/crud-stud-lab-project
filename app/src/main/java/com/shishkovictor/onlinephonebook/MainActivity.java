package com.shishkovictor.onlinephonebook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
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
    private Button button_refresh;
    private Button button_send_post_request;
    private EditText et_id;
    private Button button_change;

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
        button_refresh = (Button) findViewById(R.id.button2_refresh);
        button_refresh.setOnClickListener(onClickListener);
        button_send_post_request = (Button) findViewById(R.id.button_send);
        button_send_post_request.setOnClickListener(onClickListener);
        et_id = (EditText) findViewById(R.id.editText_id);
        button_change = (Button) findViewById(R.id.button_change);
        button_change.setOnClickListener(onClickListener);

        UpdateThread t = new UpdateThread();
        t.start();
    }

    private class UpdateThread extends Thread {
        @Override
        public void run() {
                    try {
                        String json = apiInterface.getUsers().execute().body().toString();
                        Log.d("jsongetted", json);
                        updateLV(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        }
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
        //lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView tv = (TextView) view.findViewById(R.id.listview_item_textview);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Удалить?");
                alertDialog.setMessage("Отправится DELETE запрос на удаление студента из базы данных на сервере." +
                        "Данные о студенте: " + tv.getText().toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Удалить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getBaseContext(), tv.getText().toString(), Toast.LENGTH_SHORT).show();
                                final String id = getIdFromString( tv.getText().toString());
                                Toast.makeText(getBaseContext(), id, Toast.LENGTH_SHORT).show();


                                Thread t = new Thread() {
                                    public void run() {
                                        Call<ResponseBody> deleteRequest = apiInterface.deleteUserByID(Integer.valueOf(id));
                                        deleteRequest.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                    }
                                };
                                t.start();

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button2_refresh:
                    fastrestart();
                    break;
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
                case R.id.button_change:
                    Thread t2 = new Thread() {
                        public void run() {
                            User user = new User(
                                    Integer.valueOf(et_id.getText().toString()),
                                    et_students_name.getText().toString(),
                                    et_students_phone.getText().toString());
                            Call<User> call = apiInterface.updateUserByID(Integer.valueOf(et_id.getText().toString()), user);
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
                    t2.start();
                    break;
            }
        }
    };

private String getIdFromString(String s) {
    String id = "";
    for (int i = 0; i < s.length() - 1; i++) {
        if (s.charAt(i) == '='
                && Character.isDigit(s.charAt(i + 1)) &&
                s.charAt(i - 1) == 'd') {
            id = Character.toString(s.charAt(i + 1));
            if (s.charAt(i + 2) != '.'
                    && Character.isDigit(s.charAt(i + 2))) {
                id = id + Character.toString(s.charAt(i + 2));
            }
        }
    }
    return id;
}

    private void fastrestart() {
        //Intent intent = getIntent();
        //finish();
       // startActivity(intent);
    }
    //private void doIngBackground() {

   // }


}








































