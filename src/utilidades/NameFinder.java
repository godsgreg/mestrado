package utilidades;

public class NameFinder {
	public static String findName(String fullName){
		//dado um path de uma imagem, remove o caminho e mantem apenas o nome do arquivo
		String result = "";
		String[] aux = fullName.split("/");
		int lastIndex = aux.length - 1;
		result = aux[lastIndex];
		//System.out.println(result);
		return result;
	}
}
