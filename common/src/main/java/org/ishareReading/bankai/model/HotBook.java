package org.ishareReading.bankai.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;



@Data
@NoArgsConstructor
@ToString
public class HotBook implements Serializable {

    private static final long serialVersionUID = 1L;

    String hotFormat;

    Double hot;


    Long id;
    String name;
    String author;
    String genre;
    Long coverImageId;
    BigDecimal averageRating;

    public HotBook(Double hot,Long id,String name,String author,String genre,Long coverImageId,BigDecimal averageRating){
        this.hot = hot;
        this.id = id;
        this.name = name;
        this.author = author;
        this.genre =genre;
        this.coverImageId = coverImageId;
        this.averageRating = averageRating;

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
        HotBook hotBook = (HotBook) o;
        return Objects.equals(id, hotBook.id) &&
                Objects.equals(name, hotBook.name) &&
                Objects.equals(author,hotBook.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name,author);
    }
}

