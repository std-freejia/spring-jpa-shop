package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue // id를 시퀀스값으로 자동생성시킴.
    @Column(name="member_id") // 컬럼명은 테이블명+변수명 으로 정하는 것이 관례.
    private Long id;

    private String name;

    @Embedded // 내장타입이라고 명시
    private Address address;

    @OneToMany(mappedBy = "member")  // Order 테이블에 있는 "member"에 의해 매핑당함. 매핑 기준.
    private List<Order> orders = new ArrayList<>(); // 한 사람이 여러 주문 생성 가능.

    /**
     * 컬렉션은 필드에서 바로 초기화 하는 것이 null문제에서 안전하다!!
     *
     * 컬렉션 자체를 바꾸려고 하면 하이버네이트 내부 매커니즘에 문제가 발생할 수 있다!
     *
     * 하이버네이트 테이블 및 컬럼 설정
     * 카멜 케이스 -> 언더스코어
     */
}
