package org.ishareReading.bankai.util;


import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.ai.model.Media;
import org.springframework.core.io.PathResource;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Video2ImgUtils {
    private Video2ImgUtils() {}

    private static final Map<String, List<String>> FRAME_CACHE = new ConcurrentHashMap<>();

    /**
     * 从视频文件中提取指定数量的帧作为Media列表
     *
     * @param videoFile      上传的视频文件
     * @param numberOfFrames 需要提取的帧数量
     *
     * @return 包含指定数量帧的Media列表
     */
    public static List<Media> extractFrames(MultipartFile videoFile, int numberOfFrames) throws IOException {
        // 创建临时目录存储提取的帧
        Path tempDir = Files.createTempDirectory("video-frames");
        List<String> framePaths = new ArrayList<>();

        // 将MultipartFile保存为临时文件
        Path tempVideoFile = tempDir.resolve(Objects.requireNonNull(videoFile.getOriginalFilename()));
        Files.copy(videoFile.getInputStream(), tempVideoFile, StandardCopyOption.REPLACE_EXISTING);

        // 提取帧
        extractFramesFromVideo(tempVideoFile.toFile(), tempDir.toFile(), framePaths, numberOfFrames);

        // 缓存帧路径
        FRAME_CACHE.put(tempVideoFile.toString(), framePaths);

        // 创建Media列表
        return createMediaList(framePaths, numberOfFrames);
    }

    private static void extractFramesFromVideo(File videoFile, File outputDir, List<String> framePaths, int numberOfFrames) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
             Java2DFrameConverter converter = new Java2DFrameConverter()) {

            grabber.start();
            int totalFrames = grabber.getLengthInFrames();
            int frameInterval = Math.max(totalFrames / numberOfFrames, 1);

            Frame frame;
            for (int i = 0; i < totalFrames; i++) {
                frame = grabber.grabFrame();
                if (frame == null || frame.image == null) {
                    continue;
                }

                // 按间隔提取帧
                if (i % frameInterval == 0 && framePaths.size() < numberOfFrames) {
                    BufferedImage image = converter.getBufferedImage(frame);
                    String framePath = outputDir.getAbsolutePath() + File.separator + "frame_" + i + ".png";
                    File frameFile = new File(framePath);
                    ImageIO.write(image, "png", frameFile);
                    framePaths.add(framePath);
                }
            }
        } catch (Exception ignore) {
        }
    }

    private static List<Media> createMediaList(List<String> framePaths, int numberOfFrames) {
        int totalFrames = framePaths.size();
        int interval = Math.max(totalFrames / numberOfFrames, 1);

        return IntStream.range(0, Math.min(numberOfFrames, totalFrames))
                .mapToObj(i -> framePaths.get(i * interval))
                .map(framePath -> new Media(
                        MimeType.valueOf("image/png"),
                        new PathResource(framePath)
                ))
                .collect(Collectors.toList());
    }
}
