package com.mert.havadurumu;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    public TextView cityname,degree,situation;
    ImageView imageView;
    EditText bul;
    String weather1;
    SharedPreferences sharedPreferences;
    String bulacak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityname = findViewById(R.id.cityname);
        degree = findViewById(R.id.degree);
        situation = findViewById(R.id.situation);
        imageView = findViewById(R.id.imageView);
        bul = findViewById(R.id.bul);

        // VERİ ÇEKMEK İSTEDİGİMİZ JSON MODÜLÜNÜ APİ KEYİ VE SİTEYİ KOPYALIYORUZ

        // MANİFEST DOSYASINA BUNLARI EKLİYORUZ
        // <uses-permission android:name="android.permission.INTERNET"></uses-permission>
        // android:usesCleartextTraffic="true"
        sharedPreferences = this.getSharedPreferences("com.mert.havadurumu", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("weather","null");
        bulacak = bul.getText().toString();
        System.out.println(value);
        if(!value.equals("null")){
            bulacak = value;
            Verilericek gorev = new Verilericek();
            gorev.execute();
        }else {
            Toast.makeText(MainActivity.this,"Lütfen Yukarıya Şehir Giriniz",Toast.LENGTH_LONG).show();
        }



        }


    public void veri(View view) throws ExecutionException, InterruptedException {
        // BURDA BUTONA STRİNGİ ÇEKMESİ İÇİN TANIMLAMA YAPTIK
        Verilericek gorev = new Verilericek();
        gorev.execute();
        bulacak = bul.getText().toString();
        sharedPreferences.edit().putString("weather",bulacak).apply();

    }


    // ASYNCTASK OLŞTURUYORUZ STRİNG OLACAK ŞEKİLDE CLASS TANIMLIYORUZ
    class Verilericek extends AsyncTask<Void,Void,String>{


        @Override
        protected String doInBackground(Void... voids) {

            try {
                // URL OLUŞTURDUK APİ KEYİMİZİ İÇERİSİNE VERDİK
                URL adres = new URL("http://api.weatherstack.com/current?access_key=77039aa681f978df9a93c2b7c537e85f&query="+bulacak);
                // BAGLANTI YAPACAGIMIZ HTTP URL CONNECT NESNESİ OLUŞTURUDUK
                HttpURLConnection baglanti = (HttpURLConnection) adres.openConnection();
                // ÇEKİLEN VERİLERİ OKUMAK İÇİN BUFFEREDRED OLUŞTURDUK
                BufferedReader br = new BufferedReader(new InputStreamReader(baglanti.getInputStream()));
                // ÇEKİLEN VERİLERİ SATIR SATIR OKUYACAGIZ
                String sonuc = "", satir = "";
                //BURDA JSON APİ KEYİNİN ÜZERİNDE GEZİNİRKEN İÇERİSİNDEKİ VERİLERİ ALDIK
                while ((satir= br.readLine())!=null){ // GELEN DEGER NULL OLASIYA KADAR ÇALIŞ
                    sonuc += satir;
                }
                br.close();   // BUFFERİ KAPATTIK
                baglanti.disconnect(); // BAĞLANTIYI SONLANDIRDIK
                return sonuc;  // SONUCU DİGER METODA GÖNDERİYORUZ
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // GELEN VERİLERİ BURDA YAZDIRDIK
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject Weather = jsonObject.getJSONObject("location");
                JSONObject Temperature = jsonObject.getJSONObject("current");
                String city = Weather.getString("name");
                String hat= Temperature.getString("temperature");
                String weather= Temperature.getString("weather_descriptions");
                weather1 = weather.substring(2, weather.length()-2);
                cityname.setText(city.toUpperCase());
                degree.setText(hat+" °C");
                situation.setText(weather1);
                System.out.println(weather1);
                //imageView.setImageResource(R.drawable.cloudy);
                if(Objects.equals(weather1, "Light Rain Shower")){  // yağmur
                    imageView.setImageResource(R.drawable.rain);
                }else if (Objects.equals(weather1, "Sunny")) {  // güneş
                    imageView.setImageResource(R.drawable.sun);
                } else if (Objects.equals(weather1, "Partly cloudy")) {  // parçalı bulutlu
                    imageView.setImageResource(R.drawable.cloudy);
                }else if (Objects.equals(weather1,"Light Rain Shower, Mist")){ // rüzgar
                    imageView.setImageResource(R.drawable.fog);
                }else if (Objects.equals(weather1,"Overcast")){ // bulutlu fırtına
                    imageView.setImageResource(R.drawable.storm);
                }else if (Objects.equals(weather1,"Rain Shower, Rain With Thunderstorm")){ // bulutlu fırtına
                    imageView.setImageResource(R.drawable.storm);
                }else if (Objects.equals(weather1,"Snowy")){  // karlı
                    imageView.setImageResource(R.drawable.snow);
                }else{
                    imageView.setImageResource(R.drawable.bulut);   // farklı degerde bulut

                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }


    }

}



