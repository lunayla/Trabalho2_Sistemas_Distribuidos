import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;

/** Interface de texto da loja virtual - sem sockets manuais. */
public class Cliente {
    public static void main(String[] args) {
        LojaRemota loja = null;
        OuvinteAvisos ouvinte = null;
        try (Scanner teclado = new Scanner(System.in)) {
            Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);
            loja = (LojaRemota) registro.lookup("Loja");
            Comunicacao comunicacao = new Comunicacao(loja);

            System.out.print("Usuario (cliente/admin): ");
            String usuario = teclado.nextLine().trim();
            System.out.print("Senha: ");
            String senha = teclado.nextLine();
            Map<String, String> login = JsonSimples.ler(
                    comunicacao.chamar("autenticar", JsonSimples.criar("usuario", usuario, "senha", senha)));
            if (!"OK".equals(login.get("status"))) {
                System.out.println("Login invalido.");
                return;
            }

            ouvinte = new OuvinteAvisos();
            loja.entrar(ouvinte); 
            System.out.println("Conectado a loja!");
            boolean admin = "admin".equals(usuario);
            while (true) {
                System.out.println("\n1 - Listar produtos");
                if (admin) {
                    System.out.println("2 - Consultar total de pedidos");
                    System.out.println("3 - Enviar aviso a todos os clientes");
                } else {
                    System.out.println("2 - Fazer pedido");
                    System.out.println("3 - Consultar produto");
                }
                System.out.println("0 - Sair");
                System.out.print("Opcao: ");
                String opcao = teclado.nextLine().trim();
                if ("0".equals(opcao)) break;

                String metodo;
                String argumentos;
                try {
                    switch (opcao) {
                        case "1":
                            metodo = "listarProdutos";
                            argumentos = "{}";
                            break;
                        case "2":
                            if (admin) {
                                metodo = "consultarTotalPedidos";
                                argumentos = "{}";
                            } else {
                                System.out.print("Numero do pedido: ");
                                int pedido = Integer.parseInt(teclado.nextLine());
                                System.out.print("ID do produto: ");
                                int produto = Integer.parseInt(teclado.nextLine());
                                System.out.print("Quantidade: ");
                                int quantidade = Integer.parseInt(teclado.nextLine());
                                metodo = "registrarPedido";
                                argumentos = JsonSimples.criar("pedidoId", pedido, "cliente", usuario,
                                        "produtoId", produto, "quantidade", quantidade);
                            }
                            break;
                        case "3":
                            if (admin) {
                                System.out.print("Texto do aviso: ");
                                metodo = "enviarAviso";
                                argumentos = JsonSimples.criar("mensagem", teclado.nextLine());
                            } else {
                                System.out.print("ID do produto: ");
                                metodo = "consultarProduto";
                                argumentos = JsonSimples.criar("produtoId", Integer.parseInt(teclado.nextLine()));
                            }
                            break;
                        default:
                            System.out.println("Opcao invalida.");
                            continue;
                    }
                    Map<String, String> resposta = JsonSimples.ler(comunicacao.chamar(metodo, argumentos));
                    System.out.println(resposta.get("mensagem"));
                } catch (NumberFormatException e) {
                    System.out.println("Digite um numero inteiro valido.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro de conexao. Inicie Servidor.java antes de Cliente.java.");
            System.out.println("Detalhes: " + e.getMessage());
        } finally {
            if (loja != null && ouvinte != null) {
                try { loja.sair(ouvinte); } catch (Exception ignorada) { }
                try { UnicastRemoteObject.unexportObject(ouvinte, true); } catch (Exception ignorada) { }
            }
        }
    }
}
