package programacion;

import clases.BaseDatos;
import clases.Provincias;
import com.google.gson.Gson;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 *
 * @author Víctor Díaz
 */
public class Main {
    
    public static final String BASE_DATOS = "franquicia.db";
    public static final String FINLISTAXE = "\n---------- fin da listaxe ----------";
    
    public static void main (String[] args) throws FileNotFoundException {
        
        File arquivo = new File(BASE_DATOS);        
        if (!arquivo.exists()) {
            BaseDatos.crearDB(BASE_DATOS);
            gravarProvincias();
        }          
        
        Scanner teclado = new Scanner(System.in);

        String opcionMenu="";
        
        while (!opcionMenu.equalsIgnoreCase("S")) {

            //Presentamos o menú de opcións e esperamos pola elección do usuario
            presentarMenu();
            opcionMenu = teclado.nextLine();

            switch (opcionMenu.toUpperCase()) {

                case "+T" : {
                    XestionTendas.engadirTenda();
                    break;
                }

                case "-T" : {
                    XestionTendas.eliminarTenda();
                    break;
                }
                
                case "VT" : {
                    BaseDatos.listarTendas(true);
                    break;
                }

                case "+P" : {
                    XestionProdutos.engadirProduto();
                    break;
                }

                case "-P" : {
                    XestionProdutos.eliminarProduto();
                    break;
                }
                
                case "+PT" : {
                    XestionProdutos.engadirProdutoTenda();
                    break;
                }
                
                case "-PT" : {
                    XestionProdutos.eliminarProdutoTenda();
                    break;
                }
                
                case "VS" : {
                    XestionProdutos.verStockProdutoTenda();
                    break;
                }
                
                case "AS" : {
                    XestionProdutos.actualizarStockProdutoTenda();
                    break;
                }
                
                case "VP" : {
                    XestionProdutos.listarProdutos(true);
                    break;
                }
                
                case "VPT" : {
                    BaseDatos.listarProdutosTenda(true);
                    break;
                }

                case "+E" : {
                    XestionEmpregados.engadirEmpregado();
                    break;
                }

                case "-E" : {
                    XestionEmpregados.eliminarEmpregado();
                    break;
                }

                case "VE" : {
                    BaseDatos.listarEmpregados(true);
                    break;
                }
                
                case "+EH" : {
                    XestionEmpregados.rexistroHorasTraballadas();
                    break;
                }
                
                case "VH" : {
                    XestionEmpregados.verHorasTraballadas();
                    break;
                }

                case "+C" : {
                    XestionClientes.engadirCliente();
                    break;
                }

                case "-C" : {
                    XestionClientes.eliminarCliente();
                    break;
                }
                
                case "VC" : {
                    XestionClientes.listarClientes(true);
                    break;
                }


                case "EP" : {
                    titularesEP();
                    break;
                }

                case "S" : {
                    //baseDatos.desconectarDB(conn);
                    rematarExecucion();
                    break;
                }

                default : {
                    System.out.println("******* OPCIÓN NON PRESENTE NO MENU *******");
                }
            }
        }            
            
    }
    
    private static void presentarMenu() {
        
        //Presentamos as distintas opcións do menú principal.
        System.out.println("\n==================================================");
        System.out.println("                    VDF PCs");
        System.out.println("==================================================\n");
        System.out.println("----------------- MENU PRINCIPAL -----------------\n");
        System.out.println("TENDAS -> Engadir (+T)");
        System.out.println("TENDAS -> Eliminar (-T)");
        System.out.println("TENDAS -> Ver tendas (VT)\n");
        System.out.println("PRODUTOS -> Engadir (+P)");
        System.out.println("PRODUTOS -> Eliminar (-P)");
        System.out.println("PRODUTOS -> Ver todos os produtos (VP)\n");
        System.out.println("PRODUTOS -> Engadir a unha tenda (+PT)");
        System.out.println("PRODUTOS -> Eliminar dunha tenda (-PT)");
        System.out.println("PRODUTOS -> Ver produtos dunha tenda (VPT)");
        System.out.println("PRODUTOS -> Ver stock dun produto dunha tenda (VS)");
        System.out.println("PRODUTOS -> Actualizar stock dun produto dunha tenda (AS)\n");
        System.out.println("EMPREGADOS -> Engadir (+E)");
        System.out.println("EMPREGADOS -> Eliminar (-E)");
        System.out.println("EMPREGADOS -> Ver empregados (VE)");
        System.out.println("EMPREGADOS -> Engadir horas traballadas nunha tenda (+EH)");
        System.out.println("EMPREGADOS -> Ver horas de empregados dunha tenda (VH)\n");
        System.out.println("CLIENTES -> Engadir (+C)");
        System.out.println("CLIENTES -> Eliminar (-C)");
        System.out.println("CLIENTES -> Ver clientes (VC)\n");
        System.out.println("EL PAIS, ver titulares (EP)");
        System.out.println("SAIR (S)\n");
    }        
    
    
    
    //Método para amosar en pantalla os titulares do arquivo RSS on-line de El Pais
    private static void titularesEP() {
    
        XMLReader procesadorXML = null;
        
        try {
            
            System.out.println("Buscando titulares do periódico El País ... agarde, por favor.\n");
            
            //Creamos o parseador
            procesadorXML = XMLReaderFactory.createXMLReader();
            TitularesPaisXML titularesPaisXML = new TitularesPaisXML();
            procesadorXML.setContentHandler(titularesPaisXML);
            
            //indicamos a ruta do arquivo
            InputSource arquivo = new InputSource("http://ep00.epimg.net/rss/elpais/portada.xml");
            procesadorXML.parse(arquivo);
            
            //imprimimos os datos
            ArrayList<TitularPais> titulares = titularesPaisXML.getTitulares();
            for (int i=0; i<titulares.size(); i++) {
                System.out.println(titulares.get(i).getTitular());
            }
        
        } catch (SAXException erro) {
            System.out.println("Ocorreu un erro ao ler o arquivo XML");
        } catch (IOException erro) {
            System.out.println("Ocorreu un erro ao ler o arquivo XML");
        }
         
       
    }
    
    private static void rematarExecucion() {        
        System.out.println("*** FIN DE EXECUCION ***");
        System.exit(0);
    }
    
    private static void gravarProvincias() {
        
        //Clase que gardará todos os datos da franquicia         
        Provincias provincias = new Provincias();        
        
        //cargar datos do arquivo JSON
        File arquivo = new File("provincias.json");
        
        if (arquivo.exists()) {
            
            //Cargamos os datos almacenados no arquivo json
            try {

                //Fluxo e buffer de entrada para o arquivo
                FileReader fluxoDatos = new FileReader(arquivo);
                BufferedReader entrada = new BufferedReader(fluxoDatos);

                //Lemos o arquivo liña a liña
                StringBuilder jsonBuilder = new StringBuilder();
                String linea;

                while ((linea = entrada.readLine()) != null) {
                    jsonBuilder.append(linea).append("\n");
                }

                //pechamos o arquivo
                entrada.close();

                //construimos o string con todas as liñas lidas
                String json = jsonBuilder.toString();

                //Pasamos o json á clase correspondente
                Gson gson = new Gson();
                provincias = gson.fromJson(json, Provincias.class);
                                
                BaseDatos.gravarProvincias(provincias);
                                
            } catch (IOException erro) {
                System.out.println("Erro cargando listaxe de provincias:\n" + erro.getMessage());
            }
            
        }
    }
        
}
