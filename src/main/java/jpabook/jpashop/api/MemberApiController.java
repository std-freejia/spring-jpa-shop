package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // 데이터를 JSON으로 보낼 때.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members") // 회원조회
    public List<Member> membersV1(){
        // 엔티티를 노출하면, 엔티티 간의 연관관계 까지 물려서 모두 노출시키게 된다.
        return memberService.findMembers();
    }

    @GetMapping("api/v2/members")
    public Result memberV2(){ /** 회원조회 : List<Member> 를 List<MemberDto> 로 바꿔준다. */
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m->new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @PostMapping("/api/v1/members") //회원등록 api
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //json -> Member
        // @Valid : 객체 내에서 valid 할 것들(@NotEmpty 같은..) 체크해줌
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMember2(@RequestBody @Valid CreateMemberRequest request)
    {
        /** CreateMemberRequest 에 담으니까 엔티티와는 다르게 필드를 제한시킬 수 있다. */
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id); /** DTO에 담아서 보낸다 */
    }

    @PutMapping("/api/v2/members/{id}") /** 회원 수정을 위해 요청DTO, 응답DTO를 별도로 정의한다.*/
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        /** 수정 시, 가급적 변경감지 활용하기. */
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor // 모든 파라미터를 넘기는 생성자
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }


    @Data
    static class CreateMemberRequest{
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
