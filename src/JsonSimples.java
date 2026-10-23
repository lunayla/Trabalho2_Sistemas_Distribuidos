import java.util.LinkedHashMap;
import java.util.Map;

/** JSON simples para objetos com valores String, numero, booleano ou null. */
public final class JsonSimples {
    private JsonSimples() { }

    public static String criar(Object... campos) {
        if (campos.length % 2 != 0) throw new IllegalArgumentException("Campos em pares");
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < campos.length; i += 2) {
            if (i > 0) json.append(',');
            json.append('"').append(escapar(String.valueOf(campos[i]))).append("\":");
            Object valor = campos[i + 1];
            if (valor == null) json.append("null");
            else if (valor instanceof Number || valor instanceof Boolean) json.append(valor);
            else json.append('"').append(escapar(valor.toString())).append('"');
        }
        return json.append('}').toString();
    }

    public static Map<String, String> ler(String json) {
        Leitor l = new Leitor(json);
        Map<String, String> dados = new LinkedHashMap<>();
        l.espacos();
        l.exigir('{');
        l.espacos();
        if (l.consumir('}')) { l.finalizar(); return dados; }
        while (true) {
            String chave = l.string();
            l.espacos();
            l.exigir(':');
            l.espacos();
            String valor;
            if (l.proximo() == '"') {
                valor = l.string();
            } else {
                int inicio = l.pos;
                while (!l.fim() && l.proximo() != ',' && l.proximo() != '}') l.pos++;
                valor = json.substring(inicio, l.pos).trim();
                if (!valor.matches("-?[0-9]+(\\.[0-9]+)?|true|false|null"))
                    throw new IllegalArgumentException("Valor JSON invalido: " + valor);
                if (valor.equals("null")) valor = null;
            }
            dados.put(chave, valor);
            l.espacos();
            if (l.consumir('}')) break;
            l.exigir(',');
            l.espacos();
        }
        l.finalizar();
        return dados;
    }

    private static String escapar(String valor) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '"': s.append("\\\""); break;
                case '\\': s.append("\\\\"); break;
                case '\n': s.append("\\n"); break;
                case '\r': s.append("\\r"); break;
                case '\t': s.append("\\t"); break;
                default:
                    if (c < 0x20) s.append(String.format("\\u%04x", (int) c));
                    else s.append(c);
            }
        }
        return s.toString();
    }

    private static class Leitor {
        final String entrada;
        int pos;
        Leitor(String entrada) { this.entrada = entrada; }
        boolean fim() { return pos >= entrada.length(); }
        char proximo() {
            if (fim()) throw new IllegalArgumentException("JSON incompleto");
            return entrada.charAt(pos);
        }
        void espacos() { while (!fim() && Character.isWhitespace(entrada.charAt(pos))) pos++; }
        boolean consumir(char c) {
            espacos();
            if (!fim() && entrada.charAt(pos) == c) { pos++; return true; }
            return false;
        }
        void exigir(char c) { if (!consumir(c)) throw new IllegalArgumentException("Esperado '" + c + "'"); }
        void finalizar() { espacos(); if (!fim()) throw new IllegalArgumentException("JSON excedente"); }
        String string() {
            espacos();
            exigir('"');
            StringBuilder b = new StringBuilder();
            boolean fechou = false;
            while (!fim()) {
                char c = entrada.charAt(pos++);
                if (c == '"') { fechou = true; break; }
                if (c == '\\') {
                    if (fim()) throw new IllegalArgumentException("Escape JSON incompleto");
                    c = entrada.charAt(pos++);
                    switch (c) {
                        case 'n': b.append('\n'); break;
                        case 'r': b.append('\r'); break;
                        case 't': b.append('\t'); break;
                        case '"': b.append('"'); break;
                        case '\\': b.append('\\'); break;
                        case '/': b.append('/'); break;
                        case 'u':
                            if (pos + 4 > entrada.length()) throw new IllegalArgumentException("Unicode incompleto");
                            b.append((char) Integer.parseInt(entrada.substring(pos, pos + 4), 16));
                            pos += 4;
                            break;
                        default: throw new IllegalArgumentException("Escape invalido: " + c);
                    }
                } else b.append(c);
            }
            if (!fechou) throw new IllegalArgumentException("String JSON sem aspas finais");
            return b.toString();
        }
    }
}
