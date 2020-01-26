package clases;


/**
 *
 * @author Víctor Díaz
 */
public class Provincia implements Comparable<Provincia>{
    
    private String id;
    private String nome;
    
    
    //Construtor por defecto
    public Provincia(){
        this.id ="00";
        this.nome= new String("");
    }
    
    //Contrutor con parámetros
    public Provincia(String pId, String pDescricion){
        this.id = pId;
        this.nome = new String(pDescricion);
    }
    
    //setters
    public void setId(String parametro) {this.id = parametro;}    
    public void setNome(String parametro) {this.nome = parametro;}    
    
    //getters
    public String getId() {return this.id;}    
    public String getNome() {return this.nome;}

    @Override
    public int compareTo(Provincia prv) {
        return id.compareTo(prv.getId());
    }
}
