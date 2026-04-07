package br.edu.fatecgru.builder;

import br.edu.fatecgru.model.Entity.*;
import br.edu.fatecgru.model.Enum.StatusMaterial;
import br.edu.fatecgru.model.Enum.TipoAquisicao;
import br.edu.fatecgru.model.Enum.TipoMaterial;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public final class MaterialBuilder {


    private static BigDecimal converterParaBigDecimal(TextField field) {
        if (field == null || field.getText() == null || field.getText().trim().isEmpty()) {
            return null;
        }
        try {
            String valor = field.getText().replace("R$", "").replace(".", "").replace(",", ".").trim();
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    // Coleta os dados e armazena no objeto

    public static Livro toLivro(
            Livro livro,
            TextField codigoField, TextField isbnField, TextField tituloField, TextField autorField,
            TextField editoraField, TextField edicaoField, TextField generoField, TextField assuntoField,
            TextField localPublicacaoField, TextField anoPublicacaoField, TextArea palavrasChaveArea,
            TipoAquisicao tipoAquisicao,
            NotaFiscal notaFiscal,
            TextField valorUnitarioField,
            Long idPai,
            boolean modoCopia)
    {

        // Cria ou Reutiliza o Objeto em caso de cópia

        boolean isNovo = (livro == null);
        if (isNovo) {
            livro = new Livro();
        }

        BigDecimal valor = converterParaBigDecimal(valorUnitarioField);

        dadosComuns(livro, TipoMaterial.LIVRO, tipoAquisicao, valor, notaFiscal, idPai, modoCopia);

        livro.setCodigo(codigoField.getText());
        livro.setIsbn(isbnField.getText());
        livro.setTitulo(tituloField.getText());
        livro.setAutor(autorField.getText());
        livro.setEditora(editoraField.getText());
        livro.setEdicao(edicaoField.getText());
        livro.setGenero(generoField.getText());
        livro.setAssunto(assuntoField.getText());
        livro.setAnoPublicacao(anoPublicacaoField.getText());
        livro.setLocalPublicacao(localPublicacaoField.getText());
        livro.setPalavrasChave(palavrasChaveArea.getText());

        return livro;
    }

    public static Revista toRevista(
            Revista revista,
            TextField codigoField, TextField tituloField, TextField volumeField, TextField numeroField,
            TextField editoraField, TextField assuntoField, TextField anoPublicacaoField,
            TextField localPublicacaoField, TextField generoField, TextArea palavrasChaveArea,
            TipoAquisicao tipoAquisicao,
            NotaFiscal notaFiscal,
            TextField valorUnitarioField,
            Long idPai,
            boolean modoCopia)
    {


        boolean isNovo = (revista == null);
        if (isNovo) {
            revista = new Revista();
        }

        BigDecimal valor = converterParaBigDecimal(valorUnitarioField);

        dadosComuns(revista, TipoMaterial.REVISTA, tipoAquisicao, valor, notaFiscal, idPai, modoCopia);

        revista.setCodigo(codigoField.getText());
        revista.setTitulo(tituloField.getText());
        revista.setVolume(volumeField.getText());
        revista.setNumero(numeroField.getText());
        revista.setAssunto(assuntoField.getText());
        revista.setAnoPublicacao(anoPublicacaoField.getText());
        revista.setLocalPublicacao(localPublicacaoField.getText());
        revista.setEditora(editoraField.getText());
        revista.setGenero(generoField.getText());
        revista.setPalavrasChave(palavrasChaveArea.getText());

        return revista;
    }


    public static TG toTG(
            TG tg,
            TextField codigoField, TextField tituloField, TextField subtituloField, TextField assuntoField,
            TextField autor1Field, TextField ra1Field, TextField autor2Field, TextField ra2Field,
            TextField anoPublicacaoField, TextField localPublicacaoField, TextArea palavrasChaveArea)
    {
        boolean isNovo = (tg == null);
        if (isNovo) {
            tg = new TG();
        }


        tg.setTipoMaterial(TipoMaterial.TG);
        tg.setTipoAquisicao(TipoAquisicao.DOACAO); // TG é sempre Doação
        tg.setStatusMaterial(StatusMaterial.DISPONIVEL);
        tg.setNotaFiscal(null);

        tg.setCodigo(codigoField.getText());
        tg.setTitulo(tituloField.getText());
        tg.setSubtitulo(subtituloField.getText());
        tg.setAssunto(assuntoField.getText());
        tg.setRa1(ra1Field.getText());
        tg.setAutor1(autor1Field.getText());
        tg.setRa2(ra2Field.getText());
        tg.setAutor2(autor2Field.getText());
        tg.setAnoPublicacao(anoPublicacaoField.getText());
        tg.setLocalPublicacao(localPublicacaoField.getText());
        tg.setPalavrasChave(palavrasChaveArea.getText());

        return tg;
    }

    public static Equipamento toEquipamento(
            Equipamento equipamento,
            TextField codigoField, TextField nomeField, TextArea descricaoArea,
            TipoAquisicao tipoAquisicao, NotaFiscal notaFiscal, TextField valorUnitarioField)
    {
        boolean isNovo = (equipamento == null);
        if (isNovo) {
            equipamento = new Equipamento();
        }

        BigDecimal valorConvertido = converterParaBigDecimal(valorUnitarioField);

        equipamento.setTipoMaterial(TipoMaterial.EQUIPAMENTO);
        equipamento.setTipoAquisicao(tipoAquisicao);
        equipamento.setStatusMaterial(StatusMaterial.DISPONIVEL);
        equipamento.setValorUnitario(valorConvertido);

        if (tipoAquisicao == TipoAquisicao.COMPRA) {
            equipamento.setNotaFiscal(notaFiscal);
            equipamento.setValorUnitario(valorConvertido);
        } else {
            equipamento.setNotaFiscal(null);
            equipamento.setValorUnitario(null);
        }

        equipamento.setCodigo(codigoField.getText());
        equipamento.setNome(nomeField.getText());
        equipamento.setDescricao(descricaoArea.getText());

        return equipamento;
    }

    private static void dadosComuns(
            Material material, TipoMaterial tipo, TipoAquisicao aquisicao, BigDecimal valorUnitario,
            NotaFiscal nf, Long idPai, boolean modoCopia)
    {
        material.setTipoMaterial(tipo);
        material.setTipoAquisicao(aquisicao);
        material.setStatusMaterial(StatusMaterial.DISPONIVEL);
        material.setValorUnitario(valorUnitario);

        //  Livro e Revista usam cópia (tarja vermelha)

        if (material instanceof Livro livro) {
            livro.setTarjaVermelha(!modoCopia);
            if (idPai != null) {
                livro.setIdPai(idPai);
            }
        } else if (material instanceof Revista revista) {
            revista.setTarjaVermelha(!modoCopia);
            if (idPai != null) {
                revista.setIdPai(idPai);
            }
        }

        if (aquisicao == TipoAquisicao.COMPRA) {
            material.setNotaFiscal(nf);
        } else {
            material.setNotaFiscal(null);
        }
    }

    // Preenche os campos a partir do objeto

    public static void fromLivro(
            Livro livro, TextField isbnField, TextField tituloField, TextField autorField,
            TextField editoraField, TextField edicaoField, TextField generoField, TextField assuntoField,
            TextField localPublicacaoField, TextField anoPublicacaoField, TextArea palavrasChaveArea, TextField valorUnitarioField)
    {
        isbnField.setText(livro.getIsbn());
        tituloField.setText(livro.getTitulo());
        autorField.setText(livro.getAutor());
        editoraField.setText(livro.getEditora());
        edicaoField.setText(livro.getEdicao());
        generoField.setText(livro.getGenero());
        assuntoField.setText(livro.getAssunto());
        localPublicacaoField.setText(livro.getLocalPublicacao());
        anoPublicacaoField.setText(livro.getAnoPublicacao());
        palavrasChaveArea.setText(livro.getPalavrasChave());

        if (livro.getValorUnitario() != null) {
            valorUnitarioField.setText(livro.getValorUnitario().toString());
        } else {
            valorUnitarioField.setText("");
        }

    }

    public static void fromRevista(
            Revista revista, TextField tituloField, TextField volumeField, TextField numeroField,
            TextField editoraField, TextField assuntoField, TextField anoPublicacaoField,
            TextField localPublicacaoField, TextField generoField, TextArea palavrasChaveArea, TextField valorUnitarioField)
    {
        tituloField.setText(revista.getTitulo());
        volumeField.setText(revista.getVolume());
        numeroField.setText(revista.getNumero());
        editoraField.setText(revista.getEditora());
        assuntoField.setText(revista.getAssunto());
        anoPublicacaoField.setText(revista.getAnoPublicacao());
        localPublicacaoField.setText(revista.getLocalPublicacao());
        generoField.setText(revista.getGenero());
        palavrasChaveArea.setText(revista.getPalavrasChave());
    }

    public static void fromTG(
            TG tg, TextField tituloField, TextField subtituloField, TextField assuntoField,
            TextField autor1Field, TextField ra1Field, TextField autor2Field, TextField ra2Field,
            TextField localPublicacaoField, TextField anoPublicacaoField, TextArea palavrasChaveArea)
    {
        tituloField.setText(tg.getTitulo());
        subtituloField.setText(tg.getSubtitulo());
        assuntoField.setText(tg.getAssunto());
        autor1Field.setText(tg.getAutor1());
        ra1Field.setText(tg.getRa1());
        autor2Field.setText(tg.getAutor2());
        ra2Field.setText(tg.getRa2());
        localPublicacaoField.setText(tg.getLocalPublicacao());
        anoPublicacaoField.setText(tg.getAnoPublicacao());
        palavrasChaveArea.setText(tg.getPalavrasChave());
    }

    public static void fromEquipamento(
            Equipamento equipamento, TextField nomeField, TextArea descricaoArea, TextField valorUnitarioField)
    {
        nomeField.setText(equipamento.getNome());
        descricaoArea.setText(equipamento.getDescricao());
    }

}