package com.crazy.handler;

import com.crazy.constant.MessageConstant;
import com.crazy.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Sql异常处理
     * @param e
     * @return
     */
    @ExceptionHandler
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        String message = e.getMessage();
        log.debug(message);
        if(message.contains("Duplicate entry")) {
            String[] splits = message.split(" "); // 重复的 entry
            String errorMsg = splits[2] + MessageConstant.ALREADY_EXISTS;
            return Result.error(errorMsg);
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
