package jpabook.jpashop.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateItemDto {  // 업데이트 시, 파라미터 개수가 많을 경우, DTO 를 따로 정의해서 처리하는 것이 나은 설계다.

    String name;
    int price;
    int stockQuantity;
}
