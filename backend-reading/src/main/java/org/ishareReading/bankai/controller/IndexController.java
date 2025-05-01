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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/index")
public class IndexController {
    private final CommentAop commentAop;
    private final BookVectorService bookVectorService;
    @Autowired
    private BooksService booksService;
    @Autowired
    private AuthorService authorService;

    public IndexController(CommentAop commentAop, BookVectorService bookVectorService) {
        this.commentAop = commentAop;
        this.bookVectorService = bookVectorService;
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
    @PostMapping("/addComment")
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
                    .eq(Books::getAuthor, query));
            return Response.success(booksService.convert2HomePage(list));
        } else if (query.trim().toUpperCase().contains("ISBN")) {
            Books one = booksService.getOne(new LambdaQueryWrapper<Books>()
                    .eq(Books::getIsbn, query.trim().toUpperCase()));
            return Response.success(booksService.convert2HomePage(Collections.singleton(one)));
        }
        List<String> strings = SentenceUtil.split2stopWords(query);
        Collection<BookVector> bookVectors = WordVectorUtil.getInstance().generateVector(strings);
        List<BookVector> searchResult = bookVectorService.knnSearch(bookVectors);
        HashSet<Long> ids = new HashSet<>();
        searchResult.forEach(result -> {
            ids.addAll(result.getIds());
        });
        List<Books> books = booksService.listByIds(ids);
        return Response.success(booksService.convert2HomePage(books));
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
