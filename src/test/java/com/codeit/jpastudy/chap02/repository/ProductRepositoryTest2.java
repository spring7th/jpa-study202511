package com.codeit.jpastudy.chap02.repository;

import com.codeit.jpastudy.chap02.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class ProductRepositoryTest2 {

    @Autowired
    ProductRepository productRepository; // JPA가 구현체를 제공합니다!

    @Test
    void saveTest() {

        Product p = Product.builder()
                .name("떡볶이")
                .price(3000)
                .category(Product.Category.FOOD)
                .build();

        // INSERT 후 저장된 데이터의 객체 반환.
        Product saved = productRepository.save(p);
        System.out.println("saved = " + saved);

    }

    @Test
    @DisplayName("2번 상품을 삭제한다.")
    void deleteTest() {
        // given - 준비 (테스트에 사용하는 변수, 입력값 같은 것들을 준비)
        Long id = 2L;
    
        // when - 실행 (실제 테스트를 수행하는 공간)
        productRepository.deleteById(id);
        
    
        // then - 검증 (when 절에서 실행한 테스트를 확인, 검증하는 단계)
        Optional<Product> byId = productRepository.findById(id);
        System.out.println(byId.isPresent());

    }

    @Test
    @DisplayName("상품 전체 조회를 하면 개수가 2개여야 한다.")
    void selectAllTest() {
        // given


        // when
        List<Product> products = productRepository.findAll();

        // then
        products.forEach(System.out::println);
        Assertions.assertEquals(2, products.size());

    }

    @Test
    @DisplayName("3번 상품의 이름과 가격을 변경해야 한다.")
    void updateTest() {
        // given
        Long id = 3L;
        String newName = "삼겹살";
        int newPrice = 14000;

        // when
        Product product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("조회된 객체 없음!")
        );

        product.setName(newName);
        product.setPrice(newPrice);

        // Spring-Data-JPA는 따로 UPDATE 메서드를 제공하지 않습니다.
        // 조회한 객체의 필드를 변경하면 트랜잭션 종료 후 자동으로 update가 나갑니다. (Dirty Check)
        // 가독성 때문에 save를 작성하는 경우도 있습니다.
        productRepository.save(product);


        // then


    }


    

}














