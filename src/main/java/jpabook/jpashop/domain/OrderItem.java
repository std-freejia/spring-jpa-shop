package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="order_item")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private int orderPrice; // 주문 가격

    private int count; // 주문 수량

/**     protected OrderItem(){
        // 아래의 생성 메서드(createOrderItem)로만 생성하도록 막아둔다.
        // lombok 애노테이션으로 해결 가능. --> @NoArgsConstructor(access = AccessLevel.PROTECTED)
    }  */

    // == 생성 메서드 == //
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // == 비즈니스 로직 == //
    /** 재고 수량 원복 */
    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }
}
