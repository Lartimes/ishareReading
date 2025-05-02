package org.ishareReading.bankai.util;

import dev.langchain4j.model.openai.OpenAiTokenizer;

public class TokenCountUtil {

    //    搞成map, model——name 获取工具类
//    获取token,为快速开发直接默认deepseek-chat
    private static final OpenAiTokenizer openAiTokenizer = new OpenAiTokenizer("gpt-3.5-turbo");

    /**
     * 计算文本的 token 数量
     *
     * @param text 要计算的文本
     *
     * @return token 数量
     */
    public static int countTokens(String text) {
        return openAiTokenizer.estimateTokenCountInText(text);
    }
}