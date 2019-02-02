/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author aksha
 */
public class Table {

    private Node root;

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

    /**
     * Checks if the tree is empty
     *
     * @return Returns true if tree is empty, else returns false.
     */
    private boolean isEmpty() {
        return root == null;
    }

    /**
     * Inserts a new entry to the table.
     *
     * @param current
     * @param key
     * @param value
     * @return Returns the node(root) with the new node inserted in the tree.
     */
    private Node insertRecursive(Node current, String key, String value) {
        if (current == null) {
            current = new Node(key, value);
        } else {
            if (key.compareToIgnoreCase(current.name) < 0) {
                current.left = insertRecursive(current.left, key, value);
            } else if (key.compareToIgnoreCase(current.name) > 0) {
                current.right = insertRecursive(current.right, key, value);
            }
        }
        current.height = getHeight(current);
        current.size = countNodes(current);
        return current;
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

    /**
     * Searches for the entry with the given key.
     *
     * @param curr
     * @param key
     * @return Returns the value associated with the key. If no entry is found
     * null is returned.
     */
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
     * Deletes the node with given key.
     *
     * @param r
     * @param k
     * @return Finally returns the root after the node with given key has been
     * deleted.
     */
    private Node delete(Node root, String k) {
        Node p, p2, n;
        if (root.name.compareToIgnoreCase(k) == 0) {
            Node lt, rt;
            lt = root.getLeftNode();
            rt = root.getRightNode();
            if (lt == null && rt == null) {
                return null;
            } else if (lt == null) {
                p = rt;
                return p;
            } else if (rt == null) {
                p = lt;
                return p;
            } else {
                p2 = rt;
                p = rt;
                while (p.getLeftNode() != null) {
                    p = p.getLeftNode();
                }
                p.setLeftNode(lt);
                return p2;
            }
        }
        if (k.compareToIgnoreCase(root.name) < 0) {
            n = delete(root.getLeftNode(), k);
            root.setLeftNode(n);
        } else {
            n = delete(root.getRightNode(), k);
            root.setRightNode(n);
        }
        setHeightOnDel(root);
        setSizeOnDel(root);
        return root;
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
            System.out.println();
            inOrder(r.getRightNode());
        }
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

}
