package es.gastosApi.entidades;

import comun.Usuario;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class UsuarioConId extends Usuario {

    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private List<CuentaConId> cuentas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CuentaConId> getCuenta() {
        return cuentas;
    }

    public void addCuenta(CuentaConId cuenta){
        this.cuentas.add(cuenta);
        cuenta.setUsuario(this);
    }
}
