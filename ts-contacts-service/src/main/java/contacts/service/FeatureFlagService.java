package contacts.service;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.OpenFeatureAPI;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FeatureFlagService {
    
    private Client client;
    private boolean isInitialized = false;
    
    @PostConstruct
    public void initialize() {
        try {
            // Get a named client for contacts-service
            this.client = OpenFeatureAPI.getInstance().getClient("contacts-service");
            System.out.println("[TrainTicket][Contacts][FeatureFlagService] Initialized successfully");
            this.isInitialized = true;
        } catch (Exception e) {
            System.err.println("[TrainTicket][Contacts][FeatureFlagService] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isEnabled(String flagName) {
        // Simple fix: If not initialized yet, return false (safe default)
        if (!this.isInitialized || this.client == null) {
            System.out.println("[TrainTicket][Contacts][FeatureFlagService] Client not ready yet, using safe default (false) for flag: " + flagName);
            return false;
        }
        
        try {
            FlagEvaluationDetails<Boolean> details = client.getBooleanDetails(flagName, false);
            System.out.println(String.format(
                "[TrainTicket][Contacts][FeatureFlagService] Flag %s: value=%s, reason=%s", 
                flagName, 
                details.getValue(), 
                details.getReason() != null ? details.getReason() : "N/A"
            ));
            
            // Check for ERROR reason
            if ("ERROR".equals(details.getReason())) {
                System.err.println("[TrainTicket][Contacts][FeatureFlagService] Provider error for flag " + flagName);
                return false;
            }
            
            return Boolean.TRUE.equals(details.getValue());
            
        } catch (Exception e) {
            System.err.println("[TrainTicket][Contacts][FeatureFlagService] Error getting flag " + flagName + ": " + e.getMessage());
            return false;
        }
    }
}
