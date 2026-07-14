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

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientPhone;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String safeDepositBoxCodes;

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