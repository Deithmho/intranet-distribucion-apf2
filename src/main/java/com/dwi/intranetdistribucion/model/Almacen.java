package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="almacenes")
@Getter @Setter @NoArgsConstructor
public class Almacen {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=30) private String codigo;
    @Column(nullable=false,length=120) private String nombre;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="sede_id",nullable=false) private Sede sede;
}
