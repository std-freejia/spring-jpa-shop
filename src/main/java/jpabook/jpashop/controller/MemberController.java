package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createFrom(Model model){
        // 빈 껍데기 MemberForm 객체를 가지고 화면으로 이동한다
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new") // form 전송을 받는다.
    public String create(@Valid MemberForm form, BindingResult result){
        /**
         * @Valid 애노테이션: 스프링에서 validation 작업 하라는 의미
         * BindingResult : validation 이후 발생한 에러는 BindingResult 가 에러를 처리해준다.
          */
        if(result.hasErrors()){ // 에러가 있으면, 회원가입 폼 페이지로 다시 보낸다.
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member); // 저장

        return "redirect:/"; // home 으로 보냄
    }

    @GetMapping("/members")
    public String list(Model model){
        /**
         * 화면에 뿌리는데, 엔티티를 손 대지 않아도 되는 경우에만 Member 엔티티를 그대로 쓴다.
         * 비즈니스 로직을 위해 설계된 엔티티는 순수하게 형태를 유지하도록 하기.
         * 비즈니스 로직이 아닌 화면에서 넘어오고 화면으로 넘기는 데이터는 dto 를 쓰는편이 좋다.
         * [중요]
         * api를 만들 때는 엔티티를 절대 외부로 반환하면 안된다!
         */
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }


}
