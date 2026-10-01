package com.rojas.remodeling.Api_rojas_remodeling.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Values, not a relation to the mutable material catalogue. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MaterialSnapshot {
    @Column(name = "material_id", nullable = false)
    private Long materialId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "quantity", nullable = false)
    private Double quantity;
    @Column(name = "unit")
    private String unit;
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
}
