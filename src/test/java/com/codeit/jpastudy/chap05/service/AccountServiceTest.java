package com.codeit.jpastudy.chap05.service;

import com.codeit.jpastudy.chap05.entity.Account;
import com.codeit.jpastudy.chap05.repository.AccountRepository;
import com.codeit.jpastudy.chap05.repository.TransferRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 계좌 생성
        fromAccount = new Account("111-111", "홍길동", BigDecimal.valueOf(10000));
        toAccount = new Account("222-222", "김철수", BigDecimal.valueOf(5000));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터를 정리하는 용도로 사용하는 메서드
        // 기존 데이터 삭제 (각 테스트가 독립적으로 실행되도록)
        transferRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Step 1-1: 트랜잭션 없이 이체 - 문제 상황 체험")
    void transferWithoutTransaction() {
        // given
        BigDecimal fromBalance = fromAccount.getBalance();
        BigDecimal toBalance = toAccount.getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(2000);

        // when
        assertThrows(RuntimeException.class, () -> {
            accountService.transferWithoutTransaction(
                    fromAccount.getId(),
                    toAccount.getId(),
                    transferAmount
            );
        });

        // then - 데이터 불일치 확인
        Account fromAfter = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account toAfter = accountRepository.findById(toAccount.getId()).orElseThrow();

        System.out.println("=== 트랜잭션 없이 이체 결과 ===");
        System.out.println("출금 계좌 잔액: " + fromAfter.getBalance());
        System.out.println("입금 계좌 잔액: " + toAfter.getBalance());
        System.out.println("초기 출금 계좌 잔액: " + fromBalance);
        System.out.println("초기 입금 계좌 잔액: " + toBalance);
        assertNotEquals(fromBalance, fromAfter.getBalance(),
                "출금 계좌 잔액이 변경되었습니다 (데이터 불일치!)");
        assertEquals(toBalance, toAfter.getBalance(),
                "입금 계좌 잔액은 변경되지 않았습니다");
    }

    @Test
    @DisplayName("Step 1-2: 트랜잭션을 적용한 이체 - 해결")
    void transferWithTransaction() {
        // given
        BigDecimal fromBalance = fromAccount.getBalance();
        BigDecimal toBalance = toAccount.getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(2000);

        // when
        assertThrows(RuntimeException.class, () -> {
            accountService.transfer(
                    fromAccount.getId(),
                    toAccount.getId(),
                    transferAmount
            );
        });

        // then - 데이터 일관성 확인
        Account fromAfter = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account toAfter = accountRepository.findById(toAccount.getId()).orElseThrow();

        System.out.println("=== 트랜잭션 없이 이체 결과 ===");
        System.out.println("출금 계좌 잔액: " + fromAfter.getBalance());
        System.out.println("입금 계좌 잔액: " + toAfter.getBalance());
        System.out.println("초기 출금 계좌 잔액: " + fromBalance);
        System.out.println("초기 입금 계좌 잔액: " + toBalance);

        // 모든 변경사항은 롤백 되었기 때문에 출금 계좌도 잔액은 동일할 것이다.
        assertEquals(fromBalance, fromAfter.getBalance(),
                "출금 계좌 잔액이 원래대로 돌아왔습니다. (데이터 롤백!)");
        assertEquals(toBalance, toAfter.getBalance(),
                "입금 계좌 잔액은 변경되지 않았습니다");

    }

    @Test
    @DisplayName("트랜잭션 옵션 확인하기 (readOnly)")
    void readOnlyTest() {
        Account account = accountService.getAccount(fromAccount.getId());
        Account foundAcc = accountRepository.findById(fromAccount.getId()).orElseThrow();
        System.out.println("서비스로부터 전달받은 계좌: " + account);
        System.out.println("데이터베이스로부터 전달받은 계좌: " + foundAcc);
    }

    @Test
    @DisplayName("Step2: REQUIRED 전파 옵션 테스트")
    void testRequiredPropagation() {
        // given
        BigDecimal transferAmount = BigDecimal.valueOf(500);

        // when
        assertThrows(RuntimeException.class, () -> {
            accountService.transferWithRecord(
                    fromAccount.getId(),
                    toAccount.getId(),
                    transferAmount
            );
        });

        // then
        long transferCount = transferRepository.count();
        assertEquals(0, transferCount);

        // 만약 계좌 이체 기록 과정에서 문제가 발생했다면, 입금과 출금의 작업도 취소 되지 않을까?
        Account fromAfter = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account toAfter = accountRepository.findById(toAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.valueOf(10000).compareTo(fromAfter.getBalance()));
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(toAfter.getBalance()));

    }

    @Test
    @DisplayName("Step2: REQUIRES_NEW 전파 옵션 테스트")
    void testRequiresNewPropagation() {
        // given
        BigDecimal transferAmount = BigDecimal.valueOf(500);

        // when
        assertThrows(RuntimeException.class, () -> {
            accountService.transferWithLog(
                    fromAccount.getId(),
                    toAccount.getId(),
                    transferAmount
            );
        });

        // then
        long transferCount = transferRepository.count();
        assertEquals(1, transferCount);

        // 만약 계좌 이체 기록 과정에서 문제가 발생했다면, 입금과 출금의 작업도 취소 되지 않을까?
        Account fromAfter = accountRepository.findById(fromAccount.getId()).orElseThrow();
        Account toAfter = accountRepository.findById(toAccount.getId()).orElseThrow();

        assertEquals(0, BigDecimal.valueOf(10000).compareTo(fromAfter.getBalance()));
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(toAfter.getBalance()));

    }





}











