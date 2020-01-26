
package programacion;

import clases.BaseDatos;
import clases.Provincias;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author Víctor Díaz
 */

public class XestionProdutos {
    
    public static void engadirProduto() {
        
        Scanner teclado = new Scanner(System.in);
        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("+             ENGADIR PRODUTO               +");        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++\n\n");
        
        //Pedimos datos do produto
        String produto = "";
        
        while (produto.equals("")) {
            System.out.println("Teclea a descrición do produto ('K' para cancelar).\n");
            produto = teclado.nextLine();
        }
        
        if (!produto.equalsIgnoreCase("K")) {
            
            String prezo = "";
            
            while (prezo.equals("") || !Comuns.eNumerico(prezo)) {                
                System.out.println("Teclea o prezo ('0' para cancelar).\n");
                prezo = teclado.nextLine();
            }
            
            if (Float.parseFloat(prezo) != 0.0) {

                //Pedimos confirmación para engadila
                System.out.printf("Engadimos o produto %s cun prezo de %s € (S/N)? ", produto, prezo);

                if (teclado.nextLine().equalsIgnoreCase("S")) {
                    //Engadimos o novo produto
                    BaseDatos.engadirProduto(produto, prezo);
                } else {
                    avisarNonEngadido(1);
                }
      
            } else {
                avisarNonEngadido(1);
            }
        } else {
            avisarNonEngadido(1);
        }
     }
    
    
    public static void engadirProdutoTenda() {
    
        Scanner teclado = new Scanner(System.in);
        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("+      ENGADIR PRODUTO A UNHA TENDA         +");        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++\n\n");
        
        String produto = seleccionarProduto();
        
        if (!produto.equalsIgnoreCase("K")) {
            
            String tenda = seleccionarTenda();
            
            if (!tenda.equalsIgnoreCase("K")) {
            
                if (!BaseDatos.existeProdutoTenda(produto, tenda)) {
                    
                    String stock = "";
                    
                    while (stock.equals("")) {                
                        System.out.println("Teclea o stock ('K' para cancelar).\n");
                        stock = teclado.nextLine();
                    }

                    if (!stock.equalsIgnoreCase("K")) {
                        
                        if (Comuns.eNumerico(stock)) {

                            //Pedimos confirmación para engadila
                            System.out.printf("Engadimos o produto %s á tenda %s cun stock de %s unidades (S/N)? ", produto, tenda, stock);

                            if (teclado.nextLine().equalsIgnoreCase("S")) {
                                //Engadimos o novo produto
                                BaseDatos.engadirProdutoTenda(produto, tenda, stock);
                            } else {
                                avisarNonEngadido(1);
                            }
                        }else{
                            avisarNonEngadido(6);
                        }

                    } else {
                        avisarNonEngadido(1);
                    }
                } else {
                    avisarNonEngadido(7);
                }
            }
        }        
     }
    
    private static String seleccionarProduto() {
    
        Scanner teclado = new Scanner(System.in);
        
        listarProdutos(false);
        
        String produto = "";
        
        while (produto.equals("")) {
            System.out.println("Teclea o código do produto a engadir á tenda ('K' para cancelar).\n");
            produto = teclado.nextLine();
        }
        
        if (!produto.equalsIgnoreCase("K")) {
            
            if (Comuns.eNumerico(produto)) {
                
                if (BaseDatos.existeProduto(produto)){
                    
                } else {
                    produto="K";
                    avisarNonEngadido(3);
                }
            } else {
                produto="K";
                avisarNonEngadido(2);
            }
        } else {
            avisarNonEngadido(1);
        }
        
        return produto;
    }
    
    private static String seleccionarTenda() {
            
        Scanner teclado = new Scanner(System.in);

        BaseDatos.listarTendas(false);

        String tenda = "";

        while (tenda.equals("")) {
            System.out.println("Teclea o código da tenda na que engadir o produto ('K' para cancelar).\n");
            tenda = teclado.nextLine();
        }

        if (!tenda.equalsIgnoreCase("K")) {

            if (Comuns.eNumerico(tenda)) {
                
                if (BaseDatos.existeTenda(tenda)) {
                    
                } else {
                    tenda ="K";
                    avisarNonEngadido(5);
                }
            } else {
                tenda="K";
                avisarNonEngadido(4);
            }
        } else {
            avisarNonEngadido(1);
        }
        
        return tenda;
    }
           
    
    public static void eliminarProduto() {
    
        Scanner teclado = new Scanner(System.in);
                
        System.out.println("+-------------------------------------------+");
        System.out.println("|            ELIMINAR PRODUTO               |");        
        System.out.println("+-------------------------------------------+\n\n");
        
        listarProdutos(false);
                
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        System.out.println("Teclea o nº do produto a eliminar (tecleando un nº fóra da lista ou negativo cancelamos o proceso). ");

        String id = "";
        
        while (!Comuns.eNumerico(id)) {
            id = teclado.nextLine();
        }

        try {
            
            Statement statement2 = conn.createStatement();
            ResultSet rs2 = statement2.executeQuery("select * from produtos where pr_id=" + id);
            
            if (rs2.next()) {
                System.out.printf("Desexas eliminar o produto %s da franquicia (*) (S/N)?", rs2.getString("pr_descricion"));
                System.out.println("\n(*) Tamén será eliminado este produto en cada tenda.");

                if (teclado.nextLine().equalsIgnoreCase("S")) {
                    // eliminamos o produto
                    BaseDatos.desconectarDB(conn);                    
                    BaseDatos.eliminarProduto(id);
                } else {
                    System.out.println(">>>>> O produto non foi eliminado.");
                }

            } else {                
                System.out.println(">>>>> Non existe ningún produto con código " + id);
            }
            
        } catch (Exception erro) {
            System.out.println("Erro buscando o produto a eliminar: " + erro.getMessage());
        }
        
    }
    
    public static void listarProdutos(Boolean verCabeceira) {

        if (verCabeceira) {
            
            System.out.println("+===========================================+");
            System.out.println("|        LISTAXE DE PRODUTOS                |");        
            System.out.println("+===========================================+\n\n");

        } else {        
            System.out.println("Listaxe de produtos da franquicia:\n");
        }
        
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        try {           

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from produtos");
            
            System.out.printf("%9s%2s%-30s%9s","CODIGO","", "DESCRICION", "PREZO");
                        
            while(rs.next()){
                System.out.println("");
                System.out.printf("%9s%2s%-30s%7.2f",rs.getString("pr_id"),"", rs.getString("pr_descricion"), Double.parseDouble(rs.getString("pr_prezo")));
            }
            System.out.println("");
        }
        catch(Exception erro){
            System.err.println("Erro listando produtos: " + erro.getMessage());
        }
        
        BaseDatos.desconectarDB(conn);
    }
    
    public static void eliminarProdutoTenda(){
                
        Scanner teclado = new Scanner(System.in);
                
        System.out.println("+-------------------------------------------+");
        System.out.println("|       ELIMINAR PRODUTO DUNHA TENDA        |");        
        System.out.println("+-------------------------------------------+\n\n");
        
        BaseDatos.listarProdutosTenda(false);
        
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        System.out.println("Teclea o CODIGO_ID do produto a eliminar (tecleando un nº fóra da lista ou negativo cancelamos o proceso). ");
                
        String id = "";
        
        while (!Comuns.eNumerico(id)) {
            id = teclado.nextLine();
        }

        try {
            
            Statement statement2 = conn.createStatement();
            ResultSet rs2 = statement2.executeQuery("select * from tendas_produtos where tepr_id=" + id);
            
            if (rs2.next()) {
                System.out.printf("Desexas eliminar o produto con código %s, con %s unidades de stock, da tenda con código %s (S/N)?", rs2.getString("tepr_prid"), rs2.getString("tepr_stock"), rs2.getString("tepr_teid"));

                if (teclado.nextLine().equalsIgnoreCase("S")) {
                    // eliminamos o produto
                    BaseDatos.desconectarDB(conn);                    
                    BaseDatos.eliminarProdutoTenda(id);
                } else {
                    System.out.println(">>>>> O produto non foi eliminado.");
                }

            } else {                
                System.out.println(">>>>> Non existe ningún rexistro de produtos da tenda con CODIGO_ID " + id);
            }
            
        } catch (Exception erro) {
            System.out.println("Erro buscando o produto a eliminar: " + erro.getMessage());
        }
        
    }
    
    
    public static void verStockProdutoTenda(){
                
        Scanner teclado = new Scanner(System.in);
                
        System.out.println("+-------------------------------------------+");
        System.out.println("|   VER STOCK DUN PRODUTO DUNHA TENDA       |");        
        System.out.println("+-------------------------------------------+\n\n");
        
        String produto = seleccionarProduto();
        
        if (!produto.equalsIgnoreCase("K")) {
            
            String tenda = seleccionarTenda();
            
            if (!tenda.equalsIgnoreCase("K")) {
                
                BaseDatos.listarProdutoTenda(produto, tenda);
                
            }
        }       
    }
    
    public static void actualizarStockProdutoTenda() {
    
        Scanner teclado = new Scanner(System.in);        
           
        System.out.println("+-------------------------------------------+");
        System.out.println("| ACTUALIZAR STOCK DUN PRODUTO DUNHA TENDA  |");        
        System.out.println("+-------------------------------------------+\n\n");
        
        BaseDatos.listarProdutosTenda(false);
        
        String id = "";
        
        while (id.equals("")) {
            System.out.println("Teclea o CODIGO_ID do rexistro de produtos da tenda a actualizarlle o stock ('K' para cancelar).");
            id = teclado.nextLine();
        }
                
        if (!id.equalsIgnoreCase("K")) {
            
            String stock = "";
            
            while (stock.equals("")) {                
                System.out.println("Teclea o novo stock ('K' para cancelar).\n");
                stock = teclado.nextLine();
            }
            
            if (!stock.equalsIgnoreCase("K")) {
                
                if (Comuns.eNumerico(stock)) {
                    
                    //Pedimos confirmación para engadila
                    System.out.printf("Actualizamos o stock do rexistro de produtos da tenda con CODIGO_ID %s a %s unidades (S/N)? ", id, stock);

                    if (teclado.nextLine().equalsIgnoreCase("S")) {
                        //Engadimos o novo produto
                        BaseDatos.actualizarStock(id, stock);
                    } else {
                        avisarNonActualizado(1);
                    }

                } else {
                    avisarNonActualizado(2);
                }
                
            } else {
                avisarNonActualizado(1);                
            }
                
        } else {
            avisarNonActualizado(1);
        }
        
    }
        
    private static void avisarNonEngadido(int mensaxe) {
        
        switch (mensaxe) {
            case 1:{
                System.out.println("Operación cancelada por vostede.");
                break;
            }
            case 2:{
                System.out.println("O código do produto debe ser numérico.");
                break;
            }
            case 3: {
                System.out.println("Código de produto inexistente.");
                break;
            }
            case 4: {
                System.out.println("O código da tenda debe ser numérico.");
                break;
            }
            case 5: {
                System.out.println("O código da tenda non existe.");
                break;
            }
            case 6: {
                System.out.println("O stock debe ser numérico. Deixar a 0 se non hai.");
                break;
            }
            case 7: {
                System.out.println("Este produto xa está de alta nesta tenda.");
                break;
            }
            case 8:{
                System.out.println("O prezo do produto debe ser numérico.");
                break;
            }        
        }
        System.out.println(">>>>> O produto NON foi engadido.");
    } 
    
       
    private static void avisarNonActualizado(int mensaxe) {
        
        switch (mensaxe) {
            case 1:{
                System.out.println("Operación cancelada por vostede.");
                break;
            }
            case 2:{
                System.out.println("O stock do produto debe ser numérico.");
                break;
            }        
        }
        System.out.println(">>>>> O stock NON foi actualizado.");
    } 
    
}
