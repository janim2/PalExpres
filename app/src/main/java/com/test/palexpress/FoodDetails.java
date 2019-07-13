package com.test.palexpress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FoodDetails extends AppCompatActivity {

    TextView foodname,foodprize,fooddescription,vendortosend;
    ImageView food_image;
    Button addtoCart, continuee;

    String svendorid, sfoodid, svendorname, sfoodname, sfoodprize, sfoodimage,sfooddescription, sfoodvendor;

    Intent fooddetailsIntent;
    ImageLoader imageLoader = ImageLoader.getInstance();

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
    }
}
