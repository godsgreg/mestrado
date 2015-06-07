package mmrfgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RF {
	public static Map<String, Float> combinaRanks(List<Map<String, Float>> listaRanks){
		Map<String, Float> rankCombinado;
		rankCombinado = maiorValor(listaRanks);
		//rankCombinado = mediaAritmetica(listaRanks);
		//rankCombinado = mediaPonderada(listaRanks);
		return rankCombinado;
	}
	
	public static Map<String, Float>  mediaAritmetica(List<Map<String, Float>> listaRanks){
		Map<String, Float> rankCombinado = new HashMap<String, Float>();
		for (Map<String, Float> rank : listaRanks) {
			for (String key: rank.keySet()) {
				if(rankCombinado.get(key) != null){
					rankCombinado.put(key, rankCombinado.get(key) + (rank.get(key)/listaRanks.size()));
				}else{
					rankCombinado.put(key, rank.get(key)/listaRanks.size());
				}
			}		
		}
		return rankCombinado;
	}
	
	public static Map<String, Float>  mediaPonderada(List<Map<String, Float>> listaRanks){
		int peso = listaRanks.size();
		Map<String, Float> rankCombinado = new HashMap<String, Float>();
		//System.out.println("começo");
		//System.out.println("Soma dos pesos: " + String.valueOf(((listaRanks.size()+ 1) * (listaRanks.size()/2.0f))));
		for (Map<String, Float> rank : listaRanks) {
			for (String key: rank.keySet()) {
				if(rankCombinado.get(key) != null){
					rankCombinado.put(key, rankCombinado.get(key) + ((rank.get(key) * peso )/((listaRanks.size()+ 1) * (listaRanks.size()/2.0f))));
				}else{
					rankCombinado.put(key, ((rank.get(key) * peso )/((listaRanks.size()+ 1) * (listaRanks.size()/2.0f))));
				}
				//System.out.println((listaRanks.size()+ 1) * (listaRanks.size()/2));
				
			}	
			
			peso--;
			
		}
		/*for(String key : rankCombinado.keySet()){
			System.out.println(key + " - " + rankCombinado.get(key));
		}*/
		return rankCombinado;
	}
	
	public static Map<String, Float>  maiorValor(List<Map<String, Float>> listaRanks){
		Map<String, Float> rankCombinado = new HashMap<String, Float>();
		for (Map<String, Float> rank : listaRanks) {
			for (String key: rank.keySet()) {
				if(rankCombinado.get(key) != null){
					if(rankCombinado.get(key) < rank.get(key))
						rankCombinado.put(key,rank.get(key));
				}else{
					rankCombinado.put(key, rank.get(key));
				}
			}		
		}
		return rankCombinado;
	}
	
	public static List<String> selecionaRelevantes(Map<String, Float> rank){
		List<String> relevantes = new ArrayList<String>();
		String[] rankStr = new String[rank.size()];
		float[] rankSim = new float[rank.size()];
		//System.out.println(rank.size());
		//float acc = 0.0f;
		int i = 0;
		for(String key : rank.keySet()){
		    rankStr[i] = key;
		    rankSim[i] = rank.get(key);
		    i++;
		}
		rankStr = ordena(rankStr, rankSim)[0];
		for(i = 0 ; i < 10; i++){
			relevantes.add(rankStr[i]);
		}
		
		return relevantes;
	}
	
	public static List<String> selecionaRelevantesSimulado(Map<String, Float> rank, String consulta){
		List<String> relevantes = new ArrayList<String>();
		//File[] listOfFiles = folder.listFiles(); //retorna lista de paths para cada descritor
		//System.out.println("TESTE");
		try{
			//System.out.println("TESTE");
			BufferedReader br = new BufferedReader(new FileReader(new File(utilidades.ProjectSetup.relevantesPath + "/" + consulta + ".txt")));
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				relevantes.add(line);
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		List<String> imgsRelevantes = new ArrayList<String>();
		for(String imagem : rank.keySet()){
			for(String relevante: relevantes){
				if(imagem.equals(relevante)){
					imgsRelevantes.add(relevante);
					break;
				}
			}
			if(imgsRelevantes.size() >= 5){
				break;
			}
		}
		
		return imgsRelevantes;
	}
	
	static String[][] ordena(String[] paths, float[] similaridades){
		int maior; // maior
		for(int i = 0; i < similaridades.length - 1; i++){
			maior = i;
			for(int j = i+1; j < similaridades.length; j++){
				if(similaridades[j] < similaridades[maior]){
					maior = j;
				}
			}
			if(maior != i){
				float temp;
				String tempS;
				
				temp = similaridades[i];
				similaridades[i] = similaridades[maior];
				similaridades[maior] = temp;
				
				tempS = paths[i];
				paths[i] = paths[maior];
				paths[maior] = tempS;
			}
		}
		String[][] results = new String[2][similaridades.length];
		results[0] = paths;
		for(int i = 0; i < similaridades.length - 1; i++){
			results[1][i] = String.valueOf(similaridades[i]);
			
		}
		return results;
	}
	
}
