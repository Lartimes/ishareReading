package org.ishareReading.bankai.exception;



import org.ishareReading.bankai.response.Response;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.StringJoiner;


@RestController
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public Response ex(Exception e){
        e.printStackTrace();
        String msg = ObjectUtils.isEmpty(e.getMessage()) ? e.toString() : e.getMessage();
        return Response.fail(msg);
    }

    @ExceptionHandler(BaseException.class)
    public Response baseEX(BaseException e){
        return Response.fail(e.getMsg());
    }


    @ExceptionHandler(BusinessException.class)
    public Response businessEX(BusinessException e){
        return Response.fail(e.code,e.getMessage());
    }


    // 数据校验异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response exception(MethodArgumentNotValidException e) {
        // e.getBindingResult()：获取BindingResult
        BindingResult bindingResult = e.getBindingResult();
        // 收集数据校验失败后的信息
        StringJoiner joiner = new StringJoiner(",");

        bindingResult.getFieldErrors().stream().forEach((fieldError) -> {
            joiner.add(fieldError.getDefaultMessage());

        });
        return Response.fail(joiner.toString());
    }
}
