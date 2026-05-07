package com.example.drone.service;

import com.example.drone.domain.entity.SysOperationLog;
import com.example.drone.repository.OperationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogService.class);

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    public void record(Long userId, String operation, String target, String ip) {
        try {
            SysOperationLog log = SysOperationLog.builder()
                    .userId(userId).operation(operation).target(target).ip(ip).result("SUCCESS").build();
            operationLogRepository.insert(log);
        } catch (Exception e) {
            logger.warn("操作日志记录失败: {}", e.getMessage());
        }
    }
}
