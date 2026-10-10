package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="movimientos")
@org.hibernate.annotations.Check(constraints="cantidad > 0")
@Getter @Setter @NoArgsConstructor
public class Movimiento {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="producto_id",nullable=false) private Producto producto;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="origen_id") private Almacen origen;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="destino_id",nullable=false) private Almacen destino;
    @Column(nullable=false) private Integer cantidad;
    @Column(nullable=false,length=20) private String tipo;
    @Column(nullable=false,length=200) private String motivo;
    @Column(nullable=false,length=150) private String responsable;
    @Column(nullable=false) private java.time.LocalDateTime fecha;
    @PrePersist void onCreate() { fecha=java.time.LocalDateTime.now(); }
}
