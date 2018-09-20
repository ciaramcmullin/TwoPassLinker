/**
 *
 * Ciara McMullin
 * Operating Systems Fall 2018
 * Lab 1: Two Pass Linker
 *
 */

import java.util.*;

public class Linker {

    static ArrayList<Module> mods = new ArrayList<Module>();
    static LinkedHashMap<String, Integer> symbols = new LinkedHashMap<String, Integer>();
    static ArrayList<String> multDefined = new ArrayList<String>();
    static ArrayList<String> definedAndNotUsed = new ArrayList<String>();
    static int memorySize = 300;
    static int maxAddress = 299;
    static int sizeOfModules = 0;



    public static void firstPass(Scanner input){

        try {
            // number of modules being passed in
            int numOfMods = Integer.parseInt(input.next());
            //System.out.println(numOfMods);

            // definitions
            int numOfDefs = 0;
            int baseAddress = 0;

            // uses
            int numOfUses = 0;

            // instructions/program text
            int numOfInst = 0;

            // loop through number of mods and create a new module for each and append to list of modules
            for (int i = 0; i < numOfMods; i++) {
                Module m = new Module();
                m.base = baseAddress;
                mods.add(m);

                // get definition list
                numOfDefs = input.nextInt();
                // System.out.println(numOfDefs);

                for (int j = 0; j < numOfDefs; j++) {
                    String key = input.next();
                    //System.out.println(key);
                    int value = input.nextInt();
                    // System.out.println(value);
                    m.definitions.put(key, value);

                    // check if muliply defined
                    if (symbols.containsKey(key)) {
                        multDefined.add(key);
                    }
                    symbols.put(key, value + baseAddress);
                }

                // get use list
                numOfUses = input.nextInt();
                // System.out.println(numOfUses);

                for (int k = 0; k < numOfUses; k++) {
                    String s = input.next();
                    int vals = input.nextInt();
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(vals);
                    while (true) {
                        vals = input.nextInt();
                        if (vals == -1)
                            break;
                        list.add(vals);
                    }
                    m.uses.put(list, s);
                }

                // get instructions
                numOfInst = input.nextInt();
                for (int l = 0; l < numOfInst; l++) {
                    m.instructions.add(input.nextInt());
                    sizeOfModules++;

                }

                baseAddress += numOfInst;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    // second pass
    public static void secondPass(Scanner input){
        System.out.println();
        System.out.println("Memory Map");
        int count = 0;

        for(Module m: mods){
            // System.out.println(mods.size());
            int base = m.base;
           // System.out.println(m.instructions);

            for(int i = 0; i < m.instructions.size();i++){

                int lastDigit = m.instructions.get(i) % 10;
                int address = m.instructions.get(i)/10;

                // check if immediate --> do nothing
                if(lastDigit == 1){
                    System.out.println(count + ": " + address);
                }
                // check if absolute and if it is less than max word size
                else if(lastDigit == 2){
                    int lastThree = address % 1000;
                    int first =  (address / 1000) * 1000;

                    if(lastThree >= memorySize){
                        System.out.println(count + ": " + (first + maxAddress) + " Error: Absolute address exceeds machine size; largest legal value used");
                    }
                    else{
                        System.out.println(count + ": " + (address));
                    }
                }
                // check if relative --> add the base address
                else if(lastDigit == 3){
                    System.out.println(count + ": " + (address + base));
                }

                // check if external --> look up in symbols map
                else {
                    String sym = "";
                    ArrayList<Integer> multSyms = new ArrayList<>();
                    for(ArrayList<Integer> arr : m.uses.keySet()){
                        if(arr.contains(i)){
                            sym = m.uses.get(arr);
                            multSyms.add(i);
                        }
                    }

                    address /= 1000;
                    address *= 1000;

                    if(!(symbols.containsKey(sym))){
                        System.out.println(count + ": " + (address + 111) + " Error: "+ sym+ " is not defined; 111 used.");
                    }

                    else if(multSyms.size() > 1){
                        System.out.println(count + ": " + (address + symbols.get(sym)) + " Error: Multiple variables used in instruction; all but last ignored.");
                    }
                    else {
                        // check if multiply defined
                        System.out.println(count + ": " + (address + symbols.get(sym)));
                    }
                    definedAndNotUsed.add(sym);
                }
                count++;
            }

        }

    }

    public static void main(String[] args){

        Scanner input = new Scanner(System.in);

        // first pass of the input
        firstPass(input);

        // print symbol table
        System.out.println();
        System.out.println("Symbol Table");

        for(String s: symbols.keySet()){

            if(symbols.get(s) > sizeOfModules-1){
                symbols.put(s, sizeOfModules-1);
                System.out.printf(s + "=" + symbols.get(s) + " Error: Definition exceeds module size; last word in module used" + "\n");
            }
            else if(multDefined.contains(s)){
                System.out.printf(s + "=" + symbols.get(s) + " Error: This variable is multiply defined; last value used" + "\n");
            }
            else {
                System.out.printf(s + "=" + symbols.get(s) + "\n");
            }

        }

        // second pass of the input
        secondPass(input);

        // close scanner
        input.close();
        System.out.println();
        // check if defined and not used
        int c = 0;
        for(Module m: mods){
            for(String s: m.definitions.keySet() ){
                if(!(definedAndNotUsed.contains(s))){
                    System.out.println("Warning: " + s + " was defined in module " + c + " but never used.");
                }
            }
            c++;
        }

    }
}




