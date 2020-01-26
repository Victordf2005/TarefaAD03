package clases;


import java.util.ArrayList;

/**
 *
 * @author Víctor Díaz
 */
public class Provincias {
    
    private ArrayList<Provincia> provincias;
    
    //construtor por defecto
    public Provincias() {
        this.provincias = new ArrayList<Provincia>();
    }
    
    //construtor con parámetros
    public Provincias(ArrayList<Provincia> pProvincia) {
        this.provincias = new ArrayList<Provincia>(pProvincia);
    }
    
    //setters
    public void setProvincia(ArrayList<Provincia> parametro) {this.provincias = parametro;}
    
    //getters
    public ArrayList<Provincia> getProvincias() {return provincias;}
    
}
