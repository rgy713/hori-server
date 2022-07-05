package jp.or.horih;

import java.util.ArrayList;
import java.util.Calendar;

import jp.or.horih.R;
import jp.or.horih.asynctask.LoginTask;
import jp.or.horih.common.SharedPreferencesHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuActivity extends FragmentActivity implements LoaderCallbacks<Bundle> {

    private ProgressDialog dialog;
    private SharedPreferences sp;

    AlertDialog.Builder alertBuilder;

    //RelativeLayout lay_description1;
    RelativeLayout lay_description2;

    RelativeLayout lay_pregnancy;
    RelativeLayout lay_labor_pains;
    RelativeLayout lay_trouble;

    TextView count_message;
    TextView count_message2;
    TextView txt_week_count37;
    TextView txt_week_month;
    TextView txt_expected_date;
    TextView txt_notice_count;

    TextView txt_mes1;
    TextView txt_mes2;
    TextView txt_mes3;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        //lay_description1 = (RelativeLayout)findViewById(R.id.lay_description1);
        lay_description2 = (RelativeLayout) findViewById(R.id.lay_description2);

        count_message = (TextView) findViewById(R.id.count_message);
        count_message2 = (TextView) findViewById(R.id.count_message2);
        txt_week_count37 = (TextView) findViewById(R.id.txt_week_count37);
        txt_week_month = (TextView) findViewById(R.id.txt_week_month);
        txt_expected_date = (TextView) findViewById(R.id.txt_expected_date);
        txt_notice_count = (TextView) findViewById(R.id.txt_notice_count);

        txt_mes1 = (TextView) findViewById(R.id.txt_mes1);
        txt_mes2 = (TextView) findViewById(R.id.txt_mes2);
        txt_mes3 = (TextView) findViewById(R.id.txt_mes3);

        lay_pregnancy = (RelativeLayout) findViewById(R.id.lay_pregnancy);
        lay_labor_pains = (RelativeLayout) findViewById(R.id.lay_labor_pains);
        lay_trouble = (RelativeLayout) findViewById(R.id.lay_trouble);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("処理中です。しばらくお待ちください。");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(false);

        //フォント設定
        setFont();

        //データ取得
        //getData();

        //バージョンチェック
        version_check();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //データ取得
        getData();


        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(MenuActivity.this);

        String reg_pregnancy = sp.getString(SharedPreferencesHelper.REG_PREGNANCY, "");
        String reg_birth_expected_day = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_DAY, "");

        if (reg_pregnancy.equals("1")) {
            //lay_description1.setVisibility(View.GONE);
            lay_description2.setVisibility(View.VISIBLE);

            if (!reg_birth_expected_day.equals("")) {
                //lay_description1.setVisibility(View.GONE);
                lay_description2.setVisibility(View.VISIBLE);
                lay_pregnancy.setVisibility(View.VISIBLE);
                //計算
                pregnancy_Calculation();
            } else {
                //lay_description1.setVisibility(View.VISIBLE);
                lay_description2.setVisibility(View.GONE);
                lay_pregnancy.setVisibility(View.GONE);
            }
            lay_labor_pains.setVisibility(View.VISIBLE);
            lay_trouble.setVisibility(View.VISIBLE);

        } else if (reg_pregnancy.equals("0")) {
            //lay_description1.setVisibility(View.VISIBLE);
            lay_description2.setVisibility(View.GONE);

            lay_pregnancy.setVisibility(View.GONE);
            lay_labor_pains.setVisibility(View.GONE);
            lay_trouble.setVisibility(View.GONE);
        } else {
            //画面遷移
            Intent intent = new Intent(this, RegistrationInputActivity.class);
            startActivity(intent);

        }


    }


    //--------------------
    //サーバ処理
    //--------------------
    @Override
    public Loader<Bundle> onCreateLoader(int id, Bundle argments) {
        //ログイン
        return new LoginTask(this, argments);
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle result) {
        //結果判定
        LoginTask.ResultType resultType =
                LoginTask.ResultType.valueOf(result.getString("result"));
        dialog.dismiss();

        if (resultType == LoginTask.ResultType.SUCCESS) {
            setResult(RESULT_OK);

            ArrayList<Bundle> listData = result.getParcelableArrayList("apiList");

            if (listData != null) {
                SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(MenuActivity.this);
                Editor editor = sp.edit();

                for (int i = 0; i < listData.size(); i++) {
                    Bundle apiList = listData.get(i);


                    String user_id = apiList.getString("user_id");
                    String patient_id = apiList.getString("patient_id");

                    editor.putString(SharedPreferencesHelper.USER_ID, user_id);
                    editor.putString(SharedPreferencesHelper.REG_PATIENT_ID, patient_id);
                }

                // データの保存
                editor.commit();

            }


            SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(MenuActivity.this);
            String mi_notice_count = sp.getString(SharedPreferencesHelper.MI_NOTICE_COUNT, "");


            //メッセージ未読
            if (mi_notice_count.equals("") || mi_notice_count.equals("0")) {
                txt_notice_count.setVisibility(View.GONE);
            } else {
                txt_notice_count.setText(mi_notice_count);
                txt_notice_count.setVisibility(View.VISIBLE);
            }

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
                    //msg = result.getString("cause");
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
    //処理
    //--------------------
    public void getData() {
        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(MenuActivity.this);
        String push_key = sp.getString(SharedPreferencesHelper.REGISTRATION_ID, "");

        String user_id = sp.getString(SharedPreferencesHelper.USER_ID, "");
        String patient_id = sp.getString(SharedPreferencesHelper.REG_PATIENT_ID, "");

        String birth_year = sp.getString(SharedPreferencesHelper.REG_YEAR, "");
        String birth_month = sp.getString(SharedPreferencesHelper.REG_MONTH, "");
        String birth_day = sp.getString(SharedPreferencesHelper.REG_DAY, "");


        if (!patient_id.equals("") && !push_key.equals("")) {
            dialog.show();
            Bundle argments = new Bundle();

            argments.putString("user_id", user_id);
            argments.putString("patient_id", patient_id);
            argments.putString("token", push_key);
            argments.putString("kind", "2");
            argments.putString("birth_month", birth_month);
            argments.putString("birth_day", birth_day);

            argments.putString("s_year", birth_year);
            argments.putString("s_month", birth_day);
            argments.putString("s_day", birth_day);


            getSupportLoaderManager().initLoader(0, argments, MenuActivity.this);
        }

    }

    //--------------------
    //クリック処理
    //--------------------

    //設定ボタン
    public void onClickSetting(View v) {

        Intent intent = new Intent(this, RegistrationInputActivity.class);
        startActivity(intent);

    }

    //予約するボタン
    public void onClickReserve(View v) {

        //Intent intent = new Intent(this, ReserveActivity.class);
        Intent intent = new Intent(this, ReserveMenuActivity.class);
        startActivity(intent);

    }

    //妊娠カレンダーボタン
    public void onClickPregnancy(View v) {
        Intent intent = new Intent(this, PregnancyCalendarActivity.class);
        startActivity(intent);
    }

    //陣痛タイマーボタン
    public void onClickLaborPains(View v) {
        Intent intent = new Intent(this, LaborPainsTimerActivity.class);
        startActivity(intent);


    }

    //困ったらボタン
    public void onClickTrouble(View v) {
        Intent intent = new Intent(this, TroubleActivity.class);
        startActivity(intent);
    }


    //お問い合わせボタン
    public void onClickInquiry(View v) {
        Intent intent = new Intent(this, InquiryMenuActivity.class);
        startActivity(intent);

    }

    //おしらせボタン
    public void onClickNotice(View v) {
        Intent intent = new Intent(this, NewsNoticeListActivity.class);
        startActivity(intent);

    }


    //--------------------
    //処理
    //--------------------

    //フォントを設定
    public void setFont() {

        String head_font = getString(R.string.head_font);
        String count_font = getString(R.string.count_font);

        count_message.setTypeface(Typeface.createFromAsset(getAssets(), count_font));
        count_message2.setTypeface(Typeface.createFromAsset(getAssets(), count_font));

    }


    //妊娠計算
    public void pregnancy_Calculation() {


        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(MenuActivity.this);

        String reg_birth_expected_year = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_YEAR, "");
        String reg_birth_expected_month = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_MONTH, "");
        String reg_birth_expected_day = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_DAY, "");


        if (
                !reg_birth_expected_year.equals("")
                        && !reg_birth_expected_month.equals("")
                        && !reg_birth_expected_day.equals("")
                ) {

            //数値変換
            int int_birth_expected_year = Integer.parseInt(reg_birth_expected_year);
            int int_birth_expected_month = Integer.parseInt(reg_birth_expected_month);
            int int_birth_expected_day = Integer.parseInt(reg_birth_expected_day);

            //本日用
            Calendar calendar_today;
            calendar_today = Calendar.getInstance();
            //時間は0にする
            calendar_today.set(Calendar.HOUR_OF_DAY, 0);
            calendar_today.set(Calendar.MINUTE, 0);
            calendar_today.set(Calendar.SECOND, 0);
            calendar_today.set(Calendar.MILLISECOND, 0);

            //出産予定日
            Calendar calendar_expected;
            calendar_expected = Calendar.getInstance();
            //時間は0にする
            calendar_expected.set(Calendar.HOUR_OF_DAY, 0);
            calendar_expected.set(Calendar.MINUTE, 0);
            calendar_expected.set(Calendar.SECOND, 0);
            calendar_expected.set(Calendar.MILLISECOND, 0);

            //出産開始日
            Calendar calendar_start;
            calendar_start = Calendar.getInstance();
            //時間は0にする
            calendar_start.set(Calendar.HOUR_OF_DAY, 0);
            calendar_start.set(Calendar.MINUTE, 0);
            calendar_start.set(Calendar.SECOND, 0);
            calendar_start.set(Calendar.MILLISECOND, 0);

            if (!reg_birth_expected_month.equals("")) {
                calendar_expected.set(Calendar.YEAR, Integer.parseInt(reg_birth_expected_year));
                calendar_expected.set(Calendar.MONTH, Integer.parseInt(reg_birth_expected_month) - 1);
                calendar_expected.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reg_birth_expected_day));


                calendar_start.set(Calendar.YEAR, Integer.parseInt(reg_birth_expected_year));
                calendar_start.set(Calendar.MONTH, Integer.parseInt(reg_birth_expected_month) - 1);
                calendar_start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reg_birth_expected_day));

                calendar_start.add(Calendar.DAY_OF_MONTH, -280);

            }

            //40週まで
            //long型の差分（ミリ秒）  
            long diffTime40 = calendar_expected.getTimeInMillis() - calendar_today.getTimeInMillis() - (0 * (1000 * 60 * 60 * 24));
            /*
            if(diffTime40 > 0){
            	String str_diffTime40 = String.valueOf(diffTime40 / (1000 * 60 * 60 * 24 ));
                count_message.setText(str_diffTime40);
            }else{
                count_message.setText("");
            }
             */


            if (diffTime40 >= 0) {
                txt_mes1.setVisibility(View.VISIBLE);
                txt_mes2.setVisibility(View.VISIBLE);
                txt_mes3.setVisibility(View.VISIBLE);
                txt_week_month.setVisibility(View.VISIBLE);
                txt_week_count37.setVisibility(View.VISIBLE);
                count_message.setVisibility(View.VISIBLE);
                count_message2.setVisibility(View.VISIBLE);

                long diffTime_week = diffTime40 / (1000 * 60 * 60 * 24 * 7);
                String str_diffTime_week = String.valueOf(diffTime_week);
                String str_diffTime_day = String.valueOf((diffTime40 / (1000 * 60 * 60 * 24)) - (diffTime_week * 7));

                //int int_diffTime_week = Integer.parseInt(str_diffTime_week);
                //int int_diffTime_day = Integer.parseInt(str_diffTime_day);

                if (str_diffTime_week.equals("0")) {
                    txt_mes2.setVisibility(View.GONE);
                    count_message.setVisibility(View.GONE);
                }

                count_message.setText(str_diffTime_week);
                count_message2.setText(str_diffTime_day);
            } else {
                txt_mes1.setVisibility(View.GONE);
                txt_mes2.setVisibility(View.GONE);
                txt_mes3.setVisibility(View.GONE);
                txt_week_month.setVisibility(View.GONE);
                txt_week_count37.setVisibility(View.GONE);

                count_message.setText("0");
                count_message2.setText("");

            }


            //37週まで（37週0日まであとn日）
            long diffTime37 = calendar_expected.getTimeInMillis() - calendar_today.getTimeInMillis() - (21 * (1000 * 60 * 60 * 24));
            if (diffTime37 >= 0) {
                String str_diffTime37 = String.valueOf(diffTime37 / (1000 * 60 * 60 * 24));
                int int_diffTime37 = Integer.parseInt(str_diffTime37);
                txt_week_count37.setText(String.format("37週0日まであと%3d日", int_diffTime37));
            } else {
                txt_week_count37.setText("37週0日まであと0日");
            }

            //妊娠何週目　妊娠何ヶ月目（19週と４日目／妊娠４ヶ月目）
            //long diffTime_n = calendar_today.getTimeInMillis() - calendar_start.getTimeInMillis() + (0 * (1000 * 60 * 60 * 24) );
            long diffTime_n = calendar_today.getTimeInMillis() - calendar_start.getTimeInMillis();

            if (diffTime_n >= 0) {
                long diffTime_week = diffTime_n / (1000 * 60 * 60 * 24 * 7);
                String str_diffTime_week = String.valueOf(diffTime_week);
                //String str_diffTime_day = String.valueOf( (diffTime_n % (1000 * 60 * 60 * 24 * 7) ) / (1000 * 60 * 60 * 24) );
                String str_diffTime_day = String.valueOf((diffTime_n / (1000 * 60 * 60 * 24)) - (diffTime_week * 7));

                int int_diffTime_week = Integer.parseInt(str_diffTime_week);
                int int_diffTime_day = Integer.parseInt(str_diffTime_day);
                int int_diffTime_month = int_diffTime_week / 4;
                int_diffTime_month = int_diffTime_month + 1;


                txt_week_month.setText(String.format("%3d週と%3d日目／妊娠%3dヶ月目 ", int_diffTime_week, int_diffTime_day, int_diffTime_month));
            } else {
                txt_week_month.setText("");
            }


            //出産予定日（出産予定日：）
            txt_expected_date.setText(String.format("出産予定日：%4d年%2d月%2d日", int_birth_expected_year, int_birth_expected_month, int_birth_expected_day));
        } else {
            //40週まで
            count_message.setText("");
            //37週まで（37週0日まであとn日）
            txt_week_count37.setText("");
            //妊娠何週目　妊娠何ヶ月目（19週と４日目／妊娠４ヶ月目）
            txt_week_month.setText("");
            //出産予定日（出産予定日：）
            txt_expected_date.setText("");

        }


    }


    //バージョンチェック
    public void version_check() {
        //アプリバージョン比較
        try {
            SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(MenuActivity.this);

            // SharedPreferences よりデータを取得
            String version = sp.getString(SharedPreferencesHelper.WEB_APP_VERSION, "");

            int versionCode = 0;
            PackageManager packageManager = getApplicationContext().getPackageManager();
            packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionCode = packageInfo.versionCode;
            //String str_versionCode = String.valueOf(versionCode);

            //比較
            if (!version.equals("")) {
                int int_version = Integer.parseInt(version);

                if (int_version > versionCode) {

                    alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Uri uri = Uri.parse(getString(R.string.review_url));
                                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(i);
                                }
                            });
                    alertBuilder.setMessage(getString(R.string.version_string));
                    alertBuilder.setCancelable(false);
                    alertBuilder.show();
                }

            }

        } catch (NameNotFoundException e) {
        }
    }
}
