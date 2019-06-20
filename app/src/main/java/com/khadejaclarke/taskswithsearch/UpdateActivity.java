package com.khadejaclarke.taskswithsearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import volley.Config_URL;

public class UpdateActivity extends Activity {
    EditText email_old, email_new;
    Button update, cancel;
    SQLiteHandler db;
    UpdateActivity.User user;
    private static final String TAG = UpdateActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initUser();
        initWidgets();
        handleButtonClicks();
    }

    private void initUser() {
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String uid = getIntent().getStringExtra("uid");
        String created_at = getIntent().getStringExtra("created_at");

        user = new UpdateActivity.User(name, email, uid, created_at);
    }

    private void initWidgets() {
        email_old = findViewById(R.id.email_old);
        email_new = findViewById(R.id.email_new);
        update = findViewById(R.id.btnUpdate);
        cancel = findViewById(R.id.cancel);

        email_old.setText(user.name);
        email_new.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    update.setClickable(true);
                    update.setEnabled(true);
                }
            }
        });
    }

    private void handleButtonClicks() {
        update.setOnClickListener(v -> {
            String text_email_old = email_old.getText().toString();
            String text_email_new = email_new.getText().toString();

            if (text_email_old.equals(text_email_new))
                Snackbar.make(v, "Your new email address cannot be the same as your last.", Snackbar.LENGTH_LONG).show();
            else
                updateUser(user);
        });

        cancel.setOnClickListener(v -> new AlertDialog.Builder(UpdateActivity.this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> startActivity(new Intent(UpdateActivity.this, MainActivity.class)))
                .setNegativeButton(android.R.string.no, (dialog, which) -> {})
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
    }

    public void updateUser(UpdateActivity.User user) {
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, Config_URL.URL_UPDATE,
                response -> {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        if (!error) {
                            // User successfully updated in MySQL
                            // Now update the user in SQLite
                            JSONObject user1 = jObj.getJSONObject("user");
                            String name = user1.getString("name");
                            String email = user1.getString("email");
                            String uid = user1.getString("uid");
                            String created_at = user1.getString("created_at");

                            // Updating row in users table
                            db.updateUser(name, email, uid, created_at);

                            // Launch main activity
                            Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            // Error occurred in update. Get the error // message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "update");
                params.put("name", user.name);
                params.put("email", user.email);
                params.put("uid", user.uid);

                return params;
            }

        };

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }


    class User {
        String name, email, uid, created_at;

        User(String name, String email, String uid, String created_at) {
            this.name = name;
            this.email = email;
            this.uid = uid;
            this.created_at = created_at;
        }
    }
}


