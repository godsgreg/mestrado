package utilidades;

import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RelevantConverter {
	/**
	 * Este código converte os arquivos XML na relação de relevantes para arquivos txt
	 */
	
	static String filePath_avulso = utilidades.ProjectSetup.relevantesXMLPath + "/Avulso";
	static String filePath_base = utilidades.ProjectSetup.relevantesXMLPath + "/BaseDafiti";
	static String filePath_hard = utilidades.ProjectSetup.relevantesXMLPath + "Hard";
	
	public static void convertFile(String path, String nomeFolder){
		try {
			//File to be red

			
			File folder = new File(path);
			File[] listOfFolders = folder.listFiles(); //retorna lista de paths para cada descritor

			for (int i = 0; i < listOfFolders.length; i++) {
				
				File fXmlFile = listOfFolders[i];
				
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc2 = dBuilder.parse(fXmlFile);
	
				doc2.getDocumentElement().normalize();
	
				//System.out.println("Root element :" + doc2.getDocumentElement().getNodeName());
	
				org.w3c.dom.NodeList nList = doc2.getElementsByTagName("relevante");
	
				System.out.println("Lendo XML...");
	
				if (!(new File(utilidades.ProjectSetup.relevantesPath + "/" + nomeFolder).mkdirs())) {
				    //System.exit(1);
				}
				File arq = new File(utilidades.ProjectSetup.relevantesPath + "/" + nomeFolder + "/" +listOfFolders[i].getName().split("_")[0] + ".txt");
				FileWriter fw = new FileWriter(arq);
	
				for (int temp = 0; temp < nList.getLength(); temp++) {
	
					org.w3c.dom.Node nNode = nList.item(temp);
	
					//System.out.println("\nCurrent Element :" + nNode.getNodeName());
	
					if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
	
						org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

						fw.append(eElement.getElementsByTagName("img").item(0).getTextContent() + "\n");						
					}
				}
				fw.close();
				System.out.println("File saved!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String args[]){
		convertFile(filePath_avulso,"avulso");
		convertFile(filePath_base,"baseDafiti");
		convertFile(filePath_hard,"hard");
	}
}
