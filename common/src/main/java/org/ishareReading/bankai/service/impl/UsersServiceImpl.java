package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.oss.OssService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.mapper.UsersMapper;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private static final Logger LOG = LogManager.getLogger(UsersServiceImpl.class);
    @Autowired
    private OssService ossService;

    @Override
    public Users login(Users user) {
        final String password = user.getPassword();
        LambdaQueryWrapper<Users> email = new LambdaQueryWrapper<Users>().eq(Users::getEmail, user.getEmail());
        final Users destUser = this.getOne(email);
        if (destUser != null) {
            if (destUser.getPassword().equals(password)) {
                LOG.info("用户 : {} 登录成功", destUser);
                return destUser;
            }
        }
        if (ObjectUtils.isEmpty(destUser)) {
            LOG.info("邮箱错误/不存在: {}", user);
            throw new BusinessException("邮箱错误");
        }
        LOG.info("密码错误 :{}", user);
        throw new BusinessException("密码错误");
    }

    @Override
    public String uploadAvatar(MultipartFile file, Long userId) {
        // 文件大小（字节）
        long size = file.getSize();
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 文件类型（MIME类型）
        String contentType = file.getContentType();
        // 文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        System.out.println("文件大小: " + size + " 字节");
        System.out.println("原始文件名: " + originalFilename);
        System.out.println("文件类型: " + contentType);
        System.out.println("文件扩展名: " + extension);
    }


}
