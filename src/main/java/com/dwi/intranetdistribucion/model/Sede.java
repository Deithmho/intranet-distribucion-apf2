package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="sedes")
@Getter @Setter @NoArgsConstructor
public class Sede {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=30) private String codigo;
    @Column(nullable=false,length=120) private String nombre;
    @Column(nullable=false,length=250) private String direccion;
}
