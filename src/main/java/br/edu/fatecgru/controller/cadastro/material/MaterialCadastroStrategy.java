package br.edu.fatecgru.controller.cadastro.material;

import br.edu.fatecgru.model.Enum.TipoAquisicao;

public interface MaterialCadastroStrategy {
    /**
     * Coleta os dados dos campos (via Controller), cria a entidade e persiste.
     * @param controller O controller principal para acessar os campos @FXML e o MaterialService.
     * @param tipoAquisicao O tipo de aquisição validado.
     * @throws Exception Se ocorrer erro no cadastro ou validação.
     */
    void cadastrar(CadastroMaterialController controller, TipoAquisicao tipoAquisicao) throws Exception;
}
