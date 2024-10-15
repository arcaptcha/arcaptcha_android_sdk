package com.arcaptcha.arcaptchaandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcaptcha.andsdk.ArcaptchaDialog;


public class MainActivity extends AppCompatActivity {
    ArcaptchaDialog arcaptchaDialog;
    TextView txvLog;
    Button btnExe1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExe1 = findViewById(R.id.btnExe1);
        txvLog = findViewById(R.id.txvLog);

        btnExe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txvLog.setText("Arcaptcha");

                ArcaptchaDialog.ArcaptchaListener arcaptchaListener = new ArcaptchaDialog.ArcaptchaListener() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d("XQQQToken", "|" + token + "|");
                        Toast.makeText(MainActivity.this, "Puzzle Solved, Token Generated!",
                                Toast.LENGTH_LONG).show();
                        arcaptchaDialog.dismiss();
                        appendLog("ArcaptchaToken: \n" + token);
                    }

                    @Override
                    public void onCancel() {
                        arcaptchaDialog.dismiss();
                    }
                };

                ArcaptchaDialog.Builder arcaptchaDialogBuilder = new ArcaptchaDialog.Builder(
                        "afge5xjsq6",
                        "igpro.ir", arcaptchaListener);
                arcaptchaDialogBuilder.setTheme("dark");
                arcaptchaDialogBuilder.setBackgroundColor("#7E7E7E");
                arcaptchaDialogBuilder.setResponseCodeListener(new ArcaptchaDialog.ResponseCodeListener() {
                    @Override
                    public void onResponse(int statusCode) {
                        appendLog("Response Code: " + statusCode);
                    }
                });
                arcaptchaDialogBuilder.setTimeout(10000, new ArcaptchaDialog.TimeoutCallback() {
                    @Override
                    public void onTimeout() {
                        appendLog("Timeout Happens!");
                    }
                });
                arcaptchaDialog = arcaptchaDialogBuilder.build();
                arcaptchaDialog.show(getSupportFragmentManager(), "arcaptcha_dialog");
            }
        });
    }

    public void appendLog(String log){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String starter = txvLog.getText().toString().isEmpty() ? ""
                        : txvLog.getText().toString() + "\n";
                txvLog.setText(starter + log);
            }
        });
    }
}