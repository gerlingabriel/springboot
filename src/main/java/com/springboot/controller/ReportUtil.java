package com.springboot.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Retorna o PDF em byte para download no navegador */
	public byte[] geradorRelatorio(List listaDados, String relatorio, ServletContext context) throws Exception {

		/*
		 * Cria a lista de dados para o relatório com nossa lista de objeto para
		 * imprimir
		 */
		JRBeanCollectionDataSource jrbds = new JRBeanCollectionDataSource(listaDados);

		/* Carregar o caminho do arquivo jasper complicado */
		String caminhoArquivo = context.getRealPath("relatorios") + File.separator + relatorio + ".jasper";

		/* Carrega o arquivo passando os dados */
		JasperPrint impressora = JasperFillManager.fillReport(caminhoArquivo, new HashMap(), jrbds);

		/* Exporta para byte[] para fazer o download do PDF */
		return JasperExportManager.exportReportToPdf(impressora);
	}

}
