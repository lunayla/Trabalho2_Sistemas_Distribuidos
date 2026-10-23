/** Entidade base dos produtos. Agregacao: Produto tem uma Categoria. */
public class Produto {
    private final int id;
    private final String nome;
    private final double preco;
    private final Categoria categoria;

    public Produto(int id, String nome, double preco, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public Categoria getCategoria() { return categoria; }

    public String getTipo() { return "Produto"; }

    @Override
    public String toString() {
        return id + " - " + nome + " - R$ " + String.format("%.2f", preco)
                + " | " + categoria.getNome() + " | " + getTipo();
    }
}
