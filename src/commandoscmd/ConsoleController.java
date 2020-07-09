/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commandoscmd;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;


/**
 * Clase encargada de controlar los eventos
 * en la consola y llevar el computo del sistema
 * operativo
 * @author Johann
 */
public class ConsoleController 
{
    public String VERSION = "0.3";
    private String prompt;
    private Hashtable<String, Double> vars;
    
    public ConsoleController()
    {
        vars = new Hashtable<String,Double>();
        prompt = "CommandOS> ";
        System.out.print("Buongiorno! Bienvenido a CommandOS --- [Version "+VERSION+"] by Johann"+"\n"+prompt);
    }
    
    /***
     * Lee el comando y basado en esto lo maneja
     */
    public void handleCall(String command)
    {
        if(command.trim().equals(""))
        {
            log("");
            return;
        }
        String[] commands = command.split(" ");
        switch(commands[0].trim().toLowerCase())
        {
            case "inc":
            case "dec":
            case "sqrt":
            case "ln":
            case "fact":
                doOperator(commands);
                break;
            case "sum":
            case "modus":
            case "rest":
            case "divi":
            case "multi":
            case "log":
            case "pow":
                doOperation(commands);
                break;
            case "vari":
                createVariable(commands);
                break;
            case "clear":
                clearScreen();
                break;
            case "helpti":
                showHelp(commands);
                break;
            case "value":
                listVars(commands);
                break;
            case "":
                log("");
                break;
            case "prompti":
                changePrompt(command);
                break;
            case "ciao":
                exit();
                break;
            case "date":
                currentDate();
                break;
            case "assign":
                changeValue(commands);
                break;
            default:
                log("No se reconoce el comando "+commands[0].trim()+".\nPruebe de nuevo o escriba helpti para obtener ayuda");
                break;
        }
    }
    
    private void currentDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        Date date = new Date();  
        log(formatter.format(date));  
    }
    
    /***
     * Cambia el valor de la variable correspondiente
     * @param commands arreglo de strings del comando
     * [1] variable a cambiar valor
     * [2] nuevo valor
     */
    private void changeValue(String[] commands)
    {
        if(commands.length == 3)
        {
            if(vars.containsKey(commands[1]))
            {
                Double newValue;
                if(isNumeric(commands[2]))
                {
                    newValue = getNumericValue(commands[2]);
                }
                else if(vars.containsKey(commands[2]))
                {
                    newValue = vars.get(commands[2]);
                }
                else
                {
                    log("Error, el valor destino es invalido");
                    return;
                }
                vars.replace(commands[1], newValue);
                log("La variable "+ commands[1]+" ahora tiene el valor de "+newValue);
            }
            else
            {
                log("Error, la variable a asignar el valor no existe");
            }
        }
    }
    
    private void doOperator(String[] args)
    {
        executeOperationOneArgument(args, true);
    }
    
    /***
     * Realiza las operaciones de un argumento y retorna el valor resultado
     * @param args argumento y operacion
     * @param register si va a loggear o no
     * @return valor nuevo
     */
    private Double executeOperationOneArgument(String[] args, boolean register)
    {
        if(args.length!=2)
        {
            logCheck("La operacion requiere de solo un argumento",register);
            return null;
        }
        if(args[0].toLowerCase().equals("inc") || args[0].toLowerCase().equals("dec"))
        {
            if(vars.containsKey(args[1]))
            {
                Double newValue;
                if(args[0].toLowerCase().equals("inc"))
                {
                    newValue = vars.get(args[1])+1.0;
                }
                else
                {
                    newValue = vars.get(args[1])-1.0;
                }
                logCheck("La variable "+args[1]+" ahora tiene un valor de "+newValue,register);
                vars.replace(args[1], newValue);
                return newValue;
            }
            else
            {
                logCheck("El comando "+args[0]+" solo se puede usar para variables declaradas",register);
                return null;
            }
        }
        if(isValidArgument(args[1]))
        {
            if(getValue(args[1])<0)
            {
                logCheck("No es posible realizar la operacion con numeros negativos",register);
                return null;
            }
            double variable = getValue(args[1]);
            double res;
            switch(args[0].toLowerCase()) //ToDo
            {
                case "sqrt":
                    res = Math.sqrt(variable);
                    logCheck("sqrt("+variable+") = "+res,register);
                    return res;
                case "fact":
                    if((variable == Math.floor(variable)) && !Double.isInfinite(variable))
                    {
                        int result = factorial((int)variable);
                        logCheck(""+variable+"! = "+result,register);
                        return (double) result;
                    }
                    else
                    {
                        logCheck("El factorial solo puede ser aplicado a numeros enteros",register);
                        return null;
                    }
                case "ln":
                    res = Math.log(variable);
                    logCheck("ln "+variable+" = "+res,register);
                    return res;
            }
        }
        else
        {
            logCheck("El valor no es un numero valido o una variable declarada",register);
        }
        return null;
    }
    
    /***
     * Realiza las operaciones de dos argumentos
     * Llama a la funcion executeOperation diciendo que si va a 
     * mostrar el log e ignorar el resultado, solo mostrarlo
     * @param args argumentos y operador
     */
    private void doOperation(String[] args)
    {
        executeOperationTwoArguments(args,true);
    }
    
    /***
     * Realiza las operaciones de dos argumentos
     * @param args argumentos y operacion
     * @param register indica si se loggeará el resultado
     * @return resultado
     */
    private Double executeOperationTwoArguments(String[] args, boolean register)
    {
        if(args.length != 3)
        {
            logCheck("La operacion requiere solo de dos valores",register);
            return null;
        }
        if(isValidArgument(args[1]) && isValidArgument(args[2]))
        {
            char sign;
            Double result;
            switch(args[0].toLowerCase())
            {
                case "sum":
                    result = getValue(args[1]) + getValue(args[2]);
                    sign = '+';
                    break;
                case "rest":
                    result = getValue(args[1]) - getValue(args[2]);
                    sign = '-';                    
                    break;
                case "divi":
                    if(getValue(args[2])==0)
                    {
                        logCheck("El resultado es invalido",register);
                        return null;
                    }
                    result = getValue(args[1]) / getValue(args[2]);
                    sign = '/';
                    break;
                case "multi":
                    result = getValue(args[1]) * getValue(args[2]);
                    sign = '*';
                    break;
                case "modus":
                    result = getValue(args[1]) % getValue(args[2]);
                    sign = '%';
                    break;
                case "pow":
                    result = Math.pow(getValue(args[1]), getValue(args[2]));
                    sign = '^';
                    break;
                case "log":
                    if(getValue(args[1])<0 || getValue(args[2])<0)
                    {
                        logCheck("No existe logaritmo de un numero o base negativa",register);
                        return null;
                    }
                    result = Math.log(getValue(args[2])) / Math.log(getValue(args[1]));
                    logCheck("log "+args[1]+" ("+args[2]+") = "+result,register);
                    return result;
                default:
                    logCheck("La operacion es invalida",register);
                    return null;
            }
            logCheck(args[1]+" "+sign+" "+args[2]+" = "+result.toString(),register);
            return result;
        }
        else
        {
            String log = "Uno o mas argumentos son invalidos:";
            if(!isValidArgument(args[1]))
            {
                log += "\nEl identificador "+args[1]+" no es una variable o un valor numerico valido";
            }
            if(!isValidArgument(args[2]))
            {
                log += "\nEl identificador "+args[1]+" no es una variable o un valor numerico valido";
            }
            logCheck(log,register);
            return null;
        }
    }
    
    private void listVars(String[] commands)
    {
        String text="";
        if(commands.length==1) //Listar todo
        {
            text = "Lista de variables:";
            Set<String> keys = vars.keySet();
            for(String key : keys)
            {
                text += "\n" + key + " ==> " + vars.get(key);
            }
            if(keys.size()==0)
            {
                text = "No hay variables declaradas";
            }
        }
        else
        {
            for (int i = 1; i < commands.length; i++) 
            {
                String name = commands[i].trim();
                text += name;
                text += "\n"+ ((vars.containsKey(name))? " ==> "+vars.get(name)+ "\n": " ===>No existe variable"); 
            }
        }
        log(text);
    }
    
    /***
     * Funcion encargada de manejar el comando para crear variables
     * Compara los argumentos del comando basado en su sintaxis y asigna
     * en caso de ser correcto
     * @param command comando completo
     */
    private void createVariable(String[] commands)
    {
        if(commands.length>1)
        {
            String name = commands[1].trim();
            if(Character.isAlphabetic(name.charAt(0)))
            {
                if(vars.containsKey(name))
                {
                    log("La variable "+name+" ya existe");
                    return;
                }
                Double value = 0.0;
                if(commands.length==3)
                {
                    if(isNumeric(commands[2]))
                    {
                        value = getNumericValue(commands[2]);
                    }
                    else
                    {
                        log("El valor asignado no es numerico (decimal o entero)");
                        return;
                    }
                }
                else if(commands.length>3)
                {
                    log("Demasiados argumentos para el comando vari");
                    return;
                }
                vars.put(name, value);
                log("Variable "+name+" creada con valor "+value);
            }
            else
            {
                log("Nombre invalido para la variable, no puede empezar con una letra");
            }
        }
        else
        {
            log("No se especifico el nombre de la variable. Escriba helpti vari para obtener ayuda");
        }
    }
    
    /***
     * Maneja el comando helpti
     */
    private void showHelp(String[] commands)
    {
        String text = "Para obtener mas detalles de cada comando, escriba helpti seguido "
                + "del nombre del comando:\n"
                + "ciao:      Sale de CommandOS\n"
                + "clear:     Limpia la consola\n"
                + "date:      Muestra la fecha y hora actual\n"
                + "dec:       Decrementa en uno una variable\n"
                + "divi:      Divide dos valores (variable o numeros)\n"
                + "fact:      Obtiene el factorial de un valor (variable o numero)\n"
                + "helpti:    Muestra ayuda con el manejo de CommandOS\n"
                + "inc:       Incrementa en uno una variable\n"
                + "ln:        Obtiene el logaritmo natural de un valor (variable o numero)\n"
                + "log:       Obtiene el logaritmo base n de un valor\n"
                + "modus:     Calcula el residuo de la division de dos valores (variable o numeros)\n"
                + "multi:     Multiplica dos valores(variable o numeros)\n"
                + "pow:       Eleva un valor a una potencia (variables o numeris)\n"
                + "prompti:   Cambia el prompt de la consola\n"
                + "rest:      Resta dos valores (variable o numeros)\n"
                + "sqrt:      Obtiene la raiz cuadrado de un valor (variable o numero)\n"
                + "sum:       Suma dos valores (variable o numeros)\n"
                + "value:     Muestra el valor de la(s) variable(s)\n"
                + "vari:      Crea variables numericas";
        log(text);
    }
    
    private void clearScreen()
    {
        try
        {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch(IOException | InterruptedException ex)
        {
            log("No se pudo limpiar la pantalla");
        }
        log("");
    }
    
    /***
     * Maneja el comando de salida
     * Imprime el mensaje de saida y despues de 2 segundos sale del programa
     */
    private void exit()
    {
        System.out.println("\nCiao! Gracias por usar CommandOS");
        try 
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex) 
        {
            System.out.println("Error jsjs");
        }
        System.exit(0);
    }
    
    /***
     * Maneja el comando para cambiar el prompt dependiendo de los argumentos
     */
    private void changePrompt(String command)
    {
        String newPrompt = (command.contains(" "))? command.substring(command.indexOf(" ")).trim() : "";

        if(newPrompt.equals(""))
        {
            changeCommandPrompt("CommandOS");
            log("Prompt cambiado a default");
        }
        else
        {
            changeCommandPrompt(newPrompt);
            log("Prompt cambiado con exito a "+newPrompt);
        }
    }
    
    /***
     * Permite mostrar un mensaje de log dentro de la consola
     * Inhabilita el filtro, imprime y vuelve a habilitarlo
     * @param message Log a mostrar
     */
    private void log(String message)
    {
        if(message.equals(""))
        {
            System.out.print(prompt);
        }
        else 
        {
            System.out.print(message+"\n\n"+prompt);
        }
    }
    
    /***
     * Cambia el prompt tanto en el filtro como en la consola
     * @param message nuevo prompt
     */
    private void changeCommandPrompt(String message)
    {
        prompt = message + "> ";
    }
    
    private Double getNumericValue(String value)
    {
        return Double.parseDouble(value);
    }
    
    /***
     * Comprueba si un valor es una valor numerico
     * @param value string a comprobar
     * @return si es numerico
     */
    private boolean isNumeric(String value)
    {
        try 
        {                        
            Double val  = Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) 
        {
            return false;
        }
    }
    
    /***
     * Verifica que el argumento es o una variable o un valor
     * mumerico
     * @param arg argumento
     * @return si es valido o no
     */
    private boolean isValidArgument(String arg)
    {
        return getValue(arg) != null;
    }
    
    /***
     * Retorna el valor de un argumento, sea numerico o variable
     * @param identifier identificador
     * @return valor de dicho identificador, null si no es valido
     */
    private Double getValue(String identifier)
    {
        identifier = identifier.trim();
        if(vars.containsKey(identifier))
        {
            return vars.get(identifier);
        }
        else if(isNumeric(identifier))
        {
            return getNumericValue(identifier);
        }
        return null;
    }
    
    /***
     * Loggea con "seguro", es decir, si no es necesario logear no lo hará
     * @param message log
     * @param check se logeará o no
     */
    private void logCheck(String message, boolean check)
    {
        if(check)
        {
            log(message);
        }
    }
    
    /***
     * Calcula el factorial de forma recursiva
     * @param n numero a calcular factorial
     * @return factorial
     */
    private int factorial(int n)
    {
        return n==0 ? 1 : n * factorial(n-1);
    }
}
