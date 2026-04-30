
public class Operaciones {

    // Operaciones para ALTO
    public Alto sumar(Alto a, Alto b) throws LenguajeException {
        return new Alto(a.getValor() + b.getValor());
    }

    public Alto restar(Alto a, Alto b) throws LenguajeException {
        return new Alto(a.getValor() - b.getValor());
    }

    public Alto multiplicar(Alto a, Alto b) throws LenguajeException {
        return new Alto(a.getValor() * b.getValor());
    }

    public Alto dividir(Alto a, Alto b) throws LenguajeException {
        if (b.getValor() == 0)
            throw new LenguajeException("Error en ALTO: división entre cero no permitida.", "ERROR SEMÁNTICO");
        return new Alto(a.getValor() / b.getValor());
    }

    // Operaciones para GRANDE
    public Grande sumar(Grande a, Grande b) throws LenguajeException {
        return new Grande(a.getValor() + b.getValor());
    }

    public Grande restar(Grande a, Grande b) throws LenguajeException {
        return new Grande(a.getValor() - b.getValor());
    }

    public Grande multiplicar(Grande a, Grande b) throws LenguajeException {
        return new Grande(a.getValor() * b.getValor());
    }

    public Grande dividir(Grande a, Grande b) throws LenguajeException {
        if (b.getValor() == 0)
            throw new LenguajeException("Error en Grande: división entre cero no permitida.", "ERROR SEMÁNTICO");
        return new Grande(a.getValor() / b.getValor());
    }

    // Operación para VENTI
    public Venti concatenar(Venti a, Venti b) {
        return new Venti(a.getValor() + b.getValor());
    }
}