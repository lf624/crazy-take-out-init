package com.crazy.exception;

public class ShoppingCartBusinessException extends BaseException{
    public ShoppingCartBusinessException() {
    }

    public ShoppingCartBusinessException(String msg) {
        super(msg);
    }
}
