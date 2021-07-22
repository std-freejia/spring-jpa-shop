package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    // 화면에 박혀 있는(의존적인) Dtos 를 위한 레포지토리. 구체적이므로 유지보수하기에 좋다.
    // "조회 전용으로 화면에 맞춰서 쓰고 있구나."
    public List<OrderSimpleQueryDto> findOrderDtos() {
        /** DTO에 매핑하기 위해 new Operation 을 써야 한다.  */
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address) " +
                        " from Order o"+
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
