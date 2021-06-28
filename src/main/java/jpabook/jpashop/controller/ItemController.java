package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * [매우 중요] 상품 수정
     */
    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form){
        /**
         * id를 조작하여 넘기는 보안 이슈가 있다. 주의하기
         */
        Book book = new Book();
        book.setId(form.getId());

        /** [매 우 중 요 ]
         * 준영속 엔티티
         * 이미 id가 있는 객체 (준영속상태의 객체라고 한다.) jpa가 식별할 수 있는 id를 이미 가졌다는 의미.
         * 이미 db에 저장되었다가 다시 꺼낸 정보라는 뜻.
         * 그래서 jpa가 관리를 안한다!
         *
         * 준영속 엔티티 수정하는 2가지 방법
         * 1) 변경감지기능 사용
         * 2) 병합(merge) 사용
         */

        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

}
