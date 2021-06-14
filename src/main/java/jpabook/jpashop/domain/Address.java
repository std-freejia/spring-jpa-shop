package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

/**
 * 값 타입은 변경 불가하게 설계 해야 한다!
 * 따라서 생성할 때만 값 넣어주기. Setter 제공 안 함.
 */
@Embeddable // JPA의 내장 타입
@Getter
public class Address {

    private String city;

    private String street;

    private String zipcode;

    protected Address(){ // JPA 스펙상 만들어 놓은 것임. 손대지 않음.
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
