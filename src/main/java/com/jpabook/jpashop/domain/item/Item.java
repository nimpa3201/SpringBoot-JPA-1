package com.jpabook.jpashop.domain.item;


import com.jpabook.jpashop.Exception.NotEnoughStockException;
import com.jpabook.jpashop.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;


@BatchSize(size=100) // ✅ ToMany 관계(N+1)를 줄이기 위해 Hibernate가 Item을 IN 절로 일괄 조회함
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //부모 클래스와 자식 클래스를 하나의 테이블에 저장하는 방식
@DiscriminatorColumn(name = "dtype") //구분 컬럼명 설정 , 기본값 dtype
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name= "item_id")
    private Long id;

    private String name;

    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    // 비지니스 로직

    /**
     * stock 증가
     */

    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity -quantity;
        if (restStock<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;


    }


}
