package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.mapper.BooksMapper;
import org.ishareReading.bankai.service.BooksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 书籍表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {

}
