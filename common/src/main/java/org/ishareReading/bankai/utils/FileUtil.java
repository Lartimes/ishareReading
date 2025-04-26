package org.ishareReading.bankai.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {
    private static final Set<String> IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg"
    ));
    private static final Set<String> TEXT_TYPES = new HashSet<>(Arrays.asList(
            "txt", "csv", "log", "xml", "json", "yaml", "yml", "md", "ini", "properties"
    ));
    private static final Set<String> AUDIO_TYPES = new HashSet<>(Arrays.asList(
            "mp3", "wav", "aac", "flac", "ogg", "m4a"
    ));
    private static final Set<String> VIDEO_TYPES = new HashSet<>(Arrays.asList(
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm"
    ));
    private FileUtil() {
    }

    public static String getExtension(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        return extension;
    }

    public static String detectFileType(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        String ext = "";
        if (filename != null && filename.contains(".")) {
            ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }

        // 优先根据MIME类型判断
        if (contentType != null) {
            if (contentType.startsWith("image/")) return "图片";
            if (contentType.startsWith("text/")) return "文本";
            if (contentType.startsWith("audio/")) return "音频";
            if (contentType.startsWith("video/")) return "视频";
        }

        // 再根据扩展名判断
        if (IMAGE_TYPES.contains(ext)) return "图片";
        if (TEXT_TYPES.contains(ext)) return "文本";
        if (AUDIO_TYPES.contains(ext)) return "音频";
        if (VIDEO_TYPES.contains(ext)) return "视频";

        return "未知";
    }
}