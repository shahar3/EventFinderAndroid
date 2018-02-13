package com.example.shaha.eventfinderandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shaha.eventfinderandroid.Utils.InternetUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    Button submitBtn;
    EditText emailET;
    EditText passwordET;
    EditText confirmPasswordET;
    EditText firstNameET;
    EditText lastNameET;
    EditText phoneNumberET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = (EditText) findViewById(R.id.edit_text_username_register);
        passwordET = (EditText) findViewById(R.id.edit_text_password_register);
        confirmPasswordET = (EditText) findViewById(R.id.edit_text_confirm_password_register);
        firstNameET = (EditText) findViewById(R.id.edit_text_first_name_register);
        lastNameET = (EditText) findViewById(R.id.edit_text_last_name_register);
        phoneNumberET = (EditText) findViewById(R.id.edit_text_phone_number_register);
        submitBtn = (Button) findViewById(R.id.submit_btn_register);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle registration
                checkRegistration();
            }
        });
    }

    private void checkRegistration() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        //check if the confirmed password is identical to the password
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "The confirmed password is not the same like the password", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();

        JSONObject jsonObject = buildJSONObject(email, password, firstName, lastName, phoneNumber);

        //Execute the thread
        RegisterAsyncTask task = new RegisterAsyncTask();
        task.execute(jsonObject);
    }

    private JSONObject buildJSONObject(String email, String password, String firstName, String lastName, String phoneNumber) {
        JSONObject json = new JSONObject();
        try {
            json.put("Email", email);
            json.put("Password", password);
            json.put("FirstName", firstName);
            json.put("LastName", lastName);
            json.put("PhoneNumber", phoneNumber);
            json.put("PhoneID", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private class RegisterAsyncTask extends AsyncTask<JSONObject, Void, Integer> {

        @Override
        protected Integer doInBackground(JSONObject... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return -1;
            }
            int result = InternetUtils.registerUser(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
