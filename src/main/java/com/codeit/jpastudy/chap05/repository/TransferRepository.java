package com.codeit.jpastudy.chap05.repository;

import com.codeit.jpastudy.chap05.entity.Account;
import com.codeit.jpastudy.chap05.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
