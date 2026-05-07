package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BatchStatusRequest {
    @NotEmpty(message = "ID列表不能为空")
    private List<Long> ids;
    @NotBlank(message = "状态不能为空")
    private String status;
}
