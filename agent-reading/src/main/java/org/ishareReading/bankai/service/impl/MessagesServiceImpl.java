package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.Messages;
import org.ishareReading.bankai.mapper.MessagesMapper;
import org.ishareReading.bankai.service.MessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * messages表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, Messages> implements MessagesService {

}
