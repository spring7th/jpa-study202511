package com.codeit.jpastudy.chap03.repository;

import com.codeit.jpastudy.chap03.entity.Student;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    @BeforeEach // 테스트 메서드 이전에 항상 자동 호출되는 메서드
    void insertData() {
        Student s1 = Student.builder()
                .name("쿠로미")
                .city("청양군")
                .major("경제학")
                .build();
        Student s2 = Student.builder()
                .name("춘식이")
                .city("서울시")
                .major("컴퓨터공학")
                .build();
        Student s3 = Student.builder()
                .name("어피치")
                .city("제주도")
                .major("화학공학")
                .build();
        studentRepository.save(s1);
        studentRepository.save(s2);
        studentRepository.save(s3);
    }
    
    @Test
    @DisplayName("이름이 춘식이인 학생의 모든 정보를 조회한다.")
    void findByNameTest() {
        // given
        String name = "춘식이";
    
        // when
        List<Student> list = studentRepository.findByName(name);

        // then
        assertEquals(1, list.size());
        System.out.println("\n\n\n");
        System.out.println(list.get(0));
        System.out.println("\n\n\n");

    }
    
    @Test
    @DisplayName("native sql 테스트하기")
    void nativeSQLTest() {
        // given
        String name = "춘식이";
        String city = "제주도";
    
        // when
        List<Student> students = studentRepository.getStudentByNameOrCity(name, city);

        // then
        System.out.println("\n\n\n");
        students.forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("JPQL로 이름과 도시가 포함된 학생 목록 조회하기")
    void jpqlTest() {
        // given
        String name = "%춘%";
        String city = "제주도";

        // when
        List<Tuple> students = studentRepository.searchByNameWithJPQL(name);


        // then
        System.out.println("\n\n\n");
        for (Tuple tuple : students) {
            String n = tuple.get("name", String.class);
            String c = tuple.get("city", String.class);
            System.out.println("n = " + n);
            System.out.println("c = " + c);
        }
        System.out.println("\n\n\n");


    }
    
    @Test
    @DisplayName("JPQL로 삭제해보기")
    void deleteTestWithJPQL() {
        // given
        String name = "어피치";
        String city = "제주도";
    
        // when
        studentRepository.deleteByNameAndCityWithJPQL(name, city);

    
        // then
        assertEquals(0, studentRepository.findByName(name).size());

        
    }
    

    
    

}








