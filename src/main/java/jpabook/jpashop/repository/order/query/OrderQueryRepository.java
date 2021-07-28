package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() { // v4
        List<OrderQueryDto> result = findOrders();

        /** OrderId를 이용해서 OrderItem들을 가져오자.   findOrderItems(o.getOrderId()) */
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems); // orderItems의 데이터를 채운다. Query N+1문제.
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        // v5: 컬렉션 최적화. OrderItems를 IN절로 한번에 가져오기. -> 쿼리가 2번밖에 안나감!

        /** ToOne 관계 모두 조회 */
        List<OrderQueryDto> result = findOrders();

        /** 주문을 다 가져온다. orderId 리스트를 만든다. */
        List<Long> orderIds = toOrderIds(result);

        /** orderId 리스트를 파라미터 in절로 넣는다. -> 쿼리 1번으로 OrderItem 리스트를 가져옴.  */
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new " +
                        " jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name," +
                        "oi.orderPrice, oi.count)" +
                        " from OrderItem oi " +
                        " join oi.item i"+
                        " where oi.order.id in :orderIds",
                OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        /** orderItems 최적화 : orderId 기준으로 map으로 변환 */
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        /** [핵심] OrderItems에 넣어주기. 즉, 메모리에서 Order와 OrderItem을 매칭한다.  */
        result.forEach(o-> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) { // orderItem 컬렉션을 MAP 한방에 조회
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId){ /** 1:N 관계인 orderItems 조회 */
        return em.createQuery(
                "select " +
                        " new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi " +
                        " join oi.item i"+
                        " where oi.order.id = : orderId",
                OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() { /** ToOne관계 가져오기 : 조인해도 데이터 row수가 증가하지 않음. */
        return em.createQuery(
                "select " +
                        " new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }


}
