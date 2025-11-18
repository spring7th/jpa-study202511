package com.codeit.jpastudy.chap02.repository;

import com.codeit.jpastudy.chap02.entity.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // JPA가 사용하는 영속성 컨텍스트라는 공간이 있는데, 그게 트랜잭션 환경에서 동작되도록 설계되어 있기 때문에, 반드시 붙여주어야 한다.
@Rollback(false) // 테스트 결과가 DB에 그대로 반영 -> 롤백 방지
class ProductRepositoryTest {

    @Autowired
    EntityManager em; // JPA에서 엔터티를 다룰 수 있게 해 주는 객체.

    @Test
    void saveTest() {
        Product product = new Product();
        product.setName("신발");
        product.setPrice(90000);
        product.setCategory(Product.Category.FASHION);

        em.persist(product);
    }

    @Test
    void findByIdTest() {
        Product product = em.find(Product.class, 2L);

        System.out.println("product = " + product);
    }

    @Test
    void persistTest() {
        Product product = new Product();
        product.setName("탕수육");
        product.setPrice(18000);
        product.setCategory(Product.Category.FOOD); // 비영속 상태

        em.persist(product); // 영속성 컨텍스트에 엔터티 추가 -> INSERT가 바로 나가지 않는다!

        Product foundProd = em.find(Product.class, 3L);
        System.out.println("foundProd = " + foundProd);
    }

    @Test
    void updateTest() {
        Product product = em.find(Product.class, 3L);

        product.setName("미니짜장면");
        product.setPrice(4000);

        // 조회한 엔터티를 변경 후에 영속성 컨텍스트에 넣어놓으면
        // 변경사항을 감지해서 update를 반영합니다.
        em.persist(product);

        em.flush(); // 지금까지 반영된 영속성 컨텍스트를 DB에 바로 적용.
        em.clear(); // 영속성 컨텍스트 비우기

        Product foundProd = em.find(Product.class, 3L);
        System.out.println("foundProd = " + foundProd);
    }



}







