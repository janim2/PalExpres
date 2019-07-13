package com.test.palexpress;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.test.palexpress.Models.FoodModel;
import com.test.palexpress.Models.VendorModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

public class Vendors extends AppCompatActivity {

    Intent getVendorIntent;
    String svendorid,svendorimage, svendorname,svendorfoodtags,svendorpaymentplans,
            svendorfoodpreptime,svendorminorder, svendordelivarytime;

    ImageView vendorimage;
    TextView name, others, noFoods;
    ImageLoader imageLoader = ImageLoader.getInstance();
    ListView food_list;
    ProgressBar loading;
    List<FoodModel> food_menu = new ArrayList<>();
    Accessories vendoraccessories;

    Displayer adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);

        vendoraccessories = new Accessories(Vendors.this);

        getVendorIntent = getIntent();
        getSupportActionBar().setTitle("PalExpress | Vendors");
        svendorid = getVendorIntent.getStringExtra("thevendorid");
        svendorimage = getVendorIntent.getStringExtra("thevendorimage");
        svendorname = getVendorIntent.getStringExtra("thevendorname");
        svendorfoodtags = getVendorIntent.getStringExtra("thefoodtags");
        svendorpaymentplans = getVendorIntent.getStringExtra("thepaymentplans");
        svendorfoodpreptime = getVendorIntent.getStringExtra("thefoodpreptime");
        svendorminorder = getVendorIntent.getStringExtra( "theminorder");
        svendordelivarytime = getVendorIntent.getStringExtra( "thedelivarytime");

        vendorimage = (ImageView)findViewById(R.id.thevendorimage);
        name = (TextView)findViewById(R.id.thevendorname);
        others = (TextView)findViewById(R.id.vendorothers);
        food_list = (ListView) findViewById(R.id.food_list);
        loading = (ProgressBar) findViewById(R.id.loading);
        noFoods = (TextView) findViewById(R.id.nofoods);

        DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).
                defaultDisplayImageOptions(theImageOptions).build();
        ImageLoader.getInstance().init(config);

        String svendorImage = svendorimage;
        imageLoader.displayImage(svendorImage,vendorimage);

        name.setText(svendorname);
        others.setText(svendorpaymentplans + " " + "Min Order:" + svendorminorder + " " + "Delivery Prize: " + svendordelivarytime);

        new GetFoods("https://iamjesse75.000webhostapp.com/PalExpress/getfoodinfo.php",svendorid).execute();

        noFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetFoods("https://iamjesse75.000webhostapp.com/PalExpress/getfoodinfo.php",svendorid).execute();
            }
        });
    }

    public class GetFoods extends AsyncTask<Void, Void, List<FoodModel>> {

        String task, urlLocation, vendorid;
//        ProgressDialog alertDialog;

        public GetFoods(String urlLocation, String vendorid) {
            this.urlLocation = urlLocation;
            this.vendorid = vendorid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            alertDialog = new ProgressDialog(MainActivity.this);
//            alertDialog.setMessage(task);
//            alertDialog.setCancelable(false);
//            alertDialog.show();
            noFoods.setVisibility(View.GONE);
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

                String data = URLEncoder.encode("vendorId", "UTF-8")+"="+URLEncoder.encode(vendorid, "UTF-8");

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
                noFoods.setVisibility(View.VISIBLE);
            }
            else{
                try {
                    adapter = new Displayer(Vendors.this, R.layout.food_attachments, onPostList);
                    food_list.setAdapter(adapter);
                    vendoraccessories.setListHeight(food_list);
                }catch (Exception e){
                    return;
                }
            }

        }
    }
    //
    public class Displayer extends ArrayAdapter {
        List <FoodModel> display;
        int thecon;
        LayoutInflater theInflater;
        public Displayer(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            thecon = resource;
            display = objects;
            theInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        class Viewholder{
            TextView foodname, fooddescription, prize;
            LinearLayout food_layout;
            ImageView addtoCart;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Displayer.Viewholder theViewHolder;
            //if(convertView == null){
            theViewHolder = new Displayer.Viewholder();
            convertView = theInflater.inflate(thecon,null);

            theViewHolder.food_layout = (LinearLayout) convertView.findViewById(R.id.foodlayout);
            theViewHolder.foodname = (TextView)convertView.findViewById(R.id.food_name);
            theViewHolder.fooddescription = (TextView) convertView.findViewById(R.id.food_description);
            theViewHolder.prize = (TextView) convertView.findViewById(R.id.prize);
            theViewHolder.addtoCart = (ImageView) convertView.findViewById(R.id.addtocart);

            theViewHolder.foodname.setText(display.get(position).getFood_name());
            theViewHolder.fooddescription.setText(display.get(position).getFood_description());
            theViewHolder.prize.setText(display.get(position).getFood_prize());

            theViewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Vendors.this,"Add to cart",Toast.LENGTH_LONG).show();
                }
            });

            theViewHolder.food_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fooddetails = new Intent(Vendors.this,FoodDetails.class);
                    fooddetails.putExtra("foodvendorID",svendorid);
                    fooddetails.putExtra("foodvendorname",svendorname);
                    fooddetails.putExtra("foodId",display.get(position).getFood_id());
                    fooddetails.putExtra("foodimage",display.get(position).getFood_image());
                    fooddetails.putExtra("foodname",display.get(position).getFood_name());
                    fooddetails.putExtra("fooddescripton",display.get(position).getFood_description());
                    fooddetails.putExtra("foodprize",display.get(position).getFood_prize());
                    startActivity(fooddetails);
                }
            });
//            theViewHolder.food_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent vendorInfo = new Intent(getActivity(),Vendors.class);
//                    vendorInfo.putExtra("thevendorid",display.get(position).getVendorid());
//                    vendorInfo.putExtra("thevendorimage",display.get(position).getVendorimage());
//                    vendorInfo.putExtra("thevendorname",display.get(position).getVendorname());
//                    vendorInfo.putExtra("thefoodtags",display.get(position).getFoodtags());
//                    vendorInfo.putExtra("thepaymentplans",display.get(position).getPaymentplans());
//                    vendorInfo.putExtra("thefoodpreptime",display.get(position).getFoodpreptime());
//                    vendorInfo.putExtra("theminorder",display.get(position).getMinorder());
//                    vendorInfo.putExtra("thedelivarytime",display.get(position).getDelivarytime());
//                    startActivity(vendorInfo);
//                }
//            });
            return convertView;

        }
    }
}
