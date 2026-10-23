/** Heranca: ProdutoFisico e um Produto. */
public class ProdutoFisico extends Produto {
    private final double pesoKg;

    public ProdutoFisico(int id, String nome, double preco, Categoria categoria, double pesoKg) {
        super(id, nome, preco, categoria);
        this.pesoKg = pesoKg;
    }
    public double getPesoKg() { return pesoKg; }
    @Override public String getTipo() { return "Fisico"; }
}
