package com.rojas.remodeling.Api_rojas_remodeling.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // <-- IMPORTANTE IMPORTAR ESTO
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jobs")
public class Jobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NULL means that this legacy job has no trustworthy original assignment.
    private Boolean originalAssignmentAvailable;
    private Double initialPay;

    @ElementCollection
    @CollectionTable(name = "job_original_materials", joinColumns = @JoinColumn(name = "job_id"))
    @OrderColumn(name = "line_order")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @org.hibernate.annotations.BatchSize(size = 100)
    private List<MaterialSnapshot> originalMaterials = new java.util.ArrayList<>();

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = true)
    private String clientPhone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String address;

    //numero del edificio
    @Column(name = "building_number")
    private String buildingNumber;

    //apartamento
    @Column(name = "apartment")
    private String apartment;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String safeDepositBoxCodes;

    @Column(name = "quickbooks_invoice")
    private String quickbooksInvoice;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private double pay;

    @Column(nullable = false, name = "job_date")
    private LocalDate jobDate;

    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Users employee;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Users manager;

    @Column(name = "blueprint_url")
    private String blueprintUrl;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobBlueprint> blueprints;

}