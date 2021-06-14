package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 실무에서는 다대다 관계 가급적 쓰지 말기. 여기서는 일부러 복잡한 예시를 만든 것임. (카테고리와 아이템.)
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    // 카레고리 계층구조.
    @ManyToOne // 내 부모는 한 명.
    @JoinColumn(name="parent_id")
    private Category parent; // 내 부모는 내 타입이니까 Category.

    // 내 자식은 여러개 가능. 여러개니까 List에 넣음.
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

}
