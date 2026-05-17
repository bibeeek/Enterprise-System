package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Repository.AuditLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuditLogsServices {

    private final AuditLogsRepository auditLogsRepository;


    public List<AuditLogs> viewAuditLogs(int page,
                                         int size,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         String order,
                                         String sortBy) {

        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)) {

            throw new RuntimeException(
                    "Start date cannot be after end date"
            );
        }

        Sort sort;

        if (sortBy != null && !sortBy.isBlank()) {

            sort = "asc".equalsIgnoreCase(order)
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

        } else {

            sort = Sort.by("timestamp").ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        // BETWEEN DATES

        if (startDate != null && endDate != null) {

            return auditLogsRepository
                    .findAllByTimestampBetween(
                            startDate.atStartOfDay(),
                            endDate.atTime(23, 59, 59),
                            pageable
                    );

        }

        // ONLY END DATE

        if (endDate != null) {

            return auditLogsRepository
                    .findAllByTimestampBefore(
                            endDate.atTime(23, 59, 59),
                            pageable
                    );
        }

        // ONLY START DATE

        if (startDate != null) {

            return auditLogsRepository
                    .findAllByTimestampAfter(
                            startDate.atStartOfDay(),
                            pageable
                    );
        }

        // NORMAL PAGINATION

        return auditLogsRepository.findAll(pageable).toList();
    }


}
