package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception{
        Book book = em.find(Book.class, 1L);

        // book 을 가져와서 이름 바꾸고 commit 되면, jpa가 update 쿼리 실행하여 db에 반영함. 이를 변경 감지 라고 한다.
        // 변경감지 : dirty checking (기본적인 JPA update 방식)
        book.setName("samplebook");

    }
}
