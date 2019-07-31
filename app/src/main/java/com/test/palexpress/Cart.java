package com.test.palexpress;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.test.palexpress.Models.FoodModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;

public class Cart extends AppCompatActivity {
    ProgressBar loading;
    TextView nofoods;

    Displayer adapter;
//    String userid;
    Accessories cart_accessor;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        loading = (ProgressBar) findViewById(R.id.loading);
        nofoods = (TextView) findViewById(R.id.nofoods);
        cart_accessor = new Accessories(Cart.this);

        //getting the user id from shared preference
        userid = cart_accessor.getString("uid");
    }

    public class GetFoods extends AsyncTask<Void, Void, List<FoodModel>> {

        String task, urlLocation, userid;
//        ProgressDialog alertDialog;

        public GetFoods(String urlLocation, String userid) {
            this.urlLocation = urlLocation;
            this.userid = userid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            alertDialog = new ProgressDialog(MainActivity.this);
//            alertDialog.setMessage(task);
//            alertDialog.setCancelable(false);
//            alertDialog.show();
            nofoods.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FoodModel> doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            try {
                URL url = new URL(urlLocation);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("userid", "UTF-8")+"="+URLEncoder.encode(userid, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String fetch = "";
                while((fetch = bufferedReader.readLine()) != null){
                    stringBuffer.append(fetch);
                }
                String string = stringBuffer.toString();
                inputStream.close();

                // getting the individual values based json in php using the keys there
                JSONObject userobject = new JSONObject(string);
                JSONArray getArray = userobject.getJSONArray("Server_response");
                food_menu.clear();


                for(int a = 0;a < getArray.length();a++){
                    JSONObject thegetterObject = getArray.getJSONObject(a);
                    FoodModel themodule = new FoodModel();
                    themodule.setFood_id(thegetterObject.getString("foodid"));
                    themodule.setFood_image(thegetterObject.getString("foodimage"));
                    themodule.setFood_name(thegetterObject.getString("foodname"));
                    themodule.setFood_description(thegetterObject.getString("fooddescription"));
                    themodule.setFood_prize(thegetterObject.getString("foodprize"));
                    food_menu.add(themodule);
                }
                return food_menu;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return food_menu;
        }


        @Override
        protected void onPostExecute(List <FoodModel> onPostList) {
            super.onPostExecute(onPostList);
//            alertDialog.dismiss();
            loading.setVisibility(View.GONE);
            if(onPostList.size() == 0){
                nofoods.setVisibility(View.VISIBLE);
            }
            else{
                try {
                    adapter = new Displayer(Cart.this, R.layout.cart_layout_attachment, onPostList);
                    food_list.setAdapter(adapter);
                    cart_accessor.setListHeight(food_list);
                }catch (Exception e){
                    return;
                }
            }

        }
    }

    //class for adding more(quantity) to an item
    class Adding_Quantity extends AsyncTask<Void, Void, String> {

        String url_location, userid, itemid;

        public Adding_Quantity(String url_location, String userid, String itemid) {
            this.url_location = url_location;
            this.userid = userid;
            this.itemid = itemid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(VISIBLE);
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

                String data =
                        URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8") + "&" +
                                URLEncoder.encode("itemid", "UTF-8") + "=" + URLEncoder.encode(itemid, "UTF-8");

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
            loading.setVisibility(GONE);
            if (s.equals("")){
                Toast.makeText(Cart.this, s, LENGTH_LONG).show();
                new GetFoods("",userid).execute();
            }
            Toast.makeText(Cart.this, s, LENGTH_LONG).show();
        }

    }
    //class ends here

    //class for subtracting(quantity) to an item
    class Subtracting_Quantity extends AsyncTask<Void, Void, String> {

        String url_location, userid, itemid;

        public Subtracting_Quantity(String url_location, String userid, String itemid) {
            this.url_location = url_location;
            this.userid = userid;
            this.itemid = itemid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(VISIBLE);
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

                String data =
                        URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(itemid, "UTF-8");

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
            loading.setVisibility(GONE);
            if (s.equals("")){
                Toast.makeText(Cart.this, s, LENGTH_LONG).show();
                new GetFoods("",userid).execute();
            }
            Toast.makeText(Cart.this, s, LENGTH_LONG).show();
        }

    }
    //class ends here
}
