package com.artinus.api.controller;

import com.artinus.api.dto.SubscriptionCommandRequest;
import com.artinus.api.dto.SubscriptionHistoryResponse;
import com.artinus.api.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artinus/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping(path = "/{phoneNumber}")
    public List<SubscriptionHistoryResponse> getSubscriptionHistories(@PathVariable String phoneNumber) {
        return subscriptionService.getSubscriptionHistories(phoneNumber);
    }

    @PostMapping(path = "/subscribe")
    public void createSubscription(@Valid @RequestBody SubscriptionCommandRequest request) {
        subscriptionService.createSubscription(request);
    }

    @PostMapping(path = "/cancel")
    public void cancelSubscription(@Valid @RequestBody SubscriptionCommandRequest request) {
        subscriptionService.cancelSubscription(request);
    }
}
