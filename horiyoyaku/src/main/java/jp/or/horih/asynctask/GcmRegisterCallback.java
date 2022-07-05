package jp.or.horih.asynctask;

import java.util.ArrayList;

import jp.or.horih.common.CommonUtility;
import jp.or.horih.common.SharedPreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class GcmRegisterCallback implements LoaderCallbacks<Bundle> {

    public final static int LOADER_ID_GCM_REGISTER = 1;
    public final static int LOADER_ID_LOG_IN = 2;

    private SharedPreferences sp;
    private Context context;

    public GcmRegisterCallback(Context context) {
        this.context = context;
        this.sp = SharedPreferencesHelper.getSharedPreferencesInstance(context);
    }

    @Override
    public Loader<Bundle> onCreateLoader(int id, Bundle argments) {
        if (id == LOADER_ID_GCM_REGISTER) {
            return new GcmRegisterTask(context);
        } else {
            return new LoginTask(context, argments);
        }
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle result) {


        if (loader.getId() == LOADER_ID_GCM_REGISTER) {
            String regId = result.getString("register_id");

            if (TextUtils.isEmpty(regId)
                    || TextUtils.equals(regId, "null")) {
                Log.e(this.getClass().getCanonicalName(), "レジストレーションIDの発行に失敗");
            } else {
                Log.d(this.getClass().getCanonicalName(), regId);

                Editor editor = sp.edit();
                editor.putString(SharedPreferencesHelper.REGISTRATION_ID, regId);
                editor.putInt(SharedPreferencesHelper.APP_VERSION, CommonUtility.getAppVersion(context));
                editor.commit();

                if (!TextUtils.isEmpty(sp.getString(SharedPreferencesHelper.REG_PATIENT_ID, ""))) {
                    Bundle argments = new Bundle();

                    argments.putString("patient_id", sp.getString(SharedPreferencesHelper.REG_PATIENT_ID, ""));
                    argments.putString("user_id", sp.getString(SharedPreferencesHelper.USER_ID, ""));
                    argments.putString("token", regId);
                    argments.putString("kind", "2");

                    argments.putString("s_year", sp.getString(SharedPreferencesHelper.REG_YEAR, ""));
                    argments.putString("s_month", sp.getString(SharedPreferencesHelper.REG_MONTH, ""));
                    argments.putString("s_day", sp.getString(SharedPreferencesHelper.REG_DAY, ""));

                    ((FragmentActivity) context).getSupportLoaderManager().initLoader(LOADER_ID_LOG_IN, argments, this);
                }
            }
        } else {
            //結果判定
            LoginTask.ResultType resultType =
                    LoginTask.ResultType.valueOf(result.getString("result"));

            if (resultType == LoginTask.ResultType.SUCCESS) {

                ArrayList<Bundle> listData = result.getParcelableArrayList("apiList");

                if (listData != null) {
                    SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(context);
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
                //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }


        ((FragmentActivity) context).getSupportLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {
    }

}
