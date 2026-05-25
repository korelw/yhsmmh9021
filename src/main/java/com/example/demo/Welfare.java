package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "government_services")
@Getter
@Setter
public class Welfare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String purpose;

    private String agency;
    private String region;

    @Column(name = "info_url", columnDefinition = "TEXT")
    private String infoUrl;

    @Column(name = "site_link", columnDefinition = "TEXT")
    private String siteLink;

    @Column(name = "submit_link", columnDefinition = "TEXT")
    private String submitLink;

    private String keyword;
    private String income;

    @Column(name = "Sales")
    private String sales;

    private String rent;
    private String area;

    private String age;
}