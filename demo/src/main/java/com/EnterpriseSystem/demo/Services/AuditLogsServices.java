package com.EnterpriseSystem.demo.Services;

import com.EnterpriseSystem.demo.Entity.AuditLogs;
import com.EnterpriseSystem.demo.Repository.AuditLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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


    public List<AuditLogs> viewAuditLogs(int page, int size, LocalDate startDate, LocalDate endDate,String order,String sortBy){


        Sort sort= Sort.unsorted();
        if (sortBy!=null && !sortBy.isEmpty()){

            sort=Sort.by(sortBy);
            if ("asc".equalsIgnoreCase(order)){
                sort=sort.ascending();
            }
            else{
                sort=sort.descending();
            }
        }
        else {
            sort=Sort.by(Sort.Direction.DESC,"timestamp");
        }


        if (startDate != null && endDate != null){

            LocalDateTime startDateTime=startDate.atStartOfDay();
            LocalDateTime endDateTime=endDate.atTime(23,59,59);

            return auditLogsRepository.findAllByTimestampBetween(startDateTime, endDateTime, sort);

        }
        if (endDate != null){

            LocalDateTime endDateTime=endDate.atTime(23,59,59);
            return auditLogsRepository.findAllByTimestampBefore(endDateTime,sort);

        }
        if(startDate!=null){

            LocalDateTime startDateTime= startDate.atStartOfDay();

            return auditLogsRepository.findAllByTimestampAfter(startDateTime,sort);

        }

        Page<AuditLogs> paginatedLogs = auditLogsRepository.findAll(Pageable.ofSize(size).withPage(page));
        return paginatedLogs.toList();

    }




}
