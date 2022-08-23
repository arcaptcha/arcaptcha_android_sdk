package com.arcaptcha.arcaptchaandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
                txvLog.setText("Hello");

                ArcaptchaDialog.ArcaptchaListener arcaptchaListener = new ArcaptchaDialog.ArcaptchaListener() {
                    @Override
                    public void onSuccess(String token) {
                        Toast.makeText(MainActivity.this, "Puzzle Solved, Token Generated!",
                                Toast.LENGTH_LONG).show();
                        arcaptchaDialog.dismiss();

                        Log.d("XQQQAT", "[" + token + "]");
                        txvLog.setText("ArcaptchaToken: \n" + token);
                    }

                    @Override
                    public void onCancel() {
                        arcaptchaDialog.dismiss();
                    }
                };

                arcaptchaDialog = ArcaptchaDialog.getInstance("s14ajm9xob",
                        "forsdk.com","dark","#7E7E7E", arcaptchaListener);
                arcaptchaDialog.show(getSupportFragmentManager(), "arcaptcha_dialog");
            }
        });
    }
}