
package programacion;

/**
 *
 * @author Víctor Díaz
 */
public class Comuns {
    
    public static boolean eNumerico(String cadena){
	try {
		Float.parseFloat(cadena);
		return true;
	} catch (NumberFormatException erro){
		return false;
	}
    }
}
