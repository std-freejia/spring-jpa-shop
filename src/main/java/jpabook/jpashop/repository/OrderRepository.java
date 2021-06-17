package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
    // public List<Order> findAll(String )
}
