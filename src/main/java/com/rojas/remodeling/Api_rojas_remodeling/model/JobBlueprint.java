package com.rojas.remodeling.Api_rojas_remodeling.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "job_blueprints")
@Data
public class JobBlueprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Jobs job;

    @Column(name = "url", nullable = false)
    private String url;

}