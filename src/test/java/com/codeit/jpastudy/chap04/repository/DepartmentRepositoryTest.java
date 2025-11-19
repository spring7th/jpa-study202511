package com.codeit.jpastudy.chap04.repository;

import com.codeit.jpastudy.chap04.entity.Department;
import com.codeit.jpastudy.chap04.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class DepartmentRepositoryTest {

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Test
    @DisplayName("부서 정보를 조회하면 해당 부서원들도 함께 조회되어야 한다.")
    void testFindDept() {
        // given
        Long id = 2L;

        // when
        Department department = departmentRepository.findById(id).orElseThrow();

        // then
        System.out.println("\n\n\n");
        System.out.println("department = " + department);
        System.out.println(department.getEmployees());
        System.out.println("\n\n\n");


    }

    @Test
    @DisplayName("양방향 연관관계에서 연관 데이터의 수정")
    void testChangeDept() {
        // 1번 사원의 부서를 1 -> 2번 부서로 변경해야 한다.

        // given
        Employee foundEmp = employeeRepository.findById(3L).orElseThrow();
        Department department = departmentRepository.findById(1L).orElseThrow();

        // when
//        department.getEmployees().add(foundEmp); emp 리스트에 아무리 사원 add 해봤자 실제 DB에는 적용이 안됨!
        foundEmp.changeDepartment(department);

        // then
        // update는 트랜잭션이 종료되어야 동작합니다.
        // 이 작업 단위(메서드) 안에서 부서에도 사원의 변경된 정보를 사용할 수 있도록
        // 연관관계 편의 메서드를 통해 리스트에 사원을 직접 추가해서 마치 변경사항이
        // 바로 반영된 것처럼 사용이 가능.
        employeeRepository.save(foundEmp);
        System.out.println("\n\n\n");
        department.getEmployees().forEach(System.out::println);
        System.out.println("\n\n\n");

    }

    @Test
    @DisplayName("N+1 문제 발생 예시")
    void testNPlusOneEx() {
        // given
        List<Department> departments = departmentRepository.findAll();

        // when
        departments.forEach(dept -> {
            System.out.println("========== 사원 리스트 ==========");
            List<Employee> empList = dept.getEmployees();
            System.out.println(empList);

            System.out.println("\n\n");
        });


        // then


    }

    @Test
    @DisplayName("N+1 문제 해결 예시")
    void testNPlusOneSolution() {
        // given
        List<Department> departments = departmentRepository.findAllIncludeEmployees();

        // when
        departments.forEach(dept -> {
            System.out.println("========== 사원 리스트 ==========");
            List<Employee> empList = dept.getEmployees();
            System.out.println(empList);

            System.out.println("\n\n");
        });

        // then


    }

    @Test
    @DisplayName("고아 객체 삭제")
    void orphanRemovalTest() {
        /*
            부모가 삭제되면 그 부모를 참조하던 자식들이 고아가 되면서 자식도 함께 삭제.
            + 부모쪽에서 관계를 끊어버리면 자식을 DB에서 즉시 삭제.
         */

        // given
        Department department = departmentRepository.findById(1L).orElseThrow();

        List<Employee> empList = department.getEmployees();

        // when
        Employee employee = empList.get(0);
        System.out.println("타겟 사원: " + employee);
        empList.remove(employee); // 부모도 자식을 버리고
        employee.setDepartment(null); // 자식도 부모를 더이상 참조하지 않는다.


        // then

    }

    @Test
    @DisplayName("CASCADE TYPE REMOVE test")
    void cascadeRemoveTest() {
        /*
         CascadeType.REMOVE: 부모가 삭제되면 그 부모를 참조하는 자식 또한 함께 삭제됩니다.
         다만 OrphanRemoval과 다른 점은, 연결을 끊는다고 지워주지는 않습니다.
         */

        // given
        Department department = departmentRepository.findById(3L).orElseThrow();

        List<Employee> empList = department.getEmployees();

        // when
        Employee employee = empList.get(0);
        System.out.println("타겟 사원: " + employee);
        empList.remove(employee); // 부모도 자식을 버리고
        employee.setDepartment(null); // 자식도 부모를 더이상 참조하지 않는다.


    }
    
    @Test
    @DisplayName("cascadeType을 ALL로 주었을 때 부모가 데이터 변경의 주체가 된다.")
    void cascadeAllTest() {
        // given
        Department department = departmentRepository.findById(4L).orElseThrow();

        // when
        Employee newEmp = Employee.builder()
                .name("김춘식")
                .department(department)
                .build();

        department.getEmployees().add(newEmp);


        // then

        
    }
    








}











