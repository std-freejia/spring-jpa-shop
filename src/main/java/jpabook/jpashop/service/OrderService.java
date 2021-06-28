package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 전체 디폴트로 readOnly로 만듬.
@RequiredArgsConstructor // final이 있는 필드들에 대해 생성자 만들어준다. (생성자 인젝션 대신 이런 방식 권장)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문 생성
    @Transactional
    public Long order(Long memberId, Long itemId, int count){ // 주문 회원, 주문 상품id, 상품 개수.

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 세팅
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        // 주문 상품 생성 (예제에서는 주문할 때, 상품 종류를 1개만 택하도록 제한했음)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        /**
         * Order 의 필드로 delivery, orderItem 이 있다.
         * cascade.all 이 되어있으니까, 각각의 테이블에 함께 한꺼번에 persist 된다. 별도의 레포지토리에 저장할 필요가 없다.
         * 어디까지 Cascade 해야 할 지를(어디까지 얽혀 놓을지를) 고민해야 한다.
         *
         * 이 경우, Delivery를 참조하는 것은 Order 뿐이다.
         * OrderItem을 참조하는 것은 Order 뿐이다.
         */

        // [중요] 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    // 주문 취소

    @Transactional
    public void cancelOrder(Long orderId){

        Order order = orderRepository.findOne(orderId);
        order.cancel();
        // order의 변경을 감지하여 데이터베이스에 업데이트해줌.
    }

    // 검색 (주문을 검색한다.)
    public List<Order> findOrder(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }
}