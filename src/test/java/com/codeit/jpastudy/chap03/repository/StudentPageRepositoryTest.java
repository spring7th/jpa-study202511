package com.codeit.jpastudy.chap03.repository;

import com.codeit.jpastudy.chap03.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentPageRepositoryTest {

    @Autowired
    StudentPageRepository studentPageRepository;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 147; i++) {
            Student student = Student.builder()
                    .name("김메롱" + i)
                    .city("무슨무슨시" + i)
                    .major("어쩌고저쩌고" + i)
                    .build();
            studentPageRepository.save(student);
        }
    }

    @Test
    @DisplayName("기본적인 페이지 조회 테스트")
    void basicPageTest() {
        // given
        int pageNo = 6; // 사용자가 요청한 페이지 번호
        int amount = 10; // 한 페이지에 보여줄 데이터의 양

        // 페이지 정보 객체 생성 (Pageable)
        // 여기서는 페이지 번호가 zero-based임. 1페이지를 0으로 취급
        Pageable pageable = PageRequest.of(pageNo - 1, amount);

        // when
        Page<Student> studentPage = studentPageRepository.findAll(pageable);

        // 실질적 데이터 꺼내기
        List<Student> studentList = studentPage.getContent();

        // 총 페이지 수
        int totalPages = studentPage.getTotalPages();

        // 총 데이터의 수
        long totalElements = studentPage.getTotalElements();

        // 현재 페이지 기준으로 이전, 다음 페이지가 존재하는지의 여부
        boolean next = studentPage.hasNext();
        boolean prev = studentPage.hasPrevious();

        // then
        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("totalElements = " + totalElements);
        System.out.println("prev = " + prev);
        System.out.println("next = " + next);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("이름검색 + 페이징")
    void testSearchAndPaging() {
        // given
        int pageNo = 3;
        int amount = 6;
        Pageable pageable = PageRequest.of(pageNo - 1, amount);

        // when
        Page<Student> page = studentPageRepository.findByNameContaining("3", pageable);

        List<Student> studentList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        boolean next = page.hasNext();

        /*
            페이징 처리 시에 버튼 알고리즘은 JPA에서 따로 제공되지 않기 때문에
            버튼 배치 알고리즘은 따로 로직을 구현해야 합니다.
            하지만, Page 객체가 제공하는 정보는 버튼 알고리즘에서 유용하게 사용할 수 있습니다.


            한 화면에 페이지 버튼을 5개 배치한다고 가정해 봅시다.

            1. 끝 페이지 계산
            Math.ceil((3 / 5)) * 5 = 5

            2. 시작 페이지 계산
            5 - 5 + 1 = 1

            3. 이전, 다음 버튼 활성화 여부
            이전: 비활성
            다음: 활성

            4. 끝 페이지 보정 (다음 버튼을 활성화 하지 않겠다 라고 판단)
         */


        // then
        System.out.println("\n\n\n");
        System.out.println("totalPages = " + totalPages);
        System.out.println("totalElements = " + totalElements);
        System.out.println("next = " + next);
        studentList.forEach(System.out::println);
        System.out.println("\n\n\n");


    }





}











