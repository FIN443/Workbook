package com.example.workbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class WordListActivity extends AppCompatActivity {
    private Button addList;
    private Button loadList;
    private Button stdWord;
    private Button rmList;
    private Button back;
    private ListView listview;

    // 빈 데이터 리스트 생성
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    // ArrayList -> Json으로 변환
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";
    private String state;
    private static final String version = "xls"; // xls, xlsx

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == adapter.getCount()) { // ADD LIST 담당
            if(resultCode == RESULT_OK) {
                String listName = data.getStringExtra("R1");
                items.add(listName);
                saveData(getApplicationContext(), SETTINGS_PLAYER_JSON, items);
                Toast.makeText(getApplicationContext(), "정상적으로 저장되었습니다", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == 1000) { // 엑셀 불러오기 담당
            if(resultCode == RESULT_OK) {
                int count = 0;
                String fileName = data.getStringExtra("R1");
                String temp = fileName.substring(fileName.length()-4, fileName.length());
                if(temp.equals("xlsx") == true) {
                    String listName = fileName.substring(0, fileName.length()-5);
                    for(int i=0; i<items.size(); i++) {
                        if(listName.equals(items.get(i)) == true) {
                            count++;
                            break;
                        }
                    }
                    if(count == 0) {
                        items.add(listName);
                        Toast.makeText(getApplicationContext(), fileName+"이 정상적으로 불러와졌습니다", Toast.LENGTH_LONG).show();
                        saveData(getApplicationContext(), SETTINGS_PLAYER_JSON, items);
                        adapter.notifyDataSetChanged();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), fileName+"이 이미 존재합니다", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "xlsx 확장자가 아닙니다", Toast.LENGTH_LONG).show();
                }
            }
        }
        else { // STUDY LIST 담당
            if(resultCode == RESULT_OK) {
                String listName = data.getStringExtra("R1");
                items.set(requestCode, listName);
                saveData(getApplicationContext(), SETTINGS_PLAYER_JSON, items);
                Toast.makeText(getApplicationContext(), "정상적으로 저장되었습니다", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        addList = findViewById(R.id.addList);
        loadList = findViewById(R.id.loadList);
        stdWord = findViewById(R.id.stdWord);
        rmList = findViewById(R.id.removeList);
        back = findViewById(R.id.back);

        items = loadData(getApplicationContext(), SETTINGS_PLAYER_JSON);

        // 아이템 View를 선택(single choice)가능하도록 만듬
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items);
        listview = (ListView)findViewById(R.id.list1);
        // listview 생성 및 adapter 지정
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(getApplicationContext(), SETTINGS_PLAYER_JSON, items);
                Log.d(TAG, "Put json");
                finish();
            }
        });

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddWordActivity.class);
                intent.putExtra("addList", true);
                intent.putStringArrayListExtra("lists", items);
                startActivityForResult(intent, adapter.getCount());
            }
        });

        stdWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();
                if(count > 0) {
                    // 현재 선택된 아이템의 position 획득
                    checked = listview.getCheckedItemPosition();
                    if(checked > -1 && checked < count) {
                        // 설정
                        Intent intent = new Intent(getApplicationContext(), AddWordActivity.class);
                        intent.putExtra("setListName", adapter.getItem(checked));
                        intent.putStringArrayListExtra("lists", items);
                        startActivityForResult(intent, checked);
                    }
                }
            }
        });

        loadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FileExplorer.class);
                intent.putStringArrayListExtra("lists", items);
                startActivityForResult(intent, 1000);
            }
        });

        rmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();

                if(count > 0) {
                    // 현재 선택된 아이템의 position 획득
                    checked = listview.getCheckedItemPosition();

                    if(checked > -1 && checked < count) {
                        // 아이템 삭제
                        items.remove(checked);
                        // 저장
                        saveData(getApplicationContext(), SETTINGS_PLAYER_JSON, items);
                        // listview 선택 초기화
                        listview.clearChoices();
                        // listview 갱신
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private ArrayList loadData(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private void saveData(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }
}
