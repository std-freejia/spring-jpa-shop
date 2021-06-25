package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")  // 등록 form 화면 보여주기
    public String createForm(Model model){
        model.addAttribute("form", new BookForm()); // 빈 객체를 가지고 화면으로 간다.
        return "items/createItemForm";
    }

    @PostMapping("/items/new") // form 데이터를 저장
    public String create(BookForm form){
        /**
         * 생성자 메서드로 생성하는 것이 더 좋은 설계이다. (setter의 사용을 지양하자!)
         */
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/"; // 책 목록으로 이동
    }

    @GetMapping("/items") // 책 목록
    public String list(Model model){
        List<Item> items = itemService.findItem();
        model.addAttribute("items", items);
        return "items/itemList";
    }

}
