
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
        String[] columnas = { "Tipo de Token", "Lexema", "Descripción", "¿Es Reservada?" };
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
        gbc.gridy = 3;
        gbc.weighty = 0.05;
        add(panelBotones, gbc);

        btnAnalizar.addActionListener(e -> generarTablaTokens());
        btnEjecutar.addActionListener(e -> ejecutarCodigoReal());
    }

    private void generarTablaTokens() {
        modeloTabla.setRowCount(0);
        String regex = "(alto|grande|venti)|([0-9]+\\.[0-9]+)|([0-9]+)|(\"[^\"]*\")|([=+\\-*/])|([a-zA-Z_][a-zA-Z0-9_]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txtCodigo.getText());

        while (matcher.find()) {
            String lexema = matcher.group();
            String tipo = "", desc = "", res = "No";

            if (matcher.group(1) != null) {
                tipo = "TIPO_DATO";
                desc = "Palabra clave";
                res = "Sí";
            } else if (matcher.group(2) != null) {
                tipo = "LITERAL_GRANDE";
                desc = "Decimal";
            } else if (matcher.group(3) != null) {
                tipo = "LITERAL_ALTO";
                desc = "Entero";
            } else if (matcher.group(4) != null) {
                tipo = "LITERAL_VENTI";
                desc = "Texto";
            } else if (matcher.group(5) != null) {
                tipo = "OPERADOR";
                desc = "Operación";
            } else if (matcher.group(6) != null) {
                tipo = "IDENTIFICADOR";
                desc = "Variable";
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
            if (linea.isEmpty() || linea.startsWith("//"))
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
            int res = evaluarInt(exp);
            Alto a = new Alto(res);
            memoria.put(nombre, a.getValor());
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

    private int evaluarInt(String exp) {
        if (exp.contains("+"))
            return getValInt(exp.split("\\+")[0]) + getValInt(exp.split("\\+")[1]);
        if (exp.contains("-"))
            return getValInt(exp.split("-")[0]) - getValInt(exp.split("-")[1]);
        if (exp.contains("*"))
            return getValInt(exp.split("\\*")[0]) * getValInt(exp.split("\\*")[1]);
        if (exp.contains("/"))
            return getValInt(exp.split("/")[0]) / getValInt(exp.split("/")[1]);
        return getValInt(exp);
    }

    private double evaluarDouble(String exp) {
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

    private String evaluarVenti(String exp) {
        if (exp.contains("+")) {
            String[] p = exp.split("\\+");
            return getValVenti(p[0]) + getValVenti(p[1]);
        }
        return getValVenti(exp);
    }

    private int getValInt(String s) {
        s = s.trim();
        if (!memoria.containsKey(s)) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                throw new NullPointerException(s);
            }
        }
        return (int) memoria.get(s);
    }

    private double getValDouble(String s) {
        s = s.trim();
        if (!memoria.containsKey(s)) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                throw new NullPointerException(s);
            }
        }
        return (double) memoria.get(s);
    }

    private String getValVenti(String s) {
        s = s.trim().replace("\"", "");
        return memoria.containsKey(s) ? (String) memoria.get(s) : s;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> new InterfazLenguaje().setVisible(true));
    }
}