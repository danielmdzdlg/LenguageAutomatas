
public class Alto {
    private int valor;

    public Alto(int valor) throws LenguajeException {
        validar(valor);
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    private void validar(int v) throws LenguajeException {
        if (String.valueOf(Math.abs(v)).length() > 10) {
            throw new LenguajeException("Error en ALTO: El valor excede los 10 dígitos permitidos.");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}