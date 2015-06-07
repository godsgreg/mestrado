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

import utilidades.ProjectSetup;

public class queryFileConverter {
	public static void main(String [] args) throws Exception{
		//MV = new ModeloVetorial();
		ArrayList<String> images;
		
		File file = new File(ProjectSetup.ranksConsultasPath);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		System.out.println(Arrays.toString(directories));

		for(String dir : directories){
			
			if (!(new File("novasConsultas").mkdirs())) {
				//System.exit(1);
			}
			
			try{
				File newFile = new File("novasConsultas/"+dir);
				
			 
				
				FileWriter fw = new FileWriter(newFile);
	
				
			
			
			
				File folder = new File(ProjectSetup.ranksConsultasPath + "/" + dir);
				File[] listOfFiles = folder.listFiles();
	
				for (File rank : listOfFiles) {
				    if (rank.isFile()) {
				        System.out.println(rank.getName());
				        fw.append(String.valueOf("$" + rank.getName()) + "\n");
				        try{
							//escreve um arquivo contendo o rank visual expandido pela busca textual
							BufferedReader br = new BufferedReader(new FileReader(rank));
	
							String line;
							while ((line = br.readLine()) != null) {
								fw.append(line+"\n");
							}
							br.close();
				        }catch(Exception ex){
				        	ex.printStackTrace();
				        }
				    }
				}
				
				fw.close();
				

			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
}
