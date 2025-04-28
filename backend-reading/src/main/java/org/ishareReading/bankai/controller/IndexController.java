package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.aop.CommentAop;
import org.ishareReading.bankai.response.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    BooksService booksService;

    private final CommentAop commentAop;

    public IndexController(CommentAop commentAop) {
        this.commentAop = commentAop;
    }

    /**
     * 获取门户首页评论 获取帖子评论
     *
     * @return
     */
    @PostMapping("/addComment")
    public Response comment(@RequestBody Map<String, String> map) {
        return commentAop.getComment(map);
        }

    /**
     * 获取热门书籍
     * @return
     */
    @GetMapping("/book/hot/rank")
    public Response listBookHotRank(){
        return Response.success(booksService.getBookHotRank());
    }
}
