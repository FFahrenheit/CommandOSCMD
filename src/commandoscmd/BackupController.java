/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commandoscmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase encargada de hacer backup del sistema operativo
 * @author Johann
 */
public class BackupController 
{
    public boolean backupExists(String filename)
    {
        return new File(filename+".txt").exists();
    }
    
    public boolean setBackup(String filename, ConsoleController so)
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename+".txt"));
            so.setPrompt(reader.readLine());
            so.setTime(Long.parseLong(reader.readLine()));
            String variables = reader.readLine();
            if( variables == null|| variables.equals("") )
            {
                so.setVars(new Hashtable<>());  
            }
            else
            {
                String[] vars = variables.split("&");
                Hashtable<String, Double> values = new Hashtable<>();
                for (String var : vars) 
                {
                    String[] parts = var.split(" ");
                    values.put(parts[0], Double.parseDouble(parts[1]));
                }
                so.setVars(values); 
            }
            return true;
        } catch (IOException | NumberFormatException| java.lang.NullPointerException ex) {
            return false;
        }        
    }
    
    public boolean createBackup(String filename, ConsoleController so)
    {
        FileWriter fw = null;
        BufferedWriter writer = null;
        try 
        {
            File file = new File(filename+".txt");
            fw = new FileWriter(file,false);
            writer = new BufferedWriter(fw);
            writer.write(so.getPrompt());
            writer.newLine();
            writer.write(""+so.getTime());
            writer.newLine();
            String vars = "";
            Set<String> keys = so.getVars().keySet();
            for(String key : keys)
            {
                vars += key + " " + so.getVars().get(key);
                vars += "&";
            }
            if(vars.length()>1)
            {
                vars = vars.substring(0,vars.length()-1);
            }
            writer.write(vars);
            writer.close();
            return true;
        } 
        catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
            return false;
        } 
    }
}
