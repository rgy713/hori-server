package jp.or.horih;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import jp.or.horih.R;
import jp.or.horih.adapter.TimerHistoryAdapter;
import jp.or.horih.common.CommonUtility;
import jp.or.horih.common.SharedPreferencesHelper;
import jp.or.horih.item.TimerHistoryItem;
import jp.or.horih.view.imagebutton.PressableImageButton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class LaborPainsTimerActivity extends FragmentActivity {

    private final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private SharedPreferences sp;
    public Context context;
    TextView txt_timer;
    PressableImageButton btn_timer;
    ListView list;

    boolean timerStartFlg = false;

    private Timer mainTimer;                    //タイマー用
    private MainTimerTask mainTimerTask;        //タイマタスククラス
    private int count = 0;
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    TimerHistoryAdapter adapter;
    ArrayList<String> timer_history_start_date_list;
    ArrayList<String> timer_history_time_list;

    Date nowDate = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_labor_pains_timer);

        txt_timer = (TextView) findViewById(R.id.txt_timer);
        btn_timer = (PressableImageButton) findViewById(R.id.btn_timer);
        list = (ListView) findViewById(R.id.recordListView);

        timer_history_start_date_list = new ArrayList<String>();
        timer_history_time_list = new ArrayList<String>();

        //リスト表示
        listDisplay();


    }

    //--------------------
    //クリック処理
    //--------------------
    //戻るボタン
    public void onClickBack(View v) {
        finish();
    }


    //リセットボタン
    public void onClickReset(View v) {
        AlertDialog.Builder builder = null;
        AlertDialog mMapDialog = null;

        builder = new AlertDialog.Builder(LaborPainsTimerActivity.this);
        builder.setMessage("タイマー記録をリセットしても\nよろしいですか？");
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mDialog, int which) {
                mDialog.dismiss();

                //リセット
                SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(LaborPainsTimerActivity.this);
                Editor editor = sp.edit();

                //空白にする
                editor.putString(SharedPreferencesHelper.TIMER_HISTORY_START_DATE, "");
                editor.putString(SharedPreferencesHelper.TIMER_HISTORY_TIME, "");

                // データの保存
                editor.commit();

                //リスト表示
                listDisplay();


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


    //タイマー開始終了ボタン
    public void onClickTimer(View v) {
        if (timerStartFlg) {
            //停止
            timerStop();
        } else {

            //開始
            timerStart();
        }
    }

    //電話ボタン
    public void onClickTel(View v) {

        AlertDialog.Builder builder = null;
        AlertDialog mMapDialog = null;

        builder = new AlertDialog.Builder(LaborPainsTimerActivity.this);
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
    //フォントを設定
    public void setFont() {

        String head_font = getString(R.string.head_font);
        String count_font = getString(R.string.count_font);

        txt_timer.setTypeface(Typeface.createFromAsset(getAssets(), count_font));


    }


    //タイマー開始
    public void timerStart() {
        //開始
        btn_timer.setImageResource(R.drawable.btn_stop);
        timerStartFlg = true;


        //タイマーインスタンス生成
        this.mainTimer = new Timer();
        //タスククラスインスタンス生成
        this.mainTimerTask = new MainTimerTask();
        //タイマースケジュール設定＆開始
        //TimerTask=スケジュールされるタスク
        //delay=初めのタスクが実行されるまでの時間 です。(単位ミリ秒)
        //period=タスクが実行される周期です。(単位ミリ秒)
        this.mainTimer.schedule(mainTimerTask, 1000, 1000);

        //現在日付け取得
        nowDate = new Date();

        //テキスト
        txt_timer.setText("00:00");
        count = 0;


        //10分をきった場合
        if (timer_history_start_date_list.size() > 0) {
            try {
                //リストの最後を取得
                String start_date_bk = timer_history_start_date_list.get(timer_history_start_date_list.size() - 1);

                Date d_start_bk = CommonUtility.getStringFormatDate(start_date_bk, DATE_FORMAT);
                Date d_start = new Date();

                //差を秒で取得する
                long l_interval = (d_start.getTime() - d_start_bk.getTime()) / 1000;
                int i_interval = (int) l_interval;
                int int_timeBk = 0;

                i_interval = i_interval - int_timeBk;

                int minutes = i_interval / 60;

                //10分以内の場合
                if (minutes < 10) {

                    AlertDialog.Builder builder = null;
                    AlertDialog mMapDialog = null;

                    builder = new AlertDialog.Builder(LaborPainsTimerActivity.this);
                    builder.setMessage("陣痛の間隔が10分を切りました。\n病院へ連絡してください。");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface mDialog, int which) {
                            mDialog.dismiss();

                        }
                    });

                    mMapDialog = builder.create();
                    mMapDialog.show();

                }

            } catch (Exception e) {
            }

        }

    }

    //タイマー停止
    public void timerStop() {
        btn_timer.setImageResource(R.drawable.btn_start);
        timerStartFlg = false;

        if (this.mainTimer != null) {
            this.mainTimer.cancel();
            this.mainTimer = null;
        }


        String strNow = CommonUtility.getDateChangeFormatString(nowDate, DATE_FORMAT);

        //追加
        timer_history_start_date_list.add(strNow);
        timer_history_time_list.add(txt_timer.getText().toString());

        //リスト保存
        timerSave();

        //リスト表示
        listDisplay();

        //テキスト
        txt_timer.setText("00:00");
        count = 0;
    }


    //リスト表示
    public void listDisplay() {
        //リスト取得
        getTimerList();

        ArrayList<Object> items = new ArrayList<Object>();
        TimerHistoryItem item = new TimerHistoryItem();
        //リストクリア
        list.setAdapter(null);

        String startBk = "";
        String timeBk = "";

        if (timer_history_time_list != null) {
            for (int i = 0; i < timer_history_time_list.size(); i++) {
                String start = timer_history_start_date_list.get(i);
                String start_date = "";
                String start_time = "";
                String time = timer_history_time_list.get(i);
                String interval = "";

                String[] daList = start.split(" ");
                if (daList.length >= 2) {
                    start_date = daList[0];
                    start_time = daList[1];
                }


                if (startBk.equals("")) {
                    interval = "--:--";
                } else {
                    try {
                        Date d_start_bk = CommonUtility.getStringFormatDate(startBk, DATE_FORMAT);
                        Date d_start = CommonUtility.getStringFormatDate(start, DATE_FORMAT);

                        //差を秒で取得する
                        long l_interval = (d_start.getTime() - d_start_bk.getTime()) / 1000;
                        int i_interval = (int) l_interval;
                        int int_timeBk = 0;

                        //持続時間を秒で引く
                        String[] datimeList = timeBk.split(":");
                        if (datimeList.length >= 2) {
                            int time_minute = Integer.parseInt(datimeList[0]);
                            int time_second = Integer.parseInt(datimeList[1]);
                            int_timeBk = time_minute * 60 + time_second;
                        }
                        i_interval = i_interval - int_timeBk;


                        int second = i_interval % 60;
                        int minutes = i_interval / 60;
                        interval = String.format("%02d:%02d", minutes, second);
                    } catch (Exception e) {
                    }

                }

                item = new TimerHistoryItem();
                item.setStart_date(start_date);
                item.setStart_time(start_time);
                item.setTime(time);
                item.setInterval(interval);

                items.add(item);

                //退避
                startBk = start;
                timeBk = time;
            }

            if (items.size() > 0) {
                adapter = new TimerHistoryAdapter(LaborPainsTimerActivity.this, items);
                list.setAdapter(adapter);
            }

        }

    }

    //タイマー保存
    public void timerSave() {
        //カンマ区切りで保存する

        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(LaborPainsTimerActivity.this);
        Editor editor = sp.edit();

        String sep = "";
        //スタート時間
        sep = "";
        String start_date = "";
        for (int i = 0; i < timer_history_start_date_list.size(); i++) {
            start_date = start_date + sep + timer_history_start_date_list.get(i);
            sep = ",";
        }
        //持続時間
        sep = "";
        String time = "";
        for (int i = 0; i < timer_history_time_list.size(); i++) {
            time = time + sep + timer_history_time_list.get(i);
            sep = ",";
        }

        //カンマ区切りで保存する
        editor.putString(SharedPreferencesHelper.TIMER_HISTORY_START_DATE, start_date);
        editor.putString(SharedPreferencesHelper.TIMER_HISTORY_TIME, time);

        // データの保存
        editor.commit();

    }


    //保存しているタイマーの履歴を取得
    public void getTimerList() {
        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(LaborPainsTimerActivity.this);
        String start_date = sp.getString(SharedPreferencesHelper.TIMER_HISTORY_START_DATE, "");
        String time = sp.getString(SharedPreferencesHelper.TIMER_HISTORY_TIME, "");

        timer_history_start_date_list = new ArrayList<String>();
        timer_history_time_list = new ArrayList<String>();

        //分解
        String[] start_dateList = start_date.split(",");
        for (int i = 0; i < start_dateList.length; i++) {
            if (start_dateList[i].length() > 0) {
                timer_history_start_date_list.add(start_dateList[i]);
            }
        }

        String[] timeList = time.split(",");
        for (int i = 0; i < timeList.length; i++) {
            if (timeList[i].length() > 0) {
                timer_history_time_list.add(timeList[i]);
            }
        }
    }


    /**
     * タイマータスク派生クラス
     * run()に定周期で処理したい内容を記述
     */
    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post(new Runnable() {
                public void run() {

                    //実行間隔分を加算処理
                    count += 1;

                    //画面にカウントを表示
                    int second = count % 60;
                    int minutes = count / 60;
                    String strDisp = String.format("%02d:%02d", minutes, second);

                    //テキスト
                    txt_timer.setText(strDisp);

                    //99を超える場合は停止する
                    if (minutes > 99) {
                        timerStop();
                    }

                }
            });
        }
    }
}
