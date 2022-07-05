package jp.or.horih;

import jp.or.horih.R;
import jp.or.horih.common.SharedPreferencesHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class TroubleActivity extends FragmentActivity {

    private SharedPreferences sp;
    public Context context;
    TextView page_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_trouble);
        
        /*
        page_body = (TextView)findViewById(R.id.page_body);
        
        String trouble_body = getString(R.string.trouble_body);
        //ｈｔｍｌ形式
        page_body.setText(Html.fromHtml(trouble_body));        
        */

        //取得
        getUrl();


    }

    //--------------------
    //クリック処理
    //--------------------
    //戻るボタン
    public void onClickBack(View v) {
        finish();
    }

    //電話ボタン
    public void onClickTel(View v) {
        AlertDialog.Builder builder = null;
        AlertDialog mMapDialog = null;

        builder = new AlertDialog.Builder(TroubleActivity.this);
        builder.setMessage("電話を発信します。\nよろしいですか？");
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mDialog, int which) {
                mDialog.dismiss();

                //電話
                String trouble_tel = getString(R.string.trouble_tel);
                Uri uri = Uri.parse("tel:" + trouble_tel);
                Intent i = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(i);


            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mDialog, int which) {
                mDialog.dismiss();
            }
        });

        mMapDialog = builder.create();
        mMapDialog.show();

    }


    //--------------------
    //処理
    //--------------------
    //urlを取得
    private void getUrl() {

        String url = "";
        url = this.getString(R.string.server_scheme)
                + this.getString(R.string.server_host)
                + this.getString(R.string.path_trouble);

        WebView web = (WebView) findViewById(R.id.webView1);

        //対象のWebivewを取得して設定を有効化
        web.getSettings().setJavaScriptEnabled(true);

        //その後、ページをロードします。
        web.loadUrl(url);

        //拡大縮小
        web.getSettings().setBuiltInZoomControls(true);

    }
}
