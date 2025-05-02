package org.ishareReading.bankai.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;


// TODO
@Data
@NoArgsConstructor
@ToString
public class HotAuthor {
    private static final long serialVersionUID = 1L;

    String hotFormat;

    Long fans;
    Long bookCount;
    String name;

    public HotAuthor(Double hot,Long bookId,String name,String author,String genre,Long coverImageId,Integer viewCount){


    }


    public void hotFormat(){
        BigDecimal bigDecimal = new BigDecimal(this.fans);
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
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }
}
