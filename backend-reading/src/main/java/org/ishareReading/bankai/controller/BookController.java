package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/book")
public class BookController {
    //    上传图书
//    简介
    private final BooksService booksService;

    public BookController(BooksService booksService) {
        this.booksService = booksService;
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
        return booksService.uploadOrUpdateBook(books);
    }

    /**
     * 上传图书自动获取元属性信息
     * @param file
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

}
