/**
 * Ciara McMullin
 * Operating Systems Fall 2018
 * Lab 1: Two Pass Linker
 *
 */
import java.util.*;
// module objects
public class Module{

    LinkedHashMap<String, Integer> definitions;
    LinkedHashMap<ArrayList<Integer>, String> uses;
    ArrayList<Integer> instructions;
    int base = 0;

    Module(){

        definitions = new LinkedHashMap<String, Integer>();
        uses = new LinkedHashMap<ArrayList<Integer>, String>();
        instructions = new ArrayList<Integer>();

    }

}