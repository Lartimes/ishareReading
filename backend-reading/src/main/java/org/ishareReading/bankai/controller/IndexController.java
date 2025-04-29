package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.aop.CommentAop;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * type相关 首页相关 通用的？？？ 还有那些？？ 需要想一下
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    private final BooksService booksService;
    private final CommentAop commentAop;

    public IndexController(CommentAop commentAop, BooksService booksService) {
        this.commentAop = commentAop;
        this.booksService = booksService;
    }

    /**
     * 获取门户首页评论 获取帖子评论
     *
     * @return
     */
    @PostMapping("/getComments")
    public Response comment(@RequestBody Map<String, String> map) {
        return commentAop.getComment(map);
        }

    /**
     * 获取热门书籍
     * @return
     */
    @GetMapping("/book/hot/rank")
    public Response listBookHotRank() {
        return Response.success(booksService.getBookHotRank());
    }
}
