package org.ishareReading.bankai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.constant.RedisConstant;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.mapper.BookContentPageMapper;
import org.ishareReading.bankai.model.BookUnderlineCoordinates;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.ishareReading.bankai.service.FilesService;
import org.ishareReading.bankai.utils.RedisCacheUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/book")
public class BookController {
    //    上传图书
//    简介
    private final BooksService booksService;
    private final RedisCacheUtil redisCacheUtil;
    private final FilesService filesService;
    private final BookContentPageMapper bookContentPageMapper;

    public BookController(BooksService booksService, RedisCacheUtil redisCacheUtil, FilesService filesService, BookContentPageMapper bookContentPageMapper) {
        this.booksService = booksService;
        this.redisCacheUtil = redisCacheUtil;
        this.filesService = filesService;
        this.bookContentPageMapper = bookContentPageMapper;
    }

    /**
     * 根据isbn获取图书
     *
     * @param isbn
     *
     * @return
     */
    @GetMapping("/getBooksByIsbn")
    public Response getBookById(@RequestParam("isbn") Long isbn) {
        Books one = booksService.getOne(new LambdaQueryWrapper<Books>()
                .eq(Books::getIsbn, isbn)
                .last("limit 1"));
        return Response.success(booksService.convert2HomePage(Collections.singleton(one)));
    }


    /**
     * 点击书籍获取书籍首页门户 ？？ 这里先SQL ， 后期接入ES bookinfo 首页 封面 当个书籍的热门评论 =======================
     *  todo 接入 AI鉴赏，点评？？？
     *
     * @param id
     *
     * @return
     */
    @GetMapping("/getBooksHomePageById")
    public Response getBookHomePageById(@RequestParam("id") Long id) {
        Long userId = UserHolder.get();
        new Thread(() -> {
            redisCacheUtil.addZSetWithScores(RedisConstant.BOOKS_HISTORY + userId, id, null);
        }).start();
        return Response.success(booksService.getBooksInfoById(id));
    }

    /**
     * 阅读模式获取书籍当页信息
     *
     * @param map
     *
     * @return
     */
    @PostMapping("/getBooksImgByPage")
    public Response getBooksInfoByPage(@RequestBody Map<String, String> map) {
        Long bookId = Long.valueOf(map.get("bookId"));
        Integer pageNum = Integer.valueOf(map.get("pageNum"));
        return Response.success(booksService.getBooksInfoReadingModeById(bookId, pageNum, UserHolder.get()));
    }

    /**
     * 上传图书自动获取元属性信息
     *
     * @param file
     *
     * @return
     */
    @PostMapping("/getBooksMetadata")
    public Response getBooksMetadata(@RequestPart("file") MultipartFile file) {
//        todo tika reader or pdf-reader?
//        读取元数据，返回识别信息
//        调用用大模型等等？？？
//        保存封面和书籍
        return booksService.getMetadata(file);
    }

    /**
     * 添加标注
     *
     * @param bookUnderlineCoordinates
     *
     * @return
     */
    @PostMapping("/markBook")
//    content
    public Response markBook(@RequestBody BookUnderlineCoordinates bookUnderlineCoordinates) {
        booksService.markBook(bookUnderlineCoordinates);
        return Response.success("发布成功");
    }

    /**
     * 根据页数获取书籍 默认五条，不做分页了，浪费时间
     */
    @GetMapping("/getBooksPages")
    public Response getBooksPages(@RequestParam(defaultValue = "1") Integer page) {
        List<Books> list = booksService.list();
        int size = list.size();
        list = size > 6 ? list.subList((page - 1) * 6, size) : list;
        return Response.success(booksService.convert2HomePage(list));
    }

    /**
     * 上传或者更新书籍
     * <p>
     * <p>
     * 真正保存书籍的方法调用 此时进行异步写入books_contents表 schedule 或者 异步 写入整理文档， RAG知识库直接更新
     *
     * @param books
     *
     * @return
     */
    @PostMapping(value = "/uploadUpdateBook")
    public Response uploadBook(@RequestBody Books books) {
        Long userId = UserHolder.get();
        books.setUserId(userId);
        String author = books.getAuthor();
        if (StringUtils.hasLength(author)) {
            author = author.trim();
            String finalAuthor = author;
//            保存作者清单
            new Thread(() -> {
                redisCacheUtil.addZSetWithScores(RedisConstant.AUTHOR, finalAuthor, null);
            }).start();
        }
        return booksService.uploadOrUpdateBook(books);
    }

}
