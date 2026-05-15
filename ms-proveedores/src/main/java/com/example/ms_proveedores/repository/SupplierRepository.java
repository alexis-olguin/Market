package com.example.ms_proveedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ms_proveedores.model.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
