package jp.or.horih;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import jp.or.horih.R;

public class ReserveMenuActivity extends FragmentActivity {

    private SharedPreferences sp;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_reserve_menu);

    }

    //--------------------
    //クリック処理
    //--------------------
    //戻るボタン
    public void onClickBack(View v) {
        finish();
    }

    //タブボタン１
    public void onClickReserve(View v) {

        Intent intent = new Intent(this, ReserveActivity.class);
        intent.putExtra("menu", "1");
        startActivity(intent);

    }

    //タブボタン２
    public void onClickCancel(View v) {

        Intent intent = new Intent(this, ReserveActivity.class);
        intent.putExtra("menu", "2");
        startActivity(intent);

    }

    //呼び出し状況ボタン
    public void onClickCall(View v) {

        Intent intent = new Intent(this, ReserveActivity.class);
        intent.putExtra("menu", "3");
        startActivity(intent);

    }


}
