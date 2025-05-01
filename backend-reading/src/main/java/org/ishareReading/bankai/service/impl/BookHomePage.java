package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.model.BookOpinions;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.BookOpinionsService;
import org.ishareReading.bankai.service.CommentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

//===================================================
@Service
public class BookHomePage implements CommentInterface {

    @Autowired
    private BookOpinionsService bookOpinionsService;

    @Override
    public String getType() {
        return "book";
    }

    //   todo  分页否? 有时间再写 先查所有
    @Override
    public Response getComment(Map<String, String> map) {
        long bookId = Long.parseLong(map.get("bookId"));
        Collection<BookOpinions> bookOpinions = bookOpinionsService.list(new LambdaQueryWrapper<BookOpinions>()
                .eq(BookOpinions::getId, bookId)
                .isNull(BookOpinions::getUnderlinedId)
                //非具体页数的评论,作为书籍详情
                .orderByDesc(BookOpinions::getCreateAt, BookOpinions::getUpdateAt));
        return Response.success(bookOpinions);
    }

    @Override
    public Response deleteComment(Map<String, String> map) {
        long id = Long.parseLong(map.get("id"));
        long userId = Long.parseLong(map.get("userId"));
//        主键id
        boolean remove = bookOpinionsService.remove(
                new LambdaQueryWrapper<BookOpinions>()
                        .eq(BookOpinions::getId, id)
                        .eq(BookOpinions::getUserId, userId));
        if (remove) {
            return Response.success("删除成功");
        }
        return Response.fail("删除失败");
    }

    @Override
    public Response addComment(Map<String, String> map) {
        long userId = Long.parseLong(map.get("userId"));
        BookOpinions bookOpinions = new BookOpinions();
        bookOpinions.setUserId(userId);
        bookOpinions.setBookId(Long.parseLong(map.get("id")));
        bookOpinions.setOpinionText(map.get("text"));
        boolean save = bookOpinionsService.save(bookOpinions);
        Long id = bookOpinions.getId();
        if (save) {
            BookOpinions selected = bookOpinionsService.getById(id);
            return Response.success(selected);
        }
        return Response.fail("请重新尝试");
    }
}
