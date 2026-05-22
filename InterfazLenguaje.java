import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfazLenguaje extends JFrame {
    private JTextPane txtCodigo;
    private JTextPane txtConsola;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private Map<String, Object> memoria = new HashMap<>();

    private final Color BG_MAIN = new Color(40, 42, 54);
    private final Color BG_PANEL = new Color(68, 71, 90);
    private final Color FG_TEXT = new Color(248, 248, 242);
    private final Color ACCENT_CYAN = new Color(139, 233, 253);
    private final Color ACCENT_GREEN = new Color(80, 250, 123);
    private final Color ACCENT_PINK = new Color(255, 121, 198);
    private final Color ACCENT_ORANGE = new Color(255, 184, 84);
    private final Color BTN_RED = new Color(255, 85, 85);
    private final Color BTN_BLUE = new Color(98, 114, 164);
    private final Color BTN_TEAL = new Color(62, 166, 147);

    private final Color SYN_STRING = new Color(241, 250, 140);
    private final Color SYN_NUMBER = new Color(189, 147, 249);
    private final Color SYN_COMMENT = new Color(98, 114, 164);

    public InterfazLenguaje() {
        setTitle("Intérprete VGV");
        setSize(1050, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        Font fuenteCodigo = new Font("Consolas", Font.PLAIN, 16);
        Font fuenteTitulos = new Font("Segoe UI", Font.BOLD, 14);

        txtCodigo = new JTextPane();
        txtCodigo.setFont(fuenteCodigo);
        txtCodigo.setBackground(BG_PANEL);
        txtCodigo.setForeground(FG_TEXT);
        txtCodigo.setCaretColor(Color.WHITE);
        txtCodigo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        configurarEstilosCodigo();
        agregarListenerSintaxis();

        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(txtCodigo);

        gbc.gridy = 0;
        gbc.weighty = 0.42;
        add(createModernScrollPane(noWrapPanel, "Editor de Código", ACCENT_CYAN, fuenteTitulos), gbc);

        String[] columnas = { "Token", "Lexema", "Patrón", "¿Es Reservada?" };
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setBackground(BG_MAIN);
        tablaTokens.setForeground(FG_TEXT);
        tablaTokens.setGridColor(BG_PANEL);
        tablaTokens.setRowHeight(28);
        tablaTokens.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaTokens.setSelectionBackground(BTN_BLUE);
        tablaTokens.setSelectionForeground(Color.WHITE);

        JTableHeader header = tablaTokens.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(ACCENT_PINK);
        header.setFont(fuenteTitulos);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        gbc.gridy = 1;
        gbc.weighty = 0.20; // ← tabla más compacta
        add(createModernScrollPane(tablaTokens, "Tabla de Tokens", ACCENT_PINK, fuenteTitulos), gbc);

        txtConsola = new JTextPane();
        txtConsola.setEditable(false);
        txtConsola.setBackground(new Color(30, 31, 40));
        txtConsola.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridy = 2;
        gbc.weighty = 0.28;
        add(createModernScrollPane(txtConsola, "Consola de Ejecución", ACCENT_GREEN, fuenteTitulos), gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelBotones.setBackground(BG_MAIN);

        JButton btnAnalizar = createModernButton("Generar Tabla de Tokens", BTN_BLUE, Color.WHITE);
        JButton btnEjecutar = createModernButton("Ejecutar Código", ACCENT_ORANGE, BG_MAIN);
        JButton btnGramatical = createModernButton("Análisis Gramático", ACCENT_CYAN, BG_MAIN);
        JButton btnDiccionario = createModernButton("Ver Diccionario", BTN_TEAL, Color.WHITE);
        JButton btnLimpiar = createModernButton("Limpiar Todo", BTN_RED, Color.WHITE);

        panelBotones.add(btnAnalizar);
        panelBotones.add(btnEjecutar);
        panelBotones.add(btnGramatical);
        panelBotones.add(btnDiccionario);
        panelBotones.add(btnLimpiar);

        gbc.gridy = 3;
        gbc.weighty = 0.05;
        add(panelBotones, gbc);

        btnAnalizar.addActionListener(e -> generarTablaTokens());
        btnEjecutar.addActionListener(e -> ejecutarCodigoReal());
        btnGramatical.addActionListener(e -> explicarCodigoDetallado());
        btnDiccionario.addActionListener(e -> mostrarDiccionario());
        btnLimpiar.addActionListener(e -> {
            txtCodigo.setText("");
            txtConsola.setText("");
            modeloTabla.setRowCount(0);
            memoria.clear();
        });
    }

    private void configurarEstilosCodigo() {
        Style def = txtCodigo.addStyle("Default", null);
        StyleConstants.setForeground(def, FG_TEXT);

        Style keyword = txtCodigo.addStyle("Keyword", null);
        StyleConstants.setForeground(keyword, ACCENT_PINK);
        StyleConstants.setBold(keyword, true);

        Style number = txtCodigo.addStyle("Number", null);
        StyleConstants.setForeground(number, SYN_NUMBER);

        Style string = txtCodigo.addStyle("String", null);
        StyleConstants.setForeground(string, SYN_STRING);

        Style operator = txtCodigo.addStyle("Operator", null);
        StyleConstants.setForeground(operator, ACCENT_CYAN);

        Style comment = txtCodigo.addStyle("Comment", null);
        StyleConstants.setForeground(comment, SYN_COMMENT);
        StyleConstants.setItalic(comment, true);
    }

    private void agregarListenerSintaxis() {
        txtCodigo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarColores();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarColores();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void aplicarColores() {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = txtCodigo.getStyledDocument();
            String text;
            try {
                text = doc.getText(0, doc.getLength());
            } catch (BadLocationException e) {
                return;
            }

            doc.setCharacterAttributes(0, text.length(), txtCodigo.getStyle("Default"), true);
            colorearRegex(text, doc, "([~+\\-*/;])", "Operator");
            colorearRegex(text, doc, "\\b\\d+(\\.\\d+)?\\b", "Number");
            colorearRegex(text, doc, "\\b(alto|grande|venti)\\b", "Keyword");
            colorearRegex(text, doc, "\"[^\"]*\"|'[^']*'", "String");
            colorearRegex(text, doc, "(//.*|#.*)", "Comment");
        });
    }

    private void colorearRegex(String text, StyledDocument doc, String regex, String estilo) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while (m.find()) {
            doc.setCharacterAttributes(m.start(), m.end() - m.start(),
                    txtCodigo.getStyle(estilo), false);
        }
    }

    private JScrollPane createModernScrollPane(Component comp, String title, Color titleColor, Font font) {
        JScrollPane scroll = new JScrollPane(comp);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BG_PANEL, 2, true),
                title, TitledBorder.LEFT, TitledBorder.TOP, font, titleColor));
        scroll.getVerticalScrollBar().setBackground(BG_MAIN);
        return scroll;
    }

    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(210, 40));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void imprimirEnConsola(String mensaje, Color color) {
        StyledDocument doc = txtConsola.getStyledDocument();
        Style estilo = txtConsola.addStyle("cs", null);
        StyleConstants.setForeground(estilo, color);
        StyleConstants.setFontFamily(estilo, "Consolas");
        StyleConstants.setBold(estilo, true);
        StyleConstants.setFontSize(estilo, 14);
        try {
            doc.insertString(doc.getLength(), mensaje, estilo);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getTextoEditor() {
        try {
            return txtCodigo.getDocument().getText(0, txtCodigo.getDocument().getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    private void generarTablaTokens() {
        modeloTabla.setRowCount(0);

        // Regex reales usados por el tokenizador y validadores internos
        final String RX_KEYWORD = "alto|grande|venti";
        final String RX_GRANDE = "-?[0-9]{1,10}\\.[0-9]{1,10}";
        final String RX_ALTO = "-?[0-9]{1,10}";
        final String RX_VENTI = "^(\"[^\"]*\"|'[^']*')$";
        final String RX_OPERADOR = "[~+\\-*/;]";
        final String RX_IDENT = "[a-zA-Z_][a-zA-Z0-9_]*";

        // Eliminar líneas comentadas antes de tokenizar
        String textoSinComentarios = String.join("\n",
                java.util.Arrays.stream(getTextoEditor().split("\n"))
                        .filter(l -> !l.trim().startsWith("#") && !l.trim().startsWith("//"))
                        .toArray(String[]::new));

        String regex = "(" + RX_KEYWORD + ")"
                + "|([0-9]{1,10}\\.[0-9]{1,10})"
                + "|([0-9]{1,10})"
                + "|(\"[^\"]*\"|'[^']*')"
                + "|([~+\\-*/;])"
                + "|([a-zA-Z_][a-zA-Z0-9_]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textoSinComentarios);

        while (matcher.find()) {
            String lexema = matcher.group();
            String tipo = "", desc = "", res = "No";

            if (matcher.group(1) != null) {
                tipo = lexema.toUpperCase();
                desc = RX_KEYWORD;
                res = "Sí";
            } else if (matcher.group(2) != null) {
                tipo = "LITERAL_GRANDE";
                desc = RX_GRANDE;
            } else if (matcher.group(3) != null) {
                tipo = "LITERAL_ALTO";
                desc = RX_ALTO;
            } else if (matcher.group(4) != null) {
                tipo = "LITERAL_VENTI";
                desc = RX_VENTI;
            } else if (matcher.group(5) != null) {
                tipo = "OPERADOR";
                desc = RX_OPERADOR;
                res = "Sí";
            } else if (matcher.group(6) != null) {
                tipo = "IDENTIFICADOR";
                desc = RX_IDENT;
            }

            modeloTabla.addRow(new Object[] { tipo, lexema, desc, res });
        }
    }

    private void ejecutarCodigoReal() {
        txtConsola.setText("");
        imprimirEnConsola("--- Iniciando ejecución ---\n", FG_TEXT);
        memoria.clear();
        String[] lineas = getTextoEditor().split("\n");

        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty() || linea.startsWith("#"))
                continue;
            try {
                procesarLinea(linea);
            } catch (LenguajeException ex) {
                imprimirEnConsola("[LÍNEA " + (i + 1) + "] " + ex.getMessage() + "\n", BTN_RED);
            } catch (NullPointerException ex) {
                imprimirEnConsola("[LÍNEA " + (i + 1) + "] Variable no definida.\n", BTN_RED);
            } catch (NumberFormatException ex) {
                imprimirEnConsola("[LÍNEA " + (i + 1) + "] Valor numérico inválido.\n", BTN_RED);
            } catch (Exception ex) {
                imprimirEnConsola("[LÍNEA " + (i + 1) + "] Estructura incorrecta.\n", BTN_RED);
            }
        }
        imprimirEnConsola("\n--- Ejecución terminada ---\n", FG_TEXT);
    }

    private void procesarLinea(String linea) throws LenguajeException {
        if (!linea.endsWith(";"))
            throw new LenguajeException("Error de sintaxis: falta ';' al final de la instrucción.",
                    "ERROR DE SINTAXIS");

        linea = linea.substring(0, linea.length() - 1).trim();
        if (linea.startsWith("IF") || linea.startsWith("If") || linea.startsWith("iF")) {
            throw new LenguajeException(
                    "Error de Sintaxis: la palabra reservada 'if' debe escribirse en minúsculas.",
                    "ERROR DE SINTAXIS");
        }

        if (linea.startsWith("if")) {
            procesarIf(linea);
            return;
        }

        if (!linea.contains("~"))
            throw new LenguajeException("Error de Sintaxis: falta el operador de asignación '~'.", "ERROR DE SINTAXIS");

        String[] partes = linea.split("~", 2);
        String izq = partes[0].trim();
        String exp = partes.length > 1 ? partes[1].trim() : "";
        String[] tokensIzq = izq.split("\\s+");

        if (exp.isEmpty())
            throw new LenguajeException("Error de Sintaxis: no hay valor asignado después de '~'.",
                    "ERROR DE SINTAXIS");

        String tipo = "";
        String nombre = "";

        if (tokensIzq.length == 2) {

            nombre = tokensIzq[0];
            tipo = tokensIzq[1];

            if (!nombre.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))
                throw new LenguajeException(
                        "Error de Sintaxis: '" + nombre + "' no es un nombre de variable válido.",
                        "ERROR DE SINTAXIS");

            if (nombre.equals("alto") || nombre.equals("grande") || nombre.equals("venti"))
                throw new LenguajeException(
                        "Error Semántico: no puedes usar la palabra reservada '" + nombre
                                + "' como nombre de variable.",
                        "ERROR SEMÁNTICO");

            if (memoria.containsKey(nombre))
                throw new LenguajeException(
                        "Error Semántico: la variable '" + nombre + "' ya fue declarada previamente.",
                        "ERROR SEMÁNTICO");

            if (!tipo.equals("alto") && !tipo.equals("grande") && !tipo.equals("venti"))
                throw new LenguajeException(
                        "Error de Tipo: tipo de dato '" + tipo + "' no reconocido. Usa 'alto', 'grande' o 'venti'.",
                        "ERROR DE TIPO");
        } else if (tokensIzq.length == 1) {
            nombre = tokensIzq[0];

            if (!nombre.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))
                throw new LenguajeException(
                        "Error de Sintaxis: la parte izquierda '" + nombre + "' no es un identificador válido.",
                        "ERROR DE SINTAXIS");

            if (nombre.equals("alto") || nombre.equals("grande") || nombre.equals("venti"))
                throw new LenguajeException(
                        "Error Semántico: no puedes usar la palabra reservada '" + nombre + "' como variable.",
                        "ERROR SEMÁNTICO");

            if (!memoria.containsKey(nombre))
                throw new LenguajeException(
                        "Error Semántico: la variable '" + nombre + "' no ha sido declarada antes de usarse.",
                        "ERROR SEMÁNTICO");

            Object val = memoria.get(nombre);
            if (val instanceof Long)
                tipo = "alto";
            else if (val instanceof Double)
                tipo = "grande";
            else if (val instanceof String)
                tipo = "venti";

        } else {
            throw new LenguajeException(
                    "Error de Sintaxis: formato incorrecto a la izquierda de '~'.",
                    "ERROR DE SINTAXIS");
        }

        switch (tipo) {
            case "alto": {
                long res = evaluarInt(exp);
                Alto a = new Alto(res);
                memoria.put(nombre, res);
                imprimirEnConsola("[ALTO]   " + nombre + " ~ " + a + "\n", ACCENT_GREEN);
                break;
            }
            case "grande": {
                double res = evaluarGrande(exp);
                Grande g = new Grande(res);
                memoria.put(nombre, g.getValor());
                imprimirEnConsola("[GRANDE] " + nombre + " ~ " + g + "\n", ACCENT_CYAN);
                break;
            }
            case "venti": {
                String res = evaluarVenti(exp);
                memoria.put(nombre, res);
                imprimirEnConsola("[VENTI]  " + nombre + " ~ \"" + res + "\"\n", ACCENT_ORANGE);
                break;
            }
        }
    }

    private void procesarIf(String linea) throws LenguajeException {

        String condicion = linea.substring(2).trim();

        if (condicion.isEmpty()) {
            throw new LenguajeException(
                    "Error de Sintaxis: falta la condición después de 'if'.",
                    "ERROR DE SINTAXIS");
        }

        String[] operadores = { "==", "!=", ">=", "<=", ">", "<" };
        String operadorEncontrado = "";

        for (String op : operadores) {
            if (condicion.contains(op)) {
                operadorEncontrado = op;
                break;
            }
        }

        if (operadorEncontrado.isEmpty()) {
            throw new LenguajeException(
                    "Error de Sintaxis: el if necesita un operador de comparación.",
                    "ERROR DE SINTAXIS");
        }

        String[] partes = condicion.split(Pattern.quote(operadorEncontrado), 2);

        if (partes.length < 2 || partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
            throw new LenguajeException(
                    "Error de Sintaxis: el if necesita dos datos para comparar.",
                    "ERROR DE SINTAXIS");
        }

        String dato1 = partes[0].trim();
        String dato2 = partes[1].trim();

        double valor1 = obtenerValorComparacion(dato1);
        double valor2 = obtenerValorComparacion(dato2);

        boolean resultado = false;

        switch (operadorEncontrado) {
            case "==":
                resultado = valor1 == valor2;
                break;
            case "!=":
                resultado = valor1 != valor2;
                break;
            case ">":
                resultado = valor1 > valor2;
                break;
            case "<":
                resultado = valor1 < valor2;
                break;
            case ">=":
                resultado = valor1 >= valor2;
                break;
            case "<=":
                resultado = valor1 <= valor2;
                break;
        }

        imprimirEnConsola(
                "[IF] Comparación: " + dato1 + " " + operadorEncontrado + " " + dato2
                        + " → " + resultado + "\n",
                resultado ? ACCENT_GREEN : BTN_RED);
    }

    private double obtenerValorComparacion(String dato) throws LenguajeException {

        if (memoria.containsKey(dato)) {
            Object valor = memoria.get(dato);

            if (valor instanceof Number) {
                return ((Number) valor).doubleValue();
            }

            throw new LenguajeException(
                    "Error de Tipo: el if solo puede comparar valores numéricos.",
                    "ERROR DE TIPO");
        }

        if (dato.matches("-?[0-9]+(\\.[0-9]+)?")) {
            return Double.parseDouble(dato);
        }

        throw new LenguajeException(
                "Error Semántico: el dato '" + dato + "' no existe o no es numérico.",
                "ERROR SEMÁNTICO");
    }

    private long evaluarInt(String exp) throws LenguajeException {
        exp = exp.trim();
        // (Ex. 5) Operadores al final sin operando
        if (exp.matches(".*[+\\-*/]\\s*$"))
            throw new LenguajeException("Error de Sintaxis: falta un operando al final de la expresión.",
                    "ERROR DE SINTAXIS");
        // (Ex. 5) Operadores consecutivos/repetidos
        if (exp.matches(".*[+\\-*/]{2,}.*"))
            throw new LenguajeException(
                    "Error de Sintaxis: operadores consecutivos en la expresión '" + exp + "'.",
                    "ERROR DE SINTAXIS");
        if (exp.contains("+")) {
            String[] p = exp.split("\\+", 2);
            return getValInt(p[0]) + evaluarInt(p[1]);
        }
        if (exp.contains("-")) {
            String[] p = exp.split("-", 2);
            return getValInt(p[0]) - evaluarInt(p[1]);
        }
        if (exp.contains("*")) {
            String[] p = exp.split("\\*", 2);
            return getValInt(p[0]) * evaluarInt(p[1]);
        }
        if (exp.contains("/")) {
            String[] p = exp.split("/", 2);
            long divisor = evaluarInt(p[1]);
            if (divisor == 0)
                throw new LenguajeException("Error en ALTO: división entre cero no permitida.", "ERROR SEMÁNTICO");
            return getValInt(p[0]) / divisor;
        }
        return getValInt(exp);
    }

    private double evaluarGrande(String exp) throws LenguajeException {
        exp = exp.trim();
        if (exp.matches(".*[+\\-*/]\\s*$"))
            throw new LenguajeException("Error de Sintaxis: falta un operando al final de la expresión.",
                    "ERROR DE SINTAXIS");
        if (exp.matches(".*[+\\-*/]{2,}.*"))
            throw new LenguajeException(
                    "Error de Sintaxis: operadores consecutivos en la expresión '" + exp + "'.",
                    "ERROR DE SINTAXIS");
        if (exp.contains("+")) {
            String[] p = exp.split("\\+", 2);
            return getValGrande(p[0]) + evaluarGrande(p[1]);
        }
        if (exp.contains("-")) {
            String[] p = exp.split("-", 2);
            return getValGrande(p[0]) - evaluarGrande(p[1]);
        }
        if (exp.contains("*")) {
            String[] p = exp.split("\\*", 2);
            return getValGrande(p[0]) * evaluarGrande(p[1]);
        }
        if (exp.contains("/")) {
            String[] p = exp.split("/", 2);
            double divisor = evaluarGrande(p[1]);
            if (divisor == 0.0)
                throw new LenguajeException("Error en GRANDE: división por cero no permitida.", "ERROR SEMÁNTICO");
            return getValGrande(p[0]) / divisor;
        }
        return getValGrande(exp);
    }

    private String evaluarVenti(String exp) throws LenguajeException {
        exp = exp.trim();
        if (exp.matches(".*[\\-*/].*"))
            throw new LenguajeException(
                    "Error de Tipo: solo puedes usar '+' para concatenar 'venti'. Restar, multiplicar o dividir textos no está permitido.",
                    "ERROR DE TIPO");
        if (exp.contains("+")) {
            String[] p = exp.split("\\+", 2);
            return getValVenti(p[0]) + evaluarVenti(p[1]);
        }
        return getValVenti(exp);
    }

    private long getValInt(String s) throws LenguajeException {
        s = s.trim();
        if (memoria.containsKey(s)) {
            Object val = memoria.get(s);
            if (!(val instanceof Long) && !(val instanceof Integer))
                throw new LenguajeException(
                        "Conflicto de Tipos: la variable '" + s + "' no contiene un número 'alto' (entero).",
                        "ERROR DE TIPO");
            return ((Number) val).longValue();
        }
        if (s.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))
            throw new LenguajeException("Error de Referencia: la variable '" + s + "' no ha sido definida.",
                    "ERROR SEMÁNTICO");
        if (s.contains("."))
            throw new LenguajeException(
                    "Error de Tipo: un valor 'alto' no puede tener decimales (" + s + "). Usa 'grande'.",
                    "ERROR DE TIPO");
        if (!s.matches("-?[0-9]+"))
            throw new LenguajeException("Sintaxis Inválida: '" + s + "' no es un número entero válido.",
                    "ERROR DE SINTAXIS");
        // (Ex. 11) Overflow
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new LenguajeException(
                    "Desbordamiento: el número '" + s + "' excede los 10 dígitos permitidos para 'alto'.",
                    "ERROR DE LÍMITES");
        }
    }

    private double getValGrande(String s) throws LenguajeException {
        s = s.trim();
        if (memoria.containsKey(s)) {
            Object val = memoria.get(s);
            if (val instanceof Double)
                return (double) val;
            if (val instanceof Long || val instanceof Integer)
                return ((Number) val).doubleValue(); // grande acepta enteros
            throw new LenguajeException(
                    "Conflicto de Tipos: la variable '" + s + "' no es numérica.",
                    "ERROR DE TIPO");
        }
        if (s.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))
            throw new LenguajeException("Error de Referencia: la variable '" + s + "' no ha sido definida.",
                    "ERROR SEMÁNTICO");
        if (s.matches(".*\\.$") || s.matches(".*\\.[^0-9].*"))
            throw new LenguajeException(
                    "Error de Sintaxis Decimal: se detectó un punto en '" + s
                            + "' pero falta el número después del punto.",
                    "ERROR DE SINTAXIS");
        if (!s.matches("-?[0-9]{1,10}(\\.[0-9]{1,10})?")) {
            if (s.matches("-?[0-9]+(\\.[0-9]+)?"))
                throw new LenguajeException(
                        "Límite Excedido: '" + s + "' solo puede tener hasta 10 dígitos antes y 10 después del punto.",
                        "ERROR DE LÍMITES");
            throw new LenguajeException(
                    "Sintaxis Inválida para 'grande': '" + s + "' contiene caracteres no permitidos.",
                    "ERROR DE SINTAXIS");
        }
        return Double.parseDouble(s);
    }

    private String getValVenti(String s) throws LenguajeException {
        s = s.trim();
        if (memoria.containsKey(s))
            return String.valueOf(memoria.get(s));
        if (s.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))
            throw new LenguajeException("Error de Referencia: la variable '" + s + "' no ha sido definida.",
                    "ERROR SEMÁNTICO");
        if ((!s.startsWith("\"") || !s.endsWith("\"")) && (!s.startsWith("'") || !s.endsWith("'")))
            throw new LenguajeException(
                    "Error de Sintaxis 'venti': el texto '" + s
                            + "' debe estar encerrado entre comillas dobles o simples.",
                    "ERROR DE SINTAXIS");
        return s.substring(1, s.length() - 1);
    }

    private void mostrarDiccionario() {
        txtConsola.setText("");
        imprimirEnConsola("--- Diccionario del Lenguaje VGV ---\n\n", ACCENT_CYAN);
        imprimirEnConsola(String.format("%-22s %-22s%n", "Lexema", "Tipo"), ACCENT_PINK);
        imprimirEnConsola("─".repeat(44) + "\n", BG_PANEL.brighter());

        String regex = "(alto|grande|venti)"
                + "|([0-9]{1,10}\\.[0-9]{1,10})"
                + "|([0-9]{1,10})"
                + "|(\"[^\"]*\"|'[^']*')"
                + "|([~+\\-*/;])"
                + "|([a-zA-Z_][a-zA-Z0-9_]*)";

        String textoSinComentarios = String.join("\n",
                java.util.Arrays.stream(getTextoEditor().split("\n"))
                        .filter(l -> !l.trim().startsWith("#") && !l.trim().startsWith("//"))
                        .toArray(String[]::new));

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textoSinComentarios);

        while (matcher.find()) {
            String lexema = matcher.group();
            String tipo = "";
            if (matcher.group(1) != null)
                tipo = "palabra reservada";
            else if (matcher.group(2) != null)
                tipo = "constante grande";
            else if (matcher.group(3) != null)
                tipo = "constante alto";
            else if (matcher.group(4) != null)
                tipo = "constante venti";
            else if (matcher.group(5) != null) {
                if (lexema.equals("~"))
                    tipo = "operador asignación";
                else if (lexema.equals(";"))
                    tipo = "break";
                else
                    tipo = "operador aritmético";
            } else if (matcher.group(6) != null)
                tipo = "identificador";

            Color c = tipo.equals("palabra reservada") ? ACCENT_PINK
                    : tipo.equals("identificador") ? ACCENT_CYAN
                            : tipo.startsWith("operador") ? ACCENT_ORANGE
                                    : FG_TEXT;
            imprimirEnConsola(String.format("%-22s %-22s%n", lexema, tipo), c);
        }
        imprimirEnConsola("\n--- Fin del Diccionario ---\n", FG_TEXT);
    }

    private void explicarCodigoDetallado() {

        txtConsola.setText("");

        imprimirEnConsola(
                "--- ANÁLISIS GRAMÁTICO DEL CÓDIGO ---\n\n",
                ACCENT_CYAN);

        String[] lineas = getTextoEditor().split("\n");

        for (int i = 0; i < lineas.length; i++) {

            String original = lineas[i];
            String linea = original.trim();

            imprimirEnConsola(
                    "LÍNEA " + (i + 1) + ": " + original + "\n",
                    FG_TEXT);

            if (linea.isEmpty()) {

                imprimirEnConsola(
                        "→ Línea vacía.\n\n",
                        SYN_COMMENT);

                continue;
            }

            if (linea.startsWith("#") || linea.startsWith("//")) {

                imprimirEnConsola(
                        "→ Comentario detectado.\n\n",
                        SYN_COMMENT);

                continue;
            }

            if (!linea.endsWith(";")) {

                imprimirEnConsola(
                        "ERROR DE SINTAXIS: falta ';' al final.\n\n",
                        BTN_RED);

                continue;
            }

            String sinPuntoComa = linea.substring(0, linea.length() - 1).trim();

            if (!sinPuntoComa.contains("~")) {

                imprimirEnConsola(
                        "ERROR DE SINTAXIS: falta operador '~'.\n\n",
                        BTN_RED);

                continue;
            }

            String[] partes = sinPuntoComa.split("~", 2);

            String izquierda = partes[0].trim();
            String derecha = partes[1].trim();

            if (derecha.isEmpty()) {

                imprimirEnConsola(
                        "ERROR: no existe expresión después de '~'.\n\n",
                        BTN_RED);

                continue;
            }

            String[] tokensIzq = izquierda.split("\\s+");

            String nombre = "";
            String tipo = "";

            boolean declaracion = false;

            if (tokensIzq.length == 2) {

                declaracion = true;

                nombre = tokensIzq[0];
                tipo = tokensIzq[1];

                imprimirEnConsola(
                        "TIPO DE INSTRUCCIÓN: DECLARACIÓN\n",
                        ACCENT_GREEN);

                String reglaSintactica = "";

                if (tipo.equals("alto")) {
                    reglaSintactica = "<expresión>: <identificador>: <tipo de dato>: <asignación>: <expresión>: <literal alto>: <cierre>";
                } else if (tipo.equals("grande")) {
                    reglaSintactica = "<expresión>: <identificador>: <tipo de dato>: <asignación>: <expresión>: <literal grande>: <cierre>";
                } else if (tipo.equals("venti")) {
                    reglaSintactica = "<expresión>: <identificador>: <tipo de dato>: <asignación>: <expresión>: <literal venti>: <cierre>";
                }

                imprimirEnConsola(reglaSintactica + "\n\n", ACCENT_PINK);

            } else if (tokensIzq.length == 1) {

                declaracion = false;

                nombre = tokensIzq[0];

                imprimirEnConsola(
                        "TIPO DE INSTRUCCIÓN: REASIGNACIÓN\n",
                        ACCENT_GREEN);

                String reglaSintactica = "";

                if (tipo.equals("alto")) {
                    reglaSintactica = "<expresión>: <identificador>: <tipo de dato>: <asignación>: <expresión>: <literal alto>: <cierre>";
                } else if (tipo.equals("grande")) {
                    reglaSintactica = "<expresión>: <identificador>: <tipo de dato>: <asignación>: <expresión>: <literal grande>: <cierre>";
                } else if (tipo.equals("venti")) {
                    reglaSintactica = "<expresión>: <identificador>: <tipo de dato>: <asignación>: <expresión>: <literal venti>: <cierre>";
                }

                imprimirEnConsola(reglaSintactica + "\n\n", ACCENT_PINK);

                if (!memoria.containsKey(nombre)) {

                    imprimirEnConsola(
                            "ERROR SEMÁNTICO: variable no declarada.\n\n",
                            BTN_RED);

                    continue;
                }

                Object valor = memoria.get(nombre);

                if (valor instanceof Long || valor instanceof Integer)
                    tipo = "alto";

                else if (valor instanceof Double)
                    tipo = "grande";

                else if (valor instanceof String)
                    tipo = "venti";

            } else {

                imprimirEnConsola(
                        "ERROR DE SINTAXIS: lado izquierdo inválido.\n\n",
                        BTN_RED);

                continue;
            }

            imprimirEnConsola(
                    "ANÁLISIS DE COMPONENTES:\n",
                    ACCENT_CYAN);

            imprimirEnConsola(
                    nombre + " → identificador\n",
                    FG_TEXT);

            if (!nombre.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {

                imprimirEnConsola(
                        "ERROR: identificador inválido.\n\n",
                        BTN_RED);

                continue;
            }

            if (nombre.equals("alto")
                    || nombre.equals("grande")
                    || nombre.equals("venti")) {

                imprimirEnConsola(
                        "ERROR: palabra reservada usada como variable.\n\n",
                        BTN_RED);

                continue;
            }

            if (declaracion) {

                imprimirEnConsola(
                        tipo + " → tipo de dato\n",
                        ACCENT_PINK);

                if (!tipo.equals("alto")
                        && !tipo.equals("grande")
                        && !tipo.equals("venti")) {

                    imprimirEnConsola(
                            "ERROR: tipo de dato desconocido.\n\n",
                            BTN_RED);

                    continue;
                }
            }

            imprimirEnConsola(
                    "~ → operador de asignación\n",
                    ACCENT_ORANGE);

            imprimirEnConsola(
                    derecha + " → expresión\n",
                    FG_TEXT);

            imprimirEnConsola(
                    "; → fin de instrucción\n\n",
                    FG_TEXT);

            explicarExpresion(derecha, tipo);

            imprimirEnConsola(
                    "\n----------------------------------------\n\n",
                    SYN_COMMENT);
        }

        imprimirEnConsola(
                "--- FIN DEL ANÁLISIS ---\n",
                ACCENT_CYAN);
    }

    private void explicarExpresion(String exp, String tipo) {

        imprimirEnConsola(
                "ANÁLISIS DE EXPRESIÓN:\n",
                ACCENT_CYAN);

        String regex = "\"[^\"]*\"|'[^']*'"
                + "|[a-zA-Z_][a-zA-Z0-9_]*"
                + "|-?[0-9]+(\\.[0-9]+)?"
                + "|[+\\-*/]";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(exp);

        while (matcher.find()) {

            String token = matcher.group();

            if (token.matches("[+\\-*/]")) {

                imprimirEnConsola(
                        token + " → operador\n",
                        ACCENT_ORANGE);

            } else if (token.matches("-?[0-9]+")) {

                imprimirEnConsola(
                        token + " → literal entero (alto)\n",
                        SYN_NUMBER);

            } else if (token.matches("-?[0-9]+\\.[0-9]+")) {

                imprimirEnConsola(
                        token + " → literal decimal (grande)\n",
                        SYN_NUMBER);

            } else if ((token.startsWith("\"") && token.endsWith("\""))
                    || (token.startsWith("'") && token.endsWith("'"))) {

                imprimirEnConsola(
                        token + " → literal texto (venti)\n",
                        SYN_STRING);

            } else {

                if (memoria.containsKey(token)) {

                    imprimirEnConsola(
                            token
                                    + " → variable previamente declarada\n",
                            ACCENT_GREEN);

                } else {

                    imprimirEnConsola(
                            token
                                    + " → identificador no declarado\n",
                            BTN_RED);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new InterfazLenguaje().setVisible(true));
    }
}