package utilidades;

public class ProjectSetup {
	//arquivo xml contendo informações sobre todas as imagens da base
	public static final String databaseXMLPath = "textDescDafitiPosthaus.xml";
	
	//arquivo txt gerado ao converter o xml da base
	public static final String databaseTxtPath = "DafitiPosthausText.txt";
	
	//base de imagens para ser usada pelo indexador
	public static final String databasePath = "../baseData/colecaoDafitiPosthaus";

	//diretorio onde se encontram as imagens de consulta
	public static final String consultasPath = "../baseData/Consultas_Relevantes_DafitiPosthaus/imgsConsulta/colecaoAvulso_semParticao";
	
	//diretório onde se encontram as relações de imagens relevantes para as consultas 
	public static final String relevantesPath = "../baseData/Relevantes/avulso";
	public static final String relevantesXMLPath = "../baseData/Consultas_Relevantes_DafitiPosthaus/relevantes/Avulso";
	public static final String ranksConsultasPath = "../baseData/consultasAvaliadas/colecaoAvulso_semParticao";
	public static final String ranksBasePath = "../baseData/consultas";
	
}
