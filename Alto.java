
public class Alto {
    private long valor;

    public Alto(int valor) throws LenguajeException {
        validar(valor);
        this.valor = valor;
    }

    public Alto(long valor) throws LenguajeException {
        if (String.valueOf(Math.abs(valor)).length() > 10) {
            throw new LenguajeException("Error en ALTO: El valor excede los 10 dígitos permitidos.");
        }
        this.valor = valor;
    }

    public long getValor() {
        return valor;
    }

    private void validar(long v) throws LenguajeException {
        if (!String.valueOf(Math.abs(v)).matches("[0-9]{1,10}")) {
            throw new LenguajeException("Error en ALTO: un int solo puede tener máximo 10 dígitos.");

        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}