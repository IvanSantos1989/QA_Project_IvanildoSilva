import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Testes unitários para a classe Eleicao.
 * Verifica se a eleição funciona corretamente com candidatos, votos e regras de idade mínima.
 * */
class EleicaoTest {

    private Eleicao eleicao;
    private Eleitor eleitorMaior;
    private Eleitor eleitorMenor;
    private Candidato candidato1;
    private Candidato candidato2;

    /**
     * Setup: executa antes de cada teste, para evitar repetição.
     * Cria uma eleição, dois eleitores e dois candidatos.
     */
    @BeforeEach
    void setUp() {
        eleicao = new Eleicao("Eleições 2023", 18);
        eleitorMaior = new Eleitor("Ivan", "123", 20, "ivan@gmail.pt", "123456789");
        eleitorMenor = new Eleitor("Ana", "456", 16, "ana@gmail.pt", "987654321");
        candidato1 = new Candidato("Joana Almeida", 42, "Trofa");
        candidato2 = new Candidato("Ricardo Meireles", 50, "Maia");
    }

    /**
     * Testa se um candidato válido (idade suficiente) é adicionado corretamente.
     */
    @Test
    void adicionarCandidato_Valido() {
        eleicao.adicionarCandidato(candidato1);
        // O candidato deve estar presente na lista após adição.
        assertTrue(eleicao.getCandidatos().contains(candidato1));
    }

    /**
     * Testa se o sistema impede adicionar candidato com idade abaixo da mínima.
     */
    @Test
    void adicionarCandidato_IdadeInvalida() {
        Candidato jovem = new Candidato("Jovem", 17, "Lisboa");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eleicao.adicionarCandidato(jovem);
        });
        // Verifica a mensagem de erro esperada.
        assertEquals("Candidato não tem idade mínima para participar.", exception.getMessage());
        assertFalse(eleicao.getCandidatos().contains(jovem));
    }

    /**
     * Testa o cenário padrão: eleitor válido vota em candidato registrado.
     */
    @Test
    void votar_Sucesso() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.votar(eleitorMaior, candidato1);
        assertEquals(1, eleicao.getVotos().size());
        Voto votoRegistado = eleicao.getVotos().get(0);
        // Usa assertSame porque só é igual se for o mesmo objeto (referência).
        assertSame(eleitorMaior, votoRegistado.getEleitor());
        assertSame(candidato1, votoRegistado.getCandidato());
    }

    /**
     * Testa se eleitor menor de idade é impedido de votar.
     */
    @Test
    void votar_EleitorAbaixoIdadeMinima() {
        eleicao.adicionarCandidato(candidato1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eleicao.votar(eleitorMenor, candidato1);
        });
        assertEquals("Eleitor não tem idade mínima para votar.", exception.getMessage());
        assertEquals(0, eleicao.getVotos().size());
    }

    /**
     * Testa se o sistema impede votar em candidato não registrado na eleição.
     */
    @Test
    void votar_CandidatoNaoRegistado() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eleicao.votar(eleitorMaior, candidato1);
        });
        assertEquals("Candidato não encontrado.", exception.getMessage());
        assertEquals(0, eleicao.getVotos().size());
    }

    /**
     * Testa se um eleitor só pode votar uma vez na eleição.
     */
    @Test
    void votar_DuasVezesMesmoEleitor() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.votar(eleitorMaior, candidato1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eleicao.votar(eleitorMaior, candidato1);
        });
        assertEquals("Este eleitor já votou.", exception.getMessage());
        // Deve existir apenas um voto no total.
        assertEquals(1, eleicao.getVotos().size());
    }

    /**
     * Testa a contagem de votos para cada candidato.
     */
    @Test
    void contarVotos() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.adicionarCandidato(candidato2);

        eleicao.votar(eleitorMaior, candidato1);
        Eleitor eleitor2 = new Eleitor("Carlos", "789", 25, "carlos@mail.com", "111222333");
        eleicao.votar(eleitor2, candidato1); // 2 votos para candidato1

        Eleitor eleitor3 = new Eleitor("Rita", "321", 30, "rita@mail.com", "444555666");
        eleicao.votar(eleitor3, candidato2); // 1 voto para candidato2

        assertEquals(2, eleicao.contarVotos(candidato1));
        assertEquals(1, eleicao.contarVotos(candidato2));
    }

    /**
     * Testa se o método de vencedor retorna corretamente quem tem mais votos.
     */
    @Test
    void obterVencedor_VencedorClaro() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.adicionarCandidato(candidato2);

        eleicao.votar(eleitorMaior, candidato1);
        Eleitor eleitor2 = new Eleitor("Carlos", "789", 25, "carlos@mail.com", "111222333");
        eleicao.votar(eleitor2, candidato1);

        Eleitor eleitor3 = new Eleitor("Rita", "321", 30, "rita@mail.com", "444555666");
        eleicao.votar(eleitor3, candidato2);

        // Testa se retorna o mesmo objeto usado como candidato1.
        assertSame(candidato1, eleicao.obterVencedor());
    }

    /**
     * Testa o cenário de empate, em que não há vencedor.
     */
    @Test
    void obterVencedor_Empate() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.adicionarCandidato(candidato2);

        Eleitor eleitor2 = new Eleitor("Carlos", "789", 25, "carlos@mail.com", "111222333");
        eleicao.votar(eleitorMaior, candidato1);
        eleicao.votar(eleitor2, candidato2);

        assertNull(eleicao.obterVencedor());
    }

    /**
     * Testa se a lista de candidatos contém todos os candidatos adicionados.
     */
    @Test
    void getCandidatos() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.adicionarCandidato(candidato2);

        ArrayList<Candidato> lista = eleicao.getCandidatos();
        assertEquals(2, lista.size());
        assertTrue(lista.contains(candidato1));
        assertTrue(lista.contains(candidato2));
    }

    /**
     * Testa se a lista de votos contém todos os votos lançados.
     */
    @Test
    void getVotos() {
        eleicao.adicionarCandidato(candidato1);
        eleicao.votar(eleitorMaior, candidato1);

        ArrayList<Voto> lista = eleicao.getVotos();
        assertEquals(1, lista.size());
        Voto voto = lista.get(0);
        assertSame(eleitorMaior, voto.getEleitor());
        assertSame(candidato1, voto.getCandidato());
    }

    /**
     * Testa os getters simples (nome e idade mínima da eleição).
     */
    @Test
    void gettersSimples() {
        assertEquals("Eleições 2023", eleicao.getNome());
        assertEquals(18, eleicao.getIdadeMinima());
    }
}