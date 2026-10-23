import java.util.Arrays;
import java.util.List;

/** Logica local no servidor: catalogo da loja. */
public class ProdutoService {
    private final List<Produto> produtos;

    public ProdutoService() {
        Categoria eletronicos = new Categoria(1, "Eletronicos");
        Categoria softwares = new Categoria(2, "Softwares");
        produtos = Arrays.asList(
            new ProdutoFisico(1, "Notebook", 3500.0, eletronicos, 2.0),
            new ProdutoFisico(2, "Mouse", 150.0, eletronicos, 0.15),
            new ProdutoFisico(3, "Teclado", 220.0, eletronicos, 0.8),
            new ProdutoDigital(4, "Curso de Java", 89.9, softwares, "PDF")
        );
    }
    public List<Produto> listar() { return produtos; }
    public Produto buscar(int id) {
        for (Produto p : produtos) if (p.getId() == id) return p;
        return null;
    }
}
