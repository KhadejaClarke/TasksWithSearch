package com.khadejaclarke.taskswithsearch;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;

public class MainActivity extends AppCompatActivity {
    private TextView txtName;
    private TextView txtEmail;
    private Button btnViewTasks;

    private SQLiteHandler db;
    private SessionManager session;

    private String name, email, uid, created_at;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = findViewById(R.id.name);
        txtEmail = findViewById(R.id.email);
        btnViewTasks = findViewById(R.id.btnViewTasks);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        name = user.get("name");
        email = user.get("email");
        uid = user.get("uid");
        created_at = user.get("created_at");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // ViewTasks button click event
        btnViewTasks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout :
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> logoutUser())
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {})
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            case R.id.action_update :
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                intent.putExtra("created_at", created_at);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
