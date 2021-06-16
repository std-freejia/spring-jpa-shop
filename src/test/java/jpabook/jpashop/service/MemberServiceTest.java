package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/** 요구사항
 * 회원 가입 성공하기
 * 회원 가입 할 때 같은 이름이 있으면 예외 발생
 */

@RunWith(SpringRunner.class) // 스프링이랑 통합해서 테스트
@SpringBootTest // 스프링이랑 통합해서 테스트 -> 이 어노테이션이 있어야 @Autowired가 가능.
@Transactional // 테스트에서는, 수행 후 롤백하는게 디폴트! 테스트는 반복해서 수행되어야 하기 때문임.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    // 롤백 안하고 커밋하고 싶다면,
    @Autowired EntityManager em;

    @Test
    @Rollback(value = false) // 롤백 안하는(커밋하는) 설정. 진짜 DB에 들어가는지 확인하고 싶을 때.
    public void 회원가입() throws Exception{
        //given (주어진 데이터)
        Member member = new Member();
        member.setName("jiaryu");

        //when (동작)
        Long savedId = memberService.join(member);

        //then (결과 확인)
        em.flush(); //커밋.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class) // 이러한 예외 터지면 성공하는 테스트.
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when - 같은거 두번 넣으니까 예외가 발생해야 한다!
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다!

        //then
        fail("예외가 발생해야 한다");

    }
}