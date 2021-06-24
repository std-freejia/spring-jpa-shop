package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given - 회원과 상품을 만들어둔다
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2; // 2권 구입.

        //when - 회원1이 책을 2권 주문한다.
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then - 레포지토리에 주문이 들어갔는지 검증한다.
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품 주문시 상대는 ORDER", OrderStatus.ORDER, getOrder.getStatus()); // 메시지, 기댓값, 실제값.
        assertEquals("주문한 상품 종류 수가 정확해야 한다. ", 1, getOrder.getOrderItems().size()); // 수량체크
        assertEquals("주문 가격은 가격*수량 이다.", 10000 * orderCount, getOrder.getTotalPrice()); // 주문가격
        assertEquals("주문 수량 만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11; // 재고 수량보다 많은 주문 수량!

        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다. "); // -- 여기까지 출력되면 안된다는 것을 명시.
    }


    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        // 2개 주문함
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when -- 주문취소!
        orderService.cancelOrder(orderId);

        //then -- 취소 이후에, 재고가 원상복구 되었는지 확인
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL이다. ", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }

    private Book createBook(String name, int orderPrice, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(orderPrice);
        book.setStockQuantity(stockQuantity); // 책이 10권 재고가 있다.
        em.persist(book); // insert
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "남로", "123-123"));
        em.persist(member); // insert
        return member;
    }
}
