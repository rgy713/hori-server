package jp.or.horih.asynctask;

import java.util.ArrayList;

import jp.or.horih.R;
import jp.or.horih.common.CommonHttpAccessor;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.text.format.DateFormat;

public class NewsNoticeListTask extends AsyncTaskLoader<Bundle> {
    public static enum ResultType {
        SUCCESS, NETWORK_ERROR, REGIST_ERROR_ON_WEB, REGIST_ERROR_ON_APP, REGIST_ERROR_ON_DB
    }

    private Context context;
    private Bundle argments;

    public NewsNoticeListTask(Context context, Bundle argments) {
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
                , context.getString(R.string.path_notice_news));

        Bundle params = new Bundle();
        if (argments.containsKey("user_id")) {
            params.putString("user_id", argments.getString("user_id"));
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

                JSONArray apis;
                if (!root.has("Notice")) {
                    String type = ResultType.REGIST_ERROR_ON_APP.name();
                    result.putString("result", type);
                    return result;
                }

                apis = root.getJSONArray("Notice");


                for (int i = 0; i < apis.length(); i++) {
                    JSONObject api_o = apis.getJSONObject(i);
                    apis = root.getJSONArray("Notice");

                    JSONObject api = api_o.getJSONObject("notice");

                    Bundle apiBundle = new Bundle();

                    apiBundle.putString("notice_id", api.getString("notice_id"));
                    apiBundle.putString("send_user", api.getString("send_user"));
                    apiBundle.putString("send_date", api.getString("send_date"));
                    apiBundle.putString("user_id", api.getString("user_id"));
                    apiBundle.putString("titile", api.getString("titile"));
                    apiBundle.putString("content", api.getString("content"));
                    apiBundle.putString("um_flg", api.getString("um_flg"));
                    apiBundle.putString("um_date", api.getString("um_date"));
                    apiBundle.putString("del_flg", api.getString("del_flg"));
                    apiBundle.putString("add_date", api.getString("add_date"));
                    apiBundle.putString("add_user", api.getString("add_user"));
                    apiBundle.putString("upd_date", api.getString("upd_date"));
                    apiBundle.putString("upd_user", api.getString("upd_user"));


                    list.add(apiBundle);
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
}
