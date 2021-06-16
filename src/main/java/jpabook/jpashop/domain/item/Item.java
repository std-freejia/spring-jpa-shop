package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속받은 애들을 전부 같은 테이블에 넣음.
@DiscriminatorColumn(name="dtype") // 상속받은 객체를 구분할 컬럼명.(album / book/ movie)
@Getter @Setter
public abstract class Item { // 상속 관계 매핑

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    // 다대다
    @ManyToMany(mappedBy = "items") //Category 엔티티의 items 칼럼에 매핑.
    private List<Category> categories = new ArrayList<>();

    // == 비즈니스 로직 ==// stockQuantity 데이터를 가지고 있는 클래스에서 로직까지 가지고 있으면 더 좋음
    // @Setter 를 열어두기 보다는, 비즈니스 로직을 통해 변경하는 편이 더 좋은 구조다.

    /** 재고 수량 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * 재고 줄이기 (0보다 작게 하지 말기)
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
