package com.example.covidtracer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    String confirmed="";
    String message="", active="",recovered="",dead="";
    TextView textView2,textView3;
    String response="";
    JSONObject district;
    String name="",message2="",confirmed2="",recMes="";
    ApiInterface apiInterface;
    Call<JsonArray> call;
    TextView dateText;
    filter Filter=new filter();
    TextToSpeech textToSpeech;



    /* public class downloadtask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try{
                URL url=new URL(urls[0]);
                HttpsURLConnection urlconnection=(HttpsURLConnection) url.openConnection();
                InputStream inputStream=urlconnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                     data=reader.read();
                }
                return result;

            }catch(Exception e){
                e.printStackTrace();
                return "Internet is not working";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                        try {



            }
        }

    public void enter(View view) {
        try {
            textView2.setText("Data is loading....");
            message2="";
            downloadtask download = new downloadtask();

            Log.i("response",response);
            apiInterface=ApiClient.getClient().create(ApiInterface.class);


            download.execute("https://api.covidindiatracker.com/state_data.json");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
e.printStackTrace();
        }
    }

    */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.notification_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.notification:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    NotificationChannel channel= new NotificationChannel("CovidNotification","CovidNotification",NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager=getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
            }
                NotificationCompat.Builder builder= new NotificationCompat.Builder(this,"CovidNotification")
                        .setContentTitle(editText.getText()+" CovidStats")
                        .setSmallIcon(R.drawable.aa)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(sound);
                NotificationManagerCompat manager=NotificationManagerCompat.from(this);
                manager.notify(1,builder.build());
              return true;

        }



        return false;
    }

   public void fetch(){

       textView2.setText("Data is loading");
       message2="";
       apiInterface=ApiClient.getClient().create(ApiInterface.class);
       call=apiInterface.getData();
       call.enqueue(new Callback<JsonArray>() {
           @Override
           public void onResponse(Call<JsonArray> call, Response<JsonArray> response2) {
               response = Filter.convert(editText.getText().toString().trim());



               recMes = response2.body().toString();
               try {
                   JSONArray arr = new JSONArray(recMes);
                   for (int i = 0; i < arr.length(); i++) {
                       JSONObject jpart = arr.getJSONObject(i);
                       if (jpart.getString("state").equals(response)) {
                           district = jpart;
                           confirmed = jpart.getString("confirmed");
                           active = jpart.getString("active");
                           recovered = jpart.getString("recovered");
                           dead = jpart.getString("deaths");
                           message = "Confirmed Cases:" + confirmed + "\nActive cases:" + active + "\nRecovered cases:" + recovered + "\nDead:" + dead;
                           int speech=textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH,null);

                       }
                   }

                   if (!message.isEmpty()) {

                       textView2.setText(message);

                   } else {
                       textView2.setText("Sorry could not find Data :(");
                   }
                   String districtInfo = district.getString("districtData");
                   JSONArray districArray = new JSONArray(districtInfo);
                   for (int i = 0; i < districArray.length(); i++) {
                       JSONObject districtObject = districArray.getJSONObject(i);
                       name = districtObject.getString("name");
                       confirmed2 = districtObject.getString("confirmed");
                       message2 += "Name:" + name + "\nConfirmed Cases:" + confirmed2 + "\n\n";

                   }
                   textView3.setText(message2);
                   dateText();


               } catch (Exception e) {
                   textView2.setText("Server Error");
                   e.printStackTrace();

               }

           }

           @Override
           public void onFailure(Call<JsonArray> call, Throwable t) {
               textView2.setText("Retro failure");
           }
       });




   }
   public void enter(View view){


       fetch();

   }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                    response=result.get(0);
                    fetch();
                }
                break;
        }
    }
    public void refresh(View view){
        message="";
        message2="";
        editText.getText().clear();
        textView2.setText("");
        textView3.setText("");
        dateText.setText("");


    }
    public void  dateText(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
        dateText.setText("Data as of: "+sdf.format(new Date()));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=findViewById(R.id.editText);
        textView2=findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);
        dateText=findViewById(R.id.dateText);
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int s=textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

    }
}
