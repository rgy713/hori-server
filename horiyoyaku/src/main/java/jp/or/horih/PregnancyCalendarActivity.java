package jp.or.horih;

import jp.or.horih.R;
import jp.or.horih.common.SharedPreferencesHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class PregnancyCalendarActivity extends FragmentActivity {

    private SharedPreferences sp;
    public Context context;
    RelativeLayout lay_tab1;
    RelativeLayout lay_tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_pregnancy_calendar);

        //lay_tab1 = (RelativeLayout)findViewById(R.id.lay_tab1);
        //lay_tab2 = (RelativeLayout)findViewById(R.id.lay_tab2);

        //取得
        getUrl(1);

    }

    //--------------------
    //クリック処理
    //--------------------
    //戻るボタン
    public void onClickBack(View v) {
        finish();
    }

    //タブボタン１
    public void onClickTab1(View v) {
        lay_tab1.setBackgroundResource(R.drawable.tab_left);
        lay_tab2.setBackgroundResource(R.drawable.tab_right_off);

        //取得
        getUrl(1);

    }

    //タブボタン２
    public void onClickTab2(View v) {
        lay_tab1.setBackgroundResource(R.drawable.tab_left_off);
        lay_tab2.setBackgroundResource(R.drawable.tab_right);

        //取得
        getUrl(2);

    }


    //--------------------
    //処理
    //--------------------
    //urlを取得
    private void getUrl(int tab) {

        // 現在のintentを取得する
        Intent intent = getIntent();

        String url = "";
        if (tab == 1) {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                //APIレベル10以前の機種の場合の処理
                url = this.getString(R.string.server_scheme)
                        + this.getString(R.string.server_host)
                        + this.getString(R.string.path_calendar1_1);

            } else {
                //APIレベル11以降の機種の場合の処理
                url = this.getString(R.string.server_scheme)
                        + this.getString(R.string.server_host)
                        + this.getString(R.string.path_calendar1);

            }

            SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(PregnancyCalendarActivity.this);
            String birth_expected_year = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_YEAR, "");
            String birth_expected_month = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_MONTH, "");
            String birth_expected_day = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_DAY, "");
            String reg_date_year = sp.getString(SharedPreferencesHelper.REG_DATE_YEAR, "");
            String reg_date_month = sp.getString(SharedPreferencesHelper.REG_DATE_MONTH, "");
            String reg_date_day = sp.getString(SharedPreferencesHelper.REG_DATE_DAY, "");

            String sep = "?";
            if (!birth_expected_year.equals("")) {
                url = url + sep + "birth_expected_year=" + birth_expected_year;
                sep = "&";
            }
            if (!birth_expected_month.equals("")) {
                url = url + sep + "birth_expected_month=" + birth_expected_month;
            }
            if (!birth_expected_day.equals("")) {
                url = url + sep + "birth_expected_day=" + birth_expected_day;
            }

            if (!reg_date_year.equals("")) {
                url = url + sep + "reg_date_year=" + reg_date_year;
            }
            if (!reg_date_month.equals("")) {
                url = url + sep + "reg_date_month=" + reg_date_month;
            }
            if (!reg_date_day.equals("")) {
                url = url + sep + "reg_date_day=" + reg_date_day;
            }


        } else if (tab == 2) {
            url = this.getString(R.string.server_scheme)
                    + this.getString(R.string.server_host)
                    + this.getString(R.string.path_calendar2);

        }

        WebView web = (WebView) findViewById(R.id.webView1);

        //対象のWebivewを取得して設定を有効化
        web.getSettings().setJavaScriptEnabled(true);
        //キャッシュクリア
        web.clearCache(true);

        //その後、ページをロードします。
        web.loadUrl(url);

        //拡大縮小
        //web.getSettings().setBuiltInZoomControls(true);

    }
}
