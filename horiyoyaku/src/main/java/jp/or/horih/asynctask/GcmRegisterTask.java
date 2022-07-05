package jp.or.horih.asynctask;

import java.io.IOException;

import jp.or.horih.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmRegisterTask extends AsyncTaskLoader<Bundle> {

    private Context context;

    public GcmRegisterTask(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle loadInBackground() {
        Bundle result = new Bundle();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        try {
            String regId = gcm.register(context.getString(R.string.gcm_sender_id));
            result.putString("register_id", regId);
            Log.d(this.getClass().getCanonicalName(), "registration id : " + regId);

        } catch (IOException e) {
            e.printStackTrace();
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
}
