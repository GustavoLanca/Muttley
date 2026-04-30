package trab.lesw.participacao;

import java.io.Serializable;

import trab.lesw.usuario.Usuario;
import trab.lesw.evento.Evento;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Participacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    private Integer pontos;
}