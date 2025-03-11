package com.jpabook.jpashop.domain.item;


import com.jpabook.jpashop.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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


}
