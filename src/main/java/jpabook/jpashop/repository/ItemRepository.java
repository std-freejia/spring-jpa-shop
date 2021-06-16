package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor // EntityManager를 주입해준다.
public class ItemRepository {

    public final EntityManager em; // Spring data JPA가 persistancecontext 안써도 되게 autowired 해준다.

    public void save(Item item){
        if(item.getId() == null){ // 처음에 아이템 객체 생성 시, id가 지정되어 있지 않으니까. (id는 DB에 들어가야 auto increment)
            em.persist(item); // 신규 저장(insert)
        }else{
            em.merge(item); // update와 비슷.
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id); // 객체 타입과 PK
    }

    public List<Item> findAll(){
        return em.createQuery("select i from item i", Item.class)
                .getResultList();
    }
}
