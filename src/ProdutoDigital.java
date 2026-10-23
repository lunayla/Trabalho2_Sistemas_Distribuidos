/** Heranca: ProdutoDigital e um Produto. */
public class ProdutoDigital extends Produto {
    private final String formato;

    public ProdutoDigital(int id, String nome, double preco, Categoria categoria, String formato) {
        super(id, nome, preco, categoria);
        this.formato = formato;
    }
    public String getFormato() { return formato; }
    @Override public String getTipo() { return "Digital"; }
}
