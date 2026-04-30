public class Grande {
    private double valor;

    public Grande(double valor) throws LenguajeException {
        this.valor = valor;
    }

    public Grande(String texto) throws LenguajeException {
        validar(texto);
        this.valor = Double.parseDouble(texto);
    }

    public double getValor() {
        return valor;
    }

    private void validar(String texto) throws LenguajeException {
        if (texto.startsWith("-")) texto = texto.substring(1);
        if (!texto.matches("[0-9]{1,10}\\.[0-9]{1,10}")) {
            throw new LenguajeException("Error en GRANDE: número mal escrito, formato inválido.", "ERROR DE SINTAXIS");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}