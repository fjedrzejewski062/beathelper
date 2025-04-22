package com.example.beathelper.controllers;

import com.example.beathelper.services.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/donate")
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public ResponseEntity<?> createCheckoutSession(@RequestBody DonationRequest donationRequest) {
        try {
            BigDecimal amount = new BigDecimal(donationRequest.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Amount must be greater than zero.");
            }

            String checkoutUrl = stripeService.createCheckoutSession(
                    amount,
                    "http://localhost:8080/donate/success",
                    "http://localhost:8080/donate/cancel"
            );
            return ResponseEntity.ok(checkoutUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Stripe Error");
        }
    }

    public static class DonationRequest {
        private String amount;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
