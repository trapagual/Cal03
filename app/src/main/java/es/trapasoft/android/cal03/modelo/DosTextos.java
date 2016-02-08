package es.trapasoft.android.cal03.modelo;

/**
 * Created by Administrador on 08/02/2016.
 */
public class DosTextos {
    private String primerTexto;
    private String segundoTexto;

    public DosTextos(String primerTexto, String segundoTexto) {
        this.primerTexto = primerTexto;
        this.segundoTexto = segundoTexto;
    }

    public DosTextos() {
    }

    public String getPrimerTexto() {
        return primerTexto;
    }

    public void setPrimerTexto(String primerTexto) {
        this.primerTexto = primerTexto;
    }

    public String getSegundoTexto() {
        return segundoTexto;
    }

    public void setSegundoTexto(String segundoTexto) {
        this.segundoTexto = segundoTexto;
    }

    @Override
    public String toString() {
        return  primerTexto + " " + segundoTexto ;
    }
}
