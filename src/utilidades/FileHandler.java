package utilidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {
	
	//mapa[Descritor] -> mapa[imagem] -> mapa[imagem] -> similaridade
	private Map<String, Map<String,Map<String, Float>>> data;
	
	private Map<String,Map<String, Float>> readFile(File file){
		Map<String,Map<String, Float>> result = new HashMap<>();

		String imageName;
		try{
			//escreve um arquivo contendo o rank visual expandido pela busca textual
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			Map<String,Float> rank = null;
			while ((line = br.readLine()) != null) {
				if(line.contains("$")){
					rank = new HashMap<String, Float>();
					imageName = line.replace("$", "");
					result.put(imageName,rank);
				}else{
					String[] lineData = line.split(" ");
					rank.put(lineData[1],Float.parseFloat(lineData[0])); //TODO verify
				}
			}
			br.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	public void loadData(String path){
		data = new HashMap<String, Map<String,Map<String,Float>>>();
		try{			
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        data.put(file.getName(),readFile(file));
			    }
			}
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public HashMap<String, Float> getRank(String descriptor, String image){
		return (HashMap<String, Float>) data.get(descriptor).get(image);
	}
	
}
