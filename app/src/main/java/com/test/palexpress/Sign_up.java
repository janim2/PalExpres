package com.test.palexpress;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Random;

import static android.widget.Toast.*;

public class Sign_up extends AppCompatActivity {
    //declaring widgets used in layout in xml file
    EditText phone_number, first_name, email, last_name, password, confirm_password;
    Button sign_up;
    String phone_number2, first_name2, email2, last_name2, password2, confirm_password2;
    String users_id;
    ProgressBar loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        //linking xml(files) to java files using id used in layout file
        phone_number = (EditText) findViewById(R.id.phone_number);
        first_name = (EditText) findViewById(R.id.firstnameedittext);
        email = (EditText) findViewById(R.id.email);
        last_name = (EditText) findViewById(R.id.lastname2);
        password = (EditText) findViewById(R.id.Password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        sign_up = (Button) findViewById(R.id.Sign_Up);
        loading = (ProgressBar) findViewById(R.id.loading);

        //determining what Sign Up button does when clicked
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_number2 = phone_number.getText().toString().trim();
                first_name2 = first_name.getText().toString().trim();
                email2 = email.getText().toString().trim();
                last_name2 = last_name.getText().toString().trim();
                password2 = password.getText().toString().trim();
                confirm_password2 = confirm_password.getText().toString().trim();

                if (!first_name2.equals("")) {
                    if (!last_name2.equals("")) {
                        if (!email2.equals("")) {
                            if (!phone_number2.equals("")) {
                                if (password2.equals(confirm_password2)) {
                                    //register
                                    Random random = new Random();
                                    int start = 1000+ random.nextInt(9000);
                                    users_id = "pal" + start+"";
                                    new SigningUp("http://palexpress.000webhostapp.com/PalExpress/signup.php",
                                            users_id,first_name2,last_name2,email2,phone_number2,password2).execute();
                                } else {
                                    password.setError("Please check Password");
                                }
                            } else {
                                phone_number.setError("Please check phone number");
                            }
                        } else {
                            email.setError("Please check E-Mail");
                        }
                    } else {
                        last_name.setError("Please check last name");
                    }
                } else {
                    first_name.setError("Please check first name");
                }
            }
        });
    }


    class SigningUp extends AsyncTask<Void, Void, String> {

        String url_location, user_id, first_name, last_name, email, phone_number, password;

        public SigningUp(String url_location, String user_id, String first_name, String last_name,
                         String email, String phone_number, String password) {
            this.url_location = url_location;
            this.user_id = user_id;
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.phone_number = phone_number;
            this.password = password;
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
                        URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(first_name, "UTF-8") + "&" +
                        URLEncoder.encode("lastname", "UTF-8") + "=" + URLEncoder.encode(last_name, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
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
            loading.setVisibility(View.GONE);
            Toast.makeText(Sign_up.this, s, LENGTH_LONG).show();

        }

    }
}
