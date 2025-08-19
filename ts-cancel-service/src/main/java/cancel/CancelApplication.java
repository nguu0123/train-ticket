package cancel;

import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.contrib.providers.flagd.FlagdProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
// No Swagger imports needed - springdoc works automatically!

import javax.annotation.PostConstruct;

/**
 * @author fdse
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@IntegrationComponentScan
// No Swagger annotations needed - springdoc auto-configures!
@EnableDiscoveryClient
public class CancelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CancelApplication.class, args);
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @PostConstruct
    public void initializeFeatureFlags() {
        try {
            // Configure FlagdProvider with explicit settings like Python version - no env vars
            // Constructor: FlagdProvider(host, port, tls, socketPath)
            // Note: Java version doesn't have explicit cache disable like Python's CacheType.DISABLED
            FlagdProvider provider = new FlagdProvider("flagd", 8013, false, null);
                
            OpenFeatureAPI.getInstance().setProvider(provider);
            
            System.out.println("[TrainTicket][Cancel][Feature Flags] Connected to flagd at flagd:8013");
            
        } catch (Exception e) {
            System.err.println("[TrainTicket][Cancel][Feature Flags] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
