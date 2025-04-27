package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;

@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    BooksService booksService;

    /**
     * 获取热门书籍
     * @return
     */
   @GetMapping("/book/hot/rank")
    public Response listBookHotRank(){
        return Response.success(booksService.getBookHotRank());
    }
}
