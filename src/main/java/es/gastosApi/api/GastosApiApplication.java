package es.gastosApi.api;

import comun.Cuenta;
import comun.GastoImpl;
import comun.MovimientoImpl;
import es.gastosApi.entidades.*;
import es.gastosApi.repositorios.CategoriaDAO;
import es.gastosApi.repositorios.CuentaDAO;
import es.gastosApi.repositorios.MovimientoDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.List;

@SpringBootApplication
@ImportResource({"classpath:config/jpa-config.xml"})
@Import(ConfiguracionPorJava.class)
public class GastosApiApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(GastosApiApplication.class, args);

		CuentaDAO cuentaDAO = context.getBean(CuentaDAO.class);
		CategoriaDAO categoriaDAO = context.getBean(CategoriaDAO.class);
		MovimientoDAO movimientoDAO = context.getBean(MovimientoDAO.class);

		//cargarDatos(cuentaDAO, categoriaDAO, movimientoDAO);
	}



}
