package com.wuyiming.singlecolumnadfeed_android.data.video;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class VideoCacheManager {
    private static final String VIDEO_CACHE_DIR = "video_cache";
    private static final int CONNECT_TIMEOUT_MS = 15_000;
    private static final int READ_TIMEOUT_MS = 30_000;

    private VideoCacheManager() {}

    public static Uri getPlayableVideoUri(Context context, String remoteUrl) {
        Uri cached = getCachedVideoUri(context, remoteUrl);
        return cached != null ? cached : Uri.parse(remoteUrl);
    }

    public static Uri getCachedVideoUri(Context context, String remoteUrl) {
        File cacheFile = getCacheFile(context, remoteUrl);
        if (cacheFile.exists() && cacheFile.length() > 0L) {
            return Uri.fromFile(cacheFile);
        }
        return null;
    }

    public static Uri cacheVideo(Context context, String remoteUrl) {
        File cacheFile = getCacheFile(context, remoteUrl);
        if (cacheFile.exists() && cacheFile.length() > 0L) {
            return Uri.fromFile(cacheFile);
        }
        try {
            downloadToCache(remoteUrl, cacheFile);
            return Uri.fromFile(cacheFile);
        } catch (Exception e) {
            return null;
        }
    }

    private static File getCacheFile(Context context, String remoteUrl) {
        File cacheDir = new File(context.getApplicationContext().getCacheDir(), VIDEO_CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return new File(cacheDir, sha256(remoteUrl) + ".mp4");
    }

    private static void downloadToCache(String remoteUrl, File targetFile) throws IOException {
        File tempFile = new File(targetFile.getParentFile(), targetFile.getName() + ".tmp");
        if (tempFile.exists()) {
            tempFile.delete();
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(remoteUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Accept", "video/mp4,*/*");
            connection.setRequestProperty("User-Agent", "SingleColumnAdFeed/1.0");

            int code = connection.getResponseCode();
            if (code < 200 || code > 299) {
                throw new IOException("Video download failed: HTTP " + code);
            }

            try (InputStream input = connection.getInputStream();
                 OutputStream output = new java.io.FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            if (tempFile.length() <= 0L) {
                throw new IOException("Video cache file empty");
            }

            if (targetFile.exists()) {
                targetFile.delete();
            }
            if (!tempFile.renameTo(targetFile)) {
                throw new IOException("Video cache file move failed");
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
