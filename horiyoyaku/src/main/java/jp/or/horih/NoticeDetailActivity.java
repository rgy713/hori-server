package jp.or.horih;

import java.util.ArrayList;

import jp.or.horih.R;
import jp.or.horih.asynctask.NoticeDetailTask;
import jp.or.horih.common.SharedPreferencesHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;


public class NoticeDetailActivity extends FragmentActivity implements LoaderCallbacks<Bundle> {

    private ProgressDialog dialog;
    String notice_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        TextView txt_date = (TextView) findViewById(R.id.txt_date);
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        TextView txt_content = (TextView) findViewById(R.id.txt_content);

        Intent intent = getIntent();
        notice_id = intent.getStringExtra("notice_id");
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String content = intent.getStringExtra("content");


        txt_date.setText(date);
        txt_title.setText(title);
        txt_content.setText(content);


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("処理中です。しばらくお待ちください。");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(false);


        //データ取得
        getData();


    }

    //--------------------
    //サーバ処理
    //--------------------
    @Override
    public Loader<Bundle> onCreateLoader(int id, Bundle argments) {

        return new NoticeDetailTask(this, argments);

    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle result) {
        //結果判定
        NoticeDetailTask.ResultType resultType =
                NoticeDetailTask.ResultType.valueOf(result.getString("result"));
        dialog.dismiss();

        if (resultType == NoticeDetailTask.ResultType.SUCCESS) {
            setResult(RESULT_OK);

        } else {
            String msg = null;
            switch (resultType) {
                case NETWORK_ERROR:
                    msg = this.getString(R.string.network_error_string);
                    break;
                case REGIST_ERROR_ON_WEB:
                    msg = this.getString(R.string.regist_error_web_string);
                    break;
                case REGIST_ERROR_ON_APP:
                    //msg = "エラーが発生しました。APP";
                    //msg = "登録時にエラーが発生しました。";
                    msg = result.getString("cause");
                    break;
                case REGIST_ERROR_ON_DB:
                    msg = this.getString(R.string.regist_error_db_string);
                    break;
                default:
                    break;
            }

            //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
    //戻るボタン
    public void onClickBack(View v) {
        finish();
    }


    //--------------------
    //処理
    //--------------------
    public void getData() {
        dialog.show();
        Bundle argments = new Bundle();
        argments.putString("notice_id", notice_id);

        getSupportLoaderManager().initLoader(0, argments, NoticeDetailActivity.this);
    }

}
