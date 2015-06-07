package utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;

public class XmlToTxt {
	public static void main(String args[]){
		try {
			//File to be red
			File fXmlFile = new File(utilidades.ProjectSetup.databaseXMLPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc2 = dBuilder.parse(fXmlFile);

			doc2.getDocumentElement().normalize();

			//System.out.println("Root element :" + doc2.getDocumentElement().getNodeName());

			org.w3c.dom.NodeList nList = doc2.getElementsByTagName("produto");

			System.out.println("Lendo XML...");

			File arq = new File(utilidades.ProjectSetup.databaseTxtPath);
			FileWriter fw = new FileWriter(arq);

			for (int temp = 0; temp < nList.getLength(); temp++) {

				org.w3c.dom.Node nNode = nList.item(temp);

				//System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

					org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;

					/*System.out.println("Staff id : " + eElement.getAttribute("id"));
				System.out.println("categoria : " + eElement.getElementsByTagName("categoria").item(0).getTextContent());
				System.out.println("titulo : " + eElement.getElementsByTagName("titulo").item(0).getTextContent());
				System.out.println("descricao : " + eElement.getElementsByTagName("descricao").item(0).getTextContent());
				System.out.println("preco : " + eElement.getElementsByTagName("preco").item(0).getTextContent());
				System.out.println("img : " + eElement.getElementsByTagName("img").item(0).getTextContent());
					 */
					String categoria = eElement.getElementsByTagName("categoria").item(0).getTextContent();
					categoria = categoria.replace("/"," ");
					
					fw.append(eElement.getElementsByTagName("img").item(0).getTextContent()
							+ " " + eElement.getElementsByTagName("descricao").item(0).getTextContent()
							+ " " + categoria + "\n");

					
				}
			}
			fw.close();
			System.out.println("File saved!");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
