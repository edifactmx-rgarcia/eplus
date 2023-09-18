package gm.cuentas.controlador;

import gm.cuentas.modelo.Cuenta;
import gm.cuentas.servicio.CuentaServicio;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
@ViewScoped
public class IndexControlador {

    @Autowired
    CuentaServicio cuentaServicio;
    private List<Cuenta> cuentas;
    private Cuenta cuentaSeleccionada;

    private static final Logger logger =
            LoggerFactory.getLogger(IndexControlador.class);

    @PostConstruct
    public void init(){
        cargarDatos();
    }

    public void cargarDatos(){
        this.cuentas = cuentaServicio.listarCuentas();
        cuentas.forEach((cuenta) -> logger.info(cuenta.toString()));
    }

    public void agregarCuenta() {
        logger.info("Se crea objeto de cuenta seleccionada");
        this.cuentaSeleccionada = new Cuenta();
    }

    public void guardarCuenta() {
        logger.info("Cuenta a guardar " + this.cuentaSeleccionada);
        // Guardar
        if (cuentaSeleccionada.getId() == null) {
            cuentaServicio.guardarCuenta(cuentaSeleccionada);
            cuentas.add(cuentaSeleccionada);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Cuenta Agregada"));
        } else { // Actualizar
            cuentaServicio.guardarCuenta(cuentaSeleccionada);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Cuenta Actualizada"));
        }
        // ocultamos la ventana modal
        PrimeFaces.current().executeScript("PF('ventanaModalCuenta').hide()");

        // actualizar la tabla
        PrimeFaces.current().ajax().update("forma-cuentas:mensajes",
                "forma-cuentas:cuentas-tabla");
    }

    public void eliminarCuenta() {
        logger.info("Cuenta a eliminar " + this.cuentaSeleccionada);
        cuentaServicio.eliminarCuenta(cuentaSeleccionada);
        // Eliminar el registro de la vista de cuentas
        cuentas.remove(cuentaSeleccionada);
        // Reset cuenta seleccionada a null
        cuentaSeleccionada = null;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Cuenta Eliminada"));
        PrimeFaces.current().executeScript("PF('eliminarModalCuenta').hide()");
        PrimeFaces.current().ajax().update("forma-cuentas:mensajes",
                "forma-cuentas:cuentas-tabla");
    }
}
