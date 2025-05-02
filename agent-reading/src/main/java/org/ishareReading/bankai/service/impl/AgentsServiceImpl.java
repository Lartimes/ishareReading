package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ishareReading.bankai.mapper.AgentsMapper;
import org.ishareReading.bankai.model.Agents;
import org.ishareReading.bankai.service.AgentsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * agent表 服务实现类
 * </p>
 *
 */
@Service
public class AgentsServiceImpl extends ServiceImpl<AgentsMapper, Agents> implements AgentsService {

}
