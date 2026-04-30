
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfazLenguaje extends JFrame {
    private JTextArea txtCodigo;
    private JTextArea txtConsola;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private Map<String, Object> memoria = new HashMap<>();

    public InterfazLenguaje() {
        setTitle("Intérprete VGV - Daniel Mendoza (ITO)");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Editor de Código
        txtCodigo = new JTextArea(12, 50);
        txtCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtCodigo.setBorder(BorderFactory.createTitledBorder("Editor de Código"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        add(new JScrollPane(txtCodigo), gbc);

        // 2. Tabla de Tokens
        String[] columnas = { "Token", "Lexema", "Patrón", "¿Es Reservada?" };
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaTokens = new JTable(modeloTabla);
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        add(new JScrollPane(tablaTokens), gbc);

        // 3. Consola de Salida
        txtConsola = new JTextArea(10, 50);
        txtConsola.setEditable(false);
        txtConsola.setBackground(new Color(25, 25, 25));
        txtConsola.setForeground(new Color(0, 255, 100));
        txtConsola.setFont(new Font("Monospaced", Font.BOLD, 13));
        txtConsola.setBorder(BorderFactory.createTitledBorder(null, "Consola", 0, 0, null, Color.WHITE));
        gbc.gridy = 2;
        gbc.weighty = 0.25;
        add(new JScrollPane(txtConsola), gbc);

        // 4. Panel de Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAnalizar = new JButton("Generar Tabla de Tokens");
        
        // --- BOTÓN LIMPIAR ---
        JButton btnLimpiar = new JButton("Limpiar Todo");
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setBackground(new Color(220, 53, 69)); // Rojo
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 14));

        // --- BOTÓN EJECUTAR (FORZANDO COLOR) ---
        JButton btnEjecutar = new JButton("Ejecutar Código");
        btnEjecutar.setOpaque(true);
        btnEjecutar.setBorderPainted(false);
        btnEjecutar.setBackground(new Color(255, 140, 0)); // Naranja vibrante
        btnEjecutar.setForeground(Color.BLACK); // Letras negras para máximo contraste
        btnEjecutar.setFont(new Font("Arial", Font.BOLD, 14));

        panelBotones.add(btnAnalizar);
        panelBotones.add(btnEjecutar);
        panelBotones.add(btnLimpiar);
        gbc.gridy = 3;
        gbc.weighty = 0.05;
        add(panelBotones, gbc);

        btnAnalizar.addActionListener(e -> generarTablaTokens());
        btnEjecutar.addActionListener(e -> ejecutarCodigoReal());
        btnLimpiar.addActionListener(e -> {         
        txtCodigo.setText("");
        txtConsola.setText("");
        modeloTabla.setRowCount(0);
        memoria.clear();
});
    }

    private void generarTablaTokens() {
        modeloTabla.setRowCount(0);
        String regex = "(alto|grande|venti)" +
                "|([0-9]{1,10}\\.[0-9]{1,10})" +
                "|([0-9]{1,10})" +
                "|(\"[^\"]*\")" +
                "|([=+\\-*/;])" +
                "|([a-zA-Z_][a-zA-Z0-9_]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txtCodigo.getText());

        while (matcher.find()) {
            String lexema = matcher.group();
            String tipo = "", desc = "", res = "No";

            if (matcher.group(1) != null) {
                tipo = "TIPO_DATO";
                desc = "(alto|grande|venti)";
                res = "Sí";
            } else if (matcher.group(2) != null) {
                tipo = "LITERAL_GRANDE";
                desc = "[0-9]{1,10}\\.[0-9]{1,10}";
            } else if (matcher.group(3) != null) {
                tipo = "LITERAL_ALTO";
                desc = "[0-9]{1,10}";
            } else if (matcher.group(4) != null) {
                tipo = "LITERAL_VENTI";
                desc = "\"[^\"]*\"";
            } else if (matcher.group(5) != null) {
                tipo = "OPERADOR";
                desc = "[=+\\-*/;]";
            } else if (matcher.group(6) != null) {
                tipo = "IDENTIFICADOR";
                desc = "[a-zA-Z_][a-zA-Z0-9_]*";
            }

            modeloTabla.addRow(new Object[] { tipo, lexema, desc, res });
        }
    }

    private void ejecutarCodigoReal() {
        txtConsola.setText("--- Iniciando ejecución ---\n");
        memoria.clear();
        String[] lineas = txtCodigo.getText().split("\n");

        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty() || linea.startsWith("//") || linea.startsWith("#"))
                continue;

            try {
                procesarLinea(linea);
            } catch (LenguajeException ex) {
                txtConsola.append("[ERROR DE LÍMITES - LÍNEA " + (i + 1) + "] " + ex.getMessage() + "\n");
            } catch (NullPointerException ex) {
                txtConsola.append(
                        "[ERROR SEMÁNTICO - LÍNEA " + (i + 1) + "] Variable '" + ex.getMessage() + "' no definida.\n");
            } catch (NumberFormatException ex) {
                txtConsola.append("[ERROR DE SINTAXIS - LÍNEA " + (i + 1) + "] Valor numérico inválido.\n");
            } catch (Exception ex) {
                txtConsola.append("[ERROR DE SINTAXIS - LÍNEA " + (i + 1) + "] Estructura incorrecta.\n");
            }
        }
        txtConsola.append("\n--- Ejecución terminada ---");
    }

    private void procesarLinea(String linea) throws LenguajeException {
        if (!linea.contains("="))
            throw new RuntimeException();
        String[] partes = linea.split("=");
        String[] decl = partes[0].trim().split("\\s+");
        String exp = partes[1].trim();
        String tipo = decl[0];
        String nombre = (decl.length > 1) ? decl[1] : decl[0];

        if (tipo.equals("alto")) {
            long res = evaluarInt(exp);
            Alto a = new Alto(res);
            memoria.put(nombre, res);
            txtConsola.append("[ALTO] " + nombre + " = " + a + "\n");
        } else if (tipo.equals("grande")) {
            double res = evaluarDouble(exp);
            Grande g = new Grande(res);
            memoria.put(nombre, g.getValor());
            txtConsola.append("[GRANDE] " + nombre + " = " + g + "\n");
        } else if (tipo.equals("venti")) {
            String res = evaluarVenti(exp);
            memoria.put(nombre, res);
            txtConsola.append("[VENTI] " + nombre + " = \"" + res + "\"\n");
        } else {
            throw new RuntimeException(); // Error de tipo desconocido
        }
    }


    private long evaluarInt(String exp) throws LenguajeException {
        if (exp.matches(".*[+\\-*/]\\s*$")) {
            throw new LenguajeException("Error de sintaxis: falta un operando.");
        }

        if (exp.contains("+")) {
            String[] p = exp.split("\\+");
            if (p.length < 2)
                throw new LenguajeException("Error de sintaxis: falta un operando.");
            return getValInt(p[0]) + getValInt(p[1]);
        }

        if (exp.contains("-")) {
            String[] p = exp.split("-");
            if (p.length < 2)
                throw new LenguajeException("Error de sintaxis: falta un operando.");
            return getValInt(p[0]) - getValInt(p[1]);
        }

        if (exp.contains("*")) {
            String[] p = exp.split("\\*");
            if (p.length < 2)
                throw new LenguajeException("Error de sintaxis: falta un operando.");
            return getValInt(p[0]) * getValInt(p[1]);
        }

        if (exp.contains("/")) {
            String[] p = exp.split("/");
            if (p.length < 2)
                throw new LenguajeException("Error de sintaxis: falta un operando.");
            if (getValInt(p[1]) == 0)
                throw new LenguajeException("Error: división por cero en ALTO.");
            return getValInt(p[0]) / getValInt(p[1]);
        }


        return getValInt(exp);
}

    private double evaluarDouble(String exp) throws LenguajeException  {
        if (exp.contains("+"))
            return getValDouble(exp.split("\\+")[0]) + getValDouble(exp.split("\\+")[1]);
        if (exp.contains("-"))
            return getValDouble(exp.split("-")[0]) - getValDouble(exp.split("-")[1]);
        if (exp.contains("*"))
            return getValDouble(exp.split("\\*")[0]) * getValDouble(exp.split("\\*")[1]);
        if (exp.contains("/"))
            return getValDouble(exp.split("/")[0]) / getValDouble(exp.split("/")[1]);
        return getValDouble(exp);
    }

private String evaluarVenti(String exp) throws LenguajeException {
            if (exp.contains("+")) {
            String[] p = exp.split("\\+");
            return getValVenti(p[0]) + getValVenti(p[1]);
        }
        return getValVenti(exp);
    }

    private long getValInt(String s) throws LenguajeException {
        s = s.trim();
        if (!memoria.containsKey(s)) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new LenguajeException("Error en ALTO: El valor excede los 10 dígitos permitidos.");
            } catch (Exception e) {
                throw new NullPointerException(s);
            }
        }
        return ((Number) memoria.get(s)).longValue();
    }


    private double getValDouble(String s) throws LenguajeException {
        s = s.trim();

        if (!memoria.containsKey(s)) {
            if (!s.matches("-?[0-9]{1,10}\\.[0-9]{1,10}")) {
                throw new LenguajeException(
                        "Error en GRANDE: formato inválido. Usa máximo 10 dígitos antes y después del punto.");
            }
            return Double.parseDouble(s);
        }

        return (double) memoria.get(s);
    }

    private String getValVenti(String s) throws LenguajeException {
        s = s.trim();

        if (memoria.containsKey(s)) {
            return (String) memoria.get(s);
        }

        if (!s.matches("\"[^\"]*\"")) {
            throw new LenguajeException("Error en VENTI: no debe haber caracteres después de las comillas.");
        }

        return s.substring(1, s.length() - 1);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> new InterfazLenguaje().setVisible(true));
    }
}