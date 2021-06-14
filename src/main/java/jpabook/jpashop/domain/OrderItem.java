package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    private Item item;

    @ManyToOne
    @JoinColumn(name="order_id") // FK를 쓴다.
    private Order order; // 하나의 Order는 여러개의 OrderItem을 가진다.

    private int orderPrice; // 주문 가격(총 액수)

    private int count; // 주문한 총 수량
}
