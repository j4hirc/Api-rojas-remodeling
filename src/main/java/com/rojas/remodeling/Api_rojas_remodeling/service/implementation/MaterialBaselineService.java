package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialSelectionDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.*;
import com.rojas.remodeling.Api_rojas_remodeling.repository.MaterialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialBaselineService {
    private final MaterialsRepository materialsRepository;

    public void captureOriginal(Jobs job, List<JobMaterial> assigned) {
        if (Boolean.TRUE.equals(job.getOriginalAssignmentAvailable())) return;
        job.setOriginalMaterials(assigned.stream().map(this::snapshot).collect(Collectors.toCollection(ArrayList::new)));
        job.setInitialPay(job.getPay());
        job.setOriginalAssignmentAvailable(true);
    }

    public MaterialSnapshot snapshot(JobMaterial row) {
        return new MaterialSnapshot(row.getMaterial().getId(), row.getMaterial().getName(),
                row.getQuantity() == null ? 0.0 : row.getQuantity(), row.getUnit(), price(row));
    }

    public double price(JobMaterial row) {
        double value = row.getUnitPrice() != null ? row.getUnitPrice() : row.getMaterial().getPrice();
        if (!Double.isFinite(value) || value < 0) throw new IllegalArgumentException("Precio de material inválido");
        return value;
    }

    public List<MaterialSnapshot> reportMaterials(Jobs job, List<JobMaterial> current,
                                                  List<MaterialSelectionDto> requested) {
        // A missing list means unchanged. An empty list means all materials removed.
        if (requested == null) return current.stream().map(this::snapshot).toList();
        Map<Long, JobMaterial> currentById = current.stream().collect(Collectors.toMap(r -> r.getMaterial().getId(), Function.identity()));
        Map<Long, MaterialSnapshot> originals = job.getOriginalMaterials().stream().collect(Collectors.toMap(MaterialSnapshot::getMaterialId, Function.identity()));
        Set<Long> ids = new HashSet<>();
        List<MaterialSnapshot> result = new ArrayList<>();
        for (MaterialSelectionDto input : requested) {
            if (input.getMaterialId() == null || !ids.add(input.getMaterialId()))
                throw new IllegalArgumentException("Material inválido o repetido");
            if (input.getQuantity() == null || !Double.isFinite(input.getQuantity()) || input.getQuantity() < 0)
                throw new IllegalArgumentException("La cantidad debe ser un número mayor o igual a cero");
            JobMaterial existing = currentById.get(input.getMaterialId());
            MaterialSnapshot original = originals.get(input.getMaterialId());
            Materials material = existing != null ? existing.getMaterial() : materialsRepository.findById(input.getMaterialId())
                    .orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));
            double unitPrice = original != null ? original.getUnitPrice() : existing != null ? price(existing) : material.getPrice();
            if (!Double.isFinite(unitPrice) || unitPrice < 0) throw new IllegalArgumentException("Precio de material inválido");
            String name = original != null ? original.getName() : material.getName();
            String unit = input.getUnit() != null ? input.getUnit() : existing != null ? existing.getUnit() : material.getUnit();
            result.add(new MaterialSnapshot(input.getMaterialId(), name, input.getQuantity(), unit, unitPrice));
        }
        return result;
    }

    public boolean changed(List<MaterialSnapshot> original, List<MaterialSnapshot> reported) {
        Map<Long, MaterialSnapshot> initial = original.stream().collect(Collectors.toMap(MaterialSnapshot::getMaterialId, Function.identity()));
        Map<Long, MaterialSnapshot> finalRows = reported.stream().collect(Collectors.toMap(MaterialSnapshot::getMaterialId, Function.identity()));
        Set<Long> ids = new HashSet<>(initial.keySet());
        ids.addAll(finalRows.keySet());
        for (Long id : ids) {
            MaterialSnapshot before = initial.get(id), after = finalRows.get(id);
            double beforeQty = before == null ? 0 : before.getQuantity();
            double afterQty = after == null ? 0 : after.getQuantity();
            if (Double.compare(beforeQty, afterQty) != 0) return true;
            if (before != null && after != null && !unit(before.getUnit()).equals(unit(after.getUnit()))) return true;
        }
        return false;
    }

    private String unit(String value) { return value == null || value.equals("N/A") ? "" : value.trim(); }

    public double total(List<MaterialSnapshot> rows) {
        BigDecimal total = BigDecimal.ZERO;
        for (MaterialSnapshot row : rows) {
            total = total.add(BigDecimal.valueOf(row.getQuantity()).multiply(BigDecimal.valueOf(row.getUnitPrice()))
                    .setScale(2, RoundingMode.HALF_UP));
        }
        double value = total.doubleValue();
        if (!Double.isFinite(value)) throw new IllegalArgumentException("El importe del reporte es demasiado grande");
        return value;
    }
}
