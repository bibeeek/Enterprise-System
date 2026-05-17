package com.EnterpriseSystem.demo.Repository;

import com.EnterpriseSystem.demo.Entity.AuditLogs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLogs, Long> {

    List<AuditLogs> findAllByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<AuditLogs> findAllByTimestampAfter(LocalDateTime startDateTime,Pageable pageable);

    List<AuditLogs> findAllByTimestampBefore(LocalDateTime endDateTime,Pageable pageable);
}