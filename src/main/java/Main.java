import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Stage window;
    private double saldoBilletera = 150.00; 

    private static final int ANCHO_MOVIL = 360;
    private static final int ALTO_MOVIL = 740;

    public static class Usuario {
        String cedula, nombre, correo, contrasenia, rol;
        public Usuario(String c, String n, String em, String pass, String r) {
            this.cedula = c; this.nombre = n; this.correo = em; this.contrasenia = pass; this.rol = r;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        this.window.setResizable(false); 
        mostrarLogin();
    }

    // --- PANTALLA 1: LOGIN ---
    public void mostrarLogin() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0D0E15;"); 

        VBox tarjetaLogin = new VBox(15);
        tarjetaLogin.setMaxWidth(ANCHO_MOVIL - 40);
        tarjetaLogin.setPadding(new Insets(25));
        tarjetaLogin.setAlignment(Pos.CENTER);
        tarjetaLogin.setStyle("-fx-background-color: #1A1D2B; -fx-background-radius: 12px; " +
                              "-fx-border-color: #00D2FF; -fx-border-width: 1px; -fx-border-radius: 12px;");

        Label lblTitulo = new Label("TICKETMASTER");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.web("#00D2FF")); 

        VBox boxCorreo = new VBox(5);
        Label lblCorreo = new Label("Correo Electrónico:");
        lblCorreo.setTextFill(Color.web("#8A8D9F")); 
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("ejemplo@gmail.com");
        txtCorreo.setStyle("-fx-background-color: #0D0E15; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #8A8D9F; -fx-border-color: #31354A; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 10px;");
        boxCorreo.getChildren().addAll(lblCorreo, txtCorreo);

        VBox boxClave = new VBox(5);
        Label lblClave = new Label("Contraseña:");
        lblClave.setTextFill(Color.web("#8A8D9F"));
        
        HBox passContainer = new HBox(5);
        PasswordField txtClaveOculta = new PasswordField();
        txtClaveOculta.setPromptText("••••••••");
        txtClaveOculta.setStyle("-fx-background-color: #0D0E15; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #8A8D9F; -fx-border-color: #31354A; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 10px;");
        HBox.setHgrow(txtClaveOculta, Priority.ALWAYS);
        
        TextField txtClaveVisible = new TextField();
        txtClaveVisible.setStyle("-fx-background-color: #0D0E15; -fx-text-fill: #FFFFFF; -fx-border-color: #31354A; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 10px;");
        HBox.setHgrow(txtClaveVisible, Priority.ALWAYS);
        txtClaveVisible.setManaged(false);
        txtClaveVisible.setVisible(false);

        Button btnOjito = new Button("👁");
        btnOjito.setStyle("-fx-background-color: #31354A; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-background-radius: 6px; -fx-padding: 10px;");
        
        btnOjito.setOnAction(e -> {
            if (txtClaveOculta.isVisible()) {
                txtClaveVisible.setText(txtClaveOculta.getText());
                txtClaveOculta.setVisible(false);
                txtClaveOculta.setManaged(false);
                txtClaveVisible.setVisible(true);
                txtClaveVisible.setManaged(true);
            } else {
                txtClaveOculta.setText(txtClaveVisible.getText());
                txtClaveVisible.setVisible(false);
                txtClaveVisible.setManaged(false);
                txtClaveOculta.setVisible(true);
                txtClaveOculta.setManaged(true);
            }
        });
        passContainer.getChildren().addAll(txtClaveOculta, txtClaveVisible, btnOjito);
        boxClave.getChildren().addAll(lblClave, passContainer);

        Label lblMensaje = new Label("");
        lblMensaje.setTextFill(Color.web("#FF4444")); 
        lblMensaje.setWrapText(true);

        Button btnIngresar = new Button("INICIAR SESIÓN");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setFont(Font.font("System", FontWeight.BOLD, 14));
        btnIngresar.setStyle("-fx-background-color: #00D2FF; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-background-radius: 6px; -fx-min-height: 48px;");

        Button btnIrRegistro = new Button("¿No tienes cuenta? Regístrate aquí");
        btnIrRegistro.setStyle("-fx-background-color: transparent; -fx-text-fill: #8A8D9F; -fx-cursor: hand; -fx-underline: true;");
        btnIrRegistro.setOnAction(e -> mostrarRegistro());

        btnIngresar.setOnAction(e -> {
            String correo = txtCorreo.getText().trim();
            String clave = txtClaveOculta.isVisible() ? txtClaveOculta.getText() : txtClaveVisible.getText();

            Usuario user = validarLogin(correo, clave);
            if (user != null) {
                redirigirPantalla(user);
            } else {
                lblMensaje.setText("Credenciales incorrectas.");
            }
        });

        tarjetaLogin.getChildren().addAll(lblTitulo, boxCorreo, boxClave, btnIngresar, btnIrRegistro, lblMensaje);
        root.getChildren().add(tarjetaLogin);
        window.setScene(new Scene(root, ANCHO_MOVIL, ALTO_MOVIL));
        window.setTitle("Ticketmaster");
        window.show();
    }

    // --- PANTALLA 2: REGISTRO ---
    public void mostrarRegistro() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #0D0E15;");
        root.setAlignment(Pos.CENTER);

        VBox tarjetaRegistro = new VBox(12);
        tarjetaRegistro.setPadding(new Insets(20));
        tarjetaRegistro.setStyle("-fx-background-color: #1A1D2B; -fx-background-radius: 12px; -fx-border-color: #31354A;");

        Label lblTitulo = new Label("CREAR CUENTA");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.web("#FFFFFF"));
        lblTitulo.setAlignment(Pos.CENTER);
        tarjetaRegistro.getChildren().add(lblTitulo);

        TextField txtCedula = crearCampo("Cédula / ID:", tarjetaRegistro, "V-12345678");
        TextField txtNombre = crearCampo("Nombre Completo:", tarjetaRegistro, "Nombre Apellido");
        TextField txtCorreo = crearCampo("Correo Electrónico:", tarjetaRegistro, "correo@mail.com");
        
        Label lblClave = new Label("Contraseña:");
        lblClave.setTextFill(Color.web("#8A8D9F"));
        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("••••••••");
        txtClave.setStyle("-fx-background-color: #0D0E15; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #8A8D9F; -fx-border-color: #31354A; -fx-border-radius: 6px; -fx-padding: 10px;");
        tarjetaRegistro.getChildren().addAll(lblClave, txtClave);

        Label lblMensaje = new Label("");
        lblMensaje.setTextFill(Color.web("#FF4444"));

        Button btnRegistrar = new Button("REGISTRARSE");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setFont(Font.font("System", FontWeight.BOLD, 14));
        btnRegistrar.setStyle("-fx-background-color: #39FF14; -fx-text-fill: #0D0E15; -fx-min-height: 48px; -fx-font-weight: bold; -fx-background-radius: 6px;");

        Button btnVolver = new Button("VOLVER AL LOGIN");
        btnVolver.setMaxWidth(Double.MAX_VALUE);
        btnVolver.setStyle("-fx-background-color: transparent; -fx-text-fill: #8A8D9F; -fx-border-color: #31354A; -fx-border-radius: 6px; -fx-min-height: 40px;");
        btnVolver.setOnAction(e -> mostrarLogin());

        btnRegistrar.setOnAction(e -> {
            if(txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty() || txtClave.getText().isEmpty()) {
                lblMensaje.setText("Todos los campos son obligatorios.");
                return;
            }
            Usuario nuevo = new Usuario(txtCedula.getText().trim(), txtNombre.getText().trim(), txtCorreo.getText().trim(), txtClave.getText(), "COMPRADOR");
            if (guardarUsuario(nuevo)) {
                mostrarLogin();
            } else {
                lblMensaje.setText("Error al guardar el usuario.");
            }
        });

        tarjetaRegistro.getChildren().addAll(btnRegistrar, btnVolver, lblMensaje);
        root.getChildren().add(tarjetaRegistro);
        window.setScene(new Scene(root, ANCHO_MOVIL, ALTO_MOVIL));
    }

    private void redirigirPantalla(Usuario user) {
        if (user.rol.equalsIgnoreCase("ADMINISTRADOR")) {
            mostrarPanelAdministrador(user);
        } else if (user.rol.equalsIgnoreCase("COMPRADOR")) {
            mostrarPanelComprador(user);
        } else if (user.rol.equalsIgnoreCase("PERSONAL_PUERTA")) {
            mostrarPanelPortero(user);
        }
    }

    // --- PANEL ADMINISTRADOR ---
    private void mostrarPanelAdministrador(Usuario admin) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #0D0E15;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label lblInfo = new Label("Admin: " + admin.nombre);
        lblInfo.setTextFill(Color.white());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("SALIR");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF4444; -fx-border-color: #FF4444; -fx-border-radius: 4px;");
        btnLogout.setOnAction(e -> mostrarLogin());
        topBar.getChildren().addAll(lblInfo, spacer, btnLogout);

        VBox contenedorContenido = new VBox(10);
        VBox.setVgrow(contenedorContenido, Priority.ALWAYS);

        // FORMULARIO INTEGRADO (MÓVIL FRIENDLY)
        VBox formArtista = new VBox(10);
        formArtista.setPadding(new Insets(10));
        formArtista.setStyle("-fx-background-color: #1A1D2B; -fx-background-radius: 8px;");
        Label lblSeccionArt = new Label("Registrar Artistas");
        lblSeccionArt.setTextFill(Color.web("#00D2FF"));
        TextField txtCodArtista = crearCampo("Código:", formArtista, "ART-001");
        TextField txtNomArtista = crearCampo("Nombre:", formArtista, "Los Pixels");
        TextField txtManager = crearCampo("Mánager:", formArtista, "Manager S.A.");
        
        TextArea txtAreaArtistas = new TextArea();
        txtAreaArtistas.setEditable(false);
        txtAreaArtistas.setPrefHeight(80);
        txtAreaArtistas.setStyle("-fx-control-inner-background: #0D0E15; -fx-text-fill: #FFFFFF;");
        actualizarAuditoria(txtAreaArtistas, "artistas.txt");
        
        Button btnGuardarArtista = new Button("REGISTRAR ARTISTA");
        btnGuardarArtista.setMaxWidth(Double.MAX_VALUE);
        btnGuardarArtista.setStyle("-fx-background-color: #00D2FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-height: 40px;");
        btnGuardarArtista.setOnAction(e -> {
            if(txtCodArtista.getText().isEmpty() || txtNomArtista.getText().isEmpty()) return;
            String linea = txtCodArtista.getText().trim() + "|" + txtNomArtista.getText().trim() + "|" + txtManager.getText().trim();
            if(escribirEnArchivo("artistas.txt", linea)) {
                txtCodArtista.clear(); txtNomArtista.clear(); txtManager.clear();
                actualizarAuditoria(txtAreaArtistas, "artistas.txt");
            }
        });
        formArtista.getChildren().addAll(btnGuardarArtista, txtAreaArtistas);

        ScrollPane mainScroll = new ScrollPane(formArtista);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background-color: transparent; -fx-background: #0D0E15;");

        root.getChildren().addAll(topBar, mainScroll);
        window.setScene(new Scene(root, ANCHO_MOVIL, ALTO_MOVIL));
    }

    // --- PANEL COMPRADOR ---
    private void mostrarPanelComprador(Usuario cliente) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #0D0E15;");

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label lblInfo = new Label(cliente.nombre);
        lblInfo.setTextFill(Color.WHITE);
        Label lblSaldo = new Label(String.format("Saldo: $%.2f", saldoBilletera));
        lblSaldo.setTextFill(Color.web("#39FF14")); 
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnLogout = new Button("SALIR");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF4444行业;");
        btnLogout.setOnAction(e -> mostrarLogin());
        topBar.getChildren().addAll(lblInfo, lblSaldo, spacer, btnLogout);

        VBox feedConciertos = new VBox(14); 
        feedConciertos.setPadding(new Insets(12));
        feedConciertos.setStyle("-fx-background-color: #0D0E15;");

        List<String> conciertos = leerLineasArchivo("conciertos.txt");
        for (String linea : conciertos) {
            String[] datos = linea.split("\\|");
            if(datos.length >= 7) {
                String codEvt = datos[0]; String nomEvt = datos[1]; String fecha = datos[2];
                String lugar = datos[4]; double precio = Double.parseDouble(datos[6]);

                VBox cardMobil = new VBox(10);
                cardMobil.setPadding(new Insets(15));
                cardMobil.setStyle("-fx-background-color: #1A1D2B; -fx-background-radius: 10px; -fx-border-color: #31354A;");

                Label name = new Label(nomEvt); name.setTextFill(Color.WHITE); name.setFont(Font.font("System", FontWeight.BOLD, 16));
                Label det = new Label("📅 " + fecha + "\n📍 " + lugar + "\n💵 Precio: $" + precio); det.setTextFill(Color.web("#8A8D9F"));

                Label lblStatusCompra = new Label("");
                lblStatusCompra.setWrapText(true);

                Button btnAdquirir = new Button("COMPRAR TICKET");
                btnAdquirir.setMaxWidth(Double.MAX_VALUE);
                btnAdquirir.setStyle("-fx-background-color: #00D2FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-height: 44px;");

                btnAdquirir.setOnAction(e -> {
                    if (saldoBilletera >= precio) {
                        saldoBilletera -= precio;
                        lblSaldo.setText(String.format("Saldo: $%.2f", saldoBilletera));
                        String hash = generarSHA256(cliente.cedula + codEvt + "SECURE_SALT");
                        String codTck = "TCK-" + (int)(Math.random() * 90000 + 10000);
                        String lineaTicket = codTck + "|" + cliente.cedula + "|" + codEvt + "|" + nomEvt + "|" + precio + "|" + hash + "|ACTIVO";
                        if(escribirEnArchivo("tickets.txt", lineaTicket)) {
                            generarArchivoPdfTicket(codTck, cliente.nombre, cliente.cedula, nomEvt, fecha, lugar, hash);
                            lblStatusCompra.setText("¡Compra Exitosa! Ticket Guardado.");
                            lblStatusCompra.setTextFill(Color.web("#39FF14"));
                        }
                    } else {
                        lblStatusCompra.setText("Saldo insuficiente.");
                        lblStatusCompra.setTextFill(Color.web("#FF4444"));
                    }
                });
                cardMobil.getChildren().addAll(name, det, btnAdquirir, lblStatusCompra);
                feedConciertos.getChildren().add(cardMobil);
            }
        }
        ScrollPane spFeed = new ScrollPane(feedConciertos);
        spFeed.setFitToWidth(true); spFeed.setStyle("-fx-background-color: transparent; -fx-background: #0D0E15;");

        root.getChildren().addAll(topBar, spFeed);
        window.setScene(new Scene(root, ANCHO_MOVIL, ALTO_MOVIL));
    }

    // --- PANTALLA 4: CONTROL DE ACCESO ---
    private void mostrarPanelPortero(Usuario portero) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0D0E15;");

        VBox cabecera = new VBox(5);
        cabecera.setAlignment(Pos.CENTER);
        Label lblTitulo = new Label("VALIDACIÓN DE ACCESO");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.WHITE);
        Label lblSub = new Label("Portero: " + portero.nombre);
        lblSub.setTextFill(Color.web("#8A8D9F"));
        cabecera.getChildren().addAll(lblTitulo, lblSub);

        VBox bloqueControl = new VBox(12);
        bloqueControl.setPadding(new Insets(20));
        bloqueControl.setStyle("-fx-background-color: #1A1D2B; -fx-background-radius: 12px;");

        TextField txtIdTicket = new TextField();
        txtIdTicket.setPromptText("Ej: TCK-12345");
        txtIdTicket.setAlignment(Pos.CENTER);
        txtIdTicket.setStyle("-fx-background-color: #0D0E15; -fx-text-fill: #FFF; -fx-border-color: #00D2FF; -fx-padding: 12px;");

        Button btnVerificar = new Button("🔒 VERIFICAR E-TICKET");
        btnVerificar.setMaxWidth(Double.MAX_VALUE);
        btnVerificar.setStyle("-fx-background-color: #00D2FF; -fx-text-fill: white; -fx-padding: 14px;");

        bloqueControl.getChildren().addAll(txtIdTicket, btnVerificar);

        StackPane contenedorResultado = new StackPane();
        contenedorResultado.setPadding(new Insets(20));
        contenedorResultado.setStyle("-fx-background-color: #1A1D2B;");

        Label lblResultado = new Label("Esperando lectura...");
        lblResultado.setTextFill(Color.web("#8A8D9F"));
        contenedorResultado.getChildren().add(lblResultado);

        btnVerificar.setOnAction(e -> {
            String id = txtIdTicket.getText().trim();
            if (id.isEmpty()) {
                lblResultado.setText("⚠️ INGRESE UN ID");
                return;
            }

            String estado = verificarLegalidadTicket(id);
            if (estado.equals("VALIDO")) {
                lblResultado.setText("✅ ACCESO CONCEDIDO");
                lblResultado.setTextFill(Color.web("#39FF14"));
                marcarTicketComoUsado(id);
            } else {
                lblResultado.setText("❌ ACCESO DENEGADO");
                lblResultado.setTextFill(Color.web("#FF4444"));
            }
            txtIdTicket.clear();
        });

        Button btnLogout = new Button("CERRAR SESIÓN");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #8A8D9F; -fx-border-color: #31354A;");
        btnLogout.setOnAction(e -> mostrarLogin());

        root.getChildren().addAll(cabecera, bloqueControl, contenedorResultado, btnLogout);
        window.setScene(new Scene(root, ANCHO_MOVIL, ALTO_MOVIL));
    }

    private String verificarLegalidadTicket(String idTicket) {
        List<String> lineas = leerLineasArchivo("tickets.txt");
        for (String linea : lineas) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 7 && datos[0].trim().equalsIgnoreCase(idTicket)) {
                if (datos[6].trim().equalsIgnoreCase("ACTIVO")) return "VALIDO";
                else return "USADO";
            }
        }
        return "FALSO";
    }

    private void marcarTicketComoUsado(String idTicket) {
        List<String> lineas = leerLineasArchivo("tickets.txt");
        StringBuilder nuevoContenido = new StringBuilder();
        for (String linea : lineas) {
            String[] datos = linea.split("\\|");
            if (datos.length >= 7 && datos[0].trim().equalsIgnoreCase(idTicket)) {
                nuevoContenido.append(datos[0]).append("|").append(datos[1]).append("|").append(datos[2]).append("|")
                              .append(datos[3]).append("|").append(datos[4]).append("|").append(datos[5]).append("|USADO\n");
            } else {
                nuevoContenido.append(linea).append("\n");
            }
        }
        try (FileWriter fw = new FileWriter("tickets.txt")) {
            fw.write(nuevoContenido.toString());
        } catch (IOException e) { }
    }

    private void generarArchivoPdfTicket(String codTck, String comprador, String cedula, String concierto, String fecha, String lugar, String hash) {
        String nombreArchivoPdf = codTck + "_Comprobante.pdf";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivoPdf))) {
            bw.write("=========================================================\n");
            bw.write("            TICKETMASTER DIGITAL COMPROBANTE             \n");
            bw.write("=========================================================\n");
            bw.write(" CODIGO UNICO: " + codTck + "\n");
            bw.write(" EVENTO: " + concierto + "\n");
            bw.write(" SECURITY HASH: " + hash + "\n");
        } catch (IOException e) { }
    }

    private void actualizarAuditoria(TextArea area, String archivo) {
        area.clear();
        List<String> lineas = leerLineasArchivo(archivo);
        for(String l : lineas) { area.appendText(l + "\n"); }
    }

    private TextField crearCampo(String titulo, VBox contenedor, String placeholder) {
        Label lbl = new Label(titulo); lbl.setTextFill(Color.web("#8A8D9F"));
        TextField txt = new TextField(); txt.setPromptText(placeholder);
        txt.setStyle("-fx-background-color: #0D0E15; -fx-text-fill: #FFFFFF; -fx-border-color: #31354A; -fx-padding: 8px;");
        contenedor.getChildren().addAll(lbl, txt);
        return txt;
    }

    private boolean escribirEnArchivo(String archivo, String linea) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(linea); bw.newLine(); return true;
        } catch (IOException e) { return false; }
    }

    private List<String> leerLineasArchivo(String archivo) {
        List<String> lineas = new ArrayList<>(); File f = new File(archivo);
        if (!f.exists()) return lineas;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String l; while ((l = br.readLine()) != null) lineas.add(l);
        } catch (IOException e) { }
        return lineas;
    }

    private String generarSHA256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private Usuario validarLogin(String correo, String clave) {
        File archivo = new File("usuarios.txt");
        if (!archivo.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\|");
                if (datos.length == 5 && datos[2].trim().equalsIgnoreCase(correo) && datos[3].trim().equals(clave)) {
                    return new Usuario(datos[0], datos[1], datos[2], datos[3], datos[4]);
                }
            }
        } catch (IOException e) { }
        return null;
    }

    private boolean guardarUsuario(Usuario u) { return escribirEnArchivo("usuarios.txt", u.cedula+"|"+u.nombre+"|"+u.correo+"|"+u.contrasenia+"|"+u.rol); }

    public static void main(String[] args) { launch(args); }
}
