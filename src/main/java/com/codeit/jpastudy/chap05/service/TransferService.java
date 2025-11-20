package com.codeit.jpastudy.chap05.service;

import com.codeit.jpastudy.chap05.entity.Account;
import com.codeit.jpastudy.chap05.entity.Transfer;
import com.codeit.jpastudy.chap05.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void recordTransfer(Account from, Account to, BigDecimal amount) {
        Transfer transfer = new Transfer(from, to, amount);

        if (amount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new RuntimeException("recordTransfer 과정에서 문제가 발생!");
        }

        transferRepository.save(transfer);
    }

    /**
     * 항상 새로운 트랜잭션을 시작
     * 기존 트랜잭션과 독립적으로 실행됨
     * 에러가 발생해도 이미 커밋된 작업은 롤백되지 않음.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordTransferWithNew(Account from, Account to, BigDecimal amount) {
        Transfer transfer = new Transfer(from, to, amount);
        transferRepository.save(transfer);
    }

}











