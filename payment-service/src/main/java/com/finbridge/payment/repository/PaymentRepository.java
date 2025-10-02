package com.finbridge.payment.repository;

import com.finbridge.payment.entity.Payment;
import com.finbridge.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Поиск всех платежей по счёту (входящие + исходящие), сортировка по дате.
     */
    @Query("SELECT p FROM Payment p WHERE p.fromAccountId = :accountId OR p.toAccountId = :accountId ORDER BY p.createdAt DESC")
    List<Payment> findByAccountId(@Param("accountId") Long accountId);

    /**
     * Поиск платежей по статусу.
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Поиск "зависших" платежей, находящихся в одном статусе дольше заданного времени.
     */
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt < :timeLimit")
    List<Payment> findStalePayments(@Param("status") PaymentStatus status,
                                    @Param("timeLimit") LocalDateTime timeLimit);

    /**
     * Подсчёт завершённых платежей с указанного счёта начиная с даты.
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.fromAccountId = :accountId AND p.status = 'COMPLETED' AND p.createdAt >= :startDate")
    Long countCompletedPaymentsByAccountSince(@Param("accountId") Long accountId,
                                              @Param("startDate") LocalDateTime startDate);

    /**
     * Сумма завершённых платежей с указанного счёта за период.
     */
    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.fromAccountId = :accountId AND p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumCompletedPaymentsByAccountBetween(@Param("accountId") Long accountId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Проверка наличия активного (ожидающего или в обработке) платежа между счетами.
     */
    @Query("SELECT p FROM Payment p WHERE p.fromAccountId = :fromAccountId AND p.toAccountId = :toAccountId AND p.status IN ('PENDING','PROCESSING')")
    Optional<Payment> findActivePendingPayment(@Param("fromAccountId") Long fromAccountId,
                                               @Param("toAccountId") Long toAccountId);
}
