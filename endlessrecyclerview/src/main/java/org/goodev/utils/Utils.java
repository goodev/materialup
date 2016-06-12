package org.goodev.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by goodev on 2015/1/7.
 */
public class Utils {
    public static final String SITE_URL = "https://dribbble.com";
    public static final int SITE_URL_LENGTH = SITE_URL.length() + 1;//add /
    public static final String SEARCH_URL = "https://dribbble.com/search";
    public static final String SHOTS_URL = "https://dribbble.com/shots";
    public static final String DRBL_URL = "drbl.in";
    public static final char SHOTS_DIVIDER = '-';
    public static final int SHOTS_URL_LENGHT = SHOTS_URL.length() + 1;
    //
    public static final String[] URLS = {"https://dribbble.com/designers", "https://dribbble.com/skills", "https://dribbble.com/cities", "https://dribbble.com/countries", "https://dribbble.com/teams", "https://dribbble.com/meetups", "https://dribbble.com/jobs", "https://dribbble.com/highlights", "https://dribbble.com/goods", "https://dribbble.com/projects", "https://dribbble.com/buckets", "https://dribbble.com/colors", "https://dribbble.com/tags", "https://dribbble.com/about", "https://dribbble.com/contact", "https://dribbble.com/privacy", "https://dribbble.com/testimonials", "https://dribbble.com/handbook", "https://dribbble.com/branding", "https://dribbble.com/pro", "https://dribbble.com/advertise", "https://dribbble.com/activity", "https://dribbble.com/account", "https://dribbble.com/session",};
    public static String CHANNEL = "PlayMarket";

    public static boolean isSpecialUrl(String url) {
        int length = URLS.length;
        for (int i = 0; i < length; i++) {
            if (URLS[i].equalsIgnoreCase(url)) {
                return true;
            }
        }
        return false;
    }

    public static String getUserIdOrName(String url) {
        if (url.length() <= SITE_URL_LENGTH) {
            return null;
        }
        return url.substring(SITE_URL_LENGTH);
    }

    public static String getShotId(String url) {
        if (url.length() < SHOTS_URL_LENGHT) {
            return null;
        }
        int index = url.indexOf(SHOTS_DIVIDER, SHOTS_URL_LENGHT);
        if (index == -1) {
            return null;
        }
        return url.substring(SHOTS_URL_LENGHT, index);
    }

    public static final String getFileName(final String url) {
        final String MD5 = "MD5";
        String name = null;
        String ext = getFileExt(url);
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(url.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString() + ext;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() + ext;
    }

    private static String getFileExt(String url) {
        int index = url.lastIndexOf('.');
        if (index != -1) {
            String ext = url.substring(index);
            if (ext.length() <= 5) {
                return ext;
            }
        }
        return ".png";
    }

    public static long getDirectorySize(File directory, long blockSize) {
        File[] files = directory.listFiles();
        if (files != null) {
            // space used by directory itself
            long size = 0;

            for (File file : files) {
                if (file.isDirectory()) {
                    // space used by subdirectory
                    size += getDirectorySize(file, blockSize);
                } else {
                    // file size need to rounded up to full block sizes
                    // (not a perfect function, it adds additional block to 0
                    // sized files
                    // and file who perfectly fill their blocks)
                    size += (file.length() / blockSize + 1) * blockSize;
                }
            }
            return size;
        } else {
            return 0;
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getDirectorySize(File directory) {
        if (directory == null || !directory.exists())
            return 0;
        StatFs statFs = new StatFs(directory.getAbsolutePath());
        long blockSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
        } else {
            blockSize = statFs.getBlockSize();
        }

        return getDirectorySize(directory, blockSize);
    }

    public static long getPreviewCacheSize(Context ctx) {
        // Ion image cache
        File ion = ctx.getCacheDir();
        long size = getDirectorySize(ion);

        return size;
    }

    public static void deleteCacheFiles(Context ctx) {
        File ion = ctx.getCacheDir();
        if (ion.exists()) {
            deleteRecursive(ion);
        }
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static long getCustomFontSize(Context ctx) {
        File fs = getImageDir(ctx);
        if (!fs.exists())
            return 0;
        File[] files = fs.listFiles();

        if (files == null) {
            return 0;
        }

        int length = files.length;
        int size = 0;
        for (int i = 0; i < length; i++) {
            size += files[i].length();
        }

        return size;
    }

    public static File getImageDir(Context context) {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (dir == null) {
            dir = new File(context.getFilesDir(), "shots");
        }
        return dir;
    }

    public static boolean hasInternet(Context ctx) {
        if (ctx == null) {
            return false;
        }
        return isConnected(ctx.getApplicationContext());
    }

    public static boolean isConnected(Context context) {

        if (context==null) return false;

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetworkInfo == null)
                return false;

            return ((activeNetworkInfo != null) && (activeNetworkInfo
                    .isConnectedOrConnecting()));
        }

        return false;
    }
}
