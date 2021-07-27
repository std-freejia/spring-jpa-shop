package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    // 주문 저장
    public void save(Order order){
        em.persist(order);
    }

    // 주문 단건 조회
    public Order findOne(Long id){
        return em.find(Order.class, id); // 객체 타입과 PK
    }

    // 주문 내역 검색 (동적 쿼리 필요)
    public List<Order> findAll(OrderSearch orderSearch){ // member와 조인한 Order를 조회한다
        // jpql 을 '문자열'로 생성하는 것은 버그 확률도 많고 사람이 할 짓이 아님. Criteria 도 안씀.
        // 동적 쿼리르 위한 가장 좋은 해결책은 QueryDSL 이다.
        return null;
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() { // @GetMapping("/api/v3/simple-orders")
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    /** [fetch join]
     * order 가져올 때, member와 delevery 테이블도 조회하고 싶음
     * 프록시객체 안쓰고 필요한 것 다 가져옴. fetch개념은 JPA에만 있는 문법.
     * 칼럼 하나하나 명시하지 않았기 때문에, 재사용성이 좋다.
     * */
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
                ).setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> findAllWithItem() { /** V3. 페치조인으로 컬렉션 조회 최적화! 단점은 페이징 불가. */
        return em.createQuery(
                "select distinct o from Order o"+
                        " join fetch o.member m"+
                        " join fetch o.delivery d"+
                        " join fetch o.orderItems oi"+
                        " join fetch oi.item i", Order.class)
                .getResultList(); // 참고로 이렇게 쿼리가 길어지거나 복잡해지면 queryDSL쓰기!
    }

    // V4. JPA에서 DTO를 바로조회.
    // 그치만 필드 몇개 덜 가져온다고 해도 V3보다 성능이 엄청 차이나진 않는다. 경우에 따라 V3, V4 방식 간에 성능테스트 필요!
    /*
    public List<OrderSimpleQueryDto> findOrderDtos() {
        // DTO에 매핑하기 위해 new Operation 을 써야 한다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(" +
                        "o.id, m.name, o.orderDate, o.status, d.address) " +
                        " from Order o"+
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
    */
}
