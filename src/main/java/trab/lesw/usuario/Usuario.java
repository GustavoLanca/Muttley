package trab.lesw.usuario;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import trab.lesw.medalhas.Medalha;
import trab.lesw.participacao.Participacao;
import trab.lesw.aluno.Aluno;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participacao> participacoes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medalha> medalhas;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Aluno aluno;

    public int getTotalPontos() {
        if (participacoes == null) return 0;
        return participacoes.stream()
                .mapToInt(p -> p.getPontos() != null ? p.getPontos() : 0)
                .sum();
    }
}
