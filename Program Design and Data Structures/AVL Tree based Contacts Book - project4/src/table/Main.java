package table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aksha
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Table table = new Table();
        String userChoice = new String();
        String lookUpResult;
        boolean insertRes;
        Scanner in = new Scanner(System.in);

        try {
            File file = new File(args[0]);
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            br = new BufferedReader(new FileReader(file));
            String nameLine, addressLine;

            //Reads from input file and builds a BST structure of the names and addresses.
            while ((nameLine = br.readLine()) != null && (addressLine = br.readLine()) != null) {
                insertRes = table.insert(nameLine, addressLine);

            }

            try {
                //Keeps printing the menu for the user if he enters an invalid choice number.
                while (!(userChoice.equals("1") || userChoice.equals("2") || userChoice.equals("3") || userChoice.equals("4") || userChoice.equals("5") || userChoice.equals("6"))) {
                    userChoice = PrintOptions();
                }
                //Performs the task (add a contact, lookUp, Update, Delete, Display, Save and Exit) requested by the user.
                while (!(userChoice.equals("6"))) {
                    String inKey = new String();
                    switch (userChoice) {
                        case "1":
                            System.out.print("Name: ");
                            inKey = in.nextLine();
                            System.out.print("Address: ");
                            String inVal = in.nextLine();
                            boolean insertResult = table.insert(inKey, inVal);
                            if (insertResult) {
                                System.out.println("Entry successfully inserted");
                            } else {
                                System.out.println(inKey + " already present.");
                            }
                            break;

                        case "2":
                            System.out.print("Name: ");
                            inKey = in.nextLine();
                            String address = table.lookUp(inKey);
                            if (address != null) {
                                System.out.println("Address is " + address);
                            } else {
                                System.out.println("Person not found in AddressBook");
                            }
                            break;

                        case "3":
                            System.out.print("Name: ");
                            inKey = in.nextLine();
                            lookUpResult = table.lookUp(inKey);
                            if (lookUpResult == null) {
                                System.out.println(inKey + " is not in the book.");
                            } else {

                                String oldAddress = table.lookUp(inKey);
                                System.out.println("Old address is " + oldAddress);
                                System.out.print("New Address: ");
                                inVal = in.nextLine();
                                boolean updateResult = table.update(inKey, inVal);
                                if (updateResult) {
                                    System.out.println("Contact address updated.");
                                }
                            }
                            break;

                        case "4":
                            System.out.print("Name: ");
                            inKey = in.nextLine();
                            boolean deleteContactRes = table.deleteContact(inKey);
                            if (deleteContactRes) {
                                System.out.println(inKey + " deleted.");
                            }
                            break;

                        case "5":
                            int allContacts = table.displayAll();
                            if (allContacts == 0) {
                                System.out.println("Addressbook is empty.");
                            } else {
                                System.out.println("Tree size = " + allContacts);
                                System.out.print("Number of contacts in addressbook = " + allContacts);
                            }
                            break;

                        default:
                            break;

                    }
                    System.out.println();
                    System.out.println();
                    userChoice = PrintOptions();
                }
                //Call to the save() method to save the table entries in a file in the same working directory.
                table.save();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException nE) {
            nE.printStackTrace();
        }

    }

    private static String PrintOptions() {

        System.out.println("1. Add a contact");

        System.out.println("2. Look up a contact");
        System.out.println("3. Update address");
        System.out.println("4. Delete a contact");

        System.out.println("5. Display all contacts");

        System.out.println("6. Save and exit");
        System.out.println();
        System.out.print("ENTER CHOICE No. : ");

        Scanner in = new Scanner(System.in);
        String userChoice = in.nextLine();

        return userChoice;
    }

}
