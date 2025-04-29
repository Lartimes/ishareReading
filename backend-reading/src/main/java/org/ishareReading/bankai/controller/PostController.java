package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.response.Response;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子相关的,默认Markdown格式
 */
@RestController
@RequestMapping("/post")
public class PostController {


    /**
     * 上传帖子
     */
    @PostMapping("/savePost")
    public Response savePost(@RequestBody Posts posts) {
//        posts.set
        return null;
    }

    /**
     * 点击帖子
     *
     * @return
     */
    @GetMapping("/viewPost")
    public Response viewPost(@RequestParam Long id) {
        return null;
    }


    /**
     * 根据类型获取帖子
     *
     * @param id
     *
     * @return
     */
    @GetMapping("/getPostsList")
    public Response getPostsList(@RequestParam Long id) {
//        根据类型ID获取posts
        return null;
    }

    /**
     * 根据页数获取帖子
     *
     * @param page
     *
     * @return
     */
    @GetMapping("/getPostsPages")
    public Response getPostsPages(@RequestParam(defaultValue = "1") Integer page) {

        return null;
    }


    /**
     * 根据userId获取其发布的帖子
     *
     * @param userId
     *
     * @return
     */
    @GetMapping("/getPost")
    public Response getPost(@RequestParam Long userId) {
        return null;
    }

}
