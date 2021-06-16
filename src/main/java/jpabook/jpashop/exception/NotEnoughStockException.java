package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException { // 메세지와, 원인을 넘겨주기 위해 오버라이드 필요.


    public NotEnoughStockException(){
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
