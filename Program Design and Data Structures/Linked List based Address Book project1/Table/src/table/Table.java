/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;


import java.util.Scanner;


/**
 * The class Table is an abstract data type that is used to implement a simple address book(in the form of a linked list)
 * and the operations that can be performed on it.
 * @author Akshaya Damodaran - G01129364
 */
public class Table {

    /**
     * Prints the options available to the user for using the Address Book.
     * @return 
     */
    private static String PrintOptions() {
        
        System.out.println("Add a contact (a)");
        System.out.println("Send a text message (m)");
        System.out.println("Look up a contact (l)");
        System.out.println("Update address (u)");
        System.out.println("Delete a contact (dc)");
        System.out.println("Delete a text message (dm)");
        System.out.println("Display all contacts (ac)");
        System.out.println("Display all messages to a contact (am)");
        System.out.println("Quit (q)");
        System.out.print("--> ");
        
        Scanner in = new Scanner(System.in);
        String userChoice = in.nextLine();
        
        return userChoice;
    }
      
    private int N;
    private Node head;
    private Node mark;
  
    
    public Table(){
    
    head = null;
    mark = null;
    
    N = 0;
     
}
    
    /**
     * Inserts a new entry to the table. 
     * If an entry already exists with the given key value, no insertion is made but false is returned.
     * @param key
     * @param value
     * @return true for successful insert, false otherwise.
     */
    public boolean insert(String key, String value){
        if(key==null || value==null){
            System.out.println("Either name or address is null");
            return false;
        }
        
        Node.MyDynamicArray messageArray = new Node.MyDynamicArray();
        if (!isEmpty()) {
            
            Node temp = head;
            while(temp.key != key && temp.next != null){
            temp = temp.next;
            }
            if(temp.key.equals(key)){
                System.out.println("Name already exists.");
                return false;
            }
            else{
                temp.next = new Node(key, value, messageArray, null);
                
            }
        }
        else{
            Node newNode = new Node(key, value, messageArray, null);
            head = newNode;
        }
        N++;
        return true;
    }
    
    /**
     *Looks up the entry with the given key and returns the associated value. If no entry is found null is returned.
     * @param key
     * @return value associated with the key. If key not found, null is returned.
     */
    public String lookUp(String key){
        
        if(isEmpty()){
            System.out.println("Addressbook empty. Cannot LookUp");
            return null;
        }
        
        Node temp = head;
        while(!temp.key.equals(key) && temp.next != null){
        temp = temp.next;
        }
           
        mark = temp;
        if(mark.key.equals(key))
            return this.valueAtMark();

        //if(temp.key.equals(key)){
            //return temp.value;
        //}
            
        return null;
    }
    
    /**
     * Deletes the entry with the given key. 
     * @param key
     * @return true for successful deletion, otherwise false.
     */
    public boolean deleteContact(String key){
        
        if(isEmpty()){
            System.out.println("AdressBook empty. Cannot delete");
            return false;
        }
        else{
            if(head.key.equals(key)){
                head = head.next;
                N--;
                return true;
            }
            
            Node prev = null;
            Node curr = head;
            while(curr != null && !curr.key.equals(key)){
                prev = curr;
                curr = curr.next;
            }
            if(curr==null){
                System.out.println("Person not found. Cannot delete");
                return false;
            }
            else{
                prev.next = curr.next;
                N--;
                return true;
            }
        }
    }
    
    /**
     * Replaces the old value associated with with the given key with the newValue string. 
     * @param key
     * @param newValue
     * @return true for successful update, otherwise false(in case of list being empty)
     */
    public boolean update(String key, String newValue){
        
        Node curr = head;
        while(curr != null && !curr.key.equals(key))
            curr = curr.next;
        if(curr==null)
            return false;
        
        else{
            curr.value = newValue;
            return true;
        }
        
    }
    
    /**
     * Sets the mark to the first item in the table. 
     * @return Returns false if the table is empty. Otherwise returns true.
     */
    public boolean markToStart(){
        if(isEmpty())
            return false;
        mark = head;
        return true;
    }
    
    /**
     * Moves the mark to the next item in the table. 
     * @return If there is no next item (e.g. at the end of the list) returns false. Otherwise returns true.
     */
    public boolean advanceMark(){
        if(!isEmpty() && mark.next != null){
            mark = mark.next;
            return true;
        }
        if(isEmpty())
            System.out.println("Addressbook Empty. Mark can't be advanced.");
        else if(mark.next==null)
            System.out.println("Mark pointing to last node.");
        return false;
      
    }
    
    /**
     * 
     * @return Returns the key stored in the item at the current mark. If list is empty, returns null.
     */
    public String keyAtMark(){
        if(!isEmpty())
            return mark.key;
        if(isEmpty())
            System.out.println("Addressbook Empty. Key at mark can't be obtained.");
        return null;
    }
    
    /**
     * 
     * @return Returns the value stored in the item at the current mark. If list is empty, returns null.
     */
    public String valueAtMark(){
        if(!isEmpty())
            return mark.value;
        if(isEmpty())
            System.out.println("Addressbook Empty. Value at mark can't be obtained.");
        return null;
    }
    
    /**
     * Displays Name/Address for each table entry. 
     * @return Returns total entry count. 
     */
    public int displayAll(){
        if(isEmpty())
            return 0;
        else{
            Node temp = head;
            while(temp != null){
                System.out.println("Name: "+temp.key);
                System.out.println("Address: "+temp.value);
                temp = temp.next;
            }
            return N;
        }
        }
    
    /**
     * Looks up the node corresponding to the key and passes the message to be added to the addMessage function of MyDynamicArray class.
     * @param key
     * @param msg
     * @return true for successful message addition, otherwise false.
     */
    public boolean SendMessage(String key, String msg){
        
        if(isEmpty()){
            System.out.println("Address book empty. No contact to send message.");
            return false;
        }
        Node curr = head;
        while(curr != null && !curr.key.equals(key))
            curr = curr.next;
        if(curr==null)
            return false;
        else{
            curr.messages.addMessage(msg);
            return true;
        }
        
    }
    
    /**
     * Looks up  the node corresponding to the given key and 
     * passes the index of the message to be deleted to the deleteMessage function of MyDynamicArray class for deletion of the message.
     * @param key
     * @param messageIndex
     * @exception NullPointerException will be caught if index passed is larger/smaller than (but within the capacity of the array) the indices of 
     *            the last/first messages respectively in the array.
     *            ArrayIndexOutOfBoundsException will be caught if the passed index is beyond the capacity of the array.
     */
    public void deleteMessage(String key, int messageIndex){
        
        if(isEmpty()){
            System.out.println("Address book empty. No messages to detlete.");
            return;
        }
        else{
            
            try{
                Node curr = head;
                while(curr != null && !curr.key.equals(key))
                curr = curr.next;
                if(curr==null)
                    return;
                else{
                     curr.messages.deleteMessage(messageIndex);
                }
            }
            catch(NullPointerException e){
                System.out.println("Invalid Message Index.");
            }
            catch(ArrayIndexOutOfBoundsException e){
                System.out.println("ArrayIndexOutOfBoundsException caught.");
            }
            return;
            
        }
        
    }
           
    /**
     * Displays all text messages sent to a contact. 
     * @param key
     * @return Returns total message count.
     */
    public int displayMessages(String key){
        if(isEmpty()){
            System.out.println("Address book empty. No messages.");
            return 0;
        }
        Node curr = head;
        while(curr != null && !curr.key.equals(key))
            curr = curr.next;
        if(curr==null)
            return 0;
        else{
              return curr.messages.displayMessages();
             
        }
        
        
    }
    
    /**
     * Checks of linked list is empty. 
     * @return returns true if empty, otherwise false.
     */
    public boolean isEmpty(){
        return N==0;
    }

    

    
    /**
     * The Node class corresponds to a contact in the Address Book. 
     * The members of the Node class are - 
     * key : referring to the name of the person
     * value : to hold the address of the person
     * messages : is a Generic Dynamic Array to hold the messages sent to the person.
     * next : is a pointer to the next Node in the Linked List. 
     */
    private static class Node {
        
    private String key;
    private String value;
    private MyDynamicArray messages;    
    private Node next;    

    public Node(String key, String value, MyDynamicArray messageArray, Node next) {
        this.key = key;    
        this.value = value; 
        this.messages = messageArray;
        this.next = next;
        
        
            
    } 
    
    /**
     * This class represents a generic collection of objects(messages to a contact in this case) in an array whose capacity can grow and shrink as needed.  
     * The default capacity of this array is 2. Once the array is full and a new message is to be added, the capacity is doubled.
     * If the size falls to 1/3 the capacity upon deletion of a message, the capacity is reduced to half its previous capacity.
     * @param <T> 
     */
    private static class MyDynamicArray<T> {

    /**
     * @param args the command line arguments
     */
  private T[] messages;
  private int positionPointer=0;
  public int arraySize;
  private static final int DEFAULT_ARRAY_SIZE=2; 
  
  private MyDynamicArray()
    {
        this(DEFAULT_ARRAY_SIZE);
    }
  private MyDynamicArray(int arraySize)
    {
        this.arraySize=arraySize;
        messages=(T[])new Object[arraySize];
    	
    }
  
  /**
   * Adds a message into the dynamic array associate with the entry node. 
   * @param message 
   */
  private void addMessage(T message)
  {
        adjustSize();
        messages[positionPointer]=message;
        positionPointer++;
    
  }
  
  /**
   * Deletes the message at the provided messageIndex.
   * @param messageIndex 
   * @throws throws NullPointerException for invalid messageIndex that is within the capacity of the dynamic array.
   *         throws ArrayIndexOutOfBoundsException for invalid messageIndex that is beyond the capacity of the dynamic array.
   */
  private void deleteMessage(int messageIndex)
  {
      T delMessage = null;
      try{
          delMessage = messages[messageIndex-1];
          if(messageIndex==positionPointer){
              messages[positionPointer-1]=null;
              positionPointer--;
              adjustSize();
              System.out.print("Message "+delMessage.toString()+" deleted.");
           
              return;
          }
          if(!(delMessage.equals(null))){
            for(int mIndex=messageIndex-1; mIndex<positionPointer-1; mIndex++){
            messages[mIndex] = messages[mIndex+1];
            }
            messages[positionPointer-1]=null;
            positionPointer--;
            adjustSize();
            System.out.print("Message "+delMessage.toString()+" deleted.");

            return;
          }
          
      }
      catch(ArrayIndexOutOfBoundsException e){
          throw e;
      }
      catch (NullPointerException e){
          throw e;
      }
  }
  
  /**
   * Displays all messages in the dynamic array.
   * @return 
   */
  private int displayMessages()
  {
      for(int m=0; m<positionPointer; m++)
          System.out.println(m+1 + " "+messages[m].toString());
      return positionPointer;
  }
  
  /**
   * Calls increaseSize() or decreaseSize() methods according to the value of the positionPointer variable
   * which gives the index of last message in the array.
   */
  private void adjustSize()
    {
        if(positionPointer==arraySize)
        {
            increaseSize();
        }
        else if(positionPointer<(arraySize/3))
        {
            decreaseSize();
        }
    }
  
  /**
   * Creates a new generic array of capacity 2 times that of the previous array and 
   * copies the contents(messages) in the old array to the new array.
   */
  private void increaseSize()
    {
        T[] tempArray=(T[]) new Object[2*arraySize];
        for(int i=0;i<positionPointer;i++)
        {
            tempArray[i]=messages[i];
        }
        messages=tempArray;
        System.out.println("**Doubling message array size");
        arraySize=2*arraySize;
    }

   /**
   * Creates a new generic array of capacity 1/2 that of the previous array and 
   * copies the contents(messages) in the old array to the new array.
   */
  private void decreaseSize()
    {
        T[] tempArray=(T[]) new Object[(arraySize/2)];
        for(int i=0;i<positionPointer;i++)
        {
            tempArray[i]=messages[i];
        }
        messages=tempArray;
        System.out.println("**Shrinking message array size to half it's prev. size");
        arraySize=arraySize/2;
    }
    }
}
        


    
    
    
    

    /**
     * Prints the userOptions until the user wants to Quit(i.e user presses 'q') and calls the appropriate function corresponding to the option chosen b the user.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String userChoice = new String();
        //String[] arr = new String[]{"a","m","l","u","dc","dm","ac","am","q"};
        while(!(userChoice.equals("a")||userChoice.equals("m")||userChoice.equals("l")||userChoice.equals("u")||userChoice.equals("dc")||userChoice.equals("dm")||userChoice.equals("ac")||userChoice.equals("am")||userChoice.equals("q"))){
            userChoice = PrintOptions();
        }
        Table table = new Table();
        Scanner in = new Scanner(System.in);
        String lookUpResult = new String();
        int messageNo;
        int numOfMessages = 0;
        
        while(!(userChoice.equals("q")))
        {
           String inKey = new String();
            switch(userChoice){
               case "a": System.out.print("Name: ");
                         inKey = in.nextLine();
                         System.out.print("Address: ");
                         String inVal = in.nextLine();
                         boolean insertResult = table.insert(inKey, inVal);
                         if(insertResult)
                             System.out.println("Entry successfully inserted");
                         break;
               
               
               case "m": System.out.print("Name: ");
                         inKey = in.nextLine(); 
                         lookUpResult = table.lookUp(inKey);
                         if(lookUpResult == null)
                             System.out.println(inKey+" is not in the book. Can't send message.");
                         else{
                             System.out.print("Message: ");
                             String msg = in.nextLine();
                             boolean sendMsgRes = table.SendMessage(inKey, msg);
                             if(sendMsgRes)
                                 System.out.println("Message sent.");
                             
                         }
                         break;
               
               
               case "l": System.out.print("Name: ");
                         inKey = in.nextLine(); 
                         String address = table.lookUp(inKey);
                         if(address != null)
                             System.out.println("Address is "+address);
                         else
                             System.out.println("Person not found in AddressBook");
                         break;
                         
               
               case "u": System.out.print("Name: ");
                         inKey = in.nextLine(); 
                         lookUpResult = table.lookUp(inKey);
                         if(lookUpResult == null)
                             System.out.println(inKey+" is not in the book.");
                         else{
                             //System.out.print("Name: ");
                             String oldAddress = table.lookUp(inKey);
                             System.out.println("Old address is "+oldAddress);
                             System.out.print("New Address: ");
                             inVal = in.nextLine();
                             boolean updateResult = table.update(inKey, inVal);
                             if(updateResult)
                                 System.out.println("Contact address updated.");
                         }
                          break;
               
               
               case "dc": System.out.print("Name: ");
                          inKey = in.nextLine(); 
                          boolean deleteContactRes = table.deleteContact(inKey);
                          if(deleteContactRes)
                              System.out.println("Contact deleted.");
                          break;
               
               
               case "dm": System.out.print("Name: ");
                          inKey = in.nextLine();
                          System.out.println("Choose message to delete: ");
                          numOfMessages = table.displayMessages(inKey);
                          if(numOfMessages>0)
                              System.out.println("Total number of messages = "+numOfMessages);
                          else{
                              System.out.println("No messages sent to this contact. Nothing to delete.");
                              break;
                          }
                          System.out.print("Message No. ");
                          messageNo = Integer.parseInt(in.nextLine());
                          table.deleteMessage(inKey, messageNo);
                          break;
               
               
               case "ac": int allContacts = table.displayAll();
                          if(allContacts==0)
                              System.out.println("Addressbook is empty.");
                          break;
               
               
               case "am": System.out.print("Name: ");
                          inKey = in.nextLine(); 
                          lookUpResult = table.lookUp(inKey);
                          if(lookUpResult == null)
                             System.out.println(inKey+" is not in the book.");
                          else{
                          numOfMessages = table.displayMessages(inKey);
                          if(numOfMessages>0)
                              System.out.println("Total number of messages = "+numOfMessages);
                          else
                              System.out.println("No messages sent to this contact.");
                          }
                          break;
               
               
               default:   break;
               
                         
                         
           }
            System.out.println();
            System.out.println();
            userChoice = PrintOptions();
        }
    }
}



    
    
    

