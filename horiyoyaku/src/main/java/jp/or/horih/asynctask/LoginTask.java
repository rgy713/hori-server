package jp.or.horih.asynctask;

import java.util.ArrayList;

import jp.or.horih.MenuActivity;
import jp.or.horih.R;
import jp.or.horih.common.CommonHttpAccessor;
import jp.or.horih.common.SharedPreferencesHelper;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

public class LoginTask extends AsyncTaskLoader<Bundle> {
    public static enum ResultType {
        SUCCESS, NETWORK_ERROR, REGIST_ERROR_ON_WEB, REGIST_ERROR_ON_APP, REGIST_ERROR_ON_DB
    }

    private Context context;
    private Bundle argments;

    public LoginTask(Context context, Bundle argments) {
        super(context);
        this.context = context;
        this.argments = argments;
    }

    @Override
    public Bundle loadInBackground() {
        Bundle result = new Bundle();

        //バージョン番号取得
        this.downloadVersion();

        //アカウントチェック更新
        result = this.download1();

        //結果判定
        LoginTask.ResultType resultType =
                LoginTask.ResultType.valueOf(result.getString("result"));

        LoginTask.ResultType.valueOf(result.getString("result"));
        if (resultType == LoginTask.ResultType.SUCCESS) {
            //メッセージ件数取得
            this.download2();

            //トークン更新
            result = this.download();
        }

        return result;
    }

    @Override
    public void onCanceled(Bundle data) {
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    //バージョン番号取得
    private Bundle downloadVersion() {
        Bundle result = new Bundle();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            result.putString("result", ResultType.NETWORK_ERROR.name());
            return result;
        }

        CommonHttpAccessor cha = new CommonHttpAccessor(context.getString(R.string.scheme)
                , context.getString(R.string.server_host)
                , context.getString(R.string.path_version));

        Bundle params = new Bundle();

        String response = cha.getJsonResponseString(params);

        if (TextUtils.isEmpty(response)) {
            result.putString("result", ResultType.REGIST_ERROR_ON_WEB.name());
            return result;
        }

        try {
            JSONObject root = new JSONObject(response);

            String version = root.getString("version");

            if (!version.equals("")) {

                SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(context);
                Editor editor = sp.edit();
                editor.putString(SharedPreferencesHelper.WEB_APP_VERSION, version);
                // データの保存
                editor.commit();

            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.putString("cause", e.getMessage());
            result.putString("result", ResultType.REGIST_ERROR_ON_DB.name());
            return result;
        }

    }

    //トークン更新
    private Bundle download() {
        Bundle result = new Bundle();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            result.putString("result", ResultType.NETWORK_ERROR.name());
            return result;
        }

        CommonHttpAccessor cha = new CommonHttpAccessor(context.getString(R.string.scheme)
                , context.getString(R.string.server_host)
                , context.getString(R.string.path_upd_token));

        Bundle params = new Bundle();
        if (argments.containsKey("patient_id")) {
            params.putString("id", argments.getString("patient_id"));
        }
        if (argments.containsKey("token")) {
            params.putString("token", argments.getString("token"));
        }
        if (argments.containsKey("kind")) {
            params.putString("kind", argments.getString("kind"));
        }

        if (argments.containsKey("s_year")) {
            params.putString("s_year", argments.getString("s_year"));
        }
        if (argments.containsKey("s_month")) {
            params.putString("s_month", argments.getString("s_month"));
        }
        if (argments.containsKey("s_day")) {
            params.putString("s_day", argments.getString("s_day"));
        }

        String response = cha.getJsonResponseString(params);

        if (TextUtils.isEmpty(response)) {
            result.putString("result", ResultType.REGIST_ERROR_ON_WEB.name());
            return result;
        }

        try {
            JSONObject root = new JSONObject(response);

            JSONObject commonResult = root.getJSONObject("CommonResult");
            ArrayList<Bundle> list = new ArrayList<Bundle>();

            if (commonResult.getInt("get_flg") == 0) {
                result.putString("result", ResultType.SUCCESS.name());

                JSONObject apis;
                if (!root.has("Account")) {
                    String type = ResultType.REGIST_ERROR_ON_APP.name();
                    result.putString("result", type);
                    return result;
                }

                apis = root.getJSONObject("Account");

                JSONObject api = apis.getJSONObject("user_info");

                Bundle apiBundle = new Bundle();

                apiBundle.putString("user_id", api.getString("user_id"));
                apiBundle.putString("patient_id", api.getString("patient_id"));



/*
                for(int i = 0; i < apis.length(); i++){
                    JSONObject api = apis.getJSONObject(i);

                    apiBundle = new Bundle();

                    if(api.has("user_info")){
                    	JSONArray utiwakes = api.getJSONArray("user_info");
                    	ArrayList<Bundle> utiwakeList = new ArrayList<Bundle>();
                    	
                        for(int m = 0; m < utiwakes.length(); m++){
                            JSONObject utiwake = utiwakes.getJSONObject(m);

                            Bundle utiwakeBundle = new Bundle();

                            utiwakeBundle.putString("user_id", utiwake.getString("user_id"));
                            utiwakeBundle.putString("patient_id", utiwake.getString("patient_id"));
                            utiwakeBundle.putString("kari_patient_id", utiwake.getString("kari_patient_id"));
                            
                            utiwakeList.add(utiwakeBundle);
                        	
                        }

                        apiBundle.putParcelableArrayList("user_info", utiwakeList);
                    }
                    
                    
                }
*/
                list.add(apiBundle);

                if (list.size() > 0) {
                    result.putParcelableArrayList("apiList", list);
                }

            } else {
                result.putString("result", ResultType.REGIST_ERROR_ON_APP.name());
                result.putString("cause", commonResult.getString("message"));

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.putString("cause", e.getMessage());
            result.putString("result", ResultType.REGIST_ERROR_ON_DB.name());
            return result;
        }
    }


    //アカウントチェック
    private Bundle download1() {
        Bundle result = new Bundle();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            result.putString("result", ResultType.NETWORK_ERROR.name());
            return result;
        }

        CommonHttpAccessor cha = new CommonHttpAccessor(context.getString(R.string.scheme)
                , context.getString(R.string.server_host)
                , context.getString(R.string.path_account_chk));

        Bundle params = new Bundle();
        if (argments.containsKey("patient_id")) {
            params.putString("patient_id", argments.getString("patient_id"));
        }
        if (argments.containsKey("birth_year")) {
            params.putString("b_year", argments.getString("birth_year"));
        }
        if (argments.containsKey("birth_month")) {
            params.putString("b_month", argments.getString("birth_month"));
        }
        if (argments.containsKey("birth_day")) {
            params.putString("b_day", argments.getString("birth_day"));
        }

        String response = cha.getJsonResponseString(params);

        if (TextUtils.isEmpty(response)) {
            result.putString("result", ResultType.REGIST_ERROR_ON_WEB.name());
            return result;
        }

        try {
            JSONObject root = new JSONObject(response);

            JSONObject commonResult = root.getJSONObject("CommonResult");
            ArrayList<Bundle> list = new ArrayList<Bundle>();

            if (commonResult.getInt("get_flg") == 0) {
                result.putString("result", ResultType.SUCCESS.name());

                JSONObject apis;
                if (!root.has("Account")) {
                    String type = ResultType.REGIST_ERROR_ON_APP.name();
                    result.putString("result", type);
                    return result;
                }

                apis = root.getJSONObject("Account");

                JSONObject api = apis.getJSONObject("user_info");

                Bundle apiBundle = new Bundle();

                String user_id = api.getString("user_id");
                String patient_id = api.getString("patient_id");


                if (!user_id.equals("") && !patient_id.equals("")) {

                    SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(context);
                    Editor editor = sp.edit();
                    editor.putString(SharedPreferencesHelper.USER_ID, user_id);
                    editor.putString(SharedPreferencesHelper.REG_PATIENT_ID, patient_id);

                    //とりなおしたのでいれなおす
                    argments.putString("user_id", user_id);
                    argments.putString("id", patient_id);


                    // データの保存
                    editor.commit();

                }

                if (list.size() > 0) {
                    result.putParcelableArrayList("apiList", list);
                }

            } else {
                result.putString("result", ResultType.REGIST_ERROR_ON_APP.name());
                result.putString("cause", commonResult.getString("message"));

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.putString("cause", e.getMessage());
            result.putString("result", ResultType.REGIST_ERROR_ON_DB.name());
            return result;
        }
    }


    //未読件数取得
    private Bundle download2() {
        Bundle result = new Bundle();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            result.putString("result", ResultType.NETWORK_ERROR.name());
            return result;
        }

        CommonHttpAccessor cha = new CommonHttpAccessor(context.getString(R.string.scheme)
                , context.getString(R.string.server_host)
                , context.getString(R.string.path_notice_news_mi));

        Bundle params = new Bundle();
        if (argments.containsKey("patient_id")) {
            params.putString("user_id", argments.getString("patient_id"));
        }

        String response = cha.getJsonResponseString(params);

        if (TextUtils.isEmpty(response)) {
            result.putString("result", ResultType.REGIST_ERROR_ON_WEB.name());
            return result;
        }

        try {
            JSONObject root = new JSONObject(response);

            JSONObject commonResult = root.getJSONObject("CommonResult");
            ArrayList<Bundle> list = new ArrayList<Bundle>();

            if (commonResult.getInt("get_flg") == 0) {
                result.putString("result", ResultType.SUCCESS.name());


                if (!root.has("Num")) {
                    String type = ResultType.REGIST_ERROR_ON_APP.name();
                    result.putString("result", type);
                    return result;
                }

                String num = root.getString("Num");

                if (num.equals("")) {
                    num = "0";
                }

                SharedPreferences sp = SharedPreferencesHelper.getSharedPreferencesInstance(context);
                Editor editor = sp.edit();
                editor.putString(SharedPreferencesHelper.MI_NOTICE_COUNT, num);

                // データの保存
                editor.commit();

                if (list.size() > 0) {
                    result.putParcelableArrayList("apiList", list);
                }

            } else {
                result.putString("result", ResultType.REGIST_ERROR_ON_APP.name());
                result.putString("cause", commonResult.getString("message"));

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.putString("cause", e.getMessage());
            result.putString("result", ResultType.REGIST_ERROR_ON_DB.name());
            return result;
        }
    }


}
