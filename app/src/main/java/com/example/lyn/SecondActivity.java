package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        b1=(Button) findViewById(R.id.button1);
        b1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button1)
        {
            Intent i = new Intent(SecondActivity.this, Login.class);
            startActivity(i);
        }

    }
}