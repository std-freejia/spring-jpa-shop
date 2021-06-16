package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // 컴포넌트 스캔의 대상이 됨 -> 스프링 빈으로 관리됨
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em; // @RequiredArgsConstructor

    /** Spring data JPA를 쓴다면, @PersistenceContext 애노테이션 대신, 클래스에 @RequiredArgsConstructor 사용해서 주입 가능.
    @PersistenceContext //JPA 표준 어노테이션으로 JPA 엔티티 매니저를 주입받을 수 있다.
    private EntityManager em;
     */

    // 회원 저장
    public void save(Member member){
        em.persist(member); // insert
    }

    // 회원 조회
    public Member findOne(Long id){
        // find(반환 타입, PK)
        return em.find(Member.class, id); // 단건 조회
    }

    // 회원 목록
    public List<Member> findAll(){
        // JPA쿼리 작성하기 (JPQL 작성, 반환 타입 ) Member 엔티티를 조회한다. (테이블 기준이 아니라, 엔티티 기준으로 조회한다)
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 특정 이름 회원
    public List<Member> findByName (String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
