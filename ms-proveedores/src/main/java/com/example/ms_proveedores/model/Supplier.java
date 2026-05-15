package com.example.ms_proveedores.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String ruc;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private Boolean active = true;
}
