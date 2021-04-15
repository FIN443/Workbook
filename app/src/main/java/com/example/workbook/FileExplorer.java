package com.example.workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileExplorer extends Activity {
    String mCurrent;
    String mRoot;
    String mDown;
    Button btnroot;
    Button btndown;
    Button btnup;
    TextView mCurrentTxt;
    ListView mFileList;

    ArrayAdapter<String> mAdapter;
    ArrayList<String> arFiles;

    ArrayList<String> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer);
        mCurrentTxt = (TextView)findViewById(R.id.current);
        mFileList = (ListView)findViewById(R.id.filelist);
        btnroot = (Button)findViewById(R.id.btnroot);
        btndown = (Button)findViewById(R.id.btndown);
        btnup = (Button)findViewById(R.id.btnup);

        Intent getListName = getIntent();
        lists = getListName.getStringArrayListExtra("lists");

        arFiles = new ArrayList<String>();
        //SD카드 루트 가져옴
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        mDown = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        mCurrent = mRoot;

        //어댑터를 생성하고 연결해줌
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arFiles);
        mFileList.setAdapter(mAdapter);//리스트뷰에 어댑터 연결
        mFileList.setOnItemClickListener(mItemClickListener);//리스너 연결

        refreshFiles();

        btnroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrent.compareTo(mRoot) != 0){//루트가 아니면 루트로 가기
                    mCurrent = mRoot;
                    refreshFiles();//리프레쉬
                }
            }
        });

        btndown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrent.compareTo(mDown) != 0) {//다운로드가 아니면 다운로드로 가기
                    mCurrent = mDown;
                    refreshFiles();//리프레쉬
                }
            }
        });

        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrent.compareTo(mRoot) != 0){//루트가 아니면
                    int end = mCurrent.lastIndexOf("/");///가 나오는 마지막 인덱스를 찾고
                    String uppath = mCurrent.substring(0, end);//그부분을 짤라버림 즉 위로가게됨
                    mCurrent = uppath;
                    refreshFiles();//리프레쉬
                }
            }
        });
    }
    //리스트뷰 클릭 리스너
    AdapterView.OnItemClickListener mItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    String Name = arFiles.get(position);//클릭된 위치의 값을 가져옴
                    //디렉토리이면
                    if(Name.startsWith("[") && Name.endsWith("]")){
                        Name = Name.substring(1, Name.length() - 1);//[]부분을 제거해줌
                    }
                    //들어가기 위해 /와 터치한 파일 명을 붙여줌
                    String Path = mCurrent + "/" + Name;
                    File f = new File(Path);//File 클래스 생성
                    if(f.isDirectory()){//디렉토리면?
                        mCurrent = Path;//현재를 Path로 바꿔줌
                        refreshFiles();//리프레쉬
                    }
                    else {//디렉토리가 아니면 토스트 메세지를 뿌림
                        int count = 0;
                        String temp = arFiles.get(position);
                        String tempName = temp.substring(0, temp.length()-5);
                        temp = temp.substring(temp.length()-4, temp.length());
                        for(int i=0; i<lists.size(); i++) {
                            if(tempName.equals(lists.get(i)) == true)
                                count++;
                        }
                        if(count != 0) {
                            Intent intent = new Intent();
                            intent.putExtra("R1", arFiles.get(position)); // 이름 넘겨줌
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else if(temp.equals("xlsx") == true) {
                            loadExcel(arFiles.get(position), f);
                            Intent intent = new Intent();
                            intent.putExtra("R1", arFiles.get(position)); // 이름 넘겨줌
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent();
                            intent.putExtra("R1", arFiles.get(position)); // 이름 넘겨줌
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    }
                }
            };

    void refreshFiles(){
        mCurrentTxt.setText(mCurrent);//현재 PATH를 가져옴
        arFiles.clear();//배열리스트를 지움
        File current = new File(mCurrent);//현재 경로로 File클래스를 만듬
        String[] files = current.list();//현재 경로의 파일과 폴더 이름을 문자열 배열로 리턴

        //파일이 있다면?
        if(files != null){
            //여기서 출력을 해줌
            for(int i = 0; i < files.length;i++){
                String Path = mCurrent + "/" + files[i];
                String Name = "";

                File f = new File(Path);
                if(f.isDirectory()){
                    Name = "[" + files[i] + "]";//디렉토리면 []를 붙여주고
                }else{
                    Name = files[i];//파일이면 그냥 출력
                }
                arFiles.add(Name);//배열리스트에 추가해줌
            }
        }
        //다끝나면 리스트뷰를 갱신시킴
        mAdapter.notifyDataSetChanged();
    }

    private void loadExcel(String name, File path) {
        ArrayList<AddWordItem> loadItems = new ArrayList<AddWordItem>();
        String tempName = name.substring(0, name.length()-5);
        File excelFile = new File(path, name);
        try {
            FileInputStream fis = new FileInputStream(excelFile);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for(int r=1; r<rowsCount; r++) { // 첫 줄은 필요없는 부분
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                AddWordItem temp = new AddWordItem();
                for(int c=0; c<cellsCount; c++) { // cell은 word, mean 만 쓸 거라 2개
                    String value = getCellAsString(row, c, formulaEvaluator); // 요소 값
                    if(c==0)
                        temp.setWord(value);
                    else if(c==1)
                        temp.setMean(value);
                }
                loadItems.add(temp);
            }
            fis.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loadItems);
        editor.putString(tempName, json);
        editor.commit();
    }

    protected String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */
            e.printStackTrace();
        }
        return value;
    }
}