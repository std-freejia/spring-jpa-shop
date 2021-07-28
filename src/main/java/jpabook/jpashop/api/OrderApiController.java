package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController { // "컬렉션 조회 최적화"

    private final OrderRepository orderRepository; // 엔티티 처리 위함. 중요한 비즈니스 쿼리를 위해.
    private final OrderQueryRepository orderQueryRepository; // 화면이나 api(보통 쿼리와 밀접함)에 최적화되어 데이터를 다룰 때.

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

    /** 컬렉션 패치조인의 단점 : 페이징 불가능.
     일대다 패치조인인 경우, 페이징 하면 안됩니다. (Order와 OrderItem의 관계에서 일대다)
     일대다 컬렉션 패치조인은 딱 하나만 써야 한다.
     */
    @GetMapping("/api/v3/orders") // v3 : 엔티티를 DTO로 변환- 컬렉션 페치조인!, 단점은 중복이 많다는 것.
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(o->new OrderDto(o))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v3.1/orders") // v3 : 엔티티를 DTO로 변환- 컬렉션 페치조인!
    public List<OrderDto> ordersV3_page(
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        /**
         * default_batch_fetch_size: 10  (인쿼리 수행하여 가져올 개수를 설정)
         *
         * Order와 ToOne관계인 member와 Delivery는 fetch join으로 가져온다. -> 쿼리 1개
         * Order와 일대다 연관관계인 OrderItem을 한번에 인쿼리로 수행.
         * 즉, 모든 Order 레코드에 대응되는 OrderItem 레코드들을 전부 가져온다. 모든 OrderDto에 넣을 OrderItem들을 한번에. -> 쿼리 1개
         * OrderItem과 일대다 연관관계인 Item을 한번에 인쿼리로 수행. -> 쿼리 1개
         *
         * 총 3번의 쿼리로 최적화.
         */
        return orders.stream()
                .map(o->new OrderDto(o))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v4/orders") // v4
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
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

}
