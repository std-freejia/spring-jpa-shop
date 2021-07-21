package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [n To One 관계]
 * Order 조회
 * Order -> Member  (Many To One)
 * Order -> Delivery (One To One)
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all){
            // order.getMember() 여기까지는 프록시 객체다.
            order.getMember().getName();// Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        } // -> 이런 것으로 성능 최적화 안됨..
        return all;
    }

    /**
     * V2. 엔티티를 DTO로 변환하여 반환 (fetch join 사용X)
     *    단점: 지연로딩으로 쿼리 N번 호출
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){ // List로 반환하면 안됨. Result로 감싸는 것이 좋은 방법.

        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        // Member 조회, Order조회, Delivery조회
        // [ N+1문제 ] 지연로딩은 1차로 영속성 컨텍스트를 확인함. 거기에 없으면 DB를 탐색하므로 N번.
        // ORDER 1개 -> Member N명 -> Delivery N개 ... 쿼리가 어마어마하게 실행되는 문제
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){ // V2, V3는 똑같은데 쿼리만 다르다.
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { // DTO가 파라미터를 받는것은 문제 없다.
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 (영속성 컨텍스트를 탐색 후 DB쿼리 실행)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }

}
