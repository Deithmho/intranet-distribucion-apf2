package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="existencias",uniqueConstraints=@UniqueConstraint(columnNames={"producto_id","almacen_id"}))
@org.hibernate.annotations.Check(constraints="cantidad >= 0")
@Getter @Setter @NoArgsConstructor
public class Existencia {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="producto_id",nullable=false) private Producto producto;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="almacen_id",nullable=false) private Almacen almacen;
    @Column(nullable=false) private Integer cantidad = 0;
    @Version private Long version;
}
