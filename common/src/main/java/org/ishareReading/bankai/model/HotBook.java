package org.ishareReading.bankai.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;


/**
 * @descripetion:热度排行榜
 * @algorithm: 热度 = (0.3 × 浏览量) + (0.4 × 下载量) + (0.2 × 平均评分 × log(评分人数 + 1)) + (0.1 × exp(-0.05 × 时间差))
 */
@Data
@NoArgsConstructor
@ToString
public class HotBook implements Serializable {

    private static final long serialVersionUID = 1L;

    String hotFormat;

    Double hot;


    Long bookId;
    String name;
    String author;
    String genre;
    Long coverImageId;
    Integer viewCount;

    public HotBook(Double hot,Long bookId,String name,String author,String genre,Long coverImageId,Integer viewCount){
        this.hot = hot;
        this.bookId = bookId;
        this.name = name;
        this.author = author;
        this.genre =genre;
        this.coverImageId = coverImageId;
        this.viewCount = viewCount;

    }


    public void hotFormat(){
        BigDecimal bigDecimal = new BigDecimal(this.hot);
        BigDecimal decimal = bigDecimal.divide(new BigDecimal("10000"));
        DecimalFormat formater = new DecimalFormat("0.0");
        formater.setRoundingMode(RoundingMode.HALF_UP);    // 5000008.89
        String formatNum = formater.format(decimal);
        this.setHotFormat( formatNum+"万");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotBook hotVideo = (HotBook) o;
        return Objects.equals(bookId, hotVideo.bookId) &&
                Objects.equals(name, hotVideo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, name);
    }
}

