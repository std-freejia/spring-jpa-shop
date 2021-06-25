package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm { // form 에서 가져올 객체와, 데이터베이스에서 다루는 엔티티는 다르게 다루어야 한다! 

    // 값이 비어있으면 오류가 나도록 설정. validation!
    @NotEmpty(message="회원 이름은 필수 입니다")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
