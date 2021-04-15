package com.example.workbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AddWordAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<AddWordItem> listViewItemList = new ArrayList<AddWordItem>() ;

    // ListViewAdapter의 생성자
    public AddWordAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_addword_custom, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView tv_num = (TextView) convertView.findViewById(R.id.list_addword_num);
        TextView tv_word = (TextView) convertView.findViewById(R.id.list_addword_word) ;
        TextView tv_mean = (TextView) convertView.findViewById(R.id.list_addword_mean) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        AddWordItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        int id = position + 1;
        tv_num.setText("" + id);
        tv_word.setText(listViewItem.getWord());
        tv_mean.setText(listViewItem.getMean());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String word, String mean) {
        AddWordItem item = new AddWordItem();

        item.setWord(word);
        item.setMean(mean);

        listViewItemList.add(item);
    }

    public void addItem(ArrayList<AddWordItem> items) {
        for(int i=0; i<items.size(); i++) {
            listViewItemList.add(items.get(i));
        }
    }

    public void setItem(int position, String word, String mean) {
        AddWordItem item = new AddWordItem();

        item.setWord(word);
        item.setMean(mean);

        listViewItemList.set(position, item);
    }

    public void delItem(int position) {
        listViewItemList.remove(position);
    }


}
