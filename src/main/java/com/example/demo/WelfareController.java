package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/welfares")

@RequiredArgsConstructor
public class WelfareController {

    private final WelfareService welfareService;

    @GetMapping
    public List<Welfare> getWelfareList() {
        return welfareService.getAllWelfares();
    }

    @GetMapping("/search")
    public List<Welfare> searchByTitle(@RequestParam String title) {
        return welfareService.searchByTitle(title);
    }

    @GetMapping("/keyword")
    public List<Welfare> searchByKeyword(@RequestParam String keyword) {
        return welfareService.searchByKeyword(keyword);
    }

    @GetMapping("/filter")
    public List<Welfare> filter(
            @RequestParam(required = false) String age,
            @RequestParam(required = false) String income,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String sales,
            @RequestParam(required = false) String rent
    ) {
        return welfareService.filterWelfares(age, income, region, sales, rent);
    }
}