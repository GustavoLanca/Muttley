package trab.lesw.evento;

import lombok.Getter;

@Getter
public enum TipoEvento {

    PARTICIPACAO(10, "Participação"),
    ORGANIZACAO(30, "Organização"),
    PALESTRA(50, "Palestra"),
    CURSO(40, "Curso"),
    OUTRO(20, "Outro");

    private final int pontos;
    private final String descricao;

    TipoEvento(int pontos, String descricao) {
        this.pontos = pontos;
        this.descricao = descricao;
    }
}
