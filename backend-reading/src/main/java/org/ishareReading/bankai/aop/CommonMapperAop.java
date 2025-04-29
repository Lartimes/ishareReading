package org.ishareReading.bankai.aop;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * 通用mapper控制
 */
@Aspect
@Component
public class CommonMapperAop {

    @Pointcut("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.insert(..)) " +
            "|| execution(* com.baomidou.mybatisplus.extension.service.IService.saveBatch(..)) ||" +
            " execution(* com.baomidou.mybatisplus.extension.service.IService.save(..)) ||" +
            " execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.updateById(..)) ||" +
            " execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.update(..))")
    public void dbOperation() {
    }

    @Before("dbOperation()")
    public void beforeDbOperation(JoinPoint joinPoint) {

        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return;
        }

        if ("insert".equals(methodName) || "saveBatch".equals(methodName) || "save".equals(methodName)) {
            handleInsert(args);
        } else if (methodName.startsWith("update")) {
            handleUpdate(args);
        }
    }

    private void handleInsert(Object[] args) {
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            if (arg instanceof Collection<?> collection) {
                // saveBatch的情况
                for (Object item : collection) {
                    injectCreateAndUpdateFields(item);
                }
            } else {
                // insert单条的情况
                injectCreateAndUpdateFields(arg);
            }
        }
    }

    private void handleUpdate(Object[] args) {
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            if (arg instanceof UpdateWrapper<?> updateWrapper) {
                updateWrapper.set("update_at", System.currentTimeMillis());
            } else if (arg instanceof Collection<?>) {
                for (Object item : (Collection<?>) arg) {
                    injectUpdateFields(item);
                }
            } else {
                injectUpdateFields(arg);
            }
        }
    }

    private void injectCreateAndUpdateFields(Object entity) {
        if (entity == null) return;
        long now = System.currentTimeMillis();
        try {
            Field createTimeField = getField(entity.getClass(), "createAt");
            Field updateTimeField = getField(entity.getClass(), "updateAt");

            if (createTimeField != null) {
                createTimeField.setAccessible(true);
                createTimeField.set(entity, now);
            }
            if (updateTimeField != null) {
                updateTimeField.setAccessible(true);
                updateTimeField.set(entity, now);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject create/update fields for entity: " + entity.getClass().getName(), e);

        }
    }

    private void injectUpdateFields(Object entity) {
        if (entity == null) return;
        long now = System.currentTimeMillis();
        try {
            Field updateTimeField = getField(entity.getClass(), "updateAt");
            if (updateTimeField != null) {
                updateTimeField.setAccessible(true);
                updateTimeField.set(entity, now);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject update fields for entity: " + entity.getClass().getName(), e);
        }
    }

    private Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}

