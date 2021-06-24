package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders") // 테이블 이름 매핑
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본생성자로 생성하지 않고, 특정 파라미터를 가지고만 생성하도록 유도.
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

    public void setDelivery(Delivery delivery){ // Delivery의 delivery_id를 FK로.
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    /** 생성 메서드 */   // 주문을 생성한다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem item : orderItems){
            order.addOrderItem(item);
        }

        order.setStatus(OrderStatus.ORDER); // 주문 상태로 초기화.
        order.setOrderDate(LocalDateTime.now()); //주문 시간 정보

        return order;
    }

    /** 주문 취소 */
    public void cancel(){
        // 배송 상태를 확인. 이미 배송됬으면, 취소 불가.
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송된 제품입니다.");
        }
        // 주문 상태를 취소로 변경.
        this.setStatus(OrderStatus.CANCEL);
        // 주문 수량 원복.
        for(OrderItem orderItem : this.orderItems){
            orderItem.cancel();
        }
    }


    // == 조회 로직 ==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice(){
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        /*
                int totalPrice = 0;
                for(OrderItem item : orderItems){
                    totalPrice += (item.getTotalPrice());
                }
                return totalPrice;
         */
    }
}
