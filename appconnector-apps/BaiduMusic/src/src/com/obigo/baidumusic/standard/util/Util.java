package com.obigo.baidumusic.standard.util;

public final class Util {
    public static final String BAIDU_URL = "http://hiphotos.baidu.com/wzyhi/pic/item/%s.jpg";

    private Util() {
        // not called
    }

    public static String formatDuration(long durationMs) {
        long duration = durationMs / 1000;
        long h = duration / 3600;
        long m = (duration - h * 3600) / 60;
        long s = duration - (h * 3600 + m * 60);
        String durationValue;
        if (h == 0) {
            durationValue = String.format("%02d:%02d", m, s);
        } else {
            durationValue = String.format("%02d:%02d:%02d", h, m, s);
        }

        return durationValue;
    }

    public static String imageUrlCheck(String url) {
        if (url == null) {
            return null;
        }

        url.trim();

        if (!url.startsWith("http://")) {
            url = String.format(BAIDU_URL, url);
        }

        return url;
    }
}
