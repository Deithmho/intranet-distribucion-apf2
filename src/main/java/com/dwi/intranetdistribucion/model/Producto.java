package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="productos")
@org.hibernate.annotations.Check(constraints="stock_minimo >= 0")
@Getter @Setter @NoArgsConstructor
public class Producto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=30) private String codigo;
    @Column(nullable=false,length=120) private String nombre;
    @Column(name="unidad_medida",nullable=false,length=30) private String unidadMedida;
    @Column(name="stock_minimo",nullable=false) private Integer stockMinimo;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="categoria_id",nullable=false) private Categoria categoria;
}
