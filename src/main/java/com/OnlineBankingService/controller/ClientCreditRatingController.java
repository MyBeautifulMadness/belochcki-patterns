package com.OnlineBankingService.controller;

import com.OnlineBankingService.entity.dto.ClientCreditDebtResponse;
import com.OnlineBankingService.service.ClientCreditRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientCreditRatingController {

    private final ClientCreditRatingService service;

    @GetMapping("/clientCreditRating")
    public Integer getClientCreditRating(@RequestParam UUID clientId) {
        return service.getClientCreditRating(clientId);
    }

    @GetMapping("/clientDebts")
    public List<ClientCreditDebtResponse> getClientCreditDebts(@RequestParam UUID clientId, @RequestParam(required = false) UUID creditId) {
        return service.getClientCreditDebts(clientId, creditId);
    }
}
