package com.example.shaha.eventfinderandroid;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shaha.eventfinderandroid.Utils.InternetUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    Button submitBtn;
    EditText emailET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = (EditText) findViewById(R.id.edit_text_username);
        passwordET = (EditText) findViewById(R.id.edit_text_password);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle the login
                validateLogin();
            }
        });
    }

    private void validateLogin() {
        //check if the user exist
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        //check if user exist
        LoginAsyncTask task = new LoginAsyncTask();
        JSONObject jsonObject = buildJSONObject(email, password);
        task.execute(jsonObject);
    }

    private JSONObject buildJSONObject(String email, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("Email", email);
            json.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private class LoginAsyncTask extends AsyncTask<JSONObject, Void, Integer> {

        @Override
        protected Integer doInBackground(JSONObject... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return -1;
            }

            int result = InternetUtils.userExist(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                //open the main activity
                Intent intent = new Intent(getApplicationContext(), EventsMainActivity.class);
                intent.putExtra("userId", result);
                startActivity(intent);
            } else if (result == -1) {
                Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Login.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
