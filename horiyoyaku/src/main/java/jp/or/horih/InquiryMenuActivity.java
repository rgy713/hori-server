package jp.or.horih;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import jp.or.horih.R;

public class InquiryMenuActivity extends FragmentActivity implements LoaderCallbacks<Bundle> {

    private ProgressDialog dialog;
    private InputMethodManager imm;
    private RelativeLayout main;

    EditText inq_txt_name;
    EditText inq_txt_tel;
    EditText inq_txt_address;
    EditText inq_txt_num;
    EditText inq_txt_mail;
    EditText inq_txt_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_menu);

        inq_txt_name = (EditText) findViewById(R.id.inq_txt_name);
        inq_txt_tel = (EditText) findViewById(R.id.inq_txt_tel);
        inq_txt_address = (EditText) findViewById(R.id.inq_txt_address);
        inq_txt_num = (EditText) findViewById(R.id.inq_txt_num);
        inq_txt_mail = (EditText) findViewById(R.id.inq_txt_mail);
        inq_txt_content = (EditText) findViewById(R.id.inq_txt_content);

        main = (RelativeLayout) findViewById(R.id.main);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    public void onClickMail(View v) {
        Intent intent = new Intent(this, InquiryActivity.class);
        startActivity(intent);
    }

    //"こちら" を押した時
    public void onClickTel(View v) {
        AlertDialog.Builder builder = null;
        AlertDialog mMapDialog = null;

        builder = new AlertDialog.Builder(InquiryMenuActivity.this);
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle result) {

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

    public void onClickSend(View v) {
        String mailSubject = "お問合せ";
        String mailMessage = "";

        StringBuffer buf = new StringBuffer();
        buf.append("名前：");
        buf.append(inq_txt_name.getText());
        buf.append("\n");

        buf.append("電話番号：");
        buf.append(inq_txt_tel.getText());
        buf.append("\n");

        buf.append("メールアドレス：");
        buf.append(inq_txt_mail.getText());
        buf.append("\n");

        buf.append("診察券番号：");
        buf.append(inq_txt_num.getText());
        buf.append("\n");

        buf.append("住所：");
        buf.append(inq_txt_address.getText());
        buf.append("\n");

        buf.append("お問合せ：");
        buf.append(inq_txt_content.getText());
        buf.append("\n");

        mailMessage = buf.toString();

        //メーラー起動
        String mail = getString(R.string.trouble_mail);
        Uri uri = Uri.parse("mailto:" + mail);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, mailMessage);
        startActivity(intent);

    }


}
