package kr.co.shinhan.www.MyPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.shinhan.www.R;

public class MyPageTaxiListAdapter extends BaseAdapter {
    ArrayList<MyPageTaxiDataForm> item;
    Context context;

    public MyPageTaxiListAdapter(ArrayList<MyPageTaxiDataForm> item, Context context) {
        this.item = item;
        this.context = context;
    }


    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int i) {
        return item.get(i);
    }

    @Override
    public long getItemId(int i) {
        return item.get(i).getNo();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.mp_carpool_list_draw, viewGroup, false);

        TextView dt = view.findViewById(R.id.mp_cb_departureTimeTV);
        String dtTemp = item.get(i).getDt();
        String departureTimeHuman = "20" + dtTemp.substring(0,2) + "년 " + dtTemp.substring(3, 5) + "월 " + dtTemp.substring(6, 8) + "일 " + dtTemp.substring(9, 11) + "시 " + dtTemp.substring(12, 14) + "분";
        dt.setText(departureTimeHuman);

        TextView sp = view.findViewById(R.id.mp_cb_startPointTV);
        sp.setText(item.get(i).getSp());

        TextView de = view.findViewById(R.id.mp_cb_destinationTV);
        de.setText(item.get(i).getDe());

        return view;
    }    
}
