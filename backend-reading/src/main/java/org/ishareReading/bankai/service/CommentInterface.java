package org.ishareReading.bankai.service;

import org.ishareReading.bankai.response.Response;

import java.util.Map;

public interface CommentInterface {
    String getType();

    Response getComment(Map<String, String> map);

    Response deleteComment(Map<String, String> map);

    Response addComment(Map<String, String> map);
}

