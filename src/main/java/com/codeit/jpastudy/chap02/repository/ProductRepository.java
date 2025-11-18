package com.codeit.jpastudy.chap02.repository;

import com.codeit.jpastudy.chap02.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository를 상속한 후 첫번째 제네릭에 엔터티 클래스 타입,
// 두번째에 PK의 타입을 작성합니다.
// 구현체가 제공이 되고, 구현체가 제공하는 메서드의 매개변수와 리턴 타입을 제네릭으로 맞춰야 하기 때문에.
public interface ProductRepository extends JpaRepository<Product, Long> {

}
