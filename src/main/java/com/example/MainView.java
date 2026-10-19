package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.model.Administrador;
import com.example.model.Apartamento;
import com.example.model.Propiedad;
import com.example.model.Residente;
import com.example.model.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("ADopera")
@Route("")
public class MainView extends VerticalLayout {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Residente> residentes = new ArrayList<>();
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Propiedad> propiedades = new ArrayList<>();
    private final List<Apartamento> apartamentos = new ArrayList<>();

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("ADopera");

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.add("Usuarios", crearSeccionUsuarios());
        tabSheet.add("Residentes", crearSeccionResidentes());
        tabSheet.add("Administradores", crearSeccionAdministradores());
        tabSheet.add("Propiedades", crearSeccionPropiedades());
        tabSheet.add("Apartamentos", crearSeccionApartamentos());

        add(titulo, tabSheet);
    }

    private Component crearSeccionUsuarios() {
        VerticalLayout layout = crearLayoutSeccion();

        TextField idField = new TextField("ID (vacío para generar)");
        TextField nombreField = new TextField("Nombre");
        TextField apellidoField = new TextField("Apellido");
        TextField emailField = new TextField("Correo electrónico");
        TextField telefonoField = new TextField("Teléfono");
        TextField direccionField = new TextField("Dirección");
        TextField rolField = new TextField("Rol");
        TextField estadoField = new TextField("Estado");
        TextField fechaRegistroField = new TextField("Fecha de registro (AAAA-MM-DDTHH:mm:ss)");

        FormLayout form = new FormLayout(idField, nombreField, apellidoField, emailField,
                telefonoField, direccionField, rolField, estadoField, fechaRegistroField);

        Grid<Usuario> grid = new Grid<>();
        grid.addColumn(usuario -> Objects.toString(usuario.getId(), "")).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Usuario::getApellido).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(Usuario::getEmail).setHeader("Correo").setAutoWidth(true);
        grid.addColumn(Usuario::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Usuario::getDireccion).setHeader("Dirección").setAutoWidth(true);
        grid.addColumn(Usuario::getRol).setHeader("Rol").setAutoWidth(true);
        grid.addColumn(Usuario::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(usuario -> Objects.toString(usuario.getFechaRegistro(), ""))
                .setHeader("Fecha de registro").setAutoWidth(true);
        grid.setItems(usuarios);

        Button crear = new Button("Crear", event -> {
            try {
                Long id = idField.isEmpty() ? siguienteIdUsuario() : Long.valueOf(idField.getValue().trim());
                if (buscarUsuario(id) != null) {
                    Notification.show("Ya existe un usuario con ese ID.");
                    return;
                }
                LocalDateTime fecha = fechaRegistroField.isEmpty()
                        ? LocalDateTime.now()
                        : LocalDateTime.parse(fechaRegistroField.getValue().trim());
                Usuario usuario = new Usuario(id, nombreField.getValue().trim(), apellidoField.getValue().trim(),
                        emailField.getValue().trim(), telefonoField.getValue().trim(),
                        direccionField.getValue().trim(), rolField.getValue().trim(),
                        estadoField.getValue().trim(), fecha);
                usuarios.add(usuario);
                idField.setValue(id.toString());
                grid.getDataProvider().refreshAll();
                Notification.show("Usuario creado.");
            } catch (NumberFormatException exception) {
                Notification.show("El ID debe ser un número válido.");
            } catch (java.time.format.DateTimeParseException exception) {
                Notification.show("La fecha debe usar el formato AAAA-MM-DDTHH:mm:ss.");
            }
        });
        crear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button consultar = new Button("Consultar", event -> {
            Usuario usuario = buscarUsuario(idField.getValue());
            if (usuario == null) {
                Notification.show("No se encontró el usuario.");
                return;
            }
            nombreField.setValue(valor(usuario.getNombre()));
            apellidoField.setValue(valor(usuario.getApellido()));
            emailField.setValue(valor(usuario.getEmail()));
            telefonoField.setValue(valor(usuario.getTelefono()));
            direccionField.setValue(valor(usuario.getDireccion()));
            rolField.setValue(valor(usuario.getRol()));
            estadoField.setValue(valor(usuario.getEstado()));
            fechaRegistroField.setValue(valor(usuario.getFechaRegistro()));
        });

        Button actualizar = new Button("Actualizar", event -> {
            Usuario usuario = buscarUsuario(idField.getValue());
            if (usuario == null) {
                Notification.show("No se encontró el usuario para actualizar.");
                return;
            }
            try {
                usuario.setNombre(nombreField.getValue().trim());
                usuario.setApellido(apellidoField.getValue().trim());
                usuario.setEmail(emailField.getValue().trim());
                usuario.setTelefono(telefonoField.getValue().trim());
                usuario.setDireccion(direccionField.getValue().trim());
                usuario.setRol(rolField.getValue().trim());
                usuario.setEstado(estadoField.getValue().trim());
                if (!fechaRegistroField.isEmpty()) {
                    usuario.setFechaRegistro(LocalDateTime.parse(fechaRegistroField.getValue().trim()));
                }
                grid.getDataProvider().refreshAll();
                Notification.show("Usuario actualizado.");
            } catch (java.time.format.DateTimeParseException exception) {
                Notification.show("La fecha debe usar el formato AAAA-MM-DDTHH:mm:ss.");
            }
        });

        Button eliminar = new Button("Eliminar", event -> {
            Usuario usuario = buscarUsuario(idField.getValue());
            if (usuario == null) {
                Notification.show("No se encontró el usuario para eliminar.");
                return;
            }
            usuarios.remove(usuario);
            grid.getDataProvider().refreshAll();
            Notification.show("Usuario eliminado.");
        });
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button limpiar = new Button("Limpiar", event -> {
            idField.clear();
            nombreField.clear();
            apellidoField.clear();
            emailField.clear();
            telefonoField.clear();
            direccionField.clear();
            rolField.clear();
            estadoField.clear();
            fechaRegistroField.clear();
        });

        layout.add(form, crearAcciones(crear, consultar, actualizar, eliminar, limpiar), grid);
        return layout;
    }

    private Component crearSeccionResidentes() {
        VerticalLayout layout = crearLayoutSeccion();

        TextField documentoField = new TextField("Documento");
        TextField nombreField = new TextField("Nombre");
        TextField apellidosField = new TextField("Apellidos");
        TextField fechaNacimientoField = new TextField("Fecha de nacimiento (AAAA-MM-DD)");
        TextField celularField = new TextField("Celular");
        TextField correoField = new TextField("Correo electrónico");

        FormLayout form = new FormLayout(documentoField, nombreField, apellidosField,
                fechaNacimientoField, celularField, correoField);

        Grid<Residente> grid = new Grid<>();
        grid.addColumn(Residente::getDocumento).setHeader("Documento").setAutoWidth(true);
        grid.addColumn(Residente::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Residente::getApellidos).setHeader("Apellidos").setAutoWidth(true);
        grid.addColumn(residente -> Objects.toString(residente.getFechaDeNacimiento(), ""))
                .setHeader("Fecha de nacimiento").setAutoWidth(true);
        grid.addColumn(Residente::getCelular).setHeader("Celular").setAutoWidth(true);
        grid.addColumn(Residente::getCorreo).setHeader("Correo").setAutoWidth(true);
        grid.setItems(residentes);

        Button crear = new Button("Crear", event -> {
            if (buscarResidente(documentoField.getValue()) != null) {
                Notification.show("Ya existe un residente con ese documento.");
                return;
            }
            try {
                residentes.add(new Residente(documentoField.getValue().trim(), nombreField.getValue().trim(),
                        apellidosField.getValue().trim(),
                        LocalDate.parse(fechaNacimientoField.getValue().trim()),
                        celularField.getValue().trim(), correoField.getValue().trim()));
                grid.getDataProvider().refreshAll();
                Notification.show("Residente creado.");
            } catch (java.time.format.DateTimeParseException exception) {
                Notification.show("La fecha debe usar el formato AAAA-MM-DD.");
            }
        });
        crear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button consultar = new Button("Consultar", event -> {
            Residente residente = buscarResidente(documentoField.getValue());
            if (residente == null) {
                Notification.show("No se encontró el residente.");
                return;
            }
            nombreField.setValue(valor(residente.getNombre()));
            apellidosField.setValue(valor(residente.getApellidos()));
            fechaNacimientoField.setValue(valor(residente.getFechaDeNacimiento()));
            celularField.setValue(valor(residente.getCelular()));
            correoField.setValue(valor(residente.getCorreo()));
        });

        Button actualizar = new Button("Actualizar", event -> {
            Residente residente = buscarResidente(documentoField.getValue());
            if (residente == null) {
                Notification.show("No se encontró el residente para actualizar.");
                return;
            }
            try {
                residente.setNombre(nombreField.getValue().trim());
                residente.setApellidos(apellidosField.getValue().trim());
                residente.setFechaDeNacimiento(LocalDate.parse(fechaNacimientoField.getValue().trim()));
                residente.setCelular(celularField.getValue().trim());
                residente.setCorreo(correoField.getValue().trim());
                grid.getDataProvider().refreshAll();
                Notification.show("Residente actualizado.");
            } catch (java.time.format.DateTimeParseException exception) {
                Notification.show("La fecha debe usar el formato AAAA-MM-DD.");
            }
        });

        Button eliminar = new Button("Eliminar", event -> {
            Residente residente = buscarResidente(documentoField.getValue());
            if (residente == null) {
                Notification.show("No se encontró el residente para eliminar.");
                return;
            }
            residentes.remove(residente);
            grid.getDataProvider().refreshAll();
            Notification.show("Residente eliminado.");
        });
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button limpiar = new Button("Limpiar", event -> {
            documentoField.clear();
            nombreField.clear();
            apellidosField.clear();
            fechaNacimientoField.clear();
            celularField.clear();
            correoField.clear();
        });

        layout.add(form, crearAcciones(crear, consultar, actualizar, eliminar, limpiar), grid);
        return layout;
    }

    private Component crearSeccionAdministradores() {
        VerticalLayout layout = crearLayoutSeccion();

        TextField documentoField = new TextField("Documento");
        TextField nombreField = new TextField("Nombre");
        TextField apellidosField = new TextField("Apellidos");
        TextField fechaNacimientoField = new TextField("Fecha de nacimiento (AAAA-MM-DD)");
        TextField celularField = new TextField("Celular");
        TextField correoField = new TextField("Correo electrónico");
        TextField nitField = new TextField("NIT de propiedad");

        FormLayout form = new FormLayout(documentoField, nombreField, apellidosField,
                fechaNacimientoField, celularField, correoField, nitField);

        Grid<Administrador> grid = new Grid<>();
        grid.addColumn(Administrador::getDocumento).setHeader("Documento").setAutoWidth(true);
        grid.addColumn(Administrador::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Administrador::getApellidos).setHeader("Apellidos").setAutoWidth(true);
        grid.addColumn(administrador -> Objects.toString(administrador.getFechaDeNacimiento(), ""))
                .setHeader("Fecha de nacimiento").setAutoWidth(true);
        grid.addColumn(Administrador::getCelular).setHeader("Celular").setAutoWidth(true);
        grid.addColumn(Administrador::getCorreo).setHeader("Correo").setAutoWidth(true);
        grid.addColumn(Administrador::getNit).setHeader("NIT").setAutoWidth(true);
        grid.setItems(administradores);

        Button crear = new Button("Crear", event -> {
            if (buscarAdministrador(documentoField.getValue()) != null) {
                Notification.show("Ya existe un administrador con ese documento.");
                return;
            }
            try {
                administradores.add(new Administrador(documentoField.getValue().trim(),
                        nombreField.getValue().trim(), apellidosField.getValue().trim(),
                        LocalDate.parse(fechaNacimientoField.getValue().trim()),
                        celularField.getValue().trim(), correoField.getValue().trim(),
                        nitField.getValue().trim()));
                grid.getDataProvider().refreshAll();
                Notification.show("Administrador creado.");
            } catch (java.time.format.DateTimeParseException exception) {
                Notification.show("La fecha debe usar el formato AAAA-MM-DD.");
            }
        });
        crear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button consultar = new Button("Consultar", event -> {
            Administrador administrador = buscarAdministrador(documentoField.getValue());
            if (administrador == null) {
                Notification.show("No se encontró el administrador.");
                return;
            }
            nombreField.setValue(valor(administrador.getNombre()));
            apellidosField.setValue(valor(administrador.getApellidos()));
            fechaNacimientoField.setValue(valor(administrador.getFechaDeNacimiento()));
            celularField.setValue(valor(administrador.getCelular()));
            correoField.setValue(valor(administrador.getCorreo()));
            nitField.setValue(valor(administrador.getNit()));
        });

        Button actualizar = new Button("Actualizar", event -> {
            Administrador administrador = buscarAdministrador(documentoField.getValue());
            if (administrador == null) {
                Notification.show("No se encontró el administrador para actualizar.");
                return;
            }
            try {
                administrador.setNombre(nombreField.getValue().trim());
                administrador.setApellidos(apellidosField.getValue().trim());
                administrador.setFechaDeNacimiento(LocalDate.parse(fechaNacimientoField.getValue().trim()));
                administrador.setCelular(celularField.getValue().trim());
                administrador.setCorreo(correoField.getValue().trim());
                administrador.setNit(nitField.getValue().trim());
                grid.getDataProvider().refreshAll();
                Notification.show("Administrador actualizado.");
            } catch (java.time.format.DateTimeParseException exception) {
                Notification.show("La fecha debe usar el formato AAAA-MM-DD.");
            }
        });

        Button eliminar = new Button("Eliminar", event -> {
            Administrador administrador = buscarAdministrador(documentoField.getValue());
            if (administrador == null) {
                Notification.show("No se encontró el administrador para eliminar.");
                return;
            }
            administradores.remove(administrador);
            grid.getDataProvider().refreshAll();
            Notification.show("Administrador eliminado.");
        });
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button limpiar = new Button("Limpiar", event -> {
            documentoField.clear();
            nombreField.clear();
            apellidosField.clear();
            fechaNacimientoField.clear();
            celularField.clear();
            correoField.clear();
            nitField.clear();
        });

        layout.add(form, crearAcciones(crear, consultar, actualizar, eliminar, limpiar), grid);
        return layout;
    }

    private Component crearSeccionPropiedades() {
        VerticalLayout layout = crearLayoutSeccion();

        TextField nitField = new TextField("NIT");
        TextField nombrePropiedadField = new TextField("Nombre de la propiedad");
        TextField direccionField = new TextField("Dirección");
        TextField ciudadField = new TextField("Ciudad");
        TextField departamentoField = new TextField("Departamento");
        TextField idAptoField = new TextField("ID de apartamento");

        FormLayout form = new FormLayout(nitField, nombrePropiedadField, direccionField,
                ciudadField, departamentoField, idAptoField);

        Grid<Propiedad> grid = new Grid<>();
        grid.addColumn(Propiedad::getNit).setHeader("NIT").setAutoWidth(true);
        grid.addColumn(Propiedad::getNombrePropiedad).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Propiedad::getDireccion).setHeader("Dirección").setAutoWidth(true);
        grid.addColumn(Propiedad::getCiudad).setHeader("Ciudad").setAutoWidth(true);
        grid.addColumn(Propiedad::getDepartamento).setHeader("Departamento").setAutoWidth(true);
        grid.addColumn(Propiedad::getIdApto).setHeader("ID de apartamento").setAutoWidth(true);
        grid.setItems(propiedades);

        Button crear = new Button("Crear", event -> {
            if (buscarPropiedad(nitField.getValue()) != null) {
                Notification.show("Ya existe una propiedad con ese NIT.");
                return;
            }
            propiedades.add(new Propiedad(nitField.getValue().trim(), nombrePropiedadField.getValue().trim(),
                    direccionField.getValue().trim(), ciudadField.getValue().trim(),
                    departamentoField.getValue().trim(), idAptoField.getValue().trim()));
            grid.getDataProvider().refreshAll();
            Notification.show("Propiedad creada.");
        });
        crear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button consultar = new Button("Consultar", event -> {
            Propiedad propiedad = buscarPropiedad(nitField.getValue());
            if (propiedad == null) {
                Notification.show("No se encontró la propiedad.");
                return;
            }
            nombrePropiedadField.setValue(valor(propiedad.getNombrePropiedad()));
            direccionField.setValue(valor(propiedad.getDireccion()));
            ciudadField.setValue(valor(propiedad.getCiudad()));
            departamentoField.setValue(valor(propiedad.getDepartamento()));
            idAptoField.setValue(valor(propiedad.getIdApto()));
        });

        Button actualizar = new Button("Actualizar", event -> {
            Propiedad propiedad = buscarPropiedad(nitField.getValue());
            if (propiedad == null) {
                Notification.show("No se encontró la propiedad para actualizar.");
                return;
            }
            propiedad.setNombrePropiedad(nombrePropiedadField.getValue().trim());
            propiedad.setDireccion(direccionField.getValue().trim());
            propiedad.setCiudad(ciudadField.getValue().trim());
            propiedad.setDepartamento(departamentoField.getValue().trim());
            propiedad.setIdApto(idAptoField.getValue().trim());
            grid.getDataProvider().refreshAll();
            Notification.show("Propiedad actualizada.");
        });

        Button eliminar = new Button("Eliminar", event -> {
            Propiedad propiedad = buscarPropiedad(nitField.getValue());
            if (propiedad == null) {
                Notification.show("No se encontró la propiedad para eliminar.");
                return;
            }
            propiedades.remove(propiedad);
            grid.getDataProvider().refreshAll();
            Notification.show("Propiedad eliminada.");
        });
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button limpiar = new Button("Limpiar", event -> {
            nitField.clear();
            nombrePropiedadField.clear();
            direccionField.clear();
            ciudadField.clear();
            departamentoField.clear();
            idAptoField.clear();
        });

        layout.add(form, crearAcciones(crear, consultar, actualizar, eliminar, limpiar), grid);
        return layout;
    }

    private Component crearSeccionApartamentos() {
        VerticalLayout layout = crearLayoutSeccion();

        TextField idAptoField = new TextField("ID de apartamento");
        TextField numeroAptoField = new TextField("Número de apartamento");
        TextField torreField = new TextField("Torre");
        TextField parqueaderoField = new TextField("Parqueadero");
        TextField cuartoUtilField = new TextField("Cuarto útil");
        TextField idDocumentoField = new TextField("Documento del residente");

        FormLayout form = new FormLayout(idAptoField, numeroAptoField, torreField,
                parqueaderoField, cuartoUtilField, idDocumentoField);

        Grid<Apartamento> grid = new Grid<>();
        grid.addColumn(Apartamento::getIdApto).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Apartamento::getNumeroApto).setHeader("Número").setAutoWidth(true);
        grid.addColumn(Apartamento::getTorre).setHeader("Torre").setAutoWidth(true);
        grid.addColumn(Apartamento::getParqueadero).setHeader("Parqueadero").setAutoWidth(true);
        grid.addColumn(Apartamento::getCuartoUtil).setHeader("Cuarto útil").setAutoWidth(true);
        grid.addColumn(Apartamento::getIdDocumento).setHeader("Documento del residente").setAutoWidth(true);
        grid.setItems(apartamentos);

        Button crear = new Button("Crear", event -> {
            if (buscarApartamento(idAptoField.getValue()) != null) {
                Notification.show("Ya existe un apartamento con ese ID.");
                return;
            }
            apartamentos.add(new Apartamento(idAptoField.getValue().trim(),
                    numeroAptoField.getValue().trim(), torreField.getValue().trim(),
                    parqueaderoField.getValue().trim(), cuartoUtilField.getValue().trim(),
                    idDocumentoField.getValue().trim()));
            grid.getDataProvider().refreshAll();
            Notification.show("Apartamento creado.");
        });
        crear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button consultar = new Button("Consultar", event -> {
            Apartamento apartamento = buscarApartamento(idAptoField.getValue());
            if (apartamento == null) {
                Notification.show("No se encontró el apartamento.");
                return;
            }
            numeroAptoField.setValue(valor(apartamento.getNumeroApto()));
            torreField.setValue(valor(apartamento.getTorre()));
            parqueaderoField.setValue(valor(apartamento.getParqueadero()));
            cuartoUtilField.setValue(valor(apartamento.getCuartoUtil()));
            idDocumentoField.setValue(valor(apartamento.getIdDocumento()));
        });

        Button actualizar = new Button("Actualizar", event -> {
            Apartamento apartamento = buscarApartamento(idAptoField.getValue());
            if (apartamento == null) {
                Notification.show("No se encontró el apartamento para actualizar.");
                return;
            }
            apartamento.setNumeroApto(numeroAptoField.getValue().trim());
            apartamento.setTorre(torreField.getValue().trim());
            apartamento.setParqueadero(parqueaderoField.getValue().trim());
            apartamento.setCuartoUtil(cuartoUtilField.getValue().trim());
            apartamento.setIdDocumento(idDocumentoField.getValue().trim());
            grid.getDataProvider().refreshAll();
            Notification.show("Apartamento actualizado.");
        });

        Button eliminar = new Button("Eliminar", event -> {
            Apartamento apartamento = buscarApartamento(idAptoField.getValue());
            if (apartamento == null) {
                Notification.show("No se encontró el apartamento para eliminar.");
                return;
            }
            apartamentos.remove(apartamento);
            grid.getDataProvider().refreshAll();
            Notification.show("Apartamento eliminado.");
        });
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button limpiar = new Button("Limpiar", event -> {
            idAptoField.clear();
            numeroAptoField.clear();
            torreField.clear();
            parqueaderoField.clear();
            cuartoUtilField.clear();
            idDocumentoField.clear();
        });

        layout.add(form, crearAcciones(crear, consultar, actualizar, eliminar, limpiar), grid);
        return layout;
    }

    private VerticalLayout crearLayoutSeccion() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setWidthFull();
        return layout;
    }

    private HorizontalLayout crearAcciones(Button crear, Button consultar, Button actualizar,
            Button eliminar, Button limpiar) {
        HorizontalLayout acciones = new HorizontalLayout(crear, consultar, actualizar, eliminar, limpiar);
        acciones.getStyle().set("flex-wrap", "wrap");
        return acciones;
    }

    private Long siguienteIdUsuario() {
        return usuarios.stream()
                .map(Usuario::getId)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0) + 1;
    }

    private Usuario buscarUsuario(String id) {
        try {
            return buscarUsuario(Long.valueOf(id.trim()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Usuario buscarUsuario(Long id) {
        return usuarios.stream().filter(usuario -> Objects.equals(usuario.getId(), id))
                .findFirst().orElse(null);
    }

    private Residente buscarResidente(String documento) {
        return residentes.stream().filter(residente -> Objects.equals(residente.getDocumento(),
                documento.trim())).findFirst().orElse(null);
    }

    private Administrador buscarAdministrador(String documento) {
        return administradores.stream().filter(administrador -> Objects.equals(administrador.getDocumento(),
                documento.trim())).findFirst().orElse(null);
    }

    private Propiedad buscarPropiedad(String nit) {
        return propiedades.stream().filter(propiedad -> Objects.equals(propiedad.getNit(),
                nit.trim())).findFirst().orElse(null);
    }

    private Apartamento buscarApartamento(String idApto) {
        return apartamentos.stream().filter(apartamento -> Objects.equals(apartamento.getIdApto(),
                idApto.trim())).findFirst().orElse(null);
    }

    private String valor(Object valor) {
        return Objects.toString(valor, "");
    }
}
