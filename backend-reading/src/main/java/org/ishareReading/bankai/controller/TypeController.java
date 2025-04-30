package org.ishareReading.bankai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ishareReading.bankai.model.Types;
import org.ishareReading.bankai.response.Response;
import org.ishareReading.bankai.service.TypesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类型相关的，只是CRUD，不过四种类型都放到一个表里
 * 还是做个controller吧
 * 当然，aop也可以，不过显得有点多余了，类更多了
 * ======================================
 * 类型：见解、书籍、评论、帖子
 */
@RestController
@RequestMapping("/type")
public class TypeController {

//    opinion <<<<
//    comment<<<
//    ==================这俩没必要放
//    book
//    post

    private final TypesService typesService;

    public TypeController(TypesService typesService) {
        this.typesService = typesService;
    }

    /**
     * 过去所有类型
     * @return
     */
    @GetMapping("/list")
    public Response getTypes() {
        return Response.success(typesService.list());
    }

    /**
     * 根据一大类Type 获取types
     */
    public Response getTypeByType(String type) {
        return Response.success(typesService.list(new LambdaQueryWrapper<Types>()
                .eq(Types::getType, type)));
    }
//    还要做什么呢？

}
