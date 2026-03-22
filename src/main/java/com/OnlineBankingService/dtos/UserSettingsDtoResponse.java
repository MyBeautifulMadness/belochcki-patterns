package com.OnlineBankingService.dtos;

import com.OnlineBankingService.entities.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDtoResponse {

    private UUID userId;

    private Theme theme;

    private List<UUID> hiddenAccountIds;
}