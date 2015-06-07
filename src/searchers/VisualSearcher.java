package searchers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.AbstractImageSearcher;
import net.semanticmetadata.lire.imageanalysis.bovw.SiftFeatureHistogramBuilder;
import net.semanticmetadata.lire.imageanalysis.bovw.SurfFeatureHistogramBuilder;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;
import net.semanticmetadata.lire.utils.FileUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import utilidades.NameFinder;

public class VisualSearcher {
	
	/**
	 * Este código gera os ranks visuais para cada descritor, mas antes devem ser gerados os seus índices (vetores)
	 */
	
	private static int rankSize = 100 + 1;
	public static String consultasPath = utilidades.ProjectSetup.databasePath;//consultasPath;
	public static String ranksPath = utilidades.ProjectSetup.ranksBasePath;//ranksConsultasPath;
	public static String[] indexPath = {"indexACC","indexCEDD","indexCLD","indexFCTH","indexGCH","indexJCD","indexPHOG","indexSIFT"};
	
	public static void SearchImages() throws IOException {
		BufferedImage img = null;
		ImageSearcher[] searchers = {
				ImageSearcherFactory.createAutoColorCorrelogramImageSearcher(rankSize),
				ImageSearcherFactory.createCEDDImageSearcher(rankSize),	
				ImageSearcherFactory.createColorLayoutImageSearcher(rankSize),
				ImageSearcherFactory.createFCTHImageSearcher(rankSize),
				ImageSearcherFactory.createColorHistogramImageSearcher(rankSize),
				ImageSearcherFactory.createJCDImageSearcher(rankSize),
				ImageSearcherFactory.createPHOGImageSearcher(rankSize)//,
				//AbstractImageSearcher.clasVisualWordsImageSearcher(rankSize),
		};
		
		//gera os ranks para cada descritor exceto o SIFT
		for(int j = 4; j < 5/*indexPath.length - 1*/; j++){

			IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(indexPath[j])));
			ImageSearcher searcher = searchers[j];

			ArrayList<String> images = FileUtils.getAllImages(new File(consultasPath), true);

			//File arq = new File("consultas/" + indexPath[j] + "/" + indexPath[j].substring(5));
			//FileWriter fw = new FileWriter(arq);

			System.out.println("Descritor " + indexPath[j] + "...");

			if (images.size()>0) {
				for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
					
					String imageFilePath = it.next();
					System.out.println(searcher.toString());
					img = ImageIO.read(new FileInputStream(imageFilePath));
					//System.out.println(img + " \n" + ir);
					ImageSearchHits hits = searcher.search(img, ir);
					//System.out.println("TESTE");
					//success = (new File("../potentially/long/pathname/without/all/dirs")).mkdirs();
					if (!(new File(ranksPath + "/" + indexPath[j].substring(5))).mkdirs()) {
					    //System.exit(1);
					}

					System.out.println("Results for " + NameFinder.findName(imageFilePath) + ":");
					File arq = new File(ranksPath + "/" + indexPath[j].substring(5) + "/" + NameFinder.findName(imageFilePath).replace(".jpg", ".txt"));
					FileWriter fw = new FileWriter(arq);

					for (int i = 0; i < hits.length(); i++) {
						String fileName = hits.doc(i).getValues(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
						//System.out.println(hits.score(i) + ": \t" + NameFinder.findName(fileName));
						if(!fileName.equals(imageFilePath))
							fw.append(hits.score(i) + " " + NameFinder.findName(fileName) + "\n");
					}
					
					fw.close();
				}
			}
		}
	}
	
	public static void searchBoVWIndex(BufferedImage image, String identifier, String indexPath) throws IOException {
        //String indexPath = "./bovw-test";
        VisualWordsImageSearcher searcher = new VisualWordsImageSearcher(100,
        		net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_SIFT);
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
 
        // employed for creating the visual words for the query document. 
        // Make sure you are using the same parameters as for indexing. 
        SiftFeatureHistogramBuilder sh = new SiftFeatureHistogramBuilder(reader, -1, 100);
        // extract SURF features and create query
        SiftDocumentBuilder sb = new SiftDocumentBuilder();
        Document query = sb.createDocument(image, identifier);
        // create visual words for the query
        query = sh.getVisualWords(query);
        // search
        ImageSearchHits hits = searcher.search(query, reader);
        // show or analyze your results ....
        File arq = new File(ranksPath + "/" + indexPath.substring(5) + "/" + identifier.replace(".jpg", ".txt"));
		FileWriter fw = new FileWriter(arq); 

		for (int i = 0; i < hits.length(); i++) {
			String fileName = hits.doc(i).getValues(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
			//System.out.println(hits.score(i) + ": \t" + NameFinder.findName(fileName));
			if(!fileName.equals(identifier))
				fw.append(hits.score(i) + " " + NameFinder.findName(fileName) + "\n");
		}
		
		fw.close();
	}
	
	public static void buscar(){
		
		
		if (!(new File(ranksPath)).mkdirs()) {
		    //System.exit(1);
		}
		
		try{
			System.out.println("Gerando ranks para os descritores visuais...");
			SearchImages();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		try{
			ArrayList<String> images = FileUtils.getAllImages(new File(consultasPath), true); // consultaPath
	
			//File arq = new File("consultas/" + indexPath[j] + "/" + indexPath[j].substring(5));
			//FileWriter fw = new FileWriter(arq);
	
			System.out.println("Descritor " + indexPath[7] + "...");
	
			if (images.size()>0) {
				for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
					try{
						String imageFilePath = it.next();
						//System.out.println(imageFilePath);
						BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
						searchBoVWIndex(img, NameFinder.findName(imageFilePath), indexPath[7]);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){e.printStackTrace();}
		
		try{
			System.out.println("Descritor indexBIC...");
			BicSearcher.buscadorBic();
		}catch(Exception ex){ex.printStackTrace();}
	}
}
