package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WelfareService {

    private final WelfareRepository welfareRepository;

    public List<Welfare> getAllWelfares() {
        return welfareRepository.findAll();
    }

    public Welfare saveWelfare(Welfare welfare) {
        return welfareRepository.save(welfare);
    }

    public List<Welfare> searchByRegion(String region) {
        return welfareRepository.findByRegionContaining(region);
    }

    public List<Welfare> searchByTitle(String title) {
        return welfareRepository.findByTitleContaining(title);
    }

    public List<Welfare> searchByKeyword(String keyword) {
        return welfareRepository.findByKeywordContaining(keyword);
    }

    public List<Welfare> filterWelfares(
            String age,
            String income,
            String region,
            String sales,
            String rent
    ) {

        List<Welfare> list = welfareRepository.findAll();

        return list.stream()

                .filter(w -> {

                    if (age == null || age.isEmpty()) return true;
                    // ✅ 수정: "." 처리 추가
                    if (w.getAge() == null || w.getAge().isEmpty() || w.getAge().equals(".")) return true;

                    try {
                        int userAge = Integer.parseInt(age);
                        String a = w.getAge().trim();

                        if (a.contains("~")) {
                            String[] parts = a.split("~");
                            int min = Integer.parseInt(parts[0].trim());
                            int max = Integer.parseInt(parts[1].trim());
                            return userAge >= min && userAge <= max;
                        }

                        if (a.contains(",")) {
                            String[] parts = a.split(",");
                            int min = Integer.parseInt(parts[0].trim());
                            int max = Integer.parseInt(parts[1].trim());
                            return userAge >= min && userAge <= max;
                        }

                        if (a.contains("이하")) {
                            int max = Integer.parseInt(a.replaceAll("[^0-9]", ""));
                            return userAge <= max;
                        }

                    } catch (Exception e) {
                        return false;
                    }

                    return false;
                })

                .filter(w -> {

                    if (income == null || income.isEmpty()) return true;
                    if (w.getIncome() == null) return false;
                    if (w.getIncome().equals(".")) return true;

                    try {
                        int userIncome = Integer.parseInt(income);
                        Pattern p = Pattern.compile("(\\d+)%");
                        Matcher m = p.matcher(w.getIncome());

                        while (m.find()) {
                            int dataIncome = Integer.parseInt(m.group(1));
                            if (dataIncome >= userIncome) {
                                return true;
                            }
                        }

                        return false;

                    } catch (Exception e) {
                        return false;
                    }
                })

                .filter(w -> {

                    if (region == null || region.isEmpty()) return true;
                    if (w.getArea() == null) return false;
                    if (w.getArea().equals(".")) return true;

                    return w.getArea().contains(region);
                })

                .filter(w -> {

                    if (sales == null || sales.isEmpty()) return true;
                    if (w.getSales() == null) return false;
                    if (w.getSales().equals(".")) return true;

                    return w.getSales().contains(sales);
                })

                .filter(w -> {

                    if (rent == null || rent.isEmpty()) return true;
                    if (w.getRent() == null) return false;
                    if (w.getRent().equals(".")) return true;

                    return w.getRent().contains(rent);
                })

                .collect(Collectors.toList());
    }
}