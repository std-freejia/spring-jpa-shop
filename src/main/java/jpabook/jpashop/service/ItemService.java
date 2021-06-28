package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true) // select 만 한다고 명시.
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional // 클래스가 readOnly니까, 이 메서드는 저장을 위해 (readOnly = false) 로 명시 필요.
    public void saveItem(Item item){
        itemRepository.save(item);
    }


    /** 매우 중요 [변경 감지 기능] */
    @Transactional
    public Item updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId);
        /**
         * findItem 은 레포지토리에서 꺼내 왔으니까 영속 상태다!
         * @Transactional 때문에 JPA는 findItem의 변경 사항을 커밋한다. 커밋 후에 flush 수행.
         * 영속 상태 이므로 변경이 감지되어 udpate sql이 실행된다.
         * itemRepository.save(); 할 필요가 없다!
         */
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());

        /**
         * setter 이용 지양하기.
         * changeField(price, name, stockQuantity) 과 같이 의미있는 메소드를 따로 만들자.
         * 나중에 유지보수할 때 setter가 아무데나 무더기로 깔려있으면 디버깅이 어렵다.
         */
        return findItem;
    }

    public List<Item> findItem(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
