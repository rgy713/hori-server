package jp.or.horih;

//import jp.co.nippontect.mpconcier.asynctask.ForgetPasswordTask;
//import jp.co.nippontect.mpconcier.asynctask.LoginTask;
//import jp.co.nippontect.mpconcier.asynctask.RegistAccountSecondTask;

import info.hoang8f.android.segmented.SegmentedGroup;

import java.util.ArrayList;
import java.util.Calendar;

import jp.or.horih.R;
import jp.or.horih.asynctask.LoginTask;
import jp.or.horih.common.SharedPreferencesHelper;
import jp.or.horih.view.button.PressableButton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationInputActivity extends FragmentActivity implements LoaderCallbacks<Bundle> {

    private ProgressDialog dialog;
    private InputMethodManager imm;
    private RelativeLayout main;
    private Calendar localCalendar;
    private Calendar calendar;

    private Calendar localCalendar_expected;
    private Calendar calendar_expected;


    TextView txt_header;

    EditText inq_txt_num;
    EditText inq_txt_birth_year;
    EditText inq_txt_birth_month;
    EditText inq_txt_birth_day;

    EditText inq_txt_birth_expected_year;
    EditText inq_txt_birth_expected_month;
    EditText inq_txt_birth_expected_day;

    RelativeLayout lay_birth_expected;
    String str_pregnancy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_input);

        main = (RelativeLayout) findViewById(R.id.main);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        txt_header = (TextView) findViewById(R.id.txt_header);

        inq_txt_num = (EditText) findViewById(R.id.inq_txt_num);
        inq_txt_birth_year = (EditText) findViewById(R.id.inq_txt_birth_year);
        inq_txt_birth_month = (EditText) findViewById(R.id.inq_txt_birth_month);
        inq_txt_birth_day = (EditText) findViewById(R.id.inq_txt_birth_day);

        //変更不可
        inq_txt_birth_year.setFocusable(false);
        inq_txt_birth_month.setFocusable(false);
        inq_txt_birth_day.setFocusable(false);

        lay_birth_expected = (RelativeLayout) findViewById(R.id.lay_birth_expected);
        inq_txt_birth_expected_year = (EditText) findViewById(R.id.inq_txt_birth_expected_year);
        inq_txt_birth_expected_month = (EditText) findViewById(R.id.inq_txt_birth_expected_month);
        inq_txt_birth_expected_day = (EditText) findViewById(R.id.inq_txt_birth_expected_day);

        //変更不可
        inq_txt_birth_expected_year.setFocusable(false);
        inq_txt_birth_expected_month.setFocusable(false);
        inq_txt_birth_expected_day.setFocusable(false);


        SegmentedGroup radiogroup = (SegmentedGroup) findViewById(R.id.segmented2);

        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(RegistrationInputActivity.this);
        String reg_num = sp.getString(SharedPreferencesHelper.REG_PATIENT_ID, "");
        String reg_year = sp.getString(SharedPreferencesHelper.REG_YEAR, "");
        String reg_month = sp.getString(SharedPreferencesHelper.REG_MONTH, "");
        String reg_day = sp.getString(SharedPreferencesHelper.REG_DAY, "");

        String reg_pregnancy = sp.getString(SharedPreferencesHelper.REG_PREGNANCY, "");
        String reg_birth_expected_year = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_YEAR, "");
        String reg_birth_expected_month = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_MONTH, "");
        String reg_birth_expected_day = sp.getString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_DAY, "");

        inq_txt_num.setText(reg_num);
        inq_txt_birth_year.setText(reg_year);
        inq_txt_birth_month.setText(reg_month);
        inq_txt_birth_day.setText(reg_day);

        txt_header.setText("ご利用いただくにあたって\n下記の情報をご入力ください。");

        if (reg_pregnancy.equals("1")) {
            radiogroup.check(R.id.radio_yes);
            lay_birth_expected.setVisibility(View.VISIBLE);
            str_pregnancy = "1";
        } else if (reg_pregnancy.equals("0")) {
            radiogroup.check(R.id.radio_no);
            lay_birth_expected.setVisibility(View.GONE);
            str_pregnancy = "0";
        } else {
            radiogroup.check(R.id.radio_yes);
            lay_birth_expected.setVisibility(View.VISIBLE);
            str_pregnancy = "1";

            txt_header.setText("初めてご利用いただくにあたって\n下記の情報をご入力ください。");
            PressableButton btn_ng = (PressableButton) findViewById(R.id.rgi_btn_ng);
            btn_ng.setVisibility(View.GONE);

        }

        inq_txt_birth_expected_year.setText(reg_birth_expected_year);
        inq_txt_birth_expected_month.setText(reg_birth_expected_month);
        inq_txt_birth_expected_day.setText(reg_birth_expected_day);


        calendar = Calendar.getInstance();
        if (!reg_month.equals("")) {
            calendar.set(Calendar.YEAR, Integer.parseInt(reg_year));
            calendar.set(Calendar.MONTH, Integer.parseInt(reg_month) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reg_day));
        }

        calendar_expected = Calendar.getInstance();
        if (!reg_birth_expected_month.equals("")) {
            calendar_expected.set(Calendar.YEAR, Integer.parseInt(reg_birth_expected_year));
            calendar_expected.set(Calendar.MONTH, Integer.parseInt(reg_birth_expected_month) - 1);
            calendar_expected.set(Calendar.DAY_OF_MONTH, Integer.parseInt(reg_birth_expected_day));
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        imm.hideSoftInputFromWindow(main.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        main.requestFocus();

        return super.onTouchEvent(event);
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

            //OK処理
            OkProcess();


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
                    msg = "入力された患者番号は存在しません。";
                    break;
                case REGIST_ERROR_ON_DB:
                    msg = this.getString(R.string.regist_error_db_string);
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
    //ボタンクリック処理
    //--------------------
    //戻る
    public void onClickBack(View v) {
        finish();
    }

    //ラジオはい
    public void onClickYes(View v) {
        lay_birth_expected.setVisibility(View.VISIBLE);
        str_pregnancy = "1";
    }

    //ラジオいいえ
    public void onClickNo(View v) {
        lay_birth_expected.setVisibility(View.GONE);
        str_pregnancy = "0";
    }

    //クリア
    public void onClickClear(View v) {

        inq_txt_birth_expected_year.setText("");
        inq_txt_birth_expected_month.setText("");
        inq_txt_birth_expected_day.setText("");

    }


    //決定
    public void onClickOk(View v) {
        String errMessage = "";
        String reg_num = inq_txt_num.getText().toString();
        String reg_year = inq_txt_birth_year.getText().toString();
        String reg_month = inq_txt_birth_month.getText().toString();
        String reg_day = inq_txt_birth_day.getText().toString();

        if (reg_num.equals("")) {
            //OK処理
            OkProcess();

        } else {

            if (reg_year.equals("")
                    || reg_month.equals("")
                    || reg_day.equals("")
                    ) {
                errMessage = "診察券番号を入力されている方は必ず生年月日を入力してください。";
                Toast.makeText(RegistrationInputActivity.this, errMessage, Toast.LENGTH_SHORT).show();

            } else {
                dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setMessage("処理中です。しばらくお待ちください。");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(false);

                //サーバチェック
                getData();
            }
        }

    }

    //後で入力
    public void onClickNg(View v) {

        finish();
    }


    //生年月日
    public void onClickBirth(View v) {
        localCalendar = calendar;

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationInputActivity.this);
        DatePicker datePicker = new DatePicker(RegistrationInputActivity.this);
        datePicker.init(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)
                , new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        localCalendar.set(year, monthOfYear, dayOfMonth);
                    }
                });

        builder.setView(datePicker);
        builder.setTitle("生年月日");
        builder.setPositiveButton("決定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendar.set(localCalendar.get(Calendar.YEAR)
                        , localCalendar.get(Calendar.MONTH)
                        , localCalendar.get(Calendar.DAY_OF_MONTH));


                //txt_birth.setText(DateFormat.format("yyyy/MM/dd", calendar.getTime()));
                inq_txt_birth_year.setText(DateFormat.format("yyyy", calendar.getTime()));
                inq_txt_birth_month.setText(DateFormat.format("MM", calendar.getTime()));
                inq_txt_birth_day.setText(DateFormat.format("dd", calendar.getTime()));

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    //出産予定日
    public void onClickBirthExpected(View v) {
        localCalendar_expected = calendar_expected;

        //inq_txt_birth_expected_year.setFocusable(true);
        //inq_txt_birth_expected_year.setFocusableInTouchMode(true);
        //inq_txt_birth_expected_year.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationInputActivity.this);
        DatePicker datePicker = new DatePicker(RegistrationInputActivity.this);
        datePicker.init(calendar_expected.get(Calendar.YEAR)
                , calendar_expected.get(Calendar.MONTH)
                , calendar_expected.get(Calendar.DAY_OF_MONTH)
                , new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        localCalendar_expected.set(year, monthOfYear, dayOfMonth);
                    }
                });

        builder.setView(datePicker);
        builder.setTitle("出産予定日");
        builder.setPositiveButton("決定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendar_expected.set(localCalendar_expected.get(Calendar.YEAR)
                        , localCalendar_expected.get(Calendar.MONTH)
                        , localCalendar_expected.get(Calendar.DAY_OF_MONTH));

                //inq_txt_birth_expected_year.setFocusable(false);

                inq_txt_birth_expected_year.setText(DateFormat.format("yyyy", calendar_expected.getTime()));
                inq_txt_birth_expected_month.setText(DateFormat.format("MM", calendar_expected.getTime()));
                inq_txt_birth_expected_day.setText(DateFormat.format("dd", calendar_expected.getTime()));

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    //プライバシーポリシー
    public void onClickPrivacy(View v) {
        String url = this.getString(R.string.server_scheme)
                + this.getString(R.string.server_host)
                + this.getString(R.string.path_policy);

        //プライバシーポリシー
        Intent intent = null;
        Uri uri1 = Uri.parse(url);
        intent = new Intent(Intent.ACTION_VIEW, uri1);
        startActivity(intent);
    }


    //--------------------
    //処理
    //--------------------
    public void getData() {
        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(RegistrationInputActivity.this);
        String push_key = sp.getString(SharedPreferencesHelper.REGISTRATION_ID, "");

        String patient_id = inq_txt_num.getText().toString();
        String birth_year = inq_txt_birth_year.getText().toString();
        String birth_month = inq_txt_birth_month.getText().toString();
        String birth_day = inq_txt_birth_day.getText().toString();

        //if(!patient_id.equals("") && !push_key.equals("")){
        if (!patient_id.equals("")) {
            dialog.show();
            Bundle argments = new Bundle();


            argments.putString("id", patient_id);
            argments.putString("patient_id", patient_id);
            argments.putString("token", push_key);
            argments.putString("kind", "2");
            argments.putString("birth_year", birth_year);
            argments.putString("birth_month", birth_month);
            argments.putString("birth_day", birth_day);

            argments.putString("s_year", birth_year);
            argments.putString("s_month", birth_month);
            argments.putString("s_day", birth_day);

            getSupportLoaderManager().initLoader(0, argments, RegistrationInputActivity.this);
        }

    }

    //OK処理
    public void OkProcess() {
        boolean errCheck = false;
        String errMessage = "";

        String reg_num = inq_txt_num.getText().toString();
        String reg_year = inq_txt_birth_year.getText().toString();
        String reg_month = inq_txt_birth_month.getText().toString();
        String reg_day = inq_txt_birth_day.getText().toString();

        String reg_birth_expected_year = inq_txt_birth_expected_year.getText().toString();
        String reg_birth_expected_month = inq_txt_birth_expected_month.getText().toString();
        String reg_birth_expected_day = inq_txt_birth_expected_day.getText().toString();

        /*
        if(str_pregnancy.equals("1")){
        	if(
        			reg_birth_expected_year.equals("")
                ||reg_birth_expected_month.equals("")
                ||reg_birth_expected_day.equals("")
        		){
        		
        		//空白の場合エラー
        		errCheck=true;
        		errMessage="出産予定日を入力してください。";
        	}
        }else{
        	reg_birth_expected_year = "";
        	reg_birth_expected_month = "";
        	reg_birth_expected_day = "";
        }
        */

        if (errCheck) {
            Toast.makeText(RegistrationInputActivity.this, errMessage, Toast.LENGTH_SHORT).show();
        } else {

            Calendar add_calendar = Calendar.getInstance();
            String reg_date_year = DateFormat.format("yyyy", add_calendar.getTime()).toString();
            String reg_date_month = DateFormat.format("MM", add_calendar.getTime()).toString();
            String reg_date_day = DateFormat.format("dd", add_calendar.getTime()).toString();


            SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(RegistrationInputActivity.this);
            Editor editor = sp.edit();

            editor.putString(SharedPreferencesHelper.REG_PATIENT_ID, reg_num);
            editor.putString(SharedPreferencesHelper.REG_YEAR, reg_year);
            editor.putString(SharedPreferencesHelper.REG_MONTH, reg_month);
            editor.putString(SharedPreferencesHelper.REG_DAY, reg_day);
            editor.putString(SharedPreferencesHelper.REG_PREGNANCY, str_pregnancy);
            editor.putString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_YEAR, reg_birth_expected_year);
            editor.putString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_MONTH, reg_birth_expected_month);
            editor.putString(SharedPreferencesHelper.REG_BIRTH_EXPECTED_DAY, reg_birth_expected_day);
            editor.putString(SharedPreferencesHelper.REG_DATE_YEAR, reg_date_year);
            editor.putString(SharedPreferencesHelper.REG_DATE_MONTH, reg_date_month);
            editor.putString(SharedPreferencesHelper.REG_DATE_DAY, reg_date_day);


            // データの保存
            editor.commit();

            finish();

        }
    }


}
