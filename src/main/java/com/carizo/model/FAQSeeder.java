package com.carizo.model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.carizo.repository.FaqRepository;

import java.util.List;

@Component
public class FAQSeeder implements CommandLineRunner {

    private final FaqRepository faqRepository;

    public FAQSeeder(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(faqRepository.count() == 0) {
            List<Faq> faqs = List.of(
                new Faq("How to place an order?", "You can browse products and click 'Add to Cart'."),
                new Faq("What payment methods are available?", "We accept credit cards, debit cards, and UPI."),
                new Faq("How to track my order?", "Go to your Orders page and click 'Track Order'.")
            );
            faqRepository.saveAll(faqs);
            System.out.println("FAQs seeded successfully!");
        }
    }
}
