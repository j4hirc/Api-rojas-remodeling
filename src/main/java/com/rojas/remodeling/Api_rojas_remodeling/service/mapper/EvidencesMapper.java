package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;


import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Evidences;
import org.springframework.stereotype.Component;

@Component
public class EvidencesMapper {
    public EvidencesResponseDto toResponse(Evidences entity){
        EvidencesResponseDto evidencesResponseDto = new EvidencesResponseDto();
        evidencesResponseDto.setId(entity.getId());
        evidencesResponseDto.setImageUri(entity.getImageUri());
        return evidencesResponseDto;
    }
}
