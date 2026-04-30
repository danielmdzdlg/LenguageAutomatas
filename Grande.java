
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

        if (!texto.matches("-?[0-9]{1,10}\\.[0-9]{1,10}")) {
            throw new LenguajeException(
                    "Error en GRANDE: debe tener máximo 10 dígitos a la izquierda y máximo 10 a la derecha del punto.");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}