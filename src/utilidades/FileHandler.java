package utilidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {
	
	//mapa[Descritor] -> mapa[imagem] -> mapa[imagem] -> similaridade
	private Map<String, Map<String,Map<String, Float>>> data;
	private Map<String, List<String>> relevants;
	
	private Map<String,Map<String, Float>> readFile(File file){
		Map<String,Map<String, Float>> result = new HashMap<>();

		String imageName = "";
		try{
			//escreve um arquivo contendo o rank visual expandido pela busca textual
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			Map<String,Float> rank = null;
			while ((line = br.readLine()) != null) {
				if(line.contains("$")){
//					if(!imageName.equals("")){
//						System.out.println("Rank para a imagem " + imageName);
//						for(String key : result.get(imageName).keySet()){
//							System.out.println(key);
//						}
//					}
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
	
	private ArrayList<String> readRelevantFile(File file){
		ArrayList result = new ArrayList<String>();
		try{
			//escreve um arquivo contendo o rank visual expandido pela busca textual
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				result.add(line);
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
	
	public ArrayList<String> getRelevant(String image){
		return (ArrayList<String>) relevants.get(image);
	}
	
	public void loadRelevants(String path){
		relevants = new HashMap<String, List<String>>();
		try{			
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        relevants.put(file.getName(),readRelevantFile(file));
			    }
			}
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
