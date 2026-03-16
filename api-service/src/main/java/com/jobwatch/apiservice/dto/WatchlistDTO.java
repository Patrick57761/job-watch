package com.jobwatch.apiservice.dto;

import com.jobwatch.apiservice.models.Company;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistDTO {
    private Long id;
    private UserDTO user;
    private Company company;
    private LocalDateTime createdAt;
}