package org.ishareReading.bankai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ishareReading.bankai.model.BookOpinions;
import org.ishareReading.bankai.model.BookUnderlineCoordinates;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.model.HotBook;
import org.ishareReading.bankai.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
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


    Response uploadOrUpdateBook(Books books);

    Response getMetadata(MultipartFile file);

    void insertContestPages(Long pk , Long fileId, Integer pages);

    BooksInfoHomePage getBooksInfoById(Long id);


    //        书籍模式根据页数 + id 获取内容
//    图片内容
//    AI鉴赏
//    用户标注
    BooksInfoReadingMode getBooksInfoReadingModeById(Long id, Integer pageNumber, Long userId);

    /**
     * 对书籍img 某一块内容进行标注， 此处只进行绝对坐标定位 前端不支持缩放 此处不浪费时间 如果是md、html就可以直接搞相对了
     *
     * @param bookUnderlineCoordinates
     */
    void markBook(BookUnderlineCoordinates bookUnderlineCoordinates);

    List<BooksInfoHomePage> convert2HomePage(Collection<Books> singleton);


    /**
     * 首页门户书籍信息
     */
    record BooksInfoHomePage(Books books, String coverageBase64, List<org.ishareReading.bankai.model.BookOpinions> opinions,
                             Integer pageNum) {

    }

    /**
     * 阅读模式书籍信息
     */
    record BooksInfoReadingMode(Books books, String imgBase64, Integer pageNum, List<BookOpinions> infoList) {
    }

    List<Books> getRecentlyReleaseBook();
}
