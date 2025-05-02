package org.ishareReading.bankai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.SneakyThrows;
import org.ishareReading.bankai.aop.CommentAop;
import org.ishareReading.bankai.es.BookVector;
import org.ishareReading.bankai.es.BookVectorService;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.nlp.SentenceUtil;
import org.ishareReading.bankai.nlp.WordVectorUtil;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.AuthorService;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/index")
public class IndexController {
    private final CommentAop commentAop;
    private final BookVectorService bookVectorService;
    private final BooksService booksService;
    private final AuthorService authorService;

    public IndexController(CommentAop commentAop, BookVectorService bookVectorService, BooksService booksService, AuthorService authorService) {
        this.commentAop = commentAop;
        this.bookVectorService = bookVectorService;
        this.booksService = booksService;
        this.authorService = authorService;
    }

    public static void main(String[] args) {
        List<String> strings = SentenceUtil.split2stopWords("程序员的数学关于Java的还有Agent相关的");
        System.out.println(strings);
    }

    /**
     * 获取门户首页评论 获取帖子评论
     *
     * @return
     */
    @GetMapping("/getComment")
    public Response comment(@RequestBody Map<String, String> map) {
        return commentAop.getComment(map);
    }

    /**
     * ES向量 ISBN  ？？ 作者 ？？ 图书名 ？？？
     */
    @GetMapping("/search")
    @SneakyThrows
    public Response search(@RequestParam String query) {
        if (query.trim().contains("@")) {
            List<Books> list = booksService.list(new LambdaQueryWrapper<Books>()
                    .eq(Books::getAuthor, query.substring(query.lastIndexOf("@") + 1)));
            return Response.success(booksService.convert2HomePage(list));
        } else if (query.trim().toUpperCase().contains("ISBN")) {
            Books one = booksService.getOne(new LambdaQueryWrapper<Books>()
                    .eq(Books::getIsbn, query.trim().substring("ISBN".length()).toUpperCase()));
            if (one != null) {
                return Response.success(booksService.convert2HomePage(Collections.singleton(one)));
            } else {
                return Response.success("未查询到");
            }
        }
        List<String> strings = SentenceUtil.split2stopWords(query);
        Collection<BookVector> bookVectors = WordVectorUtil.getInstance().generateVector(strings);
        List<BookVector> searchResult = bookVectorService.knnSearch(bookVectors);
        HashSet<Long> ids = new HashSet<>();
        searchResult.forEach(result -> {
            ids.addAll(result.getIds());
        });
        System.out.println(ids);
        if (!ids.isEmpty()) {
            List<Books> books = booksService.listByIds(ids);
            return Response.success(booksService.convert2HomePage(books));
        } else {
            return Response.success("系统未查询到相关书籍");
        }

    }

    /**
     * 获取热门书籍
     *
     * @return
     */
    @GetMapping("/hot/rank/book")
    public Response listBookHotRank() {
        return Response.success(booksService.getBookHotRank());
    }


    /**
     * 获取最新发布的书籍
     *
     * @return
     */
    @GetMapping("/book/release")
    public Response listRecentlyReleaseBook() {
        return Response.success(booksService.getRecentlyReleaseBook());
    }

    /**
     * 获取热门作者
     *
     * @return
     */
    @GetMapping("/hot/rank/author")
    public Response listHotAuthor() {
        return Response.success(authorService.getAuthorHotRank());
    }

}
