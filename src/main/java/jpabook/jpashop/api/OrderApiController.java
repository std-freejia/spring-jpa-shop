package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController { // "컬렉션 조회 최적화"

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders") // v1 : 엔티티를 직접 노출하므로 좋은 방법이 아니다.
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName()); //Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/orders") // v2 : 엔티티를 DTO로 변환
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        return orders.stream()
                .map(o-> new OrderDto(o))
                .collect(Collectors.toList());
    }

    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; /** OrderItem 조차 DTO로 바꿔야한다!! */

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 (영속성 컨텍스트를 탐색 후 DB쿼리 실행)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화

            // orderItems는 엔티티다. orderItems 생성자로 '필요한 필드만' 데이터를 세팅하자.
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{ /** OrderItem 엔티티를 OrderItemDto DTO로 변환 */

        private String itemName; // 상품명
        private int orderPrice; // 주문가격
        private int count; // 주문수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    @GetMapping("/api/v3/orders") // v3 : 엔티티를 DTO로 변환- 페치조인!
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        for (Order order : orders) {
            System.out.println("order = " + order + ", order = " + order.getId());
        }
        List<OrderDto> result = orders.stream()
                .map(o->new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
}
