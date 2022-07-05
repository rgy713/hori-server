package jp.or.horih;

import jp.or.horih.R;
import jp.or.horih.asynctask.GcmRegisterCallback;
import jp.or.horih.common.CommonUtility;
import jp.or.horih.common.SharedPreferencesHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class TopActivity extends FragmentActivity {

    private SharedPreferences sp;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top);

        final ImageView iv = (ImageView) findViewById(R.id.top_splash);
        iv.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(1000);
        animation.setStartOffset(400);
        iv.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.GONE);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                Intent intent = null;
                intent = new Intent(TopActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        animation.start();

        sp = SharedPreferencesHelper.getSharedPreferencesInstance(this);
        String regId = this.getRegistrationId(this);

        if (!checkPlayServices()) {
            Toast.makeText(this, R.string.gcm_google_play_service_is_not_available, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_VIEW
                    , Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=ja"));
            startActivity(intent);
            return;
        }

        getSupportLoaderManager().initLoader(GcmRegisterCallback.LOADER_ID_GCM_REGISTER, null, new GcmRegisterCallback(this));


    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = sp.getString(SharedPreferencesHelper.REGISTRATION_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(this.getClass().getCanonicalName(), "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = sp.getInt(SharedPreferencesHelper.APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = CommonUtility.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(this.getClass().getCanonicalName(), "App version changed.");
            return "";
        }
        return registrationId;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(this.getClass().getCanonicalName(), "このデバイスはサポートされていません。");
            }
            return false;
        }
        return true;
    }

}
