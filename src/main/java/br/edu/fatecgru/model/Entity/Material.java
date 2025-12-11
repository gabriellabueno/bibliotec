package br.edu.fatecgru.model.Entity;

import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


@Entity
@Table(name = "tb_material")
@Inheritance(strategy = InheritanceType.JOINED) // Estratégia de mapeamento de herança
public abstract class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_material")
    private Long idMaterial;

    @ManyToOne
    @JoinColumn(name = "fk_nota_fiscal")
    private NotaFiscal notaFiscal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_aquisicao", nullable = false)
    private TipoAquisicao tipoAquisicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_material", nullable = false)
    private TipoMaterial tipoMaterial;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_material", nullable = false)
    private StatusMaterial statusMaterial;

    private Long idPai;


    public String getCodigoNotaFiscal() {
        return notaFiscal.getCodigo();
    }
}