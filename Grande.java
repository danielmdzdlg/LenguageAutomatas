
public class Grande {
    private double valor;

    public Grande(double valor) throws LenguajeException {
        validar(valor);
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    private void validar(double v) throws LenguajeException {
        String texto = String.valueOf(v);
        String[] partes = texto.split("\\.");

        String enteros = partes[0].replace("-", "");
        String decimales = partes.length > 1 ? partes[1] : "";

        if (decimales.length() > 8) {
            throw new LenguajeException("Error en GRANDE: Máximo 8 dígitos decimales.");
        }
        if ((enteros.length() + decimales.length()) > 10) {
            throw new LenguajeException("Error en GRANDE: El total de dígitos no puede ser mayor a 10.");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}