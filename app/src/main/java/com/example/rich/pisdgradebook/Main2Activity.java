package com.example.rich.pisdgradebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tv=(TextView)findViewById(R.id.display);
        tv.setMovementMethod(new ScrollingMovementMethod());
        String result = getIntent().getStringExtra("text");
        tv.setText(result);
    }
}
