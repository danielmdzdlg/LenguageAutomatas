
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfazLenguaje extends JFrame {
    private JTextArea txtCodigo;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;

    public InterfazLenguaje() {
        setTitle("Analizador Léxico - Lenguaje VGV");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Superior: Entrada de Código
        txtCodigo = new JTextArea(10, 40);
        txtCodigo.setBorder(BorderFactory.createTitledBorder("Escribe tu código aquí"));
        add(new JScrollPane(txtCodigo), BorderLayout.NORTH);

        // Panel Central: Tabla de Tokens
        String[] columnas = { "Token", "Lexema", "Descripción" };
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaTokens = new JTable(modeloTabla);
        add(new JScrollPane(tablaTokens), BorderLayout.CENTER);

        // Panel Inferior: Botón de Análisis
        JButton btnAnalizar = new JButton("Generar Tabla de Tokens");
        btnAnalizar.setBackground(new Color(40, 167, 69));
        btnAnalizar.setForeground(Color.WHITE);
        btnAnalizar.addActionListener(e -> analizarCodigo());
        add(btnAnalizar, BorderLayout.SOUTH);
    }

    private void analizarCodigo() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        String codigo = txtCodigo.getText();

        // Definición de Patrones (Regex)
        String regex = "(alto|grande|venti)|([0-9]+\\.[0-9]+)|([0-9]+)|(\"[^\"]*\")|([=+\\-*/])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(codigo);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                modeloTabla.addRow(new Object[] { "PALABRA_RESERVADA", matcher.group(1), "Tipo de dato definido" });
            } else if (matcher.group(2) != null) {
                modeloTabla.addRow(new Object[] { "LITERAL_GRANDE", matcher.group(2), "Valor decimal" });
            } else if (matcher.group(3) != null) {
                modeloTabla.addRow(new Object[] { "LITERAL_ALTO", matcher.group(3), "Valor entero" });
            } else if (matcher.group(4) != null) {
                modeloTabla.addRow(new Object[] { "LITERAL_VENTI", matcher.group(4), "Cadena de texto" });
            } else if (matcher.group(5) != null) {
                modeloTabla.addRow(new Object[] { "OPERADOR", matcher.group(5), "Símbolo de operación" });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfazLenguaje().setVisible(true);
        });
    }
}