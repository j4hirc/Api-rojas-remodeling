    package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;


    import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
    import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
    import com.rojas.remodeling.Api_rojas_remodeling.model.Categories;
    import com.rojas.remodeling.Api_rojas_remodeling.model.Materials;
    import com.rojas.remodeling.Api_rojas_remodeling.repository.CategoriesRepository;
    import com.rojas.remodeling.Api_rojas_remodeling.repository.MaterialsRepository;
    import com.rojas.remodeling.Api_rojas_remodeling.service.MaterialsService;
    import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.MaterialsMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class MaterialsServiceImpl implements MaterialsService {

        private final MaterialsRepository materialsRepository;
        private final CategoriesRepository categoriesRepository;
        private final MaterialsMapper materialsMapper;


        @Override
        @Transactional
        public MaterialsResponseDto createMaterials(MaterialsRequestDto materialsRequestDto) {
            if (materialsRepository.existsByName(materialsRequestDto.getName())) {
                throw new RuntimeException(materialsRequestDto.getName() + " already exists");
            }

            Categories categories = categoriesRepository.findById(materialsRequestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException(materialsRequestDto.getCategoryId() + " does not exist"));

            Materials material = materialsMapper.toEntity(materialsRequestDto);
            material.setCategory(categories);
            return materialsMapper.toResponseDto(materialsRepository.save(material));
        }

        @Override
        @Transactional(readOnly = true)
        public List<MaterialsResponseDto> findAll() {
            return materialsRepository.findAll().stream()
                    .map(materialsMapper::toResponseDto)
                    .collect(Collectors.toList());
        }


        @Override
        @Transactional(readOnly = true)
        public MaterialsResponseDto findById(long id) {
            return materialsRepository.findById(id)
                    .map(materialsMapper::toResponseDto)
                    .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));
        }

        @Override
        @Transactional
        public MaterialsResponseDto updateMaterials(Long id, MaterialsRequestDto materialsRequestDto) {
            Materials existingMaterials = materialsRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Material not found with ID: " + id));

            if (materialsRepository.existsByName(materialsRequestDto.getName()) &&
                    !existingMaterials.getName().equalsIgnoreCase(materialsRequestDto.getName())) {
                throw new RuntimeException("The name " + materialsRequestDto.getName() + " is already taken by another material");
            }

            Categories categories = categoriesRepository.findById(materialsRequestDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            existingMaterials.setName(materialsRequestDto.getName());
            existingMaterials.setCount(materialsRequestDto.getCount());
            existingMaterials.setUnit(materialsRequestDto.getUnit());// <-- Agregar esta línea
            existingMaterials.setPrice(materialsRequestDto.getPrice()); // <-- Agregar esta línea
            existingMaterials.setCategory(categories);

            return materialsMapper.toResponseDto(materialsRepository.save(existingMaterials));
        }

        @Override
        @Transactional
        public void deleteMaterials(Long id) {

            if(!materialsRepository.existsById(id)) {
                throw new RuntimeException("Material not found with ID: " + id);
            }

            materialsRepository.deleteById(id);

        }

        @Override
        @Transactional(readOnly = true)
        public List<MaterialsResponseDto> findByCategoriesId(Long id) {
            return materialsRepository.findByCategoryId(id).stream()
                    .map(materialsMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<MaterialsResponseDto> findByName(String name) {
            return materialsRepository.findByNameContainingIgnoreCase(name).stream()
                    .map(materialsMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<MaterialsResponseDto> findByCategoriesName(String name) {
            List<Materials> materials = materialsRepository.findByCategoriesName(name);
            return materials.stream()
                    .map(materialsMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

    }
