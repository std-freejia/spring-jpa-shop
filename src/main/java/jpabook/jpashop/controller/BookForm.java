package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    // 상품 수정에 쓰일 id가 필요하다.

    private Long id;

    private String name;
    private int price;
    private int stockQuantity; // 여기까지 책의 공통 속성

    private String author;
    private String isbn;
}
