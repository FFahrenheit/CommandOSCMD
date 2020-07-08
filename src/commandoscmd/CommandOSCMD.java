/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commandoscmd;

import java.util.Scanner;

/**
 *
 * @author ivan_
 */
public class CommandOSCMD {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Scanner input = new Scanner(System.in);
        ConsoleController console = new ConsoleController();
        while(true)
        {
            String command = input.nextLine();
            console.handleCall(command);
        }
    } 
}
