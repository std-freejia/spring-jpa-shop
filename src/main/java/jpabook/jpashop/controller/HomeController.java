package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    /**
     * 참고 : html 수정 시, command+shift+F9 로 recompile
     * (devtools 지원 사항)
     */
    @RequestMapping("/")
    public String home(){
        log.info("home controller");
        return "home"; // template 하위에 home.html
    }
}
