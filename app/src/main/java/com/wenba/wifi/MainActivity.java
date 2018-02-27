package com.wenba.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.wenba.wifi.connecter.activity.WifiListActivity;
import com.wenba.wifi.connecter.util.WifiUtil;

/**
 * Created by Dengmao on 18/1/19.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
        Spinner spinnerSchool = findViewById(R.id.spinner_school);
        Spinner spinnerClass = findViewById(R.id.spinner_class);
        Spinner spinnerStudent = findViewById(R.id.spinner_student);
        // 建立数据源
        final String[] mItems = {"School 1", "School 2", "School 3", "School 4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSchool.setAdapter(adapter);
        spinnerClass.setAdapter(adapter);
        spinnerStudent.setAdapter(adapter);
        spinnerSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(MainActivity.this, mItems[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (WifiUtil.isWifiConn(this) && WifiUtil.isNetworkAvalible(this)) {
            Toast.makeText(this, "跳转到检查更新", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, WifiListActivity.class);
            startActivity(intent);
        }
    }
}
