package org.ishareReading.bankai.service.impl;

import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.mapper.UsersMapper;
import org.ishareReading.bankai.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}
