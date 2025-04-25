package org.ishareReading.bankai.service;

import org.ishareReading.bankai.model.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:23:29
 */
public interface UsersService extends IService<Users> {

//    todo 现在先邮箱，
//        如果有时间可以扩展一个 验证数据库是由存在该用户名，要达到非常快的验证
    /**
     *
     * 根据用户邮箱 +
     * @param user
     * @return
     */
    Users login(Users user);
}
