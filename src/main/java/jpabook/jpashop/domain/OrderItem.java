package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="order_item")
@Getter @Setter
public class OrderItem { // @ManyToOne 은 반드시 LAZY 로 지정!! xToMany 는 디폴트가 LAZY

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    // 주문상품과 상품의 관계 : 다대일. 그리고 단방향.
    // 단방향 관계 : 주문상품은 상품을(item_id)를 참조한다. 그러나, 상품(Item)은 OrderItem을 참조하지 않는다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id") // FK
    private Item item; // 주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id") // FK 연관관계의 주인.
    private Order order; // 하나의 Order는 여러개의 OrderItem을 가진다.

    private int orderPrice; // 주문 가격(총 액수)

    private int count; // 주문한 총 수량
}
