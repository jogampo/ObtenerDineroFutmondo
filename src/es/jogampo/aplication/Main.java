package es.jogampo.aplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Clase Principal de la aplicación.
 * Esta aplicación obtiene a partir de un fichero de entrada 
 * en la carpeta input que es el html de la sala de prensa de 
 * futmondo un listado con el dinero que se ha gastado
 * cada jugador.
 * 
 * @author jogampo
 *
 */
public class Main {

	private static String INPUT = "./input/entrada.html";
	private static String OUTPUT = "./output/salida.txt";
	
	/**
	 * @param args
	 */	
	public static void main(String[] args) {
		File input = new File(INPUT);
		//leemos el fichero html
		Document doc = null;
		try {
			doc = Jsoup.parse(input,"UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashMap<String, Integer> listadoJugadoresDineroQueda = dameDineroJugador(doc);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(OUTPUT, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("listado jugadores y dinero que le queda");
		writer.println("---------------------------------------");
		Set<String> keys = listadoJugadoresDineroQueda.keySet();
		for(String key : keys){
			writer.println(key+" : "+listadoJugadoresDineroQueda.get(key));
		}
		writer.close();
		
		System.out.println(listadoJugadoresDineroQueda);
		
		

	}

	
	/**
	 * funcion que calcula las transacciónes a partir del Document de la biblioteca jsoup
	 * 
	 * @param doc
	 * @return un hashmap con el dinero de cada jugador. 
	 */
	private static HashMap<String, Integer> dameDineroJugador(Document doc) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		Elements listfichajes = doc.select("#pressReleases");
		Elements listfichajes2 = listfichajes.select("article");
		
		for(Element fichaje : listfichajes2){
 			String nombrecomprador = null;
 			String vendedor = null;
			Elements todosStrong = fichaje.getElementsByTag("strong");
			Element comprador = todosStrong.get(1);	
			
			nombrecomprador = comprador.text();
			
			vendedor = todosStrong.get(2).text();
						
			Elements precioElemento = fichaje.getElementsByTag("span");
			String precio = precioElemento.first().text();
			
			if(nombrecomprador.equals("futmondo")){
				//trabajo con el vendedor y me toca sumar
				//Si el vendedor no esta en el hashmap
				if(result.get(vendedor) == null){
					result.put(vendedor, Integer.parseInt("200000000"));
				}
				result.put(vendedor, result.get(vendedor) - Integer.parseInt(precio.replace(".", "").replace("€", "").trim()));
				if(!nombrecomprador.equals("futmondo")){
					result.put(nombrecomprador, result.get(nombrecomprador) + Integer.parseInt(precio.replace(".", "").replace("€", "").trim()));
				}
			}else{
				//trabajo con el comprador y me toca restar
				if(result.get(nombrecomprador) == null){
					result.put(nombrecomprador, Integer.parseInt("200000000"));
				}
				result.put(nombrecomprador, result.get(nombrecomprador) + Integer.parseInt(precio.replace(".", "").replace("€", "").trim()));
				if(!vendedor.equals("futmondo")){
					result.put(vendedor, result.get(vendedor) - Integer.parseInt(precio.replace(".", "").replace("€", "").trim()));
				}
				
			}			
		}
		return result;
	}

}
