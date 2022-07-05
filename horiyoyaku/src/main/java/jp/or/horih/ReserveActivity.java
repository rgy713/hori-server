package jp.or.horih;

import jp.or.horih.R;
import jp.or.horih.common.SharedPreferencesHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class ReserveActivity extends FragmentActivity {

    private SharedPreferences sp;
    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_reserve);

        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(ReserveActivity.this);
        String reg_num = sp.getString(SharedPreferencesHelper.REG_PATIENT_ID, "");
        String reg_year = sp.getString(SharedPreferencesHelper.REG_YEAR, "");
        String reg_month = sp.getString(SharedPreferencesHelper.REG_MONTH, "");
        String reg_day = sp.getString(SharedPreferencesHelper.REG_DAY, "");

        // 現在のintentを取得する
        Intent intent = getIntent();
        String menu = intent.getStringExtra("menu");

        String path = this.getString(R.string.path_reserve);
        if (menu.equals("1")) {
            path = this.getString(R.string.path_reserve);
        } else if (menu.equals("2")) {
            path = this.getString(R.string.path_reserve_cancel);
        } else if (menu.equals("3")) {
            path = this.getString(R.string.path_reserve_call);
        }

        String url = this.getString(R.string.server_scheme)
                + this.getString(R.string.server_host)
                + path;

        String sep = "?";
        if (!reg_num.equals("")) {
            url = url + sep + "user_id=" + reg_num;
            sep = "&";
        }
        if (!reg_year.equals("") && !reg_month.equals("") && !reg_num.equals("")) {
            url = url + sep + "birthday=" + reg_year + "/" + reg_month + "/" + reg_day;
        }

        WebView web = (WebView) findViewById(R.id.webView1);

        //対象のWebivewを取得して設定を有効化
        web.getSettings().setJavaScriptEnabled(true);
        //キャッシュクリア
        web.clearCache(true);
        //その後、ページをロードします。
        web.loadUrl(url);

        // ロケーション変更イベントを拾う
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view_, String url_) {
                // タップされたリンクのURL（変更先ロケーション）
                Log.v("url", url_);
                Log.v("url", "can go back ? -> " + view_.canGoBack());

                String request_string = url_;

                //urlが「native://js」の場合画面をとじる
                String web_close_string = context.getString(R.string.web_close_string);
                if (request_string.startsWith(web_close_string)) {

                    finish();

                } else {

                    return false;

                }
                // trueを返却
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ReserveActivity.this);
                ad.setMessage(message);
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                ad.show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {

                AlertDialog.Builder ad = new AlertDialog.Builder(ReserveActivity.this);
                final EditText et = new EditText(ReserveActivity.this);
                ad.setView(et);
                ad.setMessage(message);

                ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm(et.getText().toString());
                        dialog.dismiss();
                    }
                });
                ad.setPositiveButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                        dialog.dismiss();
                    }
                });
                ad.create().show();
                return true;
            }
        });


    }

}
