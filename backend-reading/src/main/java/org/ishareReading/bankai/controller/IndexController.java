package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.aop.CommentAop;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.AuthorService;
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

    @Autowired
    AuthorService authorService;

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
    @GetMapping("/hot/rank/book")
    public Response listBookHotRank(){
        return Response.success(booksService.getBookHotRank());
    }


    /**
     * 获取最新发布的书籍
     * @return
     */
    @GetMapping("/book/")
    public Response listRecentlyReleaseBook(){
        return Response.success(booksService.getRecentlyReleaseBook());
    }

    /**
     * 获取热门作者
     * @return
     */
    @GetMapping("/hot/rank/author")
    public Response listHotAuthor(){
        return Response.success(authorService.getAuthorHotRank());
    }

}
