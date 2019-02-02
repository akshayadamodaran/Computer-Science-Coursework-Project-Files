package table;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author aksha
 */
public class Table {

    private Node root;

    /* Constructor */
    public Table() {
        root = null;
    }

    /**
     * The Node has two String fields (for the name and address) along with an
     * int height field and an int size field.
     */
    private class Node {

        private Node left, right;
        private String name, address;
        private int height, size;

        /* Constructor */
        public Node() {
            left = null;
            right = null;
            name = null;
            address = null;
            height = 0;
            size = 0;
        }

        /* Constructor */
        public Node(String n, String ad) {
            left = null;
            right = null;
            name = n;
            address = ad;
        }

        /* Function to set left node */
        private void setLeftNode(Node n) {
            left = n;
        }

        /* Function to set right node */
        private void setRightNode(Node n) {
            right = n;
        }

        /* Function to get left node */
        private Node getLeftNode() {
            return left;
        }

        /* Function to get right node */
        private Node getRightNode() {
            return right;
        }
    }

    /**
     * Inserts a new entry to the table. If an entry already exists with the
     * given key value it makes no insertion but returns false.
     *
     * @param key
     * @param value
     * @return Returns true on successful insertion. If an entry already exists
     * with the given key value it makes no insertion but returns false.
     */
    public boolean insert(String key, String value) {
        String lookUpRes = this.lookUp(key);
        if (lookUpRes == null) {
            root = insertRecursive(root, key, value);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Looks up the entry with the given key.
     *
     * @param key
     * @return Returns the value associated with the key. If no entry is found
     * null is returned.
     */
    public String lookUp(String key) {
        return search(root, key);

    }

    /**
     * Deletes the entry with the given key.
     *
     * @param key
     * @return Returns true on successful deletion. If no entry is found returns
     * false.
     */
    public boolean deleteContact(String key) {
        if (isEmpty()) {
            System.out.println("Address Book Empty");
            return false;
        } else if (lookUp(key) == null) {
            System.out.println(key + " is not present in Address Book");
            return false;
        } else {
            root = delete(root, key);
            return true;
        }
    }

    /**
     * Replaces the old value associated with with the given key with the
     * newValue string.
     *
     * @param key
     * @param newValue
     * @return Returns true on successful updation. Else returns false.
     */
    public boolean update(String key, String newValue) {
        Node curr = root;
        boolean updated = false;
        while ((curr != null) && newValue.compareTo("") != 0) {
            String rKey = curr.name;
            if (key.compareToIgnoreCase(rKey) < 0) {
                curr = curr.getLeftNode();
            } else if (key.compareToIgnoreCase(rKey) > 0) {
                curr = curr.getRightNode();
            } else {
                curr.address = newValue;
                updated = true;
                break;
            }
        }
        return updated;

    }

    /**
     * Displays Name/Address for each table entry, the list of entries is sorted
     * by the keys. This method also additionally displays the height of each
     * entry node the address book tree structure.
     *
     * @return Returns total entry count (size at root).
     */
    public int displayAll() {
        inOrder(root);
        return countNodes(root);
    }

    /**
     * Reads the name of a text output file, and writes a list of the table
     * entries to an the output file using PreOrder traversal.
     */
    public void save() {
        try {
            System.out.print("Enter the file name: ");
            Scanner filenameIn = new Scanner(System.in);
            String outFile = filenameIn.nextLine();
            PrintWriter writer = new PrintWriter(outFile, "UTF-8");
            preorder(root, writer);
            writer.close();
            System.out.println("Table entries saved to " + outFile);
        } catch (IOException ioExcp) {
            ioExcp.printStackTrace();
        }
    }

    /* Function to insert data recursively */
    private Node insertRecursive(Node current, String key, String value) {
        if (current == null) {
            current = new Node(key, value);
        } else if (key.compareToIgnoreCase(current.name) < 0) {
            current.left = insertRecursive(current.left, key, value);

        } else if (key.compareToIgnoreCase(current.name) > 0) {
            current.right = insertRecursive(current.right, key, value);

        } else
           ;  // Duplicate; do nothing
        current = rebalance(current, key);
        current.height = getHeight(current);
        current.size = countNodes(current);
        return current;
    }

    /**
     * Rebalance the tree after insertion
     */
    private Node rebalance(Node current, String key) {

        int balanceValue = getBalanceValue(current);

        if (balanceValue < -1) {
            if (key.compareToIgnoreCase(current.getRightNode().name) > 0) {
                current = rotateLeft(current);   // Right heavy situation - left rotation
            } else {
                current = rotateRightThenLeft(current); // Right - Left
            }
        }

        if (balanceValue > 1) {
            if (key.compareToIgnoreCase(current.getLeftNode().name) < 0) {
                current = rotateRight(current); // Left heavy situation - Right rotation
            } else {
                current = rotateLeftThenRight(current); //Left - Right
            }
        }
        return current;
    }

    /* Rotate binary tree node with right child */
    private Node rotateRight(Node k2) {
        Node k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = max(getHeight(k2.left), getHeight(k2.right)) + 1;
        k1.height = max(getHeight(k1.left), k2.height) + 1;
        return k1;
    }

    /* Rotate binary tree node with left child */
    private Node rotateLeft(Node k1) {
        Node k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = max(getHeight(k1.left), getHeight(k1.right)) + 1;
        k2.height = max(getHeight(k2.right), k1.height) + 1;
        return k2;
    }

    /**
     * Double rotate binary tree node: first right and then left
     */
    private Node rotateRightThenLeft(Node k3) {
        k3.right = rotateRight(k3.right);
        return rotateLeft(k3);
    }

    /**
     * Double rotate binary tree node: first left and then right
     */
    private Node rotateLeftThenRight(Node k1) {
        k1.left = rotateLeft(k1.left);
        return rotateRight(k1);
    }

    /**
     * Get the maximum node is a particular sub-tree
     *
     * @param currentNode the root node of the sub-tree we will be examining
     *
     */
    private Node getMaxNode(Node currentNode) {
        while (currentNode.getRightNode() != null) {
            currentNode = currentNode.getRightNode();
        }
        return currentNode;
    }

    /**
     * Recursive implementation of removing data from a tree Three cases: 1. No
     * child nodes. 2. Single child. 3. Two children.
     *
     * @return the node that is to be removed. Return null if no data is
     * removed.
     *
     */
    private Node delete(Node currentNode, String key) {

        // Base case
        if (currentNode == null) {
            return null;
        }

        Node leftChild = currentNode.getLeftNode();
        Node rightChild = currentNode.getRightNode();
        String currentKey = currentNode.name;

        if (key.compareToIgnoreCase(currentKey) == 0) {
            if (leftChild == null && rightChild == null) {
                return null;
            } else if (leftChild == null) {
                currentNode = null;
                return rightChild;
            } else if (rightChild == null) {
                currentNode = null;
                return leftChild;
            } else {
                // Find the largest node on the left sub-tree
                Node largestInLeftSubtree = getMaxNode(leftChild);

                // Swap the root node with the largest in left sub-tree
                currentNode.name = largestInLeftSubtree.name;
                currentNode.address = largestInLeftSubtree.address;
                // Set left-child recursively. Remove the copy left of the largest left child
                currentNode.setLeftNode(delete(currentNode.getLeftNode(), largestInLeftSubtree.name));

            }
        } else if (key.compareToIgnoreCase(currentKey) < 0) {
            currentNode.setLeftNode(delete(leftChild, key));
        } else {
            currentNode.setRightNode(delete(rightChild, key));
        }

        // Update the height and size parameters
        setHeightOnDel(root);
        setSizeOnDel(root);

        // Check on every delete operation whether tree has become unbalanced
        return rebalance(currentNode);
    }

    /**
     * Check whether the tree is unbalanced after a delete operation
     *
     * @return Node The node that is deleted.
     *
     */
    private Node rebalance(Node currentNode) {

        int balanceValue = getBalanceValue(currentNode);

        // Left heavy situation. Can be left-left or left-right
        if (balanceValue > 1) {
            // Left-right rotation required. Left rotation on the right child of the root node.
            if (getBalanceValue(currentNode.getLeftNode()) < 0) {
                currentNode.setLeftNode(rotateLeft(currentNode.getLeftNode()));
            }

            return rotateRight(currentNode);
        }

        // Right heavy situation. Can be right-right or right-left
        if (balanceValue < -1) {
            // right - left situation. Left rotation on the right child of the root node.
            if (getBalanceValue(currentNode.getRightNode()) > 0) {
                currentNode.setRightNode(rotateRight(currentNode.getRightNode()));
            }
            // left rotation on the root node
            return rotateLeft(currentNode);
        }

        return currentNode;
    }

    /**
     * Get the balance factor of the current node.
     */
    private int getBalanceValue(Node currentNode) {
        if (currentNode == null) {
            return 0;
        }
        return getHeight(currentNode.getLeftNode()) - getHeight(currentNode.getRightNode());
    }

    private String search(Node curr, String key) {
        String address = null;
        while ((curr != null) && address == null) {
            String rKey = curr.name;
            if (key.compareToIgnoreCase(rKey) < 0) {
                curr = curr.getLeftNode();
            } else if (key.compareToIgnoreCase(rKey) > 0) {
                curr = curr.getRightNode();
            } else {
                address = curr.address;
                break;
            }
            address = search(curr, key);
        }
        return address;
    }

    /**
     * This method is used to set the heights of the nodes in the tree upon
     * deletion of a node.
     *
     * @param key
     * @return Returns the value associated with the key. If no entry is found
     * null is returned.
     */
    private void setHeightOnDel(Node r) {
        if (r != null) {
            setHeightOnDel(r.getLeftNode());
            r.height = getHeight(r);
            setHeightOnDel(r.getRightNode());
        }
    }

    /**
     * This method is used to set the size of the nodes in the tree upon
     * deletion of a node.
     *
     * @param key
     * @return Returns the value associated with the key. If no entry is found
     * null is returned.
     */
    private void setSizeOnDel(Node r) {
        if (r != null) {
            setSizeOnDel(r.getLeftNode());
            r.size = countNodes(r);
            setSizeOnDel(r.getRightNode());
        }
    }

    /* Function to check if tree is empty */
    private boolean isEmpty() {
        return root == null;
    }

    /**
     * Gets the height of the given node.
     *
     * @param aNode
     * @return Returns the height of the node.
     */
    private int getHeight(Node aNode) {
        if (aNode == null) {
            return -1;
        }

        int lefth = getHeight(aNode.left);
        int righth = getHeight(aNode.right);

        if (lefth > righth) {
            return lefth + 1;
        } else {
            return righth + 1;
        }
    }

    /* Function to max of left/right node */
    private int max(int lhs, int rhs) {
        return lhs > rhs ? lhs : rhs;
    }

    /**
     * Calculates the size for a given node.
     *
     * @param r
     * @return Returns the size of the node.
     */
    private int countNodes(Node r) {
        if (r == null) {
            return 0;
        } else {
            int l = 1;
            l += countNodes(r.getLeftNode());
            l += countNodes(r.getRightNode());
            return l;
        }
    }

    /**
     * Performs inorder traversal of the tree. Prints the name, address and
     * height of each node.
     *
     * @param r
     */
    private void inOrder(Node r) {
        if (r != null) {
            inOrder(r.getLeftNode());
            System.out.println(r.name);
            System.out.println(r.address);
            System.out.println("\t--- " + "Node Height = " + r.height);
            System.out.println("\t--- " + "Node's Balance Factor = " + getBalanceValue(r));
            System.out.println();
            inOrder(r.getRightNode());
        }
    }

    /**
     * Performs pre order traversal on the tree and writes the key value pairs
     * to a file.
     *
     * @param r
     * @param writer
     */
    private void preorder(Node r, PrintWriter writer) {

        if (r != null) {
            writer.println(r.name);
            writer.println(r.address);
            preorder(r.getLeftNode(), writer);
            preorder(r.getRightNode(), writer);
        }

    }

}
