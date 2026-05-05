package com.rojas.remodeling.Api_rojas_remodeling.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "evidences")
public class Evidences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUri;

    @ManyToOne
    @JoinColumn(name = "job_update_id", nullable = false)
    private JobUpdates jobUpdate;


}
