package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersService;

    /**
     * 根据用户id查看个人信息
     *
     * @param userId
     *
     * @return
     */
    @GetMapping("/personalInfo")
    public Response getPersonalInfo(@RequestParam("userId") Long userId) {
        Users byId = usersService.getById(userId);
        if (byId == null) {
            return Response.fail("不存在该用户");
        }
        byId.setPassword(null);
        return Response.success(byId);
    }


    /**
     * 更新用户头像
     *
     * @param file
     * @param userId
     *
     * @return
     */
    @PostMapping("/uploadAvatar")
    public Response uploadAvatar(@RequestParam("file") MultipartFile file,
                                 @RequestParam("userId") Long userId) {
        Long currentUserId = UserHolder.get();
        if(!Objects.equals(currentUserId, userId)){
            return Response.fail("只可以上传自己的头像");
        }
        String url = usersService.uploadAvatar(file, userId);
        if (StringUtils.hasLength(url)) {
            return Response.success("上传成功", url);
        }
        return Response.fail("上传失败");
    }


//    这个关注的需要重构， aop + context + 切面 类似于SPI ，
//    @GetMapping("/follow/{targetUserId}")
//    public Response follow(@PathVariable("targetUserId") Long targetUserId) {
//        Long userId = UserHolder.get();
//        Users byId = usersService.getById(targetUserId);
//        usersService.followUsers();
//    }


}
