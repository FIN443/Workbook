package com.example.workbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddWordActivity extends AppCompatActivity {
    EditText et1;
    EditText et2;
    EditText et3;
    Button add;
    Button mod;
    Button del;
    Button export;
    Button save;

    ListView listview;
    AddWordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        et1 = findViewById(R.id.editText);
        et2 = findViewById(R.id.editText2);
        et3 = findViewById(R.id.editText3);
        add = findViewById(R.id.add);
        mod = findViewById(R.id.mod);
        del = findViewById(R.id.del);
        export = findViewById(R.id.saveExcel);
        save = findViewById(R.id.saveList);

        Intent getListName = getIntent();
        final String listName = getListName.getStringExtra("setListName");
        final ArrayList<String> lists = getIntent().getStringArrayListExtra("lists");
        et3.setText(listName);

        // Adapter 생성
        adapter = new AddWordAdapter();
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView)findViewById(R.id.wordList1);
        listview.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int count;
                //count = adapter.getCount();
                String word, mean;
                word = et1.getText().toString();
                mean = et2.getText().toString();
                // 아이템 추가
                adapter.addItem(word, mean);
                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        });

        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();
                String word, mean;
                word = et1.getText().toString();
                mean = et2.getText().toString();

                if(count > 0) {
                    // 현재 선택된 아이템의 position 획득
                    checked = listview.getCheckedItemPosition();
                    if(checked > -1 && checked < count) {
                        // 아이템 수정
                        adapter.setItem(checked, word, mean);
                        // listview 갱신
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();

                if(count > 0) {
                    // 현재 선택된 아이템의 position 획득
                    checked = listview.getCheckedItemPosition();

                    if(checked > -1 && checked < count) {
                        // 아이템 삭제
                        adapter.delItem(checked);
                        // listview 선택 초기화
                        listview.clearChoices();
                        // listview 갱신
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddWordItem ad;
                ad = (AddWordItem) adapter.getItem(position);
                et1.setText(ad.getWord());
                et2.setText(ad.getMean());
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et3.getText().toString();
                if(str.equals("") == true)
                    saveExcel(listName);
                else
                    saveExcel(str);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                String str = et3.getText().toString();
                for(int i=0; i<lists.size(); i++) {
                    if(str.equals(lists.get(i)) == true) {
                        count++;
                        break;
                    }
                }
                if(str.equals("") == true) {
                    // 단어장 내부저장소 저장
                    saveData(listName);
                    // intent 종료 및 리스트 이름 전달
                    Intent intent = new Intent();
                    intent.putExtra("R1", listName); // 리스트 이름 넘겨줌
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if(count == 0) {
                    saveData(str);
                    Intent intent = new Intent();
                    intent.putExtra("R1", et3.getText().toString()); // 리스트 이름 넘겨줌
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if(str.equals(listName) == true ) {
                    saveData(str);
                    Intent intent = new Intent();
                    intent.putExtra("R1", et3.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), str+"이 이미 존재합니다", Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent getCheck = getIntent();
        if(getCheck.getBooleanExtra("addList", false) == false) // 이전 액티비티에서 STUDY LIST를 선택했을 때
            loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void saveData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        ArrayList<AddWordItem> items = new ArrayList<AddWordItem>();
        for(int i=0; i<adapter.getCount(); i++) {
            items.add((AddWordItem) adapter.getItem(i));
        }
        String json = gson.toJson(items);
        editor.putString(name, json);
        editor.commit();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        Intent intent = getIntent();
        String listName = intent.getStringExtra("setListName");
        String json = sharedPreferences.getString(listName, null);
        ArrayList<AddWordItem> items = new ArrayList<AddWordItem>();
        Type type = new TypeToken<ArrayList<AddWordItem>>() {}.getType();
        items = gson.fromJson(json, type);

        adapter.addItem(items);
    }

    private void saveExcel(String name) { // 엑셀로 저장
        ArrayList<AddWordItem> items = new ArrayList<AddWordItem>();
        for(int i=0; i<adapter.getCount(); i++) {
            items.add((AddWordItem) adapter.getItem(i));
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("mysheet"));
        Row row = sheet.createRow(0); // 새로운 행
        Cell cell; // 엑셀의 셀

        // 1번 셀 생성과 입력
        cell = row.createCell(0);
        cell.setCellValue("단어");
        cell = row.createCell(1);
        cell.setCellValue("뜻");

        for(int i=0; i<items.size(); i++) { // 데이터 엑셀에 입력
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(items.get(i).getWord());
            cell = row.createCell(1);
            cell.setCellValue(items.get(i).getMean());
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File excelFile = new File(path, name+".xlsx");
        try {
            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            fos.close();
            Toast.makeText(getApplicationContext(), excelFile.getAbsolutePath()+"에 저장 성공", Toast.LENGTH_LONG).show();
        }catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "저장 실패", Toast.LENGTH_LONG).show();
        }
    }
}
