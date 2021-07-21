package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(
            Long orderId, String name, LocalDateTime orderDate,
            OrderStatus orderStatus, Address address
    ) { // DTO가 파라미터를 받는것은 문제 없다.
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        /*
        orderId = order.getId();
        name = order.getMember().getName(); // LAZY 초기화 (영속성 컨텍스트를 탐색 후 DB쿼리 실행)
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress(); // LAZY 초기화  */
    }

}
