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
            Item mergeItem = em.merge(item); // update와 비슷. ItemService.updateItem() 와 여기의 merge()는 완전 똑같다.

            /** 병합: 기존에 있는 엔티티 대상.
             *
             * 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 엔티티를 조회한다.
             * 조회한 영속엔티티에 준영속엔티티의 데이터를 덮어씌운다.
             * 그리고 덮어씌운 내용이 반영된 영속엔티티를 반환해준다!
             *
             * 파라미터로 넘긴 item는 영속 엔티티가 아니다.
             * merge()가 반환해준 mergeItem가 영속 엔티티다.
             */
        }
    }

    /** [ 병합 주의사항 ]
     * 변경 감지 기능 : 원하는 속성만 선택해서 변경할 수 있다.
     * 병합 : 모든 속성이 변경된다.
     *
     * 즉, 병합 시 값이 없으면 null 로 업데이트할 위험이 있다. (병합은 모든 필드를 교체하기 때문에 위험하다.)
     * 업데이트 할 필드만 작성하여 '변경 감지 기능'을 사용하고, merge 사용은 지양하자.
     */

    public Item findOne(Long id){
        return em.find(Item.class, id); // 객체 타입과 PK
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
