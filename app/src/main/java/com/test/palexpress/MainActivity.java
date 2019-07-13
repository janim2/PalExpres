package com.test.palexpress;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    //declaring the type of widgets used in the layout or xml file
    EditText phone_number,password;
    Button logIn;
    TextView sign_up;
    String mobile_number,passworhd;
    ProgressBar loading;
    Accessories main_accessories;

    //rewritting the original template made in the android studio.
    //This is the reason for the override
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        main_accessories = new Accessories(MainActivity.this);

        //linking the xml(layout file) to the java file using the id's used in the layout file
        phone_number = (EditText) findViewById(R.id.phone_number_id);
        password = (EditText) findViewById(R.id.password);
        logIn = (Button) findViewById(R.id.login_button);
        sign_up = (TextView) findViewById(R.id.sign_up);
        loading = (ProgressBar) findViewById(R.id.loading);
        //determing what happens when the login button is clicked.
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the value that a user inputs into the password field.
                mobile_number = phone_number.getText().toString();

                //getting value that user inputs into the mobile number field.
                passworhd = password.getText().toString();

                //checking to see of phone number field is blank
                if (!mobile_number.equals("")) {

                    //checking to see if password field is blank
                    if (!passworhd.equals("")) {

                        //checking to see if password is greater than 10
                        if (passworhd.length() >= 6) {

                            //checking to see if mobile number is greater than 6
                            if (mobile_number.length() >= 10) {
                                //if all the above are true then show a welcome message.
//                                Toast.makeText(MainActivity.this,"WELCOME",Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(MainActivity.this,Quiz_Layout.class));
                                new LoggingIn("http://palexpress.000webhostapp.com/PalExpress/login.php",mobile_number,passworhd).execute();
                            } else {
                                //if phone number field is less than 10 characters show an error
                                phone_number.setError("should be ten characters");
                            }
                        } else {
                            //if password field is less than 6 characters show an error
                            password.setError("should be more than six characters");
                        }
                    } else {
                        //if phone number field is empty show an error
                        password.setError("should not be blank");
                    }
                } else {
                    //if phone password field is empty show an error
                    phone_number.setError("should not be blank");
                }
            }
        });

       sign_up.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //opening the sign up page,changing the activity via intent
               Intent signup = new Intent(MainActivity.this,Sign_up.class);
               startActivity(signup);
           }
       });

    }

    class LoggingIn extends AsyncTask<Void, Void, String> {

        String url_location, phone_number, password;

        public LoggingIn(String url_location, String phone_number, String password) {
            this.url_location = url_location;
            this.phone_number = phone_number;
            this.password = password;
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
                        URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone_number, "UTF-8") + "&" +
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

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
            if (s.equals("login Successful")){
                Toast.makeText(MainActivity.this, s, LENGTH_LONG).show();
                main_accessories.put("login_checker",true);
                startActivity(new Intent(MainActivity.this,HomeScreen.class));

            }
            Toast.makeText(MainActivity.this, s, LENGTH_LONG).show();
        }

    }
}






