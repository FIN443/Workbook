package com.example.workbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView mtv1;
    Button mWordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWordList = findViewById(R.id.list1);
        mtv1 = findViewById(R.id.textCont1);
        mtv1.setText("개발자: 20170662장지훈\n\n");
        mtv1.append("사용법\n\n");
        mtv1.append("- STUDY LIST: 선택한 단어장 보기\n");
        mtv1.append("- ADD LIST: 단어장 새로 추가\n");
        mtv1.append("- LOAD LIST: 엑셀 단어장 불러오기(.xlsx 확장자만 지원)\n");
        mtv1.append("- REMOVE LIST: 선택한 단어장 보기\n");
        mtv1.append("- EXPORT: 현재 단어장 엑셀로 내보내기\n\n");
        mtv1.append("추후에 .xls 형식도 지원하고 퀴즈기능도 넣을 예정");
        mtv1.append("github테스트");

        requestPermission();

        mWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WordListActivity.class);
                startActivity(intent);
            }
        });
    }

    void requestPermission() {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
