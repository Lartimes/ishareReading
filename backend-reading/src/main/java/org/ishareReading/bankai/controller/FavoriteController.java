package org.ishareReading.bankai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.Favorites;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.FavoritesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {


    private final FavoritesService favoritesService;

    public FavoriteController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    /**
     * 收藏或者取消收藏
     */
    @GetMapping("/{type}/{id}")
    public Response doOrUndoFavorite(@PathVariable String type, @PathVariable Long id) {
        Long userId = UserHolder.get();
        Favorites one = favoritesService.getOne(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getType, type)
                .eq(Favorites::getObjectId, id)
                .eq(Favorites::getUserId, userId));
        if (one != null) { //收藏过
            favoritesService.removeById(one.getId());
            return Response.success("取消收藏");
        }
        Favorites entity = new Favorites();
        entity.setUserId(userId);
        entity.setType(type);
        entity.setObjectId(id);
        favoritesService.save(entity);
        return Response.success("收藏成功");
    }

    /**
     * 根据类型获取收藏夹
     */
    @GetMapping("/all")
    public Response getFavorites(@RequestParam String type) {
        LambdaQueryWrapper<Favorites> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            lambdaQueryWrapper = lambdaQueryWrapper.eq(Favorites::getType, type);
        }
        return Response.success(favoritesService.list(lambdaQueryWrapper));
    }


}
