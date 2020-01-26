
package programacion;

import clases.BaseDatos;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author Víctor Díaz
 */
public class XestionClientes {
    
    public static void engadirCliente() {
        
        Scanner teclado = new Scanner(System.in);
        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("+             ENGADIR CLIENTE               +");        
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++\n\n");
        
        //Pedimos datos do cliente
        String nome = "";
        
        while (nome.equals("")) {
            System.out.println("Teclea o nome do/da cliente/a ('K' para cancelar).\n");
            nome = teclado.nextLine();
        }
        
        if (!nome.equalsIgnoreCase("K")) {
            
            String apelidos = "";
            
            while (apelidos.equals("")) {                
                System.out.println("Teclea os apelidos ('K' para cancelar).\n");
                apelidos = teclado.nextLine();
            }
            
            if (!apelidos.equalsIgnoreCase("K")) {

                String email = "/";

                while (email.equals("/")) {                
                    System.out.println("Teclea o email ('K' para cancelar).\n");
                    email = teclado.nextLine();
                }
                    if (!email.equalsIgnoreCase("K")) {
                        //Pedimos confirmación para engadilo/a
                        System.out.printf("Engadimos %s %s, con email %s, como cliente (S/N)? ", nome, apelidos, email);

                        if (teclado.nextLine().equalsIgnoreCase("S")) {
                            BaseDatos.engadirCliente(nome, apelidos, email);
                        } else {
                            avisarNonEngadido();
                        }
                    } else {
                        avisarNonEngadido();
                    }
      
            } else {
                avisarNonEngadido();
            }
        } else {
            avisarNonEngadido();
        }
     }
    
    private static void avisarNonEngadido() {
        System.out.println(">>>>> O/A cliente/a NON foi engadido/a.");
    }    
    
    public static void eliminarCliente() {
    
        Scanner teclado = new Scanner(System.in);
                
        System.out.println("+-------------------------------------------+");
        System.out.println("|            ELIMINAR CLIENTE               |");        
        System.out.println("+-------------------------------------------+\n\n");
        
        listarClientes(false);
                
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        System.out.println("Teclea o código do/da cliente/a a eliminar (tecleando un código fóra da listaxe ou negativo cancelamos o proceso). ");

        String id = "";
        
        while (!Comuns.eNumerico(id)) {
            id = teclado.nextLine();
        }

        try {
            
            Statement statement2 = conn.createStatement();
            ResultSet rs2 = statement2.executeQuery("select * from clientes where em_id=" + id);
            
            if (rs2.next()) {
                System.out.printf("Desexas eliminar o/a cliente/a %s %s (S/N)?", rs2.getString("cl_nome"), rs2.getString("cl_apelidos"));
  
                if (teclado.nextLine().equalsIgnoreCase("S")) {
                    // eliminamos o cliente
                    BaseDatos.desconectarDB(conn);                    
                    BaseDatos.eliminarCliente(id);
                } else {
                    System.out.println(">>>>> O/A cliente/a non foi eliminado/a.");
                }

            } else {                
                System.out.println(">>>>> Non existe ningún/ha cliente/a con código " + id);
            }
            
        } catch (Exception erro) {
            System.out.println("Erro buscando o/a cliente a eliminar: " + erro.getMessage());
        }
        
    }
    
    public static void listarClientes(Boolean verCabeceira) {

            
        System.out.println("+===========================================+");
        System.out.println("|             LISTAR CLIENTES               |");        
        System.out.println("+===========================================+\n\n");
        
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        try {           

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from clientes");
            
            System.out.printf("%9s%2s%-30s%2s%-25s%2s%-20s","CODIGO","","NOME","", "APELIDOS", "", "EMAIL");
            
            while(rs.next()){
                System.out.println("");
                System.out.printf("%9s%2s%-30s%2s%-25s%2s%-20s",rs.getString("cl_id"), "", rs.getString("cl_nome"),"", rs.getString("cl_apelidos"), "", rs.getString("cl_email"));
            }
            System.out.println(programacion.Main.FINLISTAXE);
        }
        catch(Exception erro){
            System.err.println("Erro listando tendas: " + erro.getMessage());
        }
        
        BaseDatos.desconectarDB(conn);
    }
    
        
}
