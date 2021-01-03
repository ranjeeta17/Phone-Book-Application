/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DELL
 */

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class Node3 {
    String Name ,Phone,Email;
    Node3 left ,right,next;
    int bf = 0;

    public Node3(String Name ,String Phone,String Email) {
        this.Name = Name;
        this.Email = Email;
        this.Phone = Phone;
        left = right = next=null;
    }
}

class PhoneBook1 {
        Node3 root;
        String path;
        private boolean doneReading = false;
        final ArrayList<Node3> sortedList = new ArrayList<Node3>();
        final ArrayList<Node3> contactList = new ArrayList<>();
        final ArrayList<Node3> results = new ArrayList<>();
        java.util.Queue<Node3> recents = new java.util.LinkedList<Node3>();

        public PhoneBook1(String path) { this.path = path; }
        // update the list whenever the "Sort by Name" button is pressed
        // (it is possible that nodes could have been added or removed)
        public void updateSortedList() {
            sortedList.clear();
            fillSortedList(root);
        }

        private void fillSortedList(final Node3 root) {
            if(root == null) return;
            fillSortedList(root.left);
            sortedList.add(root);
            fillSortedList(root.right);
        }

        public void updateContactsList() {
            contactList.clear();
            fillContactList();
        }

        
        // Filling contacts as they are appearing in the tree "level wise"
        private void fillContactList() {
            final java.util.Queue<Node3> q = new java.util.LinkedList<Node3>();
            q.offer(root);
            Node3 curr;

            while(!q.isEmpty()) {
                curr = q.poll();
                if(curr != null) {
                    contactList.add(curr);
                    q.offer(curr.left);
                    q.offer(curr.right);
                }
            }
        }

        public void readRecents() {
            try {
                File file = new File("NewFile.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String Name, Phone, Email ;
                if(file.exists()) {
                     while((Name=br.readLine())!=null&&(Phone=br.readLine())!=null
                       && (Email=br.readLine())!=null){
                          recents.offer(new Node3(Name, Phone, Email));
                    }
                }
                br.close();
            } catch(Exception e) { System.out.println(e); }
        }

        public void showRecents() {
            new RecentRecordShow(recents).setVisible(true);
        }

        public void newFileForAccess(Node3 node) {
            try {
                FileWriter obj = new FileWriter("NewFile.txt", true);
                obj.write(node.Name + "\n");
                obj.write(node.Phone + "\n");
                obj.write(node.Email + "\n");
                obj.flush();
            } catch(Exception e) { System.out.println(e); }
        }

        public void addRecent(String name ,String phone ,String email) {
            Node3 newNode = new Node3(name, phone, email);
            if(recents.size() >= 4) {
                try {
                    recents.poll();
                    recents.offer(newNode);
                    Node3 current;
                    FileWriter fw = new FileWriter("NewFile.txt", false);
                    fw.write("");
                    fw.close();
                    while(recents.size() > 0) {
                        current = recents.poll();
                        newFileForAccess(current);
                    }
                    readRecents();
                } catch(Exception e) {}
            } else {
                recents.offer(newNode);
                newFileForAccess(newNode);
            }
        }

        public void searchInitials(String name) {
            searchInitials(root, name, name.length());
        }

        public void searchInitials(Node3 node, String name, int n) {
            if(node == null) return;
            if(node.Name.length() >= n && 
                node.Name.substring(0, n).equals(name))
                results.add(node);
            searchInitials(node.left, name, n);
            searchInitials(node.right, name, n);
        }

        public boolean search(String name, String phone, String email) {
            return search(root, name, phone, email);
        }

        private boolean search(Node3 root, String name, String phone, String email) {
            if(root == null) return false;
            int compare = name.compareTo(root.Name);
            if(compare == 0)
                return true;
            else if(compare < 0)
                return search(root.left, name, phone, email);
            else if(compare > 0)
                return search(root.right, name, phone, email);
            return false;
        }

        public int height(Node3 n){
            if(n==null) return -1;
            return Math.max(height(n.left), height(n.right))+1;
        }
        
        public void updatebf(Node3 n) {
            if(n == null) return;
            n.bf = (height(n.right) - height(n.left));
        }
    
        public Node3 leftRightRotation(Node3 node) {
            node.left = leftRotation(node.left);
            return rightRotation(node);
        }
    
        public Node3 rightLeftRotation(Node3 node) {
            node.right = rightRotation(node.right);
            return leftRotation(node);
        }
    
        public Node3 rightRotation(Node3 n) {
            Node3 x = n.left;
            Node3 y = x.right;
            
            x.right = n;
            n.left =y;
            
            updatebf(x);
            updatebf(n);
            return x;
        }
    
        
        public Node3 leftRotation(Node3 n){
            Node3 x = n.right;
            Node3 y = x.left;
            
            x.left =n;
            n.right = y;
            updatebf(x);
            updatebf(n);
            return  x;
        }
    
        public Node3 balance(Node3 node) {
            if(node.bf == -2) {
                if(node.left.bf <= 0)
                    return rightRotation(node);
                else
                    return leftRightRotation(node);
            } else if(node.bf == 2) {
                if(node.right.bf >= 0)
                    return leftRotation(node);
                else
                    return rightLeftRotation(node);
            }
            return node;
        }
        
    
            public void insert(String name, String phone, String email) {
                root = insert(root, name, phone, email);
            }
    
            private Node3 insert(Node3 node, String name, String phone, String email) {
                if(node == null) {
                    Node3 newest = new Node3(name, phone, email);
                    if(doneReading) {
                        FileInsert(newest);
                        JOptionPane.showMessageDialog(null, "Contact added!");
                    }
                    return newest;
                }
                int compare = name.compareTo(node.Name);
                if(compare < 0) 
                    node.left = insert(node.left, name, phone, email);
                else if(compare > 0)
                    node.right = insert(node.right, name, phone, email);
                updatebf(node);
                return balance(node);
            }
      
         public  Node3 delete(Node3 root , String name ){
        if(root == null ) return null;
        int compare = name.compareTo(root.Name);
        if(compare<0) root.left = delete(root.left , name);
        else if(compare>0) root.right = delete(root.right , name);
        else{
            if(root.left == null || root.right==null){
                Node3 temp = null;
                temp = root.left == null?root.right:root.left;
                
                if(temp == null) return null;
                else return temp;
            }
            else{
                Node3 succeesor = getSuccessor(root);
                root.Name = succeesor.Name;
                root.right = delete(root.right , root.Name);
            }
        
            //JOptionPane.showMessageDialog(null, "Deletion SuccessFull!");
        
        }
        return root;
    }
    
    public Node3  getSuccessor(Node3 root){
        
        Node3 temp = root.right;
        while(temp.left!=null){
            temp= temp.left;
        }
        return temp;
    
    
    }

    public void removeRecord(String fileName , String removeTerm){
        Scanner x;
        File oldfile = new File(path);
        File newfile = new File(path + "1.txt");
        
        String name ="", phone = "",email="";
        try{
            FileWriter fw = new FileWriter(newfile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            x = new Scanner(new File(fileName));
            x.useDelimiter("[\n]");
            while(x.hasNext()){
                name = x.nextLine();
                phone = x.nextLine();
                email = x.nextLine();
                if(!name.equals(removeTerm)){
                    pw.println(name+"\n"+phone+"\n"+email);
                }
                
            }
            x.close();
            pw.flush();
            pw.close();
            oldfile.delete();
            File updateFile = new File(fileName);
            newfile.renameTo(updateFile);
        
        
        }
        catch(Exception e){
             System.out.println(e);
        }
    }
        public void FileInsert(Node3 node) {
            try {
                FileWriter obj = new FileWriter(path, true);
                obj.write(node.Name + "\n");
                obj.write(node.Phone + "\n");
                obj.write(node.Email + "\n");
                obj.flush();
            } catch(Exception e) { System.out.println(e); }
        }
        

        public void readContacts(File f,PhoneBook1 phBook) {
            if(!f.exists()) {
                doneReading = true;
                return;
            }
            try {
                 BufferedReader br = new BufferedReader(new FileReader(f));
                 String Name,Phone,Email ;
                 while((Name=br.readLine())!=null&&(Phone=br.readLine())!=null
                        && (Email=br.readLine())!=null)
                    phBook.insert(Name,Phone,Email);
                br.close();
            } catch(Exception e) { System.out.println(e); }
            doneReading = true;
        }
}

class TableExample1 extends JFrame {
    //JFrame f;
    JTable jt;
    TableExample1(ArrayList<Node3> list ) {
    //f=new JFrame(); ??
    String column[]={"NAME","Phone","Email"};
    String row[][]= new String[list.size()][3];
    int i=0;
    for(Node3 list1 : list){
            row[i][0]=list1.Name;
            row[i][1]=list1.Phone;
            row[i][2]=list1.Email;
            i++;
    }
    jt=new JTable(row, column);
   // jt.setBounds(30,40,200,300);
    JScrollPane sp=new JScrollPane(jt);
    add(sp);
    setSize(800, 600);
    setVisible(true);
    //addWindowListener(new TableExample1.MyWindow1());
    setLocationRelativeTo(null); // shows the table window at the middle of the screen
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
}
   
}
// Class for displaying contacts in a table.
class DisplayTable extends JFrame {
    public DisplayTable(final ArrayList<Node3> contactList) {
        final String[] headings = {"NAME", "PHONE NO.", "EMAIL"};
        final String[][] rows = new String[contactList.size()][3];

        int i = 0;
        for(final Node3 contact : contactList) {
                rows[i][0] = contact.Name;
                rows[i][1] = contact.Phone;
                rows[i++][2] = contact.Email;
        }

        add(new JScrollPane(new JTable(rows, headings)));
        setSize(800, 600);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}

class SearchInitialTable extends JFrame {
    public SearchInitialTable(final ArrayList<Node3> results) {
        final String[] headings = {"NAME", "PHONE NO.", "EMAIL"};
        final String[][] rows = new String[results.size()][3];

        int i = 0;
        for(final Node3 result : results) {
                rows[i][0] = result.Name;
                rows[i][1] = result.Phone;
                rows[i++][2] = result.Email;
        }

        add(new JScrollPane(new JTable(rows, headings)));
        setSize(800, 600);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}

class RecentRecordShow extends JFrame {
    public RecentRecordShow(java.util.Queue<Node3> q) {
        final String[] headings = {"NAME", "PHONE NO.", "EMAIL"};
        final String[][] rows = new String[q.size()][3];
        int i = 0;
        try {
            File file = new File("NewFile.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String Name, Phone, Email ;
            if(file.exists()) {
                 while((Name=br.readLine())!=null&&(Phone=br.readLine())!=null
                   && (Email=br.readLine())!=null){
                      rows[i][0] = Name;
                      rows[i][1] = Phone;
                      rows[i++][2] = Email;
                      
                }
            }
            br.close();
        } catch(Exception e) { System.out.println(e); }
    
        add(new JScrollPane(new JTable(rows, headings)));
        setSize(800, 600);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}

class PhoneBookFrame extends JFrame {
    JButton BInsert =  new JButton("Insert");
    JButton BDelete =  new JButton("Delete");
    JButton BSearch  = new JButton("Search"); // name this button "Search by Info"
    JButton BSort =  new JButton("Sort By Name");
    JButton BDisplay =  new JButton("Display"); // name this button "Display Contacts"
    JButton BSInitials =  new JButton("Search by Initials");
    JButton BExit  = new JButton("Exit");
    JButton BAccess  = new JButton("Recently Accessed");
    JTextField TName ,TEmail,TPhone;
    JLabel LName,LEmail,LPhone,Ltitle;
    String name , phone ,email;
    PhoneBookFrame current = this;

    String mainPath = "C:\\Users\\Prem Sagar\\Documents\\JavaApplication14\\Info.txt";
    private PhoneBook1 phBook = new PhoneBook1(mainPath); // keep this as an instance

    public PhoneBookFrame(){
        JButton[] buttonArray = {BInsert, BDelete, BSearch, BSort, BDisplay, BExit, BAccess, BSInitials};
        setLayout(null);

        for(final JButton b : buttonArray) {
            b.addActionListener(new ListenToButton());
            b.setFont(new Font("Aerial", Font.BOLD, 15));
            add(b);
        }

        LName = new JLabel("NAME:");
        LPhone = new JLabel("Phone No. :");
        LEmail = new JLabel("Email:");
        Ltitle = new JLabel("PHONEBOOK MANAGMENT SYSTEM");
        TName = new JTextField(20);
        TPhone = new JTextField(20);
        TEmail = new JTextField(40);
        Ltitle.setBounds(380,40,620,90);
        LName.setBounds(180, 175, 70, 70);
        LPhone.setBounds(180, 280, 90, 70);
        LEmail.setBounds(180, 380, 70, 70);
        
        BInsert.setBounds(300, 520, 100, 50);
        BSearch.setBounds(450, 520, 100, 50);
        BDelete.setBounds(600, 520, 100, 50);
        BSort.setBounds(750, 520, 150, 50);
        BDisplay.setBounds(950, 520, 100, 50);
        BExit.setBounds(1110, 520, 100, 50);
        BAccess.setBounds(80, 520, 180, 50);
        BSInitials.setBounds(80, 580, 180, 50);

        TName.setBounds(300, 200, 250, 30);
        TPhone.setBounds(300, 300, 250, 30);
        TEmail.setBounds(300, 400, 250, 30);
        Ltitle.setFont(new Font("Aerial", Font.BOLD, 30));
        TName.setFont(new Font("Aerial", Font.PLAIN, 19));
        TPhone.setFont(new Font("Aerial", Font.PLAIN, 19));
        TEmail.setFont(new Font("Aerial", Font.PLAIN, 19));
        add(Ltitle);
        add(TName);
        add(TPhone);
        add(TEmail);
        add(LName);
        add(LPhone);
        add(LEmail);

        setSize(1280, 700);
        setVisible(true);
        setLocationRelativeTo(null);
        addWindowListener(new MyWindow());
    }

    private  class MyWindow extends WindowAdapter {
        public void windowOpened(WindowEvent evt) {
            try {
                phBook.readContacts(new File(mainPath), phBook);
                JOptionPane.showMessageDialog(null, "Previous contacts added succesfully");
                phBook.readRecents();
            }
            catch(Exception e) { System.out.println(e); }
        }
        public void windowClosing(WindowEvent we) { System.exit(0); }
    }

        private class ListenToButton implements ActionListener {
            public void actionPerformed(ActionEvent ae) {
                if(ae.getSource() ==  BInsert) {
                    try {
                        name = TName.getText();
                        phone = TPhone.getText();
                        email = TEmail.getText();
                        checkValidity(name, phone, email);
                        phBook.insert(name ,phone,email);
                    } catch(Exception e) {
                              JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    }

                }
                else if(ae.getSource()==BSearch) {
                    try {
                        name = TName.getText();
                        phone = TPhone.getText();
                        email = TEmail.getText();
                        checkValidity(name, phone, email);
                        
                        if(phBook.search(TName.getText(), TPhone.getText(), TEmail.getText())) {
                            JOptionPane.showMessageDialog(null, "Contact found");
                            phBook.addRecent(name,phone,email);
                        }
                        else 
                            JOptionPane.showMessageDialog(null, "Can not find the specified contact");     
                    } catch(Exception e) {
                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    }            
                } 
                else if(ae.getSource()==BSort) {
                    phBook.updateSortedList();
                    new TableExample1(phBook.sortedList);
                } 
                else if(ae.getSource() == BDisplay) {
                    phBook.updateContactsList();
                    new DisplayTable(phBook.contactList).setVisible(true);
                }
                else if(ae.getSource() == BExit) {
                      current.setVisible(false);
                }
                else if(ae.getSource() ==  BDelete) {
                    try {
                        name = TName.getText();
                        phone = TPhone.getText();
                        email = TEmail.getText();
                        checkValidity(name, phone, email);
                        
                        phBook.delete(phBook.root,name);
                        phBook.removeRecord(mainPath,name);
                        
                    } catch(Exception e) {
                              JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    }

                }
                
                else if(ae.getSource() == BAccess){
                    phBook.showRecents();
                } else if(ae.getSource() == BSInitials) {
                    name = TName.getText();
                    if(name.isEmpty()) return;
                    phBook.results.clear();
                    phBook.searchInitials(name);
                    if(phBook.results.size() == 0)
                        JOptionPane.showMessageDialog(null, "No contacts starting with " 
                        + name + " were found");
                    else {
                        SearchInitialTable table = new SearchInitialTable(phBook.results);
                        table.setVisible(true);
                    }
                }
            }
   }

   public void checkValidity(String name, String phone, String email) {
       // allow only capital first letter of the name. for sorting to properly work.
       if(name.length()==0) throw new RuntimeException("Name Field is Empty");	
       if(name.charAt(0) < 65 || name.charAt(0) > 90)
       throw new RuntimeException("First letter of the name must be capital.");
   for(int i=0 ; i<name.length() ; i++) {
       if((name.charAt(i)<'a' || name.charAt(i)>'z') && (name.charAt(i)<'A' || name.charAt(i)>'Z')){
           throw new RuntimeException("Name is not correct\nplease try again");
       }
   }

   if(phone.length()==0) throw new RuntimeException("Phone Field is Empty");
   if(phone.length()==11){
       for(int i=0 ; i<phone.length() ; i++){
           if(phone.charAt(i)<'0' || phone.charAt(i)>'9'){
               throw new RuntimeException("phone is not correct \nplease try again");
           }
       }

   }
   else throw new RuntimeException("phone size must be equal to 11\nplease try again");
   if(email.length()==0) throw new RuntimeException("Email Field is Empty");	
   for(int i=0 ;i<email.length() ; i++){
       if(email.charAt(i)=='@'){
           String mail = email.substring(i,email.length());
           if(mail.equals("@gmail.com")){
               break;
           }
           else throw new RuntimeException("Email is not correct----gmail is allowed only");
       }

   }
   }
}

public class FinalProject{
        public static void main(String arg[]) {
            new PhoneBookFrame().setVisible(true);
        }
}




 

