package com.OnlineBankingService.dtos;

import com.OnlineBankingService.entities.Theme;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateUserSettingsDtoRequest {
    UUID userId;
    Theme theme;
    List<UUID> hiddenAccountIds;
}
