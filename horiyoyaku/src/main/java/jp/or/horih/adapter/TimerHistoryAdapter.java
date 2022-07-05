package jp.or.horih.adapter;

import java.util.ArrayList;
import java.util.List;

import jp.or.horih.R;
import jp.or.horih.item.TimerHistoryItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimerHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<Object> items;
    private List<Object> viewTypes;

    static class ViewHolder {
    }


    public TimerHistoryAdapter(Context context, List<Object> items) {
        this.context = context;

        if (items == null) {
            this.items = new ArrayList<Object>();
        } else {
            this.items = items;
        }

        this.viewTypes = new ArrayList<Object>();
        this.viewTypes.add(TimerHistoryItem.class);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customItemView = null;
        ViewHolder vh;

        Object baseItem = items.get(position);

        if (baseItem instanceof TimerHistoryItem) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customItemView = inflater.inflate(R.layout.item_timer_history, parent, false);
                vh = new ViewHolder();
                customItemView.setTag(vh);
            } else {
                customItemView = convertView;
                vh = (ViewHolder) convertView.getTag();
            }

            Activity activity = (Activity) context;

            TimerHistoryItem item = (TimerHistoryItem) baseItem;

            TextView txt_start_date = (TextView) customItemView.findViewById(R.id.txt_start_date);
            TextView txt_start_time = (TextView) customItemView.findViewById(R.id.txt_start_time);
            TextView txt_time = (TextView) customItemView.findViewById(R.id.txt_time);
            TextView txt_interval = (TextView) customItemView.findViewById(R.id.txt_interval);

            txt_start_date.setText(item.getStart_date());
            txt_start_time.setText(item.getStart_time());
            txt_time.setText(item.getTime());
            txt_interval.setText(item.getInterval());


        } else {

        }

        return customItemView;
    }

    public void addItem(Object obj) {
        items.add(obj);
    }

    public void clearItems() {
        items.clear();
    }

    @Override
    public int getViewTypeCount() {
        return viewTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        return viewTypes.indexOf(item.getClass());
    }


}
