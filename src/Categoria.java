/** Entidade que classifica um produto. */
public class Categoria {
    private final int id;
    private final String nome;

    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
}
