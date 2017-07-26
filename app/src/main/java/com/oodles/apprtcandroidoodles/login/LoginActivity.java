package com.oodles.apprtcandroidoodles.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oodles.apprtcandroidoodles.MyPrefs;
import com.oodles.apprtcandroidoodles.R;

/**
 * Created by ankita on 20/4/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button loginbtn;
    EditText mobileNumber, password;
    MyPrefs myPrefs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPrefs = new MyPrefs(LoginActivity.this);
        if (myPrefs.isLogin()) {
            Intent loginUser = new Intent(LoginActivity.this, LoginUserActivity.class);
            startActivity(loginUser);
            finish();
        } else {
            setContentView(R.layout.login_activity);
            loginbtn = (Button) findViewById(R.id.loginbtn);
            mobileNumber = (EditText) findViewById(R.id.mobileNumber);
            password = (EditText) findViewById(R.id.password);
            loginbtn.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginbtn:
                if (!mobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    if (password.getText().toString().toString().equalsIgnoreCase("oodles")) {
                        Intent intent = new Intent(LoginActivity.this, LoginUserActivity.class);
                        startActivity(intent);
                        myPrefs.setCallFrom(mobileNumber.getText().toString().trim());
                        myPrefs.setLogin(true);
                        finish();
                    } else {
                        Toast.makeText(this, "Please Enter Correct Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.mobileNumber:
                break;
            case R.id.password:
                break;
            default:
                break;

        }
    }
}
