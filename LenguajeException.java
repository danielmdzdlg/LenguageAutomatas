public class LenguajeException extends Exception {
    private String tipo;

    public LenguajeException(String mensaje, String tipo) {
        super(mensaje);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}