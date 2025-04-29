package org.ishareReading.bankai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.model.Posts;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帖子相关的,默认Markdown格式
 */
@RestController
@RequestMapping("/post")
public class PostController {


    @Autowired
    private PostsService postsService;

    /**
     * 上传帖子 这个帖子也可能关联 book ， 需要前端是否挂起超链接？？？
     */
    @PostMapping("/savePost")
    public Response savePost(@RequestBody Posts posts) {
        postsService.save(posts);
        return Response.success("发布成功");
    }

    /**
     * TODO AI进行优化帖子/笔记优化，这个到时候再写
     */
    @PostMapping("/beautifyPosts")
    public Response beautifyPosts(@RequestParam String content) {
        return null;
    }

    /**
     * 点击帖子，也就是查看详情
     *
     * @return
     */
    @GetMapping("/viewPost")
    public Response viewPost(@RequestParam Long id) {
        return Response.success(postsService.getPostInfo(id));
    }


    /**
     * 根据类型获取帖子
     */
    @GetMapping("/getPostsList")
    public Response getPostsList(@RequestParam Long id) {
        return Response.success(postsService.getPostsByTypeId(id));
    }

    /**
     * 根据页数获取帖子 默认五条，不做分页了，浪费时间
     */
    @GetMapping("/getPostsPages")
    public Response getPostsPages(@RequestParam(defaultValue = "1") Integer page) {
        List<Posts> list = postsService.list(
                new LambdaQueryWrapper<Posts>()
                        .orderByDesc(Posts::getLikeCount, Posts::getViewCount, Posts::getStartCount,
                                Posts::getCreateAt)
        );
        int size = list.size();
        list = size > 5 ? list.subList((page - 1) * 5, size) : list;
        return Response.success(list);
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
        return Response.success(postsService.list(new LambdaQueryWrapper<Posts>()
                .eq(Posts::getUserId, userId)
                .orderByDesc(Posts::getLikeCount, Posts::getViewCount, Posts::getStartCount,
                        Posts::getCreateAt)));
    }

}
