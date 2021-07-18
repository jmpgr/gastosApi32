package es.gastosApi.rest;


import comun.Cuenta;
import comun.FicheroUtils;
import comun.GastoImpl;
import comun.MovimientoImpl;
import es.gastosApi.entidades.*;
import es.gastosApi.repositorios.CategoriaDAO;
import es.gastosApi.repositorios.CuentaDAO;
import es.gastosApi.repositorios.MovimientoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RepositoryRestController
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoDAO movimientoDAO;

    @Autowired
    private CuentaDAO cuentaDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    @GetMapping(value ="/search/por-categoria")
    @ResponseBody
    public CollectionModel<PersistentEntityResource> getMovimientosPorCategoria(@RequestParam String categoria, @RequestParam Long idUsuario, PersistentEntityResourceAssembler assembler){
        final List<MovimientoConId> movimientosPorCategorias = movimientoDAO.getMovimientosPorCategorias(categoria, idUsuario);
        return assembler.toCollectionModel(movimientosPorCategorias);
    }

    @PostMapping(value = "/cargar/{idCuenta}")
    @ResponseBody
    public boolean cargarMovimientos(MultipartFile file, @PathVariable("idCuenta") long idCuenta, PersistentEntityResourceAssembler assembler) throws IOException {
        byte[] bytes = file.getBytes();
        Path path = Paths.get(file.getOriginalFilename());
        Files.write(path, bytes);
        List<MovimientoConId> movimientos = cargarDatos(path.toFile(), idCuenta);
        return true;
    }

    public List<MovimientoConId> cargarDatos(File datos, Long idCuenta) {

        CuentaConId cuentaConId = cuentaDAO.findById(idCuenta).get();
        if(cuentaConId == null) {
            throw new RuntimeException("cuenta no existe");
        }
        final List<MovimientoImpl> movimientos = Cuenta.cargarMovimientos(datos);
        final List<MovimientoConId> respuesta = new ArrayList<>();
        for(int i = 0; i < movimientos.size(); i++) {
            MovimientoImpl movimiento = movimientos.get(i);
            CategoriaConId categoriaConId = categoriaDAO.findByNombreCategoria(movimiento.getCategoria().getNombreCategoria());

            if (categoriaConId == null) {
                categoriaConId = new CategoriaConId();
                categoriaConId.setNombreCategoria(movimiento.getCategoria().getNombreCategoria());
            }

            final MovimientoConId movimientoExistente = movimientoDAO.findByIdMovimiento(movimiento.getIdMovimiento());
            if (movimientoExistente == null) {
                MovimientoConId movimientoConId = new MovimientoConId();
                movimientoConId.setIdMovimiento(movimiento.getIdMovimiento());
                movimientoConId.setConcepto(movimiento.getConcepto());
                movimientoConId.setImporte(movimiento.getImporte());
                movimientoConId.setCuenta(cuentaConId);
                if (movimiento instanceof GastoImpl){
                    GastoConId gastoConId = new GastoConId();
                    gastoConId.setIdMovimiento(movimiento.getIdMovimiento());
                    gastoConId.setConcepto(movimiento.getConcepto());
                    gastoConId.setImporte(movimiento.getImporte());
                    gastoConId.setFecha(movimiento.getFecha());
                    gastoConId.setCuenta(cuentaConId);
                    gastoConId.setAutorizado(true);
                    categoriaConId.addMovimiento(gastoConId);
                } else {
                    IngresoConId ingresoConId = new IngresoConId();
                    ingresoConId.setIdMovimiento(movimiento.getIdMovimiento());
                    ingresoConId.setConcepto(movimiento.getConcepto());
                    ingresoConId.setImporte(movimiento.getImporte());
                    ingresoConId.setFecha(movimiento.getFecha());
                    ingresoConId.setCuenta(cuentaConId);
                    categoriaConId.addMovimiento(ingresoConId);
                    ingresoConId.setCompartido(true);
                }
                categoriaDAO.save(categoriaConId);
                respuesta.add(movimientoConId);
            }
        }

        return respuesta;
    }

}
