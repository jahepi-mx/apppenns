package pennsylvania.jahepi.com.apppenns;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pennsylvania.jahepi.com.apppenns.entities.Attachment;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Util {

    private static Location location1 = new Location("");
    private static Location location2 = new Location("");

    public static String getAndroidId(Context context) {
        TelephonyManager manager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = manager.getDeviceId();
        String filtered = id.substring(0, 5);
        return filtered;
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

    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static float getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        location1.setLatitude(latitude1);
        location1.setLongitude(longitude1);
        location2.setLatitude(latitude2);
        location2.setLongitude(longitude2);
        return location1.distanceTo(location2) / 1000;
    }

    public static File createImageFile(String suffix) {
        File image = null;
        try {
            String imageFileName = new SimpleDateFormat("yyMMdd_HHmmss_" + suffix).format(new Date());
            // File storageDir = Environment.getExternalStorageDirectory();
            // image = File.createTempFile(imageFileName, ".jpg", storageDir);
            image = new File(Environment.getExternalStorageDirectory(), imageFileName + ".jpg");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return image;
    }

    public static Attachment buildAttachment(File file) {
        Attachment.File attachmentFile = new Attachment.File();
        attachmentFile.setPath(file.getAbsolutePath());
        attachmentFile.setName(file.getName());
        String extension = "";
        String mime = "";
        try {
            extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString());
        } catch (MalformedURLException exp) {
            exp.printStackTrace();;
        }
        if (extension != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        attachmentFile.setMime(mime);
        attachmentFile.setModifiedDate(Util.getDateTime());
        Attachment attachment = new Attachment();
        attachment.setFile(attachmentFile);
        return attachment;
    }
}
