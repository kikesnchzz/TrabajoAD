package vista;

import dao.fjson.DepartamentoDAOJSON;
import dao.fjson.EspacioDAOJSON;
import dao.fjson.ProfesorDAOJSON;
import dao.fjson.ReservaDAOJSON;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Departamento;
import modelo.Espacio;
import modelo.EstadoReserva;
import modelo.Profesor;
import modelo.Reserva;
import servicio.CentroEducativo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class MainFX extends Application {
    // Cambiar dependiendo del tipo de archivo
    private static final String dondeGuardoLosDatos = "json";

    private final BorderPane root = new BorderPane();
    private CentroEducativo centro;

    public static void main(String[] args){ launch(args); }

    @Override public void start(Stage stage){
        try{
            centro=crearCentro();
        }catch(IOException | IllegalArgumentException | UnsupportedOperationException e){ mostrarError("Error al cargar los datos ("+fuente()+")",e.getMessage()); Platform.exit(); return; }
        root.setTop(crearMenu()); root.setCenter(pantallaInicio());
        stage.setScene(new Scene(root,1100,680));
        stage.setTitle("Gestión de reservas - Centro Educativo ("+fuente()+")");
        stage.show();
    }

    private CentroEducativo crearCentro() throws IOException {
        switch(dondeGuardoLosDatos.trim().toLowerCase()){
            case "json":
                return new CentroEducativo(new DepartamentoDAOJSON(),new ProfesorDAOJSON(),new EspacioDAOJSON(),new ReservaDAOJSON());
            case "csv":
                // return new CentroEducativo(new DepartamentoDAOCSV(),new ProfesorDAOCSV(),new EspacioDAOCSV(),new ReservaDAOCSV());

                // quitar linea
                throw new UnsupportedOperationException("Kike");
            case "dat":
                // return new CentroEducativo(new DepartamentoDAODAT(),new ProfesorDAODAT(),new EspacioDAODAT(),new ReservaDAODAT());

                // quitar linea
                throw new UnsupportedOperationException("Joseluis");
            default:
                throw new IllegalArgumentException("Fuente invalida");
        }
    }

    private String fuente(){
        return dondeGuardoLosDatos.trim().toUpperCase();
    }

    private MenuBar crearMenu(){
        Menu archivo=new Menu("Archivo");
        MenuItem guardar=new MenuItem("Guardar datos "+fuente()); guardar.setOnAction(e->guardarDatos());
        MenuItem salir=new MenuItem("Salir"); salir.setOnAction(e->Platform.exit()); archivo.getItems().addAll(guardar,salir);

        Menu dep=new Menu("Departamentos");
        MenuItem ldep=new MenuItem("Listar"); ldep.setOnAction(e->root.setCenter(pantallaDepartamentos()));
        MenuItem ndep=new MenuItem("Nuevo"); ndep.setOnAction(e->root.setCenter(pantallaAltaDepartamento())); dep.getItems().addAll(ldep,ndep);

        Menu prof=new Menu("Profesores");
        MenuItem lprof=new MenuItem("Listar"); lprof.setOnAction(e->root.setCenter(pantallaProfesores()));
        MenuItem nprof=new MenuItem("Nuevo"); nprof.setOnAction(e->root.setCenter(pantallaAltaProfesor())); prof.getItems().addAll(lprof,nprof);

        Menu esp=new Menu("Espacios");
        MenuItem lesp=new MenuItem("Listar"); lesp.setOnAction(e->root.setCenter(pantallaEspacios()));
        MenuItem nesp=new MenuItem("Nuevo"); nesp.setOnAction(e->root.setCenter(pantallaAltaEspacio())); esp.getItems().addAll(lesp,nesp);

        Menu res=new Menu("Reservas");
        MenuItem lres=new MenuItem("Listar"); lres.setOnAction(e->root.setCenter(pantallaReservas()));
        MenuItem nres=new MenuItem("Nueva reserva"); nres.setOnAction(e->root.setCenter(pantallaNuevaReserva())); res.getItems().addAll(lres,nres);
        return new MenuBar(archivo,dep,prof,esp,res);
    }

    private VBox pantallaInicio(){
        VBox box=new VBox(12); box.setPadding(new Insets(30));
        Label t=new Label("GESTIÓN DE RESERVAS"); t.setStyle("-fx-font-size:28px;-fx-font-weight:bold;");
        Label s=new Label("Fuente de datos: ficheros "+fuente()); s.setStyle("-fx-font-size:16px;");
        box.getChildren().addAll(t,s,new Label("Utilice el menú para gestionar departamentos, profesores, espacios y reservas."));
        return box;
    }

    private VBox pantallaDepartamentos(){
        TableView<Departamento> tabla=new TableView<>();
        TableColumn<Departamento,String> id=new TableColumn<>("ID"); id.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getIdDepartamento()));
        TableColumn<Departamento,String> nombre=new TableColumn<>("Nombre"); nombre.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getNombre()));
        tabla.getColumns().addAll(id,nombre); cargar(tabla,centro.getDepartamentos());
        Button eliminar=new Button("Eliminar seleccionado");
        eliminar.setOnAction(e->{
            Departamento d=tabla.getSelectionModel().getSelectedItem();
            if(d==null){mostrarError("Aviso","Seleccione un departamento.");return;}
            try{centro.bajaDepartamento(d.getId());cargar(tabla,centro.getDepartamentos());}catch(Exception ex){mostrarError("No se puede eliminar",ex.getMessage());}
        });
        return contenedor("DEPARTAMENTOS",tabla,eliminar);
    }

    private VBox pantallaAltaDepartamento(){
        TextField id=campo("ID"), nombre=campo("Nombre"); Button guardar=new Button("Guardar");
        guardar.setOnAction(e->{
            try{
                centro.altaDepartamento(new Departamento(id.getText().trim(),nombre.getText().trim()));
                mostrarInfo("Departamento creado.");
                root.setCenter(pantallaDepartamentos());
            }catch(Exception ex){mostrarError("Error",ex.getMessage());}
        });
        return formulario("NUEVO DEPARTAMENTO",new Control[]{id,nombre,guardar},new String[]{"ID","Nombre"});
    }

    private VBox pantallaProfesores(){
        TableView<Profesor> tabla=new TableView<>();
        TableColumn<Profesor,String> id=new TableColumn<>("ID"); id.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getIdProfesor()));
        TableColumn<Profesor,String> nombre=new TableColumn<>("Nombre"); nombre.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getNombreCompleto()));
        TableColumn<Profesor,String> email=new TableColumn<>("Email"); email.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEmail()));
        TableColumn<Profesor,String> dep=new TableColumn<>("Departamento"); dep.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getDepartamento()==null?"":d.getValue().getDepartamento().getNombre()));
        tabla.getColumns().addAll(id,nombre,email,dep); cargar(tabla,centro.getProfesores());
        Button eliminar=new Button("Eliminar seleccionado");
        eliminar.setOnAction(e->{
            Profesor p=tabla.getSelectionModel().getSelectedItem();
            if(p==null){mostrarError("Aviso","Seleccione un profesor.");return;}
            try{centro.bajaProfesor(p.getId());cargar(tabla,centro.getProfesores());}catch(Exception ex){mostrarError("No se puede eliminar",ex.getMessage());}
        });
        return contenedor("PROFESORES",tabla,eliminar);
    }

    private VBox pantallaAltaProfesor(){
        TextField id=campo("ID"),nombre=campo("Nombre"),apellidos=campo("Apellidos"),email=campo("Email");
        ComboBox<Departamento> dep=new ComboBox<>(FXCollections.observableArrayList(centro.getDepartamentos())); dep.setMaxWidth(Double.MAX_VALUE);
        Button guardar=new Button("Guardar");
        guardar.setOnAction(e->{
            try{
                centro.altaProfesor(new Profesor(id.getText().trim(),nombre.getText().trim(),apellidos.getText().trim(),email.getText().trim(),dep.getValue()));
                mostrarInfo("Profesor creado.");
                root.setCenter(pantallaProfesores());
            }catch(Exception ex){mostrarError("Error",ex.getMessage());}
        });
        VBox b=new VBox(10);b.setPadding(new Insets(30));Label t=new Label("NUEVO PROFESOR");t.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");b.getChildren().addAll(t,new Label("ID"),id,new Label("Nombre"),nombre,new Label("Apellidos"),apellidos,new Label("Email"),email,new Label("Departamento"),dep,guardar);return b;
    }

    private VBox pantallaEspacios(){
        TableView<Espacio> tabla=new TableView<>();
        TableColumn<Espacio,String> id=new TableColumn<>("ID");id.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getIdEspacio()));
        TableColumn<Espacio,String> nombre=new TableColumn<>("Nombre");nombre.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getNombre()));
        TableColumn<Espacio,String> tipo=new TableColumn<>("Tipo");tipo.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getTipo()));
        TableColumn<Espacio,Number> cap=new TableColumn<>("Capacidad");cap.setCellValueFactory(d->new SimpleIntegerProperty(d.getValue().getCapacidad()));
        TableColumn<Espacio,String> disponible=new TableColumn<>("Disponible");disponible.setCellValueFactory(d->new SimpleStringProperty(d.getValue().isDisponible()?"Sí":"No"));
        tabla.getColumns().addAll(id,nombre,tipo,cap,disponible);cargar(tabla,centro.getEspacios());
        Button cambiar=new Button("Cambiar disponibilidad");cambiar.setOnAction(e->{
            Espacio x=tabla.getSelectionModel().getSelectedItem();
            if(x==null){
                mostrarError("Aviso","Seleccione un espacio.");
                return;
            }
            try{centro.modificarDisponibilidadEspacio(x.getId(),!x.isDisponible());tabla.refresh();}catch(Exception ex){mostrarError("Error",ex.getMessage());}
        });
        Button eliminar=new Button("Eliminar seleccionado");
        eliminar.setOnAction(e->{
            Espacio x=tabla.getSelectionModel().getSelectedItem();
            if(x==null){mostrarError("Aviso","Seleccione un espacio.");return;}
            try{centro.bajaEspacio(x.getId());cargar(tabla,centro.getEspacios());}catch(Exception ex){mostrarError("No se puede eliminar",ex.getMessage());}
        });
        return contenedor("ESPACIOS",tabla,cambiar,eliminar);
    }

    private VBox pantallaAltaEspacio(){
        TextField id=campo("ID"),nombre=campo("Nombre"),tipo=campo("Tipo"),cap=campo("Capacidad");CheckBox disponible=new CheckBox("Disponible");disponible.setSelected(true);Button guardar=new Button("Guardar");
        guardar.setOnAction(e->{
            try{
                centro.altaEspacio(new Espacio(id.getText().trim(),nombre.getText().trim(),tipo.getText().trim(),Integer.parseInt(cap.getText().trim()),disponible.isSelected()));
                mostrarInfo("Espacio creado.");
                root.setCenter(pantallaEspacios());
            }catch(NumberFormatException ex){mostrarError("Error","La capacidad debe ser un número entero.");}
            catch(Exception ex){mostrarError("Error",ex.getMessage());}
        });
        VBox b=new VBox(10);b.setPadding(new Insets(30));Label t=new Label("NUEVO ESPACIO");t.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");b.getChildren().addAll(t,new Label("ID"),id,new Label("Nombre"),nombre,new Label("Tipo"),tipo,new Label("Capacidad"),cap,disponible,guardar);return b;
    }

    private VBox pantallaReservas(){
        TableView<Reserva> tabla=new TableView<>();
        TableColumn<Reserva,String> id=new TableColumn<>("ID");id.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getIdReserva()));
        TableColumn<Reserva,String> fecha=new TableColumn<>("Fecha");fecha.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getFecha().toString()));
        TableColumn<Reserva,String> horario=new TableColumn<>("Horario");horario.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getHoraInicio()+" - "+d.getValue().getHoraFin()));
        TableColumn<Reserva,String> profesor=new TableColumn<>("Profesor");profesor.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getProfesor().getNombreCompleto()));
        TableColumn<Reserva,String> espacio=new TableColumn<>("Espacio");espacio.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEspacio().getNombre()));
        TableColumn<Reserva,Number> asistentes=new TableColumn<>("Asistentes");asistentes.setCellValueFactory(d->new SimpleIntegerProperty(d.getValue().getNumAsistentes()));
        TableColumn<Reserva,String> estado=new TableColumn<>("Estado");estado.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getEstado().toString()));
        tabla.getColumns().addAll(id,fecha,horario,profesor,espacio,asistentes,estado);cargar(tabla,centro.getReservas());

        ComboBox<Object> filtro=new ComboBox<>();
        filtro.getItems().add("Todas");
        filtro.getItems().addAll(centro.getProfesores());
        filtro.getItems().addAll(centro.getEspacios());
        filtro.setValue("Todas");
        filtro.setOnAction(e->{
            Object f=filtro.getValue();
            if(f instanceof Profesor p) cargar(tabla,centro.getReservasProfesor(p.getId()));
            else if(f instanceof Espacio x) cargar(tabla,centro.getReservasEspacio(x.getId()));
            else cargar(tabla,centro.getReservas());
        });

        Button cancelar=new Button("Cancelar reserva seleccionada");
        cancelar.setOnAction(e->{
            Reserva r=tabla.getSelectionModel().getSelectedItem();
            if(r==null){
                mostrarError("Aviso","Seleccione una reserva.");
                return;
            }
            try{centro.cancelarReserva(r.getId());tabla.refresh();}catch(Exception ex){mostrarError("Error",ex.getMessage());}
        });
        VBox b=contenedor("RESERVAS",tabla,cancelar);
        b.getChildren().add(1,new HBox(10,new Label("Filtrar por profesor o espacio:"),filtro));
        return b;
    }

    private VBox pantallaNuevaReserva(){
        TextField id=campo("ID"),inicio=campo("HH:mm"),fin=campo("HH:mm"),asist=campo("Número de asistentes"),motivo=campo("Motivo");DatePicker fecha=new DatePicker(LocalDate.now());
        ComboBox<Profesor> profesor=new ComboBox<>(FXCollections.observableArrayList(centro.getProfesores()));
        ComboBox<Espacio> espacio=new ComboBox<>(FXCollections.observableArrayList(centro.getEspacios()));profesor.setMaxWidth(Double.MAX_VALUE);espacio.setMaxWidth(Double.MAX_VALUE);
        Button guardar=new Button("Crear reserva");guardar.setOnAction(e->{
            try{
                centro.crearReserva(new Reserva(id.getText().trim(),fecha.getValue(),LocalTime.parse(inicio.getText().trim()),LocalTime.parse(fin.getText().trim()),profesor.getValue(),espacio.getValue(),Integer.parseInt(asist.getText().trim()),EstadoReserva.CONFIRMADA,motivo.getText().trim()));
                mostrarInfo("Reserva creada.");
                root.setCenter(pantallaReservas());
            }catch(NumberFormatException ex){mostrarError("No se puede crear la reserva","El número de asistentes debe ser un número entero.");}
            catch(java.time.format.DateTimeParseException ex){mostrarError("No se puede crear la reserva","Las horas deben tener el formato HH:mm.");}
            catch(Exception ex){mostrarError("No se puede crear la reserva",ex.getMessage());}
        });
        VBox b=new VBox(10);b.setPadding(new Insets(30));Label t=new Label("NUEVA RESERVA");t.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");b.getChildren().addAll(t,new Label("ID"),id,new Label("Fecha"),fecha,new Label("Profesor"),profesor,new Label("Espacio"),espacio,new Label("Hora inicio (HH:mm)"),inicio,new Label("Hora fin (HH:mm)"),fin,new Label("Asistentes"),asist,new Label("Motivo"),motivo,guardar);
        ScrollPane scroll=new ScrollPane(b);scroll.setFitToWidth(true);
        return new VBox(scroll);
    }

    private <T> void cargar(TableView<T> tabla,java.util.List<T> datos){tabla.setItems(FXCollections.observableArrayList(datos));tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);}
    private VBox contenedor(String titulo,TableView<?> tabla,Button... botones){VBox b=new VBox(12);b.setPadding(new Insets(25));Label t=new Label(titulo);t.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");HBox acciones=new HBox(10,botones);VBox.setVgrow(tabla,Priority.ALWAYS);b.getChildren().addAll(t,tabla,acciones);return b;}
    private VBox formulario(String titulo,Control[] controles,String[] etiquetas){VBox b=new VBox(10);b.setPadding(new Insets(30));Label t=new Label(titulo);t.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");b.getChildren().add(t);for(int i=0;i<controles.length;i++){if(i<etiquetas.length)b.getChildren().add(new Label(etiquetas[i]));b.getChildren().add(controles[i]);}return b;}
    private TextField campo(String prompt){TextField f=new TextField();f.setPromptText(prompt);return f;}
    private void guardarDatos(){try{centro.guardarTodo();mostrarInfo("Datos guardados correctamente en la carpeta "+dondeGuardoLosDatos.trim().toLowerCase()+".");}catch(IOException e){mostrarError("Error al guardar",e.getMessage());}}
    private void mostrarInfo(String m){new Alert(Alert.AlertType.INFORMATION,m,ButtonType.OK).showAndWait();}
    private void mostrarError(String titulo,String m){Alert a=new Alert(Alert.AlertType.ERROR,m==null?"Error desconocido":m,ButtonType.OK);a.setHeaderText(titulo);a.showAndWait();}
}
