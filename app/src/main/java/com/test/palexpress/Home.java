package com.test.palexpress;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.test.palexpress.Models.FoodModel;
import com.test.palexpress.Models.VendorModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    ProgressBar pgbar;
    TextView refresh;
    ListView vendor_list;
    List<VendorModel> vendor_menu = new ArrayList<>();
    Displayer adapter;
    Accessories homeaccessor;
    ImageLoader imgloader = ImageLoader.getInstance();

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View home= inflater.inflate(R.layout.fragment_home, container, false);
        pgbar = (ProgressBar) home.findViewById(R.id.loading);
        refresh = (TextView) home.findViewById(R.id.homer);
        vendor_list = (ListView) home.findViewById(R.id.vendor_list);
        homeaccessor = new Accessories(getActivity());
        new GetVendors().execute();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetVendors().execute();
            }
        });
        return home;
    }

    public class GetVendors extends AsyncTask<Void, Void, List<VendorModel>> {

        String task, urlLocation;
//        ProgressDialog alertDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            alertDialog = new ProgressDialog(MainActivity.this);
//            alertDialog.setMessage(task);
//            alertDialog.setCancelable(false);
//            alertDialog.show();
            pgbar.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
            urlLocation = "https://iamjesse75.000webhostapp.com/PalExpress/getVendors.php";
        }

        @Override
        protected List<VendorModel> doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            try {
                URL url = new URL(urlLocation);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String fetch = "";
                while((fetch = bufferedReader.readLine()) != null){
                    stringBuffer.append(fetch);
                }
                String string = stringBuffer.toString();
                inputStream.close();

                JSONObject getDetials = new JSONObject(string);
                JSONArray getArray = getDetials.getJSONArray("Server_response");
                vendor_menu.clear();


                for(int a = 0;a < getArray.length();a++){
                    JSONObject thegetterObject = getArray.getJSONObject(a);
                    VendorModel themodule = new VendorModel();
                    themodule.setVendorid(thegetterObject.getString("vendorid"));
                    themodule.setVendorimage(thegetterObject.getString("vendorimage"));
                    themodule.setVendorname(thegetterObject.getString("vendorname"));
                    themodule.setFoodtags(thegetterObject.getString("foodtags"));
                    themodule.setPaymentplans(thegetterObject.getString("paymentplans"));
                    themodule.setFoodpreptime(thegetterObject.getString("foodpreptime"));
                    themodule.setMinorder(thegetterObject.getString("minorder"));
                    themodule.setDelivarytime(thegetterObject.getString("deliveryPrize"));
                    vendor_menu.add(themodule);

                }

                return vendor_menu;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return vendor_menu;
        }

        @Override
        protected void onPostExecute(List <VendorModel> onPostList) {
            super.onPostExecute(onPostList);
//            alertDialog.dismiss();
            pgbar.setVisibility(View.GONE);
            if(onPostList.size() == 0){
                refresh.setVisibility(View.VISIBLE);
            }
            else{
                try {
                    adapter = new Displayer(getActivity(), R.layout.vendorattachment, onPostList);
                    vendor_list.setAdapter(adapter);
                    homeaccessor.setListHeight(vendor_list);
                }catch (Exception e){
                    return;
                }
            }

        }
    }
    //
    public class Displayer extends ArrayAdapter {
        List <VendorModel> display;
        int thecon;
        LayoutInflater theInflater;
        public Displayer(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            thecon = resource;
            display = objects;
            theInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        class Viewholder{
            TextView vendorpreptime, vendorname, vendortags, vendorpaymentplans;
            ImageView vendor_image;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Viewholder theViewHolder;
            //if(convertView == null){
            theViewHolder = new Viewholder();
            convertView = theInflater.inflate(thecon,null);

            theViewHolder.vendor_image = (ImageView) convertView.findViewById(R.id.vendorimage);
            theViewHolder.vendorpreptime = (TextView)convertView.findViewById(R.id.vendorpreptime);
            theViewHolder.vendorname = (TextView) convertView.findViewById(R.id.vendorname);
            theViewHolder.vendortags = (TextView) convertView.findViewById(R.id.foodtags);
            theViewHolder.vendorpaymentplans = (TextView) convertView.findViewById(R.id.vendorpaymentplan);

            DisplayImageOptions theImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).
                    cacheOnDisk(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).
                    defaultDisplayImageOptions(theImageOptions).build();
            ImageLoader.getInstance().init(config);

            String orgImage = display.get(position).getVendorimage();
            imgloader.displayImage(orgImage,theViewHolder.vendor_image);

            theViewHolder.vendorpreptime.setText(display.get(position).getFoodpreptime());
            theViewHolder.vendorname.setText(display.get(position).getVendorname());
            theViewHolder.vendortags.setText(display.get(position).getFoodtags());
            theViewHolder.vendorpaymentplans.setText(display.get(position).getPaymentplans());

            theViewHolder.vendor_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent vendorInfo = new Intent(getActivity(),Vendors.class);
                    vendorInfo.putExtra("thevendorid",display.get(position).getVendorid());
                    vendorInfo.putExtra("thevendorimage",display.get(position).getVendorimage());
                    vendorInfo.putExtra("thevendorname",display.get(position).getVendorname());
                    vendorInfo.putExtra("thefoodtags",display.get(position).getFoodtags());
                    vendorInfo.putExtra("thepaymentplans",display.get(position).getPaymentplans());
                    vendorInfo.putExtra("thefoodpreptime",display.get(position).getFoodpreptime());
                    vendorInfo.putExtra("theminorder",display.get(position).getMinorder());
                    vendorInfo.putExtra("thedelivarytime",display.get(position).getDelivarytime());
                    startActivity(vendorInfo);
                }
            });
            return convertView;

        }
    }

}
