/**
 *
 * @author Akshaya Damodaran G01129364
 */
package expressionevaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author aksha
 */
public class ExpressionEvaluator {

    public SymbolTable symTab;

    /**
     * The constructor of ExpressionEvaluator initializes a SymbolTable object.
     */
    public ExpressionEvaluator() {
        symTab = new SymbolTable();
    }

    /**
     * Performs the specified arithmetic operator on operands a and b and
     * returns the result.
     *
     * @param a
     * @param b
     * @param operator
     * @return
     */
    public int performOperation(int a, int b, String operator) {

        int res = 0;
        switch (operator) {

            case ("+"):
                res = a + b;
                break;
            case ("-"):
                res = a - b;
                break;
            case ("*"):
                res = a * b;
                break;
            case ("/"):
                res = a / b;
                break;
            case ("^"):
                res = (int) Math.pow(a, b);
                break;
            default:
                System.out.println("Invalid operator.");
                break;

        }

        return res;

    }

    /**
     * Passes the variable and its value to put method of SymbolTable class for
     * entry to the Symbol Table.
     *
     * @param var
     * @param val
     */
    public void createPair(String var, int val) {
        symTab.put(var, val);
    }

    /**
     * Gets the assigned value for the variable(key) from get method of
     * SymboTable class.
     *
     * @param key
     * @return Returns the value if key was assigned, else returns null.
     */
    public Integer getValue(String key) {

        Object val;
        val = symTab.get(key);
        if (val != null) {
            return (int) val;
        } else {
            return null;
        }

    }

    /**
     * Clears the Symbol Table by calling clear method of SymbolTable class.
     */
    public void clearTable(boolean invalidFormat, boolean hasSymbolTableEntries) {

        if (!invalidFormat && hasSymbolTableEntries) {
            symTab.printEntries();
        } else {
            symTab.clear();
        }

    }

    private static class ProgramStack<T> {

        private LinkListStack<T> linkedListStack = new LinkListStack<>();

        public void push(T data) {
            linkedListStack.addToEnd(data);
        }

        public T pop() {
            return linkedListStack.removeFromEnd();
        }

        public T peek() {
            return (T) linkedListStack.top;
        }

        public void clear() {
            linkedListStack = new LinkListStack<>();
        }

    }

    private static class inputQueue<T> {

        private LinkListQueue<T> linkedListQ = new LinkListQueue<>();

        private void enQueue(T data) {
            linkedListQ.addToEnd(data);
        }

        private T deQueue() {
            return linkedListQ.removeFirst();
        }

        private boolean isEmpty() {
            return linkedListQ.isEmpty();
        }

    }

    private static class Node<T> {

        private T data;
        private Node<T> next;

        private Node(T data) {
            this.data = data;
        }

    }

    private static class LinkListStack<T> {

        private Node<T> top;

        private LinkListStack() {
            this.top = null;
        }

        private void addToEnd(T data) {
            Node<T> newTop = new Node<>(data);
            newTop.next = top;
            top = newTop;
        }

        private T removeFromEnd() {
            Node<T> oldTop = top;
            top = top.next;
            return oldTop.data;
        }

        private boolean isEmpty() {
            return top == null;
        }

    }

    private static class LinkListQueue<T> {

        private Node<T> front, rear;

        private LinkListQueue() {
            this.front = this.rear = null;
        }

        private void addToEnd(T item) {
            Node newNode = new Node(item);

            // If queue is empty, then new node is front and rear both  
            if (this.rear == null) {
                this.front = this.rear = newNode;
            } else {
                // Add the new node at the end of queue and change rear  
                this.rear.next = newNode;
                this.rear = newNode;
            }
        }

        private T removeFirst() {
            // If queue is empty, return NULL.  
            if (this.front == null) {
                System.out.println("The Queue is empty");
                return null;
            }

            // Store previous front and move front one node ahead  
            Node<T> temp = this.front;
            this.front = this.front.next;

            // If front becomes NULL, then change rear also as NULL  
            if (this.front == null) {
                this.rear = null;
            }
            return temp.data;

        }

        private boolean isEmpty() {
            return front == null;
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ExpressionEvaluator expEv = new ExpressionEvaluator();
        ProgramStack myStack = new ProgramStack();
        inputQueue inputQ = new inputQueue();
        boolean invalidFormat;
        boolean hasSymbolTableEntries;
        String finalRes;
        String inputExp;

        try {
            File file = new File(args[0]);
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                inputQ.enQueue(line);
            }

            while (!(inputQ.isEmpty())) {
                invalidFormat = false;
                hasSymbolTableEntries = false;
                inputExp = (inputQ.deQueue()).toString();
                System.out.println("Input Expression: " + inputExp);
                String[] splitString = inputExp.split("\\s+");
                for (String element : splitString) {
                    if (element.matches("-?\\d+(\\.\\d+)?")) {
                        myStack.push(Integer.parseInt(element));
                    } else if (element.equals("+") || element.equals("-") || element.equals("*") || element.equals("/") || element.equals("^")) {

                        Integer b;
                        Integer a;
                        //pop two operands and peroform the operation
                        try {
                            String pop1 = myStack.pop().toString();

                            if (pop1.matches("-?\\d+(\\.\\d+)?")) {
                                b = (int) Integer.parseInt(pop1);
                            } else {
                                b = expEv.getValue(pop1);
                                if (b == null) {
                                    System.out.println("INVALID format");
                                    invalidFormat = true;
                                    continue;
                                }

                            }

                            String pop2 = myStack.pop().toString();
                            if (pop2.matches("-?\\d+(\\.\\d+)?")) {
                                a = (int) Integer.parseInt(pop2);
                            } else {
                                a = expEv.getValue(pop2);
                                if (a == null) {
                                    System.out.println("INVALID format \n");
                                    invalidFormat = true;
                                    continue;
                                }

                            }
                            int res = expEv.performOperation(a, b, element);
                            myStack.push(res);
                        } catch (NullPointerException nE) {
                            System.out.println("INVALID format \n");
                            invalidFormat = true;

                        }
                    } //pop two elemets from stack and perform the specified assignment
                    else if (element.equals("=") || element.equals("+=") || element.equals("-=") || element.equals("*=") || element.equals("/=")) {

                        String pop1 = myStack.pop().toString();
                        if (!(pop1.matches("-?\\d+(\\.\\d+)?"))) {
                            System.out.println("INVALID format \n");
                            invalidFormat = true;
                            continue;
                        }
                        int val = Integer.valueOf(pop1);

                        String pop2 = myStack.pop().toString();
                        if (!(pop2.matches("-?\\d+(\\.\\d+)?"))) {

                            Integer a = expEv.getValue(pop2);
                            if (a == null && element.equals("=")) {
                                expEv.createPair(pop2, val);
                                continue;
                            }
                            a = (int) a;
                            int ans = 0;
                            switch (element) {
                                case "+=":
                                    ans = expEv.performOperation(a, val, "+");
                                    break;
                                case "-=":
                                    ans = expEv.performOperation(a, val, "-");
                                    break;
                                case "*=":
                                    ans = expEv.performOperation(a, val, "*");
                                    break;
                                case "/=":
                                    ans = expEv.performOperation(a, val, "/");
                                    break;
                                default:
                                    break;

                            }
                            expEv.createPair(pop2, ans);
                        }

                    } // if a variable is encountered, push to stack
                    else {
                        myStack.push(element);
                        hasSymbolTableEntries = true;
                    }

                }
                try {
                    finalRes = myStack.pop().toString();
                } catch (NullPointerException nE) {
                    System.out.println("INVALID format \n");
                    invalidFormat = true;
                    expEv.clearTable(invalidFormat, hasSymbolTableEntries);
                    continue;
                }
                if (!(finalRes.matches("-?\\d+(\\.\\d+)?"))) {
                    finalRes = expEv.getValue(finalRes).toString();
                    if (finalRes == null) {
                        System.out.println("INVALID format \n");
                        invalidFormat = true;
                        expEv.clearTable(invalidFormat, hasSymbolTableEntries);
                        myStack.clear();
                        continue;
                    }
                }
                if (!invalidFormat) {
                    System.out.println("Value: " + finalRes);
                    if (hasSymbolTableEntries) {
                        System.out.print("Symbol Table Entries: ");
                    }

                }
                expEv.clearTable(invalidFormat, hasSymbolTableEntries);
                myStack.clear();
            }
            System.out.println("\n");
            System.out.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException nE) {
            nE.printStackTrace();
        }

    }
}
