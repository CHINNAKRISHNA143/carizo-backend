package com.carizo.controller;

import com.carizo.model.Faq;
import com.carizo.repository.FaqRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    private final FaqRepository faqRepository;

    public FaqController(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    // ✅ fetch all FAQs
    @GetMapping
    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }
}
