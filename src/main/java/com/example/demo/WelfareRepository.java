package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface WelfareRepository extends JpaRepository<Welfare, Long> {

    List<Welfare> findByRegionContaining(String region);

    List<Welfare> findByTitleContaining(String keyword);

    List<Welfare> findByKeywordContaining(String keyword);
}