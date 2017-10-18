package com.letiger.customedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    setContentView(R.layout.activity_main);

    CustomEditText custom = (CustomEditText) findViewById(R.id.custom);

    MyAdapter adapter =
        new MyAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
            getResources().getStringArray(R.array.countries));
    custom.setAdapter(adapter);
    custom.setDropDownAnimationStyle(R.style.CustomPopupAnimation);
  }
}
