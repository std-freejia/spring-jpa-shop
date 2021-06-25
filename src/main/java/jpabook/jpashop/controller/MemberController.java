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

}
