package org.ishareReading.bankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ishareReading.bankai.model.BookContentPage;

import java.util.Collection;

public interface BookContentPageMapper extends BaseMapper<BookContentPage> {

    void insertBatchSomeColumn(Collection<BookContentPage> list);
}
