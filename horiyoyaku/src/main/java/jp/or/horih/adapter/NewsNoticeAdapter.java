package jp.or.horih.adapter;

import java.util.ArrayList;
import java.util.List;

import jp.or.horih.NoticeDetailActivity;
import jp.or.horih.R;
import jp.or.horih.item.NewsNoticeItem;
import jp.or.horih.view.textview.EllipsizingTextView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsNoticeAdapter extends BaseAdapter {

    private Context context;
    private List<Object> items;
    private List<Object> viewTypes;

    static class ViewHolder {
        String vh_notice_id;
        String vh_title;
        String vh_date;
        String vh_content;
    }


    public NewsNoticeAdapter(Context context, List<Object> items) {
        this.context = context;

        if (items == null) {
            this.items = new ArrayList<Object>();
        } else {
            this.items = items;
        }

        this.viewTypes = new ArrayList<Object>();
        this.viewTypes.add(NewsNoticeItem.class);
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
        if (baseItem instanceof NewsNoticeItem) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                customItemView = inflater.inflate(R.layout.item_news_notice, parent, false);
                vh = new ViewHolder();
                customItemView.setTag(vh);
            } else {
                customItemView = convertView;
                vh = (ViewHolder) convertView.getTag();
            }

            NewsNoticeItem item = (NewsNoticeItem) baseItem;

            RelativeLayout lay_item = (RelativeLayout) customItemView.findViewById(R.id.item_lay_cell);
            EllipsizingTextView txt_subject = (EllipsizingTextView) customItemView.findViewById(R.id.txt_subject);
            TextView txt_date = (TextView) customItemView.findViewById(R.id.txt_date);
            EllipsizingTextView txt_content = (EllipsizingTextView) customItemView.findViewById(R.id.txt_content);
            TextView txt_new = (TextView) customItemView.findViewById(R.id.txt_new);

            txt_subject.setText(item.getTitle());
            txt_date.setText(item.getSend_date());
            txt_content.setText(item.getContent());

            if (item.getUm_flg().equals("1")) {
                txt_new.setVisibility(View.GONE);
            } else {
                txt_new.setVisibility(View.VISIBLE);
            }

            //遷移先
            vh.vh_notice_id = item.getNotice_id();
            vh.vh_title = item.getTitle();
            vh.vh_date = item.getSend_date();
            vh.vh_content = item.getContent();

            //クリック処理
            lay_item.setTag(vh);
            lay_item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //画面遷移
                    ViewHolder holder = (ViewHolder) v.getTag();

                    Intent intent = new Intent(context, NoticeDetailActivity.class);
                    intent.putExtra("notice_id", holder.vh_notice_id);
                    intent.putExtra("title", holder.vh_title);
                    intent.putExtra("date", holder.vh_date);
                    intent.putExtra("content", holder.vh_content);
                    context.startActivity(intent);
                }
            });


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
