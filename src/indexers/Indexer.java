package indexers;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.xml.parsers.DocumentBuilder;

import net.semanticmetadata.lire.imageanalysis.bovw.SiftFeatureHistogramBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	
	/**
	 * Este código gera os indices para cada descritor visual
	 */
	
	private static String databasePath = utilidades.ProjectSetup.databasePath;//"colecaoDafitiPosthaus";
	public static String IndexPath;
	//numero de palavras visuais para o descritor SIFT
	public static final int nBoVW = 128;
	
    public static void startIndex(int descriptor) throws IOException {
    	// Dado um descritor, gera os arquivos de indice para serem usados pelo buscador
    	
        // Getting all images from a directory and its sub directories.
        ArrayList<String> images = FileUtils.getAllImages(new File(databasePath), true);
 
        ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
        switch(descriptor){
        case 1:
        	IndexPath = "indexACC";
        	builder.addBuilder(net.semanticmetadata.lire.DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder());
        	break;
        case 2:
        	IndexPath = "indexCLD";
        	builder.addBuilder(net.semanticmetadata.lire.DocumentBuilderFactory.getColorLayoutBuilder());
        	break;
        case 3:
        	IndexPath = "indexFCTH";
        	builder.addBuilder(net.semanticmetadata.lire.DocumentBuilderFactory.getFCTHDocumentBuilder());
        	break;
        case 4:
        	IndexPath = "indexCEDD";
        	builder.addBuilder(net.semanticmetadata.lire.DocumentBuilderFactory.getCEDDDocumentBuilder());
        	break;
        case 5:
        	IndexPath = "indexJDC";
        	builder.addBuilder(net.semanticmetadata.lire.DocumentBuilderFactory.getJCDDocumentBuilder());
        	break;
        case 6:
        	IndexPath = "indexPHOG";
        	builder.addBuilder(net.semanticmetadata.lire.DocumentBuilderFactory.getPHOGDocumentBuilder());
        	break;
        case 7:
        	IndexPath = "indexSIFT";
        	builder.addBuilder(new SiftDocumentBuilder());
        	break;
        }

        System.out.println("Indexing images in "+ IndexPath);       
        
     // Creating an Lucene IndexWriter
        IndexWriterConfig conf = new IndexWriterConfig(LuceneUtils.LUCENE_VERSION, new WhitespaceAnalyzer(LuceneUtils.LUCENE_VERSION));
        IndexWriter iw = new IndexWriter(FSDirectory.open(new File(IndexPath)), conf);
        // Iterating through images building the low level features
        for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
            String imageFilePath = it.next();
            System.out.println("Indexing " + imageFilePath);
            try {
                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                Document document = builder.createDocument(img, imageFilePath);
                iw.addDocument(document);
            } catch (Exception e) {
                System.err.println("Error reading image or indexing it.");
                e.printStackTrace();
            }
        }
        // closing the IndexWriter
        iw.close();
       
        if(descriptor == 7){
	        // create the visual words.
	        IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));
	        // create a BoVW indexer, "-1" means that half of the images in the index are
	        // employed for creating the vocabulary. "100" is the number of visual words to be created.
	        SiftFeatureHistogramBuilder sh = new SiftFeatureHistogramBuilder(ir, -1, nBoVW);
	        // progress monitoring is optional and opens a window showing you the progress.
	        sh.setProgressMonitor(new ProgressMonitor(null, "", "", 0, 100));
	        sh.index();
        }
        System.out.println("Finished indexing.");
    }
   /* 
    static Map<String, Map<String,String>> readIndex() throws IOException{  
    	Map<String, Map<String,String>> ImageFeatures = new HashMap();
    	IndexReader ir = IndexReader.open(FSDirectory.open(new File(IndexPath)));	
    	
  		for (int j = 0; j < ir.numDocs(); j++) {
				
  			org.apache.lucene.document.Document doc = ir.document(j);
  			
  			String docString = doc.getFields().toString();
  			/*File arq = new File("docText.txt");
	    	FileWriter fw = new FileWriter(arq);
	    	fw.append(docString);
	    	fw.close();
			System.exit(0);

			String imageName = doc.getField("descriptorImageIdentifier").stringValue();
			imageName = NameFinder.findName(imageName);
			System.out.println(imageName);
			String vectorSift = docString.substring(docString.indexOf("featureSiftHistogram:")+21,docString.length() - 2);
			System.out.println(vectorSift);
			String vectorGCH = docString.substring(docString.indexOf("featureColorHistogram"),docString.indexOf("featureColorHistogram")+ 255 * 3 + 10);
				
			//Matcher m1 = Pattern.compile("\\[(.*?)\\]").matcher(vectorSift);
			Matcher m2 = Pattern.compile("\\[(.*?)\\]").matcher(vectorGCH);
			
		   /* if(m1.find()) {
		    	vectorSift = m1.group(1);
		    }
		    if(m2.find()) {
		    	vectorGCH = m2.group(1);
		    }
		    Map<String,String> aux = new HashMap<String, String>();
		    aux.put("Sift", vectorSift);
		    aux.put("GCH", vectorGCH);
		    ImageFeatures.put(imageName,aux);
	   	  
  	    }
  		return ImageFeatures;
    }
   
    public static void main(String args[]){
		try{
			BIC.index(databasePath, "indexBIC");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i = 1; i < 8; i++){
			try{
				startIndex(i);
			}catch(Exception ex){ex.printStackTrace();}
		}
    }*/
}