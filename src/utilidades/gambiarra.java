package utilidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.semanticmetadata.lire.utils.FileUtils;

public class gambiarra {
	//static File images;
	public static void main(String args[]){
		try {
			
			File folder = new File("consultas/BIC");
			File[] listOfFiles = folder.listFiles();

			//para cada imagem da consuysolta
			for(File image: listOfFiles){
				System.out.println("Corrigindo arquivo "+ image.getName());
				//System.exit(0);
				BufferedReader br = new BufferedReader(new FileReader(image));
				String line, nova = "";
				while ((line = br.readLine()) != null) {
					//para cada linha, le o nome da imagem e valor de similaridade, em seguida adiciona em um mapa
					String[] dados = line.split(" ");
					nova += dados[1] + " " + dados[0] + "\n";
					
				}
				br.close();
				File Novofile = new File(image.getName());
				FileWriter fw = new FileWriter("consultas/BIC/" + Novofile);
				fw.append(nova);
				fw.close();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
