package tech.linjiang.pandora.util;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tech.linjiang.pandora.core.R;

/**
 * Created by linjiang on 05/06/2018.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final HashMap<String, String> mFileTypes = new HashMap<>();

    static {
        mFileTypes.put("apk", "application/vnd.android.package-archive");
        mFileTypes.put("avi", "video/x-msvideo");
        mFileTypes.put("bmp", "image/bmp");
        mFileTypes.put("c", "text/plain");
        mFileTypes.put("class", "application/octet-stream");
        mFileTypes.put("conf", "text/plain");
        mFileTypes.put("doc", "application/msword");
        mFileTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mFileTypes.put("xls", "application/vnd.ms-excel");
        mFileTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mFileTypes.put("gif", "image/gif");
        mFileTypes.put("gtar", "application/x-gtar");
        mFileTypes.put("gz", "application/x-gzip");
        mFileTypes.put("htm", "text/html");
        mFileTypes.put("html", "text/html");
        mFileTypes.put("jar", "application/java-archive");
        mFileTypes.put("java", "text/plain");
        mFileTypes.put("jpeg", "image/jpeg");
        mFileTypes.put("jpg", "image/jpeg");
        mFileTypes.put("js", "application/x-javascript");
        mFileTypes.put("log", "text/plain");
        mFileTypes.put("mov", "video/quicktime");
        mFileTypes.put("mp3", "audio/x-mpeg");
        mFileTypes.put("mp4", "video/mp4");
        mFileTypes.put("mpeg", "video/mpeg");
        mFileTypes.put("mpg", "video/mpeg");
        mFileTypes.put("mpg4", "video/mp4");
        mFileTypes.put("ogg", "audio/ogg");
        mFileTypes.put("pdf", "application/pdf");
        mFileTypes.put("png", "image/png");
        mFileTypes.put("ppt", "application/vnd.ms-powerpoint");
        mFileTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mFileTypes.put("prop", "text/plain");
        mFileTypes.put("rc", "text/plain");
        mFileTypes.put("rmvb", "audio/x-pn-realaudio");
        mFileTypes.put("rtf", "application/rtf");
        mFileTypes.put("sh", "text/plain");
        mFileTypes.put("tar", "application/x-tar");
        mFileTypes.put("tgz", "application/x-compressed");
        mFileTypes.put("txt", "text/plain");
        mFileTypes.put("wav", "audio/x-wav");
        mFileTypes.put("wps", "application/vnd.ms-works");
        mFileTypes.put("xml", "text/plain");
        mFileTypes.put("zip", "application/x-zip-compressed");
        mFileTypes.put("", "*/*");
    }


    public static String getFileType(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }
        String name = file.getName();
        try {
            String suffix = !name.contains(".") ? "" : name.substring(name.lastIndexOf(".") + 1);
            String type = mFileTypes.get(suffix);
            Log.d(TAG, "getFileType: " + type);
            return type;
        } catch (Throwable t) {
            t.printStackTrace();
            return "";
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static Intent getFileIntent(String filePath) {
        File file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(Utils.getContext(),
                Utils.getContext().getPackageName() + ".fileProvider", file);
        intent.setDataAndType(uri, getFileType(filePath));
        return intent;
    }

    public static String fileSize(File file) {
        return Utils.formatSize(getFolderSize(file));
    }



    private static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

    public static String fileCopy2Tmp(File originPath) {
        if (!originPath.exists()) {
            return null;
        }
        File externalCacheDir = Utils.getContext().getExternalCacheDir();
        if (externalCacheDir == null) {
            return null;
        }
        String targetPath = externalCacheDir.getPath().concat("/tmp/");
        File target = new File(targetPath);
        if (!target.exists()) {
            boolean success = target.mkdirs();
            if (!success) {
                return null;
            }
        }
        try {
            String targetFile = targetPath.concat(originPath.getName());
            FileInputStream inputStream = new FileInputStream(originPath);
            byte[] data = new byte[1024];
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
            inputStream.close();
            outputStream.close();
            return targetFile;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static String md5File(File file) {
        try {
            byte[] fileBytes = getFileBytes(file);
            byte[] md5Bytes = MessageDigest.getInstance("MD5").digest(fileBytes);
            return bytesToHexString(md5Bytes);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "--";
    }

    private static byte[] getFileBytes(File file) throws IOException {
        byte[] buffer;
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        byte[] b = new byte[1024];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        buffer = bos.toByteArray();
        return buffer;
    }

    public static boolean renameTo(File oldFile, String newName) {
        if (!oldFile.exists() || oldFile.isDirectory()) {
            return false;
        }
        String newFilePath = oldFile.getPath().replaceAll(oldFile.getName(), newName);
        File file2 = new File(newFilePath);
        if (file2.exists()) {
            return false;
        }
        return oldFile.renameTo(file2);
    }

    public static List<String> readAsPlainText(File file) {
        List<String> text = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text;
    }
}
