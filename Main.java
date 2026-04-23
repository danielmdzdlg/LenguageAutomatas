
public class Main {
    public static void main(String[] args) {
        try {
            Operaciones op = new Operaciones();

            // Prueba ALTO
            Alto a = new Alto(100);
            Alto b = new Alto(50);
            System.out.println("Suma Alto: " + op.sumar(a, b));

            // Prueba GRANDE
            Grande g1 = new Grande(1.12345678);
            System.out.println("Grande OK: " + g1);

            // Prueba VENTI
            Venti v1 = new Venti("ITO ");
            Venti v2 = new Venti("Oaxaca");
            System.out.println("Venti: " + op.concatenar(v1, v2));

            // ESTO LANZARÁ ERROR (Mas de 8 decimales)
            System.out.println("Intentando crear Grande con 9 decimales...");
            Grande error = new Grande(1.123456789);

        } catch (LenguajeException e) {
            System.out.println(">>> " + e.getMessage());
        }
    }
}
