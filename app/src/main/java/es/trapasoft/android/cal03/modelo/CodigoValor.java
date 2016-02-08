package es.trapasoft.android.cal03.modelo;

/**
 * Created by Administrador on 08/02/2016.
 */
public class CodigoValor {

    private long codigo;
    private String valor;

    public CodigoValor(long codigo, String valor) {
        this.codigo = codigo;
        this.valor = valor;
    }

    public CodigoValor() {
        codigo=0;
        valor="";
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String toString() {
        return codigo + ": " + valor;
    }
}
