package com.crazy.aspect;

import com.crazy.annotation.AutoFill;
import com.crazy.constant.AutoFillConstant;
import com.crazy.context.BaseContext;
import com.crazy.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Component
@Aspect
public class AutoFillAspect {

    // 可以直接绑定传递使用annotation参数
    @Pointcut("execution(* com.crazy.mapper.*.*(..)) && @annotation(autoFill)")
    public void autoFillPointCut(AutoFill autoFill) {}

    // JointPoint参数最好放在第一个参数，其他位置可能会绑定出错，导致无法切入
    @Before(value = "autoFillPointCut(autoFill)", argNames = "jp,autoFill")
    public void autoFill(JoinPoint jp, AutoFill autoFill) {
        log.info("Starts auto-filling public fields...");

        OperationType type = autoFill.value();
        Object[] args = jp.getArgs();
        if(args == null || args.length < 1)
            return;
        Object entity = args[0];

        LocalDateTime ldt = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if(type == OperationType.INSERT) {
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, ldt);
                setUpdateTime.invoke(entity, ldt);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            }catch (NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException e) {
                log.error(e.getMessage());
            }
        } else if (type == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, ldt);
                setUpdateUser.invoke(entity, currentId);
            }catch (NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException e) {
                log.error(e.getMessage());
            }
        }
    }
}
