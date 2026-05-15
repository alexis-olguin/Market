package com.example.ms_proveedores.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ms_proveedores.dto.SupplierRequest;
import com.example.ms_proveedores.dto.SupplierResponse;
import com.example.ms_proveedores.model.Supplier;
import com.example.ms_proveedores.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

    private final SupplierRepository repository;

    @Transactional
    public SupplierResponse crearProveedor(SupplierRequest request) {
        log.info("Creando proveedor: {}", request.getName());

        Supplier supplier = new Supplier(null, request.getName(), request.getRuc(), request.getEmail(), request.getPhone(), true);
        supplier = repository.save(supplier);

        return mapToResponse(supplier);
    }

    public List<SupplierResponse> listarProveedores() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .ruc(supplier.getRuc())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .active(supplier.getActive())
                .build();
    }
}
