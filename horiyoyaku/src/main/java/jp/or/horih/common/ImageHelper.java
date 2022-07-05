package jp.or.horih.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * {@link Bitmap}, {@link Drawable}をリサイズするためのユーティリティクラス。
 * <p>
 * 画像のサイズが大きい場合、目的のサイズを設定することでリサイズ出来る
 *
 * @author kumagai
 */
public final class ImageHelper {
    /**
     * ビットマップをリサイズする。
     * <p>
     * 幅と高さのうちどちらかの縮小率に合わせてリサイズされます。
     *
     * @param bitmap       ソースビットマップ
     * @param desireWidth  目的の幅
     * @param desireHeight 目的の高さ
     * @return リサイズしたビットマップ
     */
    public static final Bitmap resize(Bitmap bitmap, int desireWidth, int desireHeight) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width <= 0 || height <= 0 || (width < desireWidth && height < desireHeight)) {
            return bitmap;
        }

        // 高さの方が大きい場合
        if (width < height) {
            boolean useWidthScaling = false;
            // 高さを基準とした縮小率を計算する
            float scale = (float) desireHeight / height;

            // 高さを基準とした縮小率で計算した幅が目的の値より大きい場合
            if (desireWidth < width * scale) {
                // 高さの縮小率の場合、目的の幅より縮小されないので幅を基準とした縮小率を計算する
                scale = (float) desireWidth / width;
                useWidthScaling = true;
            }

            // スケーリングしたビットマップを作成する
            final Bitmap scaledBmp = createScaledBitmap(bitmap, scale);
            // 目的のサイズの空のビットマップを作成する
            final Bitmap newBmp = Bitmap.createBitmap(desireWidth, desireHeight, Config.ARGB_4444);

            // 空のビットマップにスケーリングしたビットマップを描画する
            final Canvas canvas = new Canvas(newBmp);
            // 中心に描画されるように位置を計算する
            if (useWidthScaling) {
                canvas.drawBitmap(scaledBmp, 0f, (desireHeight - (height * scale)) / 2f, null);
            } else {
                canvas.drawBitmap(scaledBmp, (desireWidth - (width * scale)) / 2f, 0f, null);
            }
            // 不要になったビットマップをリサイクルする
            scaledBmp.recycle();
            bitmap.recycle();

            return newBmp;
        } else {
            // スケーリングしたビットマップを作成する
            final Bitmap newBmp = createScaledBitmap(bitmap, (float) desireWidth / width);
            // 不要になったビットマップをリサイクルする
            bitmap.recycle();

            return newBmp;
        }
    }

    /**
     * スケーリングしたビットマップを作成する
     *
     * @param src   ソースビットマップ
     * @param scale スケーリング値
     * @return スケーリングしたビットマップ
     */
    private static Bitmap createScaledBitmap(Bitmap src, float scale) {
        // スケーリング行列を作成する
        final Matrix scaling = new Matrix();
        scaling.postScale(scale, scale);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), scaling, true);
    }
}
