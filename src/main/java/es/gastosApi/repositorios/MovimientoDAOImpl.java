package es.gastosApi.repositorios;

import comun.Movimiento;
import es.gastosApi.entidades.CuentaConId;
import es.gastosApi.entidades.MovimientoConId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovimientoDAOImpl implements MovimientoDAOCustom{

    @Autowired
    CuentaDAO cuentaDAO;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<MovimientoConId> getMovimientosPorCategorias(String categoria, Long usuarioId) {
        Query query = entityManager.createNativeQuery(
                "SELECT c.* FROM cuentas as c " +
                        "WHERE c.id_usuario= ?1", CuentaConId.class);
        query.setParameter(1, usuarioId);
        List<CuentaConId> cuentas = query.getResultList();

        List<MovimientoConId> movimientos = new ArrayList<>();
        for (CuentaConId cuenta:cuentas) {
            for (Movimiento movimiento : cuenta.getMovimientos()) {
                MovimientoConId movimientoConId = (MovimientoConId) movimiento ;
                if(movimiento.getCategoria().getNombreCategoria().equals(categoria)){
                    movimientos.add(movimientoConId);
                }
            }
        }

        return movimientos;
    }
}
