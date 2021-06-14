package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery { // Order와 1:1 관계. 무엇을 중심으로 조회하는지를 생각해서 FK를 정하면 된다.

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery") //delivery라는 칼럼과 매핑.
    private Order order;

    @Embedded
    private Address address;

     // EnumType은 반드시 String으로 써야 한다!! (왜냐하면 ORDINARL 타입은 '선언 순서대로' 정수값이 주어지므로 혼란 생길 수 있음.)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // READY, COMP
}
