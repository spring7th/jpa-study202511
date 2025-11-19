package com.codeit.jpastudy.chap04.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@ToString(exclude = "department")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_emp")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long id;

    @Column(name = "emp_name", nullable = false)
    private String name;

    // EAGER: 해당 필드를 사용 하든 말든간에 무조건 조인을 수행.
    // LAZY: 필요한 경우에만 데이터를 가져온다. -> 실무
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id") // FK 컬럼명 (연관 테이블의 컬럼명과 일치하게)
    private Department department;


    // 연관관계 편의 메서드 (양방향에서 연관된 필드가 수정된 경우
    // 실제 테이블과의 데이터를 맞춰 주기 위한 메서드)
    public void changeDepartment(Department department) {
        this.department = department;
        // 사원의 부서가 변경돼? -> 부서쪽 사원 리스트에도 이 객체를 추가해 주자.
        department.getEmployees().add(this);
    }


}








