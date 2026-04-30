
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
        if (!String.valueOf(Math.abs(v)).matches("[0-9]{1,10}")) {
            throw new LenguajeException("Error en ALTO: un int solo puede tener máximo 10 dígitos.");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}