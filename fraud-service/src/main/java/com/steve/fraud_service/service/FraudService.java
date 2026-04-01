package com.steve.fraud_service.service;

import com.steve.fraud_service.model.FraudActivity;
import com.steve.fraud_service.repository.FraudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FraudService {

    private final FraudRepository fraudRepository;

    @Autowired
    public FraudService(FraudRepository fraudRepository) {
        this.fraudRepository = fraudRepository;
    }

    // Save a new fraud activity
    public FraudActivity saveFraudActivity(FraudActivity activity) {
        return fraudRepository.save(activity);
    }

    // Get all fraud activities
    public List<FraudActivity> getAllFraudActivities() {
        return fraudRepository.findAll();
    }

    // Get fraud activities for a specific user
    public List<FraudActivity> getFraudActivitiesByUser(String userId) {
        return fraudRepository.findByUserId(userId);
    }

    // Get fraud activities for a specific transaction
    public List<FraudActivity> getFraudActivitiesByTransaction(String transactionId) {
        return fraudRepository.findByTransactionId(transactionId);
    }

    // Optional: Get a single fraud activity by ID
    public Optional<FraudActivity> getFraudActivityById(Long id) {
        return fraudRepository.findById(id);
    }

    // Optional: Delete a fraud activity
    public void deleteFraudActivity(Long id) {
        fraudRepository.deleteById(id);
    }
}