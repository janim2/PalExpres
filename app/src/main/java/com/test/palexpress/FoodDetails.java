package com.test.palexpress;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.widget.Toast.LENGTH_LONG;

public class FoodDetails extends AppCompatActivity {

    TextView foodname,foodprize,fooddescription,vendortosend;
    ImageView food_image;
    Button addtoCart, continuee;

    String svendorid, sfoodid, svendorname, sfoodname, sfoodprize, sfoodimage,sfooddescription, sfoodvendor;

    Intent fooddetailsIntent;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        getSupportActionBar().setTitle("PalExpress | Food Details");

        fooddetailsIntent = getIntent();

        foodname = (TextView) findViewById(R.id.foodnamee);
        foodprize = (TextView) findViewById(R.id.foodprize);
        vendortosend = (TextView) findViewById(R.id.foodvendorname);
        fooddescription = (TextView) findViewById(R.id.fooddescription);

        food_image = (ImageView) findViewById(R.id.foodimage);

        addtoCart = (Button) findViewById(R.id.foodaddtocart);
        continuee = (Button) findViewById(R.id.foodcontinue);
        loading = (ProgressBar) findViewById(R.id.loading);

        svendorid = fooddetailsIntent.getStringExtra("foodvendorID");
        svendorname = fooddetailsIntent.getStringExtra("foodvendorname");
        sfoodid = fooddetailsIntent.getStringExtra("foodId");
        sfoodprize = fooddetailsIntent.getStringExtra("foodprize");
        sfoodimage = fooddetailsIntent.getStringExtra("foodimage");
        sfoodname = fooddetailsIntent.getStringExtra("foodname");
        sfooddescription = fooddetailsIntent.getStringExtra("fooddescripton");


        DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).
                defaultDisplayImageOptions(theImageOptions).build();
        ImageLoader.getInstance().init(config);

        String svendorImage = sfoodimage;
        imageLoader.displayImage(svendorImage,food_image);

        foodname.setText(sfoodname);
        foodprize.setText(sfoodprize);
        vendortosend.setText("Will be delivered from: " + svendorname);
        fooddescription.setText(sfooddescription);

        continuee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                add to cart logic
            }
        });
    }

    class AddToCart extends AsyncTask<Void, Void, String> {

        String url_location, user_id, the_itemID, the_quantity, the_prize;

        public AddToCart(String url_location, String user_id, String the_itemID, String the_quantity,
                         String the_prize) {
            this.url_location = url_location;
            this.user_id = user_id;
            this.the_itemID = the_itemID;
            this.the_quantity = the_quantity;
            this.the_prize = the_prize;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                URL url = new URL(url_location);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("item_ID", "UTF-8") + "=" + URLEncoder.encode(the_itemID, "UTF-8") + "&" +
                        URLEncoder.encode("the_quantity", "UTF-8") + "=" + URLEncoder.encode(the_quantity, "UTF-8") + "&" +
                        URLEncoder.encode("the_prize", "UTF-8") + "=" + URLEncoder.encode(the_prize, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String fetch;
                while ((fetch = bufferedReader.readLine()) != null) {
                    stringBuffer.append(fetch);
                }
                String string = stringBuffer.toString();
                inputStream.close();
                return string;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return "please check internet connection";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.setVisibility(View.GONE);
            if(s.equals("")){
                Toast.makeText(FoodDetails.this, s, LENGTH_LONG).show();
            }else{
                Toast.makeText(FoodDetails.this, "Failed", LENGTH_LONG).show();
            }

        }

    }
}
