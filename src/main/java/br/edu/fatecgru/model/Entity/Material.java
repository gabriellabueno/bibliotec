package br.edu.fatecgru.model.Entity;

import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_material")
@Inheritance(strategy = InheritanceType.JOINED) // Estratégia de mapeamento de herança
public abstract class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_material")
    private Long idMaterial;

    @ManyToOne
    @JoinColumn(name = "fk_nota_fiscal", nullable = false)
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

    public Material(Long idMaterial, NotaFiscal notaFiscal, TipoAquisicao tipoAquisicao, TipoMaterial tipoMaterial, StatusMaterial statusMaterial) {
        this.idMaterial = idMaterial;
        this.notaFiscal = notaFiscal;
        this.tipoAquisicao = tipoAquisicao;
        this.tipoMaterial = tipoMaterial;
        this.statusMaterial = statusMaterial;
    }

    public Material() {
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public TipoAquisicao getTipoAquisicao() {
        return tipoAquisicao;
    }

    public void setTipoAquisicao(TipoAquisicao tipoAquisicao) {
        this.tipoAquisicao = tipoAquisicao;
    }

    public TipoMaterial getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(TipoMaterial tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public StatusMaterial getStatusMaterial() {
        return statusMaterial;
    }

    public void setStatusMaterial(StatusMaterial statusMaterial) {
        this.statusMaterial = statusMaterial;
    }
}