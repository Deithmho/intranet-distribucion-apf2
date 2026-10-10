package com.dwi.intranetdistribucion.service;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.*;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.*;
import java.util.List;
public interface InventarioService {
    List<ExistenciaView> consultar(Long almacenId,Long categoriaId,boolean critico);
    MovimientoView entrada(EntradaRequest r,String responsable);
    MovimientoView trasladar(TrasladoRequest r,String responsable);
    List<MovimientoView> movimientos();
}
