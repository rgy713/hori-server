package jp.or.horih;

import java.util.ArrayList;

import jp.or.horih.R;
import jp.or.horih.adapter.NewsNoticeAdapter;
import jp.or.horih.asynctask.NewsNoticeListTask;
import jp.or.horih.common.SharedPreferencesHelper;
import jp.or.horih.item.NewsNoticeItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class NewsNoticeListActivity extends FragmentActivity implements LoaderCallbacks<Bundle> {

    private ProgressDialog dialog;
    ListView list;
    NewsNoticeAdapter adapter;
    ArrayList<Bundle> listData = new ArrayList<Bundle>();
    String user_id;
    String patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_notice_list);

        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(NewsNoticeListActivity.this);
        user_id = sp.getString(SharedPreferencesHelper.USER_ID, "");
        patient_id = sp.getString(SharedPreferencesHelper.REG_PATIENT_ID, "");

        list = (ListView) findViewById(R.id.recordListView);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("処理中です。しばらくお待ちください。");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(false);


        //データ取得
        //getData();

    }

    @Override
    protected void onResume() {
        // TODO 自動生成されたメソッド・スタブ
        super.onResume();
        //データ取得
        getData();

    }

    @Override
    public Loader<Bundle> onCreateLoader(int id, Bundle argments) {
        return new NewsNoticeListTask(this, argments);
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle result) {
        //結果判定
        NewsNoticeListTask.ResultType resultType =
                NewsNoticeListTask.ResultType.valueOf(result.getString("result"));
        dialog.dismiss();

        if (resultType == NewsNoticeListTask.ResultType.SUCCESS) {
            setResult(RESULT_OK);

            listData = result.getParcelableArrayList("apiList");

            setData();

        } else {
            String msg = null;
            switch (resultType) {
                case NETWORK_ERROR:
                    msg = "ネットワークに接続されていません。";
                    break;
                case REGIST_ERROR_ON_WEB:
                    msg = "ネットワークでエラーが発生しました。";
                    break;
                case REGIST_ERROR_ON_APP:
                    //msg = "エラーが発生しました。APP";
                    //msg = "登録時にエラーが発生しました。";
                    msg = result.getString("cause");
                    break;
                case REGIST_ERROR_ON_DB:
                    msg = "エラーが発生しました。DB";
                    break;
                default:
                    break;
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        getSupportLoaderManager().destroyLoader(loader.getId());
        dialog.dismiss();

    }

    @Override
    public void onLoaderReset(Loader<Bundle> arg0) {
        dialog.dismiss();
    }

    //--------------------
    //共通ボタンクリック処理
    //--------------------
    //header共通 start----------
    public void onClickBack(View v) {
        finish();
    }

    //ホーム
    public void onClickHome(View v) {
        Intent intent = new Intent(NewsNoticeListActivity.this, NewsNoticeListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    //header共通 end----------

    //--------------------
    //ボタンクリック処理
    //--------------------

    //--------------------
    //処理
    //--------------------
    public void getData() {
        dialog.show();
        Bundle argments = new Bundle();
        argments.putString("user_id", patient_id);

        getSupportLoaderManager().initLoader(0, argments, NewsNoticeListActivity.this);
    }

    public void setData() {
        //リスト表示--------------------
        ArrayList<Object> items = new ArrayList<Object>();

        NewsNoticeItem item = new NewsNoticeItem();

        if (listData != null) {
            for (int i = 0; i < listData.size(); i++) {
                Bundle apiList = listData.get(i);

                item = new NewsNoticeItem();
                item.setNotice_id(apiList.getString("notice_id"));
                item.setTitle(apiList.getString("titile"));
                item.setSend_date(apiList.getString("send_date"));
                item.setContent(apiList.getString("content"));
                item.setUm_flg(apiList.getString("um_flg"));


                items.add(item);

            }

            adapter = new NewsNoticeAdapter(NewsNoticeListActivity.this, items);
            list.setAdapter(adapter);
        }

    }

}
