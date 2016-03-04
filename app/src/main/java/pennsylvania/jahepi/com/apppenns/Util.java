package pennsylvania.jahepi.com.apppenns;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Util {

    public static String getAndroidId(Context context) {
        TelephonyManager manager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

    public static boolean isEmpty(String value) {
        if (value != null) {
            return value.length() == 0;
        }
        return true;
    }

    public static String SHA1(String text) {
        String hash = "";
        try {
            MessageDigest md;
            byte[] buffer, digest;

            buffer = text.getBytes();
            md = MessageDigest.getInstance("SHA1");
            md.update(buffer);
            digest = md.digest();

            for(byte aux : digest) {
                int b = aux & 0xff;
                if (Integer.toHexString(b).length() == 1) hash += "0";
                hash += Integer.toHexString(b);
            }
        } catch(NoSuchAlgorithmException exp) {
            exp.printStackTrace();
        }

        return hash;
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if ((str.length() - offset) < (maxWidth - 3)) {
            offset = str.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if ((offset + (maxWidth - 3)) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
