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

public class NoticeDetailTask extends AsyncTaskLoader<Bundle> {
    public static enum ResultType {
        SUCCESS, NETWORK_ERROR, REGIST_ERROR_ON_WEB, REGIST_ERROR_ON_APP, REGIST_ERROR_ON_DB
    }

    private Context context;
    private Bundle argments;

    public NoticeDetailTask(Context context, Bundle argments) {
        super(context);
        this.context = context;
        this.argments = argments;
    }

    @Override
    public Bundle loadInBackground() {
        Bundle result = new Bundle();

        result = this.download();

        return result;
    }

    @Override
    public void onCanceled(Bundle data) {
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

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
                , context.getString(R.string.path_notice_detail));

        Bundle params = new Bundle();
        if (argments.containsKey("notice_id")) {
            params.putString("id", argments.getString("notice_id"));
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
                if (!root.has("Notice")) {
                    String type = ResultType.REGIST_ERROR_ON_APP.name();
                    result.putString("result", type);
                    return result;
                }

                apis = root.getJSONObject("Notice");


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
