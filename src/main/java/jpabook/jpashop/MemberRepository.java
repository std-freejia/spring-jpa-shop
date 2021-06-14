package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository { // DAO와 비슷.

    // 스프링이 EntityManager 를 주입해준다. (설정 : application.yml)
    @PersistenceContext  EntityManager em;

    // 저장
    public Long save(Member member){
        em.persist(member);
        return member.getId(); // 저장 후, 리턴 값을 거의 안만듬. id정도는 좋음.
    }

    // 아이디 1개로 조회
    public Member find(Long id){
        return em.find(Member.class, id);
    }

}
