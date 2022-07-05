package jp.or.horih.view.button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class PressableButton extends Button {

    public PressableButton(Context context) {
        super(context);
    }

    public PressableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        //ColorStateList list = getResources().getColorStateList(R.color.press_image);
        //int color = list.getColorForState(getDrawableState(), Color.TRANSPARENT);
        //setColorFilter(color);
        //半透明
//        setAlpha(60);

    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO 自動生成されたメソッド・スタブ
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //半透明
                if (Build.VERSION.SDK_INT < 11) {
                    final AlphaAnimation anim = new AlphaAnimation(1.0f, 0.6f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    startAnimation(anim);
                } else {
                    setAlpha(0.6f);
                }
                break;
            case MotionEvent.ACTION_UP:
                //透明なし
                if (Build.VERSION.SDK_INT < 11) {
                    final AlphaAnimation anim = new AlphaAnimation(0.6f, 1.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    startAnimation(anim);
                } else {
                    setAlpha(1.0f);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_CANCEL:
                //透明なし
                if (Build.VERSION.SDK_INT < 11) {
                    final AlphaAnimation anim = new AlphaAnimation(0.6f, 1.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    startAnimation(anim);
                } else {
                    setAlpha(1.0f);
                }
                break;
        }

        invalidate();

        return super.onTouchEvent(event);

    }
}