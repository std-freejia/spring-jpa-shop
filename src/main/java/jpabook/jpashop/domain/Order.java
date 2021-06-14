package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders") // 테이블 이름 매핑
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name="order_id") // 칼럼명 매핑
    private Long id;

    @ManyToOne  // Member 1 : Order many
    @JoinColumn(name="member_id") // FK  연관관계의 주인!
    private Member member;

    @OneToMany(mappedBy = "order") // 하나의 Order는 여러개의 OrderItem을 가질 수 있다.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="delivery_id") //delivery_id를 FK로 쓴다. 연관관계의 주인.
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시각. (Date 자료형으로 치환된다. )

    @Enumerated(EnumType.STRING)  // Enum은 반드시 String 지정하기.
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

}
