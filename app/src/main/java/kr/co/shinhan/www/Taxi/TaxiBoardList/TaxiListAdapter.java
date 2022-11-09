package kr.co.shinhan.www.Taxi.TaxiBoardList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.co.shinhan.www.R;

public class TaxiListAdapter extends BaseAdapter {

    ArrayList<TaxiBoardListForm> items;
    Context context;

    public TaxiListAdapter(ArrayList<TaxiBoardListForm> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) { return items.get(i).getNo(); }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.carpool_board_list_draw, viewGroup, false);

        TextView title = view.findViewById(R.id.cb_title);
        title.setText(items.get(i).getSp());

        TextView startPoint = view.findViewById(R.id.startPointTV);
        startPoint.setText(items.get(i).getSp());

        TextView destination = view.findViewById(R.id.destinationTV);
        destination.setText(items.get(i).getDe());

        TextView admit = view.findViewById(R.id.admitTV);
        admit.setText("(" + items.get(i).getCurAdmit() + " / " + items.get(i).getAdmit() + ")");

        return view;
    }
}
