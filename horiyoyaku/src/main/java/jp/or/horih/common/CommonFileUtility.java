package jp.or.horih.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;

public class CommonFileUtility {

    private static final String SAVE_DIR = "/mpconcier/";

    //アプリ内にjpgファイルを保存
    public static boolean saveBitmapImageInAppliDir(Context _context, Bitmap _mBitmap, String _fileName) throws IOException {
        FileOutputStream out = null;
        try {
            // openFileOutputはContextのメソッドなのでActivity内ならばthisでOK
            out = _context.openFileOutput(_fileName, Context.MODE_PRIVATE);
            _mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            // エラー処理
            return false;
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
        }

        return true;

    }

    //アプリ内にのファイルを読み込み
    public static Bitmap readImageInAppliDir(Context _context, String _fileName) throws IOException {
        InputStream input = null;
        try {
            input = _context.openFileInput(_fileName);
        } catch (FileNotFoundException e) {
            // エラー処理
            return null;
        }
        Bitmap image = BitmapFactory.decodeStream(input);

        return image;
    }

    //アプリ内にのファイルを削除
    public static void delImageInAppliDir(Context _context, String _fileName) throws IOException {

        _context.deleteFile(_fileName);

    }


    //ギャラリーにファイルを保存
    public static boolean saveBitmapInGallery(Context _context, Bitmap _mBitmap, String _fileName) throws IOException {

        File file = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR);
        try {
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }

        String AttachName = file.getAbsolutePath() + "/" + _fileName;

        try {
            FileOutputStream out = new FileOutputStream(AttachName);
            _mBitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        File addFile = new File(
                Environment.getExternalStorageDirectory().getPath() + SAVE_DIR,
                _fileName);

        // MediaScannerConnection を使用する場合
        MediaScannerConnection.scanFile( // API Level 8
                _context, // Context
                new String[]{addFile.getPath()},
                new String[]{"image/jpeg"},
                null);

        return true;

    }
}
