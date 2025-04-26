package org.ishareReading.bankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ishare.oss.OssService;
import org.ishareReading.bankai.constant.BucketConstant;
import org.ishareReading.bankai.exception.BusinessException;
import org.ishareReading.bankai.mapper.FilesMapper;
import org.ishareReading.bankai.mapper.UsersMapper;
import org.ishareReading.bankai.model.Files;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.service.UsersService;
import org.ishareReading.bankai.utils.FileUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-04-25 03:30:37..
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    private static final Logger LOG = LogManager.getLogger(UsersServiceImpl.class);
    private final OssService ossService;
    private final FilesMapper filesMapper;

    public UsersServiceImpl(@Qualifier("filesMapper") FilesMapper filesMapper, OssService ossService) {
        this.filesMapper = filesMapper;
        this.ossService = ossService;
    }

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String uploadAvatar(MultipartFile file, Long userId) {
        Users currentUser = this.getById(userId);
        String key = null;
        try {
            InputStream inputStream = file.getInputStream();
            key = ossService.upload(BucketConstant.COMMON_BUCKET_NAME, inputStream);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
        long size = file.getSize();
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.detectFileType(file); //获取文件type ,音频、文本。。。
        String contentType = file.getContentType();
        currentUser.setAvatar(key);
        this.updateById(currentUser);
        Files files = new Files();
        files.setFileName(originalFilename);
        files.setFilePath(key);
        files.setSize(size);
        files.setFormat(contentType);
        files.setType(type);
        files.setUserId(userId);
        filesMapper.insert(files);
        return key;
    }


}
