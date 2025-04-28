package org.ishareReading.bankai.service;

import lombok.SneakyThrows;
import org.ishareReading.bankai.model.Books;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ishareReading.bankai.model.HotBook;
import org.ishareReading.bankai.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 书籍表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
public interface BooksService extends IService<Books> {

    List<HotBook> getBookHotRank();


    @SneakyThrows
    Response uploadOrUpdateBook(Books books);

    Response getMetadata(MultipartFile file);

    void insertContestPages(Long fileId , Integer pages);

}
