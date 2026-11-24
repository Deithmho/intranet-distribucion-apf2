package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
@org.hibernate.annotations.SQLDelete(sql="UPDATE almacenes SET activo = false WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("activo = true")
@Entity
@Table(name="almacenes")
@Getter @Setter @NoArgsConstructor
public class Almacen {
    @Column(nullable=false) private boolean activo = true;
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=30) private String codigo;
    @Column(nullable=false,length=120) private String nombre;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="sede_id",nullable=false) private Sede sede;
}
