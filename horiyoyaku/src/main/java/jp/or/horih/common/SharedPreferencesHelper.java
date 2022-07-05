package jp.or.horih.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesHelper {
    public static final String REGISTRATION_ID = "registration_id";
    public static final String APP_VERSION = "app_version";

    public static final String WEB_APP_VERSION = "web_app_version";

    //ユーザーID
    public static final String USER_ID = "user_id";
    //患者番号
    public static final String REG_PATIENT_ID = "reg_patient_id";
    //誕生日
    public static final String REG_YEAR = "reg_year";
    public static final String REG_MONTH = "reg_month";
    public static final String REG_DAY = "reg_day";

    //出産予定日
    public static final String REG_PREGNANCY = "reg_pregnancy";
    public static final String REG_BIRTH_EXPECTED_YEAR = "reg_birth_expected_year";
    public static final String REG_BIRTH_EXPECTED_MONTH = "reg_birth_expected_month";
    public static final String REG_BIRTH_EXPECTED_DAY = "reg_birth_expected_day";

    //陣痛タイマー履歴
    public static final String TIMER_HISTORY_START_DATE = "timer_history_start_date";
    public static final String TIMER_HISTORY_TIME = "timer_history_time";

    //登録日
    public static final String REG_DATE_YEAR = "reg_date_year";
    public static final String REG_DATE_MONTH = "reg_date_month";
    public static final String REG_DATE_DAY = "reg_date_day";

    //未読件数
    public static final String MI_NOTICE_COUNT = "mi_notice_count";

    private SharedPreferencesHelper() {
    }

    public synchronized static SharedPreferences getSharedPreferencesInstance(Context context) {
        return context.getSharedPreferences("hori", Context.MODE_PRIVATE);
    }


    //クリア
    public static void spClear(Context context) {
        //クリア
        SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(context);
        Editor editor = sp.edit();

        //editor.putString(SharedPreferencesHelper.LOGIN_MODE, ""  );

        // データの保存
        editor.commit();
    }

}
