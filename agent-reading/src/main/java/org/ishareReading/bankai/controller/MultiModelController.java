package org.ishareReading.bankai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import lombok.SneakyThrows;
import org.ishareReading.bankai.util.SSEUtils;
import org.ishareReading.bankai.util.Video2ImgUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/multiMode")
public class MultiModelController {
    private static final String DEFAULT_PROMPT = "这些是什么？";
    private static final String DEFAULT_VIDEO_PROMPT = "这是一组从视频中提取的图片帧，请描述此视频中的内容。";
    private static final String DEFAULT_MODEL = "qwen-vl-max-latest";

    @Autowired
    private ChatClient dashScopeChatClient;

//    qwen-vl-ocr

    @SneakyThrows
    @PostMapping("/stream/video")
    public Flux<ServerSentEvent<String>> video(
//    public String video(
            @RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_VIDEO_PROMPT) String prompt,
            @RequestPart("file") MultipartFile file
    ) {

        List<Media> mediaList = Video2ImgUtils.extractFrames(file, 10);
        UserMessage message = new UserMessage(prompt, mediaList);
        message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.IMAGE);

        Flux<ChatResponse> flux = dashScopeChatClient.prompt(
                new Prompt(
                        message,
                        DashScopeChatOptions.builder()
                                .withModel(DEFAULT_MODEL)
                                .withMultiModel(true)
                                .build()
                )
        ).stream().chatResponse();
        return SSEUtils.result(flux);
    }

    @PostMapping("/stream/image")
    public Flux<ServerSentEvent<String>> streamImage(
            @RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT) String prompt,
            @RequestPart("file") MultipartFile file
    ) {
        UserMessage message = new UserMessage(
                prompt,
                new Media(
                        MimeTypeUtils.IMAGE_JPEG,
                        file.getResource()
                ));
        message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.IMAGE);

        Flux<ChatResponse> flux = dashScopeChatClient.prompt(
                new Prompt(
                        message,
                        DashScopeChatOptions.builder()
                                .withModel(DEFAULT_MODEL)
                                .withMultiModel(true)
                                .build()
                )
        ).stream().chatResponse();
        System.out.println("发送了请求？？？");
        return SSEUtils.result(flux);
    }


}
