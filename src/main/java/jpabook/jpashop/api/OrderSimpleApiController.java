package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}
