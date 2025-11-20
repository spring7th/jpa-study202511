package com.codeit.jpastudy.chap05.service;

import com.codeit.jpastudy.chap05.entity.Account;
import com.codeit.jpastudy.chap05.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
//@Transactional // 클래스의 모든 public 메서드가 트랜잭션
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransferService transferService;

    /**
     * 트랜잭션 없이 이체 (문제 상황 체험용)
     * ❌ 이 메서드는 트랜잭션이 없어서 데이터 불일치가 발생할 수 있음!
     */
    public void transferWithoutTransaction(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepository.findById(fromId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );
        Account to = accountRepository.findById(toId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );

        // 출금
        from.withdraw(amount);
        accountRepository.save(from);

        // 의도적으로 예외 발생 (말도 안되는 예외)
        if (amount.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw new RuntimeException("이체 금액이 너무 큽니다!");
        }

        // 입금
        to.deposit(amount);
        accountRepository.save(to);
    }

//    @Transactional // 이 메서드만 트랜잭션
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepository.findById(fromId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );
        Account to = accountRepository.findById(toId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );

        // 출금
        from.withdraw(amount);
        accountRepository.save(from);

        // 의도적으로 예외 발생 (말도 안되는 예외)
        if (amount.compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw new RuntimeException("이체 금액이 너무 큽니다!");
        }

        // 입금
        to.deposit(amount);
        accountRepository.save(to);
    }

    // JPA에서 SELECT를 제외한 나머지 sql은 flush를 진행하지 않습니다.
    @Transactional(readOnly = true) // 메서드 레벨의 트랜잭션이 우선 적용
    public Account getAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        // readOnly를 true로 세팅하면 더티 체킹 등 INSERT, UPDATE, DELETE 쿼리를 실행하지 않습니다.
        // 예외를 발생시키고 싶으면 DB쪽에 진짜 읽기 전용 설정을 진행해야 합니다.
        account.setBalance(BigDecimal.valueOf(10000000));
        return account;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * REQUIRED 전파 옵션 사용
     * 메서드 내부에서 다른 트랜잭션 메서드 호출 시 REQUIRED로 설정되어 있다면
     * 하나의 트랜잭션 단위로 모두를 묶는다.
     * 입금, 출금, 기록 중 하나라도 문제가 발생하면 전체를 취소.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void transferWithRecord(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepository.findById(fromId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );
        Account to = accountRepository.findById(toId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );

        // 계좌 이체
        from.withdraw(amount);
        to.deposit(amount);

        accountRepository.save(from);
        accountRepository.save(to);

        // 이체 내역을 기록하고 싶다.
        transferService.recordTransfer(from, to, amount);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void transferWithLog(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepository.findById(fromId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );
        Account to = accountRepository.findById(toId).orElseThrow(
                () -> new RuntimeException("출금 계좌를 찾을 수 없습니다.")
        );

        // 계좌 이체
        from.withdraw(amount);
        to.deposit(amount);

        accountRepository.save(from);
        accountRepository.save(to);

        // 이체 내역을 기록하고 싶다.
        transferService.recordTransferWithNew(from, to, amount);

        throw new RuntimeException("이체 처리 중 오류 발생!");
    }


}
















