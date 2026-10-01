package com.rojas.remodeling.Api_rojas_remodeling.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_updates")
public class JobUpdates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean materialSnapshotAvailable;
    private Boolean hasModifications;
    private Double initialPay;

    @ElementCollection
    @CollectionTable(name = "job_update_materials", joinColumns = @JoinColumn(name = "job_update_id"))
    @OrderColumn(name = "line_order")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @org.hibernate.annotations.BatchSize(size = 100)
    private java.util.List<MaterialSnapshot> reportedMaterials = new java.util.ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column
    private Double price;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Jobs job;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Users employee;




}
