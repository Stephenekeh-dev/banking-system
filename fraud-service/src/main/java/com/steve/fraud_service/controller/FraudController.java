package com.steve.fraud_service.controller;

import com.steve.fraud_service.model.FraudActivity;
import com.steve.fraud_service.service.FraudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud")
public class FraudController {

    private final FraudService fraudService;

    @Autowired
    public FraudController(FraudService fraudService) {
        this.fraudService = fraudService;
    }

    // =====================
    // Create a new fraud activity
    // =====================
    @PostMapping
    public ResponseEntity<FraudActivity> createFraudActivity(@RequestBody FraudActivity activity) {
        FraudActivity savedActivity = fraudService.saveFraudActivity(activity);
        return ResponseEntity.ok(savedActivity);
    }

    // =====================
    // Get all fraud activities
    // =====================
    @GetMapping
    public ResponseEntity<List<FraudActivity>> getAllFraudActivities() {
        List<FraudActivity> activities = fraudService.getAllFraudActivities();
        return ResponseEntity.ok(activities);
    }

    // =====================
    // Get fraud activities by user ID
    // =====================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FraudActivity>> getFraudByUser(@PathVariable String userId) {
        List<FraudActivity> activities = fraudService.getFraudActivitiesByUser(userId);
        return ResponseEntity.ok(activities);
    }

    // =====================
    // Get fraud activities by transaction ID
    // =====================
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<FraudActivity>> getFraudByTransaction(@PathVariable String transactionId) {
        List<FraudActivity> activities = fraudService.getFraudActivitiesByTransaction(transactionId);
        return ResponseEntity.ok(activities);
    }

    // =====================
    // Optional: Delete a fraud activity
    // =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFraudActivity(@PathVariable Long id) {
        fraudService.deleteFraudActivity(id);
        return ResponseEntity.noContent().build();
    }
}