package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)  // 기본적으로 데이터 변경시 @Transactional 명시. (참고. 스프링이 제공하는 Transactional 쓰기)
@RequiredArgsConstructor// final이 있는 필드만 가지고 생성자 만들어준다. (생성자 인젝션 대신 이런 방식 권장)
public class MemberService {

    // 변경할 일 없으니까 final 하기.(컴파일 시점에 체크해줄 수 있기 때문이다)
    private final MemberRepository memberRepository;

    /** 생성자 인젝션 쓰기!! 근데 클래스에 생성자 1개면, @Autowired 애노테이션 생략 가능.
    @Autowired
    public MemberService(MemberRepository memberRepository){ // 멤버 서비스는 멤버 리포지토리가 필요해!
        this.memberRepository = memberRepository;
    }
    */

    // 회원 가입
    @Transactional(readOnly = false) // 이 함수만 false!
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        // 두 사람이 동시에 검증 통과 받을 수 있으니까, DB의 name 컬럼에는 반드시 unique 제약 조건 걸기!
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    // @Transactional(readOnly = true) // 최적화 방법. 가급적 조회는 readOnly =true 넣기.
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    // 아이디로 단건 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) { /** 회원 수정 API 에서 사용. */
        Member member = memberRepository.findOne(id); // member 가 영속상태로 올라온다.
        member.setName(name);
        /** update() 가 종료되면, @Transactional 에 의해 트렌젝션 커밋이 되고 JPA가 플러시. */
        /** update 후에 즉시 조회하지 말자. 수정과 조회를 분리하기 위함. */
    }
}
