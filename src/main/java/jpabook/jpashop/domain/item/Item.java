package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속받은 애들을 전부 같은 테이블에 넣음.
@DiscriminatorColumn(name="dtype") // 상속받은 객체를 구분할 컬럼명.
@Getter @Setter
public abstract class Item { // 상속 관계 매핑

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

}
