package jp.or.horih.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class CommonUtility {

    public static boolean mailCheck(String mail) {

        String ptnStr = "([a-zA-Z0-9][a-zA-Z0-9_.+\\-]*)@(([a-zA-Z0-9][a-zA-Z0-9_\\-]+\\.)+[a-zA-Z]{2,6})";
        //Pattern ptn = Pattern.compile(ptnStr);
        //Matcher mc = ptn.matcher(mail);

        boolean result;
        if (mail.matches(ptnStr)) {

            result = true;

        } else {

            result = false;

        }

        return result;

    }


    /**
     * 　文字列から日付取得(フォーマット指定)
     *
     * @param
     * @return
     * @throws Exception
     */
    public static Date getStringFormatDate(String _stringDate, String _format) throws Exception {

        Date formatDate = null;

        try {
            SimpleDateFormat format = new SimpleDateFormat(_format);

            formatDate = format.parse(_stringDate);

        } catch (Exception e) {
            throw e;
        }

        return formatDate;
    }

    /**
     * 　文字列から日付取得
     *
     * @param
     * @return
     * @throws Exception
     */
    public static Date getStringChangeDate(String _stringDate) throws Exception {
        return getStringFormatDate(_stringDate, "yyyy/MM/dd");
    }

    /**
     * 　日付から文字列取得（フォーマット）
     *
     * @param
     * @return
     * @throws
     */
    public static String getDateChangeFormatString(Date da, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateString = sdf.format(da);

        return dateString;
    }

    /**
     * 　日付から文字列取得
     *
     * @param
     * @return
     * @throws
     */
    public static String getDateChangeString(Date da) {
        return getDateChangeFormatString(da, "yyyy/MM/dd");
    }

    /**
     * 　文字列の日付形式変換
     *
     * @param
     * @return
     * @throws
     */
    public static String changeStringDateFormat(String da) {
        String[] daList = da.split(" ");
        String strDa = "";
        if (daList.length >= 2) {
            strDa = daList[0];
        } else {
            strDa = da;
        }
        String retDa = strDa.replaceAll("-", "/");

        return retDa;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


}
