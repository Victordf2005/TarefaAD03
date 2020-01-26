package clases;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import programacion.Comuns;

/**
 *
 * @author Víctor Díaz
 */
public class BaseDatos {
        
    private static final String JDBC = "jdbc:sqlite:";
        
    /*
    Este método crea unha nova base de datos en SQLLite co nome que se lle pasa como argunmento
    */
    public static void crearDB(String nome){
        
        String baseDatos =  JDBC + nome;
        
        try{
            Connection conexion = DriverManager.getConnection(baseDatos);
            if(conexion != null){
                DatabaseMetaData meta = conexion.getMetaData();
                System.out.println("A base de datos foi creada");
                crearTaboas(conexion);
            }
        }
        catch(SQLException erro){
            System.out.println("Erro creando a base de datos:\n" + erro.getMessage());
        }
        
    }
    
    private static void crearTaboas(Connection conexion) {
                
        try{
            Statement stmt = conexion.createStatement();
            
            String sql = "CREATE TABLE IF NOT EXISTS tendas " +
                    "(te_id INTEGER PRIMARY KEY," +
                    "te_prvid TEXT (2) NOT NULL,"+
                    "te_cidade TEXT (25) NOT NULL,"+
                    "te_nome TEXT (30) NOT NULL"+
                    ");";

            stmt.execute(sql);
                        
            sql = "CREATE TABLE IF NOT EXISTS produtos " + 
                    "(pr_id INTEGER PRIMARY KEY,"+
                    "pr_descricion TEXT (30) NOT NULL,"+
                    "pr_prezo DECIMAL (7, 2) NOT NULL DEFAULT (0.0)"+
                    ");";
            
            stmt.execute(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS empregados " +
                    "(em_id INTEGER PRIMARY KEY," +
                    "em_nome TEXT (25) NOT NULL,"+
                    "em_apelidos TEXT (40) NOT NULL"+
                    ");";

            stmt.execute(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS clientes " +
                    "(cl_id INTEGER PRIMARY KEY," +
                    "cl_nome TEXT (25) NOT NULL,"+
                    "cl_apelidos TEXT (40) NOT NULL,"+
                    "cl_email TEXT (100) NOT NULL"+
                    ");";

            stmt.execute(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS tendas_produtos " +
                    "(tepr_id INTEGER PRIMARY KEY," +
                    "tepr_teid INTEGER REFERENCES tendas (te_id) ON DELETE CASCADE\n" +
                                " ON UPDATE CASCADE\n" +
                                " MATCH SIMPLE," +
                    "tepr_prid INTEGER REFERENCES produtos (pr_id) ON DELETE CASCADE\n" +
                                " ON UPDATE CASCADE\n" +
                                " MATCH SIMPLE," +
                    "tepr_stock INTEGER DEFAULT (0)" +
                    ");";

            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS tendas_empregados " +
                    "(teem_teid INTEGER REFERENCES tendas (te_id) ON DELETE CASCADE\n" +
                                " ON UPDATE CASCADE\n" +
                                " MATCH SIMPLE," +
                    "teem_emid INTEGER REFERENCES empregados (em_id) ON DELETE CASCADE\n" +
                                " ON UPDATE CASCADE\n" +
                                " MATCH SIMPLE," +
                    "teem_ano INTEGER DEFAULT (0)," +
                    "teem_semana INTEGER DEFAULT (0)," +
                    "teem_horas INTEGER DEFAULT (0)," +
                    "PRIMARY KEY (teem_teid, teem_emid)" +
                    ");";

            stmt.execute(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS provincias " +
                    "(prv_id INTEGER PRIMARY KEY," +
                    "prv_nome TEXT (30) NOT NULL"+
                    ");";

            stmt.execute(sql);
            
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
  
    public static Connection conectarDB(String nome){
        Connection conexion = null;
        try
        {
            //Creamos a conexión a base de datos
            Properties propiedadesConex=new Properties();
            propiedadesConex.setProperty("foreign_keys", "true");   //comprobar claves foráneas
            conexion = DriverManager.getConnection(JDBC + nome, propiedadesConex);
            return conexion;
             
        }
        catch(SQLException erro){
            System.err.println(erro.getMessage());
            return null;
        }
    }
    
    /*
    Este método desconectase dunha base de datos en SQLLite a que se lle pasa a conexión
    */
    public static void desconectarDB(Connection conexion){
        try{
            if(conexion != null){
                conexion.close();
            }
        }
        catch(SQLException erro){
            System.out.println("Erro pechando a conexión:\n" + erro.getMessage());
        }
    }
    
    /*
    Engadir nova tenda á base de datos
    */
    
    public static void engadirTenda(String nome, String codProv, String cidade) {
                 
        Connection conn = conectarDB("franquicia.db");
        
        try{
            String sql ="INSERT INTO tendas (te_prvid, te_cidade, te_nome) " +
                        " VALUES (?, ?, ?);";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, codProv);
            pstmt.setString(2, cidade);
            pstmt.setString(3, nome);
            pstmt.executeUpdate();
            
            System.out.println("Tenda engadida correctamente á base de datos.");
        }
        
        catch(SQLException erro){
            System.out.println("Erro engadindo a tenda á base de datos: " + erro.getMessage());
        }
        
        desconectarDB(conn);
    }
    
    
    public static void eliminarTenda(String id) {
    
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "DELETE FROM tendas where te_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            switch (filasAfectadas) {
                case 0:
                    System.out.println(">>>>> Ningunha tenda eliminada.");
                    break;
                case 1:
                    System.out.println("A tenda foi eliminada correctamente.");
                    break;
                default:
                    System.out.println(">>>>> Eliminadas varias tendas.");
            }
        } catch (Exception erro) {
            System.out.println("Erro eliminando a tenda seleccionada: " + erro.getMessage());
        }
    }
    
    public static void listarTendas(Boolean verCabeceira) {

        if (verCabeceira) {
            
            System.out.println("+===========================================+");
            System.out.println("|              LISTAR TENDAS                |");        
            System.out.println("+===========================================+\n\n");

        } else {            
            System.out.println("Listaxe de tendas da franquicia:\n");
        }        
        
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        try {           

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from tendas left join provincias on te_prvid=prv_id");
            
            System.out.printf("%9s%2s%-30s%2s%-25s%2s%-20s","CODIGO","","NOME","", "CIDADE", "", "PROVINCIA");
            int indiceTenda = 0;
            
            while(rs.next()){
                System.out.println("");
                indiceTenda = Integer.parseInt(rs.getString("te_prvid")) - 1;
                System.out.printf("%9s%2s%-30s%2s%-25s%2s%-20s",rs.getString("te_id"), "", rs.getString("te_nome"),"", rs.getString("te_cidade"), "", rs.getString("prv_nome"));
            }
            System.out.println(programacion.Main.FINLISTAXE);
        }
        catch(Exception erro){
            System.err.println("Erro listando tendas: " + erro.getMessage());
        }
        
        BaseDatos.desconectarDB(conn);
    }
    
    
    public static void engadirProduto(String descricion, String prezo) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "INSERT INTO produtos (pr_descricion, pr_prezo) " +
                    "VALUES (?, ?);";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, descricion);
            pstmt.setString(2, prezo);
            pstmt.executeUpdate();
            
            System.out.println("O produto foi engadido correctamente á base de datos.");
            
        } catch (Exception erro) {
            System.out.println("Erro engadindo o produto á base de datos: " + erro.getMessage());
        }
        
        desconectarDB(conn);
           
    }
    
    public static void eliminarProduto(String id) {
    
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "DELETE FROM produtos where pr_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            switch (filasAfectadas) {
                case 0:
                    System.out.println(">>>>> Ningún produto eliminado.");
                    break;
                case 1:
                    System.out.println("O produto foi eliminado correctamente.");
                    break;
                default:
                    System.out.println(">>>>> Eliminados varios produtos.");
            }
        } catch (Exception erro) {
            System.out.println("Erro eliminando o produto seleccionado: " + erro.getMessage());
        }
    }
    
    public static void eliminarProdutoTenda(String id) {
    
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "DELETE FROM tendas_produtos where tepr_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            switch (filasAfectadas) {
                case 0:
                    System.out.println(">>>>> Ningún produto eliminado da tenda.");
                    break;
                case 1:
                    System.out.println("O produto foi eliminado correctamente da tenda.");
                    break;
                default:
                    System.out.println(">>>>> Eliminados varios produtos da tenda.");
            }
        } catch (Exception erro) {
            System.out.println("Erro eliminando o produto seleccionado da tenda seleccionada: " + erro.getMessage());
        }
    }
    
    public static void listarProdutosTenda(Boolean verCabeceira) {
            
        Scanner teclado = new Scanner(System.in);

        if (verCabeceira) {
            System.out.println("+===========================================+");
            System.out.println("|    LISTAR PRODUTOS DUNHA TENDA            |");        
            System.out.println("+===========================================+\n\n");
        }
        
        BaseDatos.listarTendas(false);
                
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        System.out.println("Teclea o código do/da tenda/a para listar os seus produtos (tecleando un código fóra da listaxe ou negativo cancelamos o proceso). ");

        String id = "";
        
        while (!Comuns.eNumerico(id)) {
            id = teclado.nextLine();
        }
        
        if (existe(id,"tendas")) {
            try {           

                System.out.println("\nListaxe de produtos desta tenda.");
                
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select * from tendas_produtos left join produtos on tepr_prid=pr_id where tepr_teid=" + id);

                System.out.printf("%9s%2s%9s%2s%-30s%2s%7s%2s%9s","CODIGO_ID","","COD.PROD.","","PRODUTO","", "PREZO", "", "STOCK");

                while(rs.next()){
                    System.out.println("");
                    System.out.printf("%9s%2s%9s%2s%-30s%2s%7.2f%2s%9s",rs.getString("tepr_id"),"", rs.getString("tepr_prid"), "", rs.getString("pr_descricion"),"", Double.parseDouble(rs.getString("pr_prezo")), "", rs.getString("tepr_stock"));
                }
                System.out.println(programacion.Main.FINLISTAXE);
            }
            catch(Exception erro){
                System.err.println("Erro listando tendas: " + erro.getMessage());
            }

            BaseDatos.desconectarDB(conn);
        } else {
            System.out.println("Non existe a tenda co código " + id);
        }
        
    }
    
    public static void listarProdutoTenda(String produto, String tenda) {

        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        System.out.println("Teclea o código do/da tenda/a para listar os seus produtos (tecleando un código fóra da listaxe ou negativo cancelamos o proceso). ");

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from tendas_produtos left join produtos on tepr_prid=pr_id where tepr_prid=" + produto + " and tepr_teid=" + tenda);

            System.out.printf("%9s%2s%9s%2s%-30s%2s%9s","CODIGO_ID","","COD.PROD.","","PRODUTO", "", "STOCK");

            while(rs.next()){
                System.out.println("");
                System.out.printf("%9s%2s%9s%2s%-30s%2s%9s",rs.getString("tepr_id"),"", rs.getString("tepr_prid"), "", rs.getString("pr_descricion"), "", rs.getString("tepr_stock"));
            }
            System.out.println(programacion.Main.FINLISTAXE);
        }
        catch(Exception erro) {
            System.out.println("Erro listando stock: " + erro.getMessage());
        }
    }
        
    public static void engadirProdutoTenda(String produto, String tenda, String stock) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "INSERT INTO tendas_produtos (tepr_teid, tepr_prid, tepr_stock) " +
                    "VALUES (?, ?, ?);";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, tenda);
            pstmt.setString(2, produto);
            pstmt.setString(3, stock);
            pstmt.executeUpdate();
            
            System.out.println("O produto foi engadido correctamente á tenda seleccionada.");
            
        } catch (Exception erro) {
            System.out.println("Erro engadindo o produto á tenda seleccionada: " + erro.getMessage());
        }
        
        desconectarDB(conn);
           
    }
    
    public static void actualizarStock(String id, String stock) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "UPDATE tendas_produtos set tepr_stock=? where tepr_id=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, stock);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
            
            System.out.println("O stock foi actualizado correctamente.");
            
        } catch (Exception erro) {
            System.out.println("Erro actualizando o stock do produto seleccionado: " + erro.getMessage());
        }
        
        desconectarDB(conn);
    
    }
    
    
    public static void engadirEmpregado(String nome, String apelidos) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "INSERT INTO empregados (em_nome, em_apelidos) " +
                    "VALUES (?, ?);";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, nome);
            pstmt.setString(2, apelidos);
            pstmt.executeUpdate();
            
            System.out.println("O/A empregado/a foi engadido/a correctamente á base de datos.");
            
        } catch (Exception erro) {
            System.out.println("Erro engadindo o/a empregado/a á base de datos: " + erro.getMessage());
        }
        
        desconectarDB(conn);
           
    }
        
    public static void eliminarEmpregado(String id) {
    
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "DELETE FROM empregados where em_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            switch (filasAfectadas) {
                case 0:
                    System.out.println(">>>>> Ningún/ha empregado/a eliminado/a.");
                    break;
                case 1:
                    System.out.println("O/A empregado foi eliminado/a correctamente.");
                    break;
                default:
                    System.out.println(">>>>> Eliminados varios/as empregados/as.");
            }
        } catch (Exception erro) {
            System.out.println("Erro eliminando o/a empregado/a seleccionado/a: " + erro.getMessage());
        }
    }
        
    public static void listarEmpregados(Boolean verCabeceira) {

        if (verCabeceira) {
            
            System.out.println("+===========================================+");
            System.out.println("|        LISTAXE DE EMPREGADOS/AS           |");        
            System.out.println("+===========================================+\n\n");

        } else {            
            System.out.println("Listaxe de empregados/as da franquicia:\n");
        }        
        
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        try {           

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from empregados");
            
            System.out.printf("%9s%2s%-20s%2s%-30s","CODIGO","","NOME","", "APELIDOS");
                        
            while(rs.next()){
                System.out.println("");
                System.out.printf("%9s%2s%-20s%2s%-30s",rs.getString("em_id"), "", rs.getString("em_nome"),"", rs.getString("em_apelidos"));
            }
            System.out.println("");
        }
        catch(Exception erro){
            System.err.println("Erro listando empregados/as: " + erro.getMessage());
        }
        
        BaseDatos.desconectarDB(conn);
    }
    
    public static void rexistrarHorasEmpregado(String empregado, String tenda, String ano, String semana, String horas) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "INSERT INTO tendas_empregados (teem_teid, teem_emid, teem_ano, teem_semana, teem_horas) " +
                    "VALUES (?, ?, ?, ?, ?);";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, tenda);
            pstmt.setString(2, empregado);
            pstmt.setString(3, ano);
            pstmt.setString(4, semana);
            pstmt.setString(5, horas);
            pstmt.executeUpdate();
            
            System.out.println("Rexistradas correctamente as horas traballadas.");
            
        } catch (Exception erro) {
            System.out.println("Erro rexistrando as horas traballadas: " + erro.getMessage());
        }
        
        desconectarDB(conn);
           
    }
    
    public static void verHorasTraballadas(String empregado, String tenda) {
    
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        try {           

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from tendas_empregados where teem_emid=" + empregado + " and teem_teid=" + tenda);
            
            System.out.printf("%5s%8s%7s","ANO","SEMANA", "HORAS");
                        
            while(rs.next()){
                System.out.println("");
                System.out.printf("%5s%8s%7s",rs.getString("teem_ano"), rs.getString("teem_semana"), rs.getString("teem_horas"));
            }
            System.out.println("");
        }
        catch(Exception erro){
            System.err.println("Erro listando as horas traballadas: " + erro.getMessage());
        }
        
        BaseDatos.desconectarDB(conn);
    }
    
    
    public static void engadirCliente(String nome, String apelidos, String email) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "INSERT INTO clientes (cl_nome, cl_apelidos, cl_email) " +
                    "VALUES (?, ?, ?);";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, nome);
            pstmt.setString(2, apelidos);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            
            System.out.println("O/A cliente/a foi engadido/a correctamente á base de datos.");
            
        } catch (Exception erro) {
            System.out.println("Erro engadindo o/a cliente/a á base de datos: " + erro.getMessage());
        }
        
        desconectarDB(conn);
           
    }
        
    public static void eliminarCliente(String id) {
    
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        try {
            String sql = "DELETE FROM clientes where cl_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            switch (filasAfectadas) {
                case 0: {
                    System.out.println(">>>>> Ningún/ha cliente/a eliminado/a.");
                    break;
                }
                case 1: {
                    System.out.println("O/A cliente foi eliminado/a correctamente.");
                    break;
                }
                default: {
                    System.out.println(">>>>> Eliminados varios/as clientes/as.");
                }
            }
        } catch (Exception erro) {
            System.out.println("Erro eliminando o/a cliente/a seleccionado/a: " + erro.getMessage());
        }
    }
    
    public static void listarProvincias() {
                
        System.out.println("Listaxe de provincias:\n");
        
        Connection conn = BaseDatos.conectarDB(programacion.Main.BASE_DATOS);
        
        try {           

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from provincias");
            
            System.out.printf("%3s%2s%-30s","COD","","PROVINCIA");
                        
            while(rs.next()){
                System.out.println("");
                System.out.printf("%3s%2s%-30s",rs.getString("prv_id"), "", rs.getString("prv_nome"));
            }
            System.out.println("");
        }
        catch(Exception erro){
            System.err.println("Erro listando provincias: " + erro.getMessage());
        }
        
        BaseDatos.desconectarDB(conn);
    }
    
    
    private static boolean existe(String id, String taboa ) {
        
        Boolean retorno = false;

        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        String condicion = "";
        
        switch (taboa) {
            case "tendas": {
                condicion = "te_id=" + id;
                break;
            }
            case "produtos": {
                condicion = "pr_id=" + id;
                break;
            }
            case "empregados": {
                condicion = "em_id=" + id;
                break;
            }
            case "provincias": {
                condicion = "prv_id=" + id;
                break;
            }
        }
        
        try {
            String sql = "select * from " + taboa + " where " + condicion;
            
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
        
            while (rs.next()) {
                retorno = true;
            }
        } catch (Exception erro) {
            System.out.println(erro.getMessage());
        }
        
        return retorno;
    }

    public static boolean existeProduto(String id){
        return existe(id, "produtos");
    }
    
    public static boolean existeTenda(String id) {
        return existe(id, "tendas");
    }
    
    public static boolean existeEmpregado(String id){
        return existe(id, "empregados");
    }
    
    public static boolean existeProvincia(String id){
        return existe(id, "provincias");
    }
    
    public static boolean existeProdutoTenda(String produto, String tenda) {
        
        Boolean retorno = false;

        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
                
        try {
            String sql = "select * from tendas_produtos where tepr_prid=" + produto + " and tepr_teid=" + tenda;
            
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
        
            while (rs.next()) {
                retorno = true;
            }
        } catch (Exception erro) {
            System.out.println(erro.getMessage());
        }
        
        return retorno;
    }
    
    public static void gravarProvincias(Provincias provincias) {
        
        Connection conn = conectarDB(programacion.Main.BASE_DATOS);
        
        System.out.println("Gravando provincias na base de datos ...");
        
        try {
            for (int i = 0; i<provincias.getProvincias().size(); i++) {
                String sql = "INSERT INTO provincias (prv_id, prv_nome) " +
                        "VALUES (?, ?);";

                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, provincias.getProvincias().get(i).getId());
                pstmt.setString(2, provincias.getProvincias().get(i).getNome());
                pstmt.executeUpdate();
            }
            
            System.out.println("Gravadas as provincias na base de datos.");
            
        } catch (Exception erro) {
            System.out.println("Erro engadindo as provincias á base de datos: " + erro.getMessage());
        }
        
        desconectarDB(conn);
        
    }
}