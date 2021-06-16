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
public class Order { // @XToOne(OneToOne, ManyToOne)관계는 기본이 즉시로딩이므로, 반드시 지연로딩(LAZY)로 설정하기!!

    @Id @GeneratedValue
    @Column(name="order_id") // 칼럼명 매핑
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)   // Member 1 : Order many -> many쪽에 FK를 쓴다! 따라서 연관관계의 주인.
    @JoinColumn(name="member_id") // FK  연관관계의 주인!
    private Member member;

    // 하나의 Order는 여러개의 OrderItem을 가질 수 있다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Delivery와 Order는 1:1 관계.
    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id") //delivery_id를 FK로 쓴다. 연관관계의 주인.
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시각. (Date 자료형으로 치환된다. )

    @Enumerated(EnumType.STRING)  // Enum은 반드시 String 지정하기.
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]


    /** 연관관계 메서드 */

    public void setMember(Member member){ // Member의 member_id를 FK로.
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){ //하나의 Order는 여러개의 OrderItem을 가진다.
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery dilivery){ // Delivery의 delivery_id를 FK로.
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
