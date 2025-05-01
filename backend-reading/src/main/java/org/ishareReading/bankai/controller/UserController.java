package org.ishareReading.bankai.controller;

import org.ishareReading.bankai.aop.CommentAop;
import org.ishareReading.bankai.aop.FollowOrSubscribeAop;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Users;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.LikesService;
import org.ishareReading.bankai.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private CommentAop commentAop;
    @Autowired
    private LikesService likesService;
    @Autowired
    private FollowOrSubscribeAop followOrSubscribeAop;
//    这个关注的需要重构， aop + context + 切面 类似于SPI ，
//    @GetMapping("/follow/{targetUserId}")
//    public Response follow(@PathVariable("targetUserId") Long targetUserId) {
//        Long userId = UserHolder.get();
//        Users byId = usersService.getById(targetUserId);
//        usersService.followUsers();
//    }

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
        if (!Objects.equals(currentUserId, userId)) {
            return Response.fail("只可以上传自己的头像");
        }
        String url = usersService.uploadAvatar(file, userId);
        if (StringUtils.hasLength(url)) {
            return Response.success("上传成功", url);
        }
        return Response.fail("上传失败");
    }

    /**
     * 评论、见解，包含帖子评论、书籍首页鉴赏信息等
     *
     * @param map
     *
     * @return
     */
    @PostMapping("/addComment")
    public Response comment(@RequestBody Map<String, String> map) {
        Long userId = UserHolder.get();
        if (userId == null) {
            return Response.fail("请登录后进行评论");
        }
        map.put("userId", String.valueOf(userId));
        return commentAop.comment(map);
    }

    /**
     * 删除评论,帖子评论 , 首页评论
     *
     * @param map
     *
     * @return
     */
    @DeleteMapping("/delComment")
    public Response delComment(@RequestBody Map<String, String> map) {
        Long userId = UserHolder.get();
        assert userId != null;
        String param = map.get("userId");
        if (!param.equals(userId.toString())) {
            return Response.fail("只能删除自己的评论");
        }
        return commentAop.delComment(map);
    }


    /**
     * 进行点赞
     *
     * @param objectId
     * @param type
     *
     * @return
     */
    @GetMapping("/likeObject/{type}/{objectId}")
    public Response likeObject(@PathVariable("objectId") Long objectId,
                               @PathVariable("type") String type) {
        return likesService.likeObject(objectId, type, UserHolder.get());
    }

    /**
     * 取消点赞
     *
     * @param objectId
     * @param type
     *
     * @return
     */
    @GetMapping("/unlikeObject/{type}/{objectId}")
    public Response unlikeObject(@PathVariable("objectId") Long objectId,
                                 @PathVariable("type") String type) {
        return likesService.unlikeObject(objectId, type, UserHolder.get());
    }


    /**
     * 关注或者取关 书籍 帖子 作者 用户
     *  TODO 消息信箱
     *
     * @return
     */
    @GetMapping("/followObejct")
    public Response followObejct(@RequestBody Map<String, String> map) {
        boolean equals = "true".equals(map.get("do"));
        followOrSubscribeAop.followOrUnfollow(map);
        if (equals) {
            return Response.success("关注成功");
        } else {
            return Response.success("取关成功");
        }
    }


}
