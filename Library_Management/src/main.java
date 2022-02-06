import java.sql.*;
import java.util.*;
import java.util.Scanner;

import com.mysql.cj.exceptions.RSAException;
import com.mysql.cj.x.protobuf.MysqlxCrud.Insert;

public class main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println("Welcome !!!");

    try {

      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
      final String DB_URL = "jdbc:mysql://localhost:3306/MANAGE";

      final String USER = "root";
      final String PASS = "Abhinandan@13";

      Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

      Scanner sc = new Scanner(System.in);
      int option, choice;
      String k1, k2, k3, roll;

      while (true) {
    	
    	System.out.println("");
        System.out.println("Select any number to proceed further");
        System.out.println("Enter 1 to login as Admin");
        System.out.println("Enter 2 to login as Student");
        System.out.println("Enter 3 to exit");

        option = sc.nextInt();

        if (option > 3 || option < 1) {
          System.out.println("Invalid Choice");
          continue;
        }

        if (option == 3) {
          System.out.println("Exits...");
          System.exit(0);
        }

        if (option == 1) {
          
          System.out.println("");
          System.out.println("Enter a number to proceed further");
          System.out.println("Enter 1 to Add, Edit or Update details a book");
          System.out.println("Enter 2 to Delete a book");
          System.out.println("Enter 3 to Issue a book");
          System.out.println("Enter 4 to Return a book");
          System.out.println("Enter 5 to View books of a student");
          System.out.println("Enter 6 to View books issued to a student");
          System.out.println("Enter 7 to Return to main menu");

          choice = sc.nextInt();
          if (choice < 1 || choice > 7) {
            System.out.println("Invalid Choice");
            continue;
          }
          if (choice == 7) {
            continue;
          }

          if (choice == 1) {
        	  
            System.out.println("Enter ID");
            k1 = sc.next();
            
            System.out.println("Enter Book Name");
            k2 = sc.next();
            
            System.out.println("Enter Author");
            k3 = sc.next();

            String query = "insert into book values(?,?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, k1);
            st.setString(2, k2);
            st.setString(3, k3);
            st.setInt(4, 0);
            st.setString(5, "NULL");

            int a = st.executeUpdate();

            System.out.println("Details have been updated sucessfully");
            continue;

          }

          if (choice == 2) {
            System.out.println("Enter ID");
            k1 = sc.next();

            String query = "delete from book where id=?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, k1);

            st.execute();

            System.out.println("Book has been deleted sucessfully");
            continue;
          }

          if (choice == 3) {
            System.out.println("Enter ID");
            k1 = sc.next();
            
            System.out.println("Enter Roll Number");
            roll = sc.next();

            String query = "select issue from book where id=?;";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, k1);

            ResultSet r = st.executeQuery();

            if (!r.next()) {
              System.out.println("Book isn't there in library");
              System.out.println("Wrong details!!!");
              continue;
            }

            int count = r.getInt(1);

            if (count == 0) {

              String query1 = "select count(*) from record where id=? and issue=1;";
              PreparedStatement st1 = conn.prepareStatement(query1);
              st1.setString(1, k1);
              //st1.setInt(2, 1);

              r = st.executeQuery();
              r.next();

              count = r.getInt(1);

              if (count <= 3) {

                query1 = "select count(*) from record where id=? and rollno=? and issue=1;";
                st1 = conn.prepareStatement(query1);
                st1.setString(1, k1);
                st1.setString(2, roll);

                r = st.executeQuery();

                if (r.next()) {
                  count = r.getInt(1);
                } else {
                  count = 0;
                }

                if (count > 0) {
                  System.out.println("Book is already issued to the student with roll number" + roll);
                  continue;
                }

                String name, date;

                System.out.println("Enter name of student");
                name = sc.next();

                System.out.println("Enter the due date for book in format YYYY-MM-DD");
                date = sc.next();

                query1 = "insert into record(id,rollno,studentname,issue,lastdate) values(?,?,?,1,?)";
                st1 = conn.prepareStatement(query1);

                st1.setString(1, k1);
                st1.setString(2, roll);
                st1.setString(3, name);
                st1.setString(4, date);

                st1.execute();
                System.out.println("Book issued!!!");
                
                query1 = "update book set issue=1 where id=?;";
                st1 = conn.prepareStatement(query1);
                st1.setString(1, k1);
                st1.execute();

              } else {
                System.out.println("Student has already issued " + count + "books, so can't issue more");
              }

            } else {
              System.out.println("Book is already issued");
              continue;
            }

          }

          if (choice == 4) {
            System.out.println("Enter ID");
            k1 = sc.next();
            System.out.println("Enter Roll Number");
            roll = sc.next();

            String query = "select count(*) from record where id=? and rollno=? and issue=1;";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, k1);
            st.setString(2, roll);

            ResultSet r = st.executeQuery();

            if (!r.next()) {
              System.out.println("Wrong details!!!");
              continue;
            }

            int k = r.getInt(1);

            if (k == 0) {
              System.out.println("The entered book isn't issued to this student");
              continue;
            }

            System.out.println("Enter today's date");
            String dateString = sc.next();

            query = "select lastdate from record where id=? and rollno=?";
            st = conn.prepareStatement(query);
            st.setString(1, k1);
            st.setString(2, roll);

            r = st.executeQuery();

            if (!r.next()) {
              System.out.println("Wrong details!!!");
              continue;
            }

            String due = r.getString(1);
            if (due.compareTo(dateString) >= 0) {
              System.out.println("Book returned !!!");
            } else {
              System.out.println("You are late and have to pay a fine");
            }

            query = "update record set issue=0 where id=? and rollno=?";
            st = conn.prepareStatement(query);
            st.setString(1, k1);
            st.setString(2, roll);

            r = st.executeQuery();

            if (!r.next()) {
              System.out.println("Wrong details!!!");
              continue;
            }

            query = "update book set issue=0 where id=?";
            st = conn.prepareStatement(query);
            st.setString(1, k1);
            st.execute();
          }

          if (choice == 5) {
            System.out.println("Enter Roll Number");
            roll = sc.next();

            String query = "select idnumber,id,rollno,studentname,issue,lastdate from record where rollno=? and issue=1;";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, roll);
            ResultSet r = st.executeQuery();

            while (r.next()) {
            	
              int idnum = r.getInt("idnumber");
              String bid = r.getString("id");
              roll = r.getString("rollno");
              String name = r.getString("studentname");
              String date = r.getString("lastdate");
              Integer p = r.getInt("issue");
              
              System.out.println("Transaction number " + idnum);
              System.out.println("Book id " + bid);
              System.out.println("Roll Number " + roll);
              System.out.println("Student name " + name);
              System.out.println("Due date " + date);
              System.out.println("Issue status " + p);
              System.out.println("");
            }

          }

          if (choice == 6) {
            System.out.println("Enter Roll Number");
            roll = sc.next();

            String query = "select idnumber,id,rollno,studentname,issue,lastdate from record where rollno=?";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, roll);
            ResultSet r = st.executeQuery();

            while (r.next()) {
            	
              int idnum = r.getInt("idnumber");
              String bid = r.getString("id");
              roll = r.getString("rollno");
              String name = r.getString("studentname");
              String date = r.getString("lastdate");
              Integer p = r.getInt("issue");
              
              System.out.println("Transaction number " + idnum);
              System.out.println("Book id " + bid);
              System.out.println("Roll Number " + roll);
              System.out.println("Student name " + name);
              System.out.println("Due date " + date);
              System.out.println("Issue status " + p);
              System.out.println("");
            }
          }

        }

        if (option == 2) {
        
          System.out.println("");
          System.out.println("Enter a number to proceed further");
          System.out.println("Enter 1 to Search a book");
          System.out.println("Enter 2 to View current status");
          System.out.println("Enter 3 to View history of books issued");
          System.out.println("Enter 4 to Return to main menu");

          choice = sc.nextInt();
          if (choice < 1 || choice > 4) {
            System.out.println("Invalid Choice");
            continue;
          }
          if (choice == 4) {
            continue;
          }

          if (choice == 1) {
            System.out.println("Enter ID");
            k1 = sc.next();

            String query = "select id,bookname,author,issue from book where id=?";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, k1);
            ResultSet r = st.executeQuery();

            if (!r.next()) {
              System.out.println("Book isn't there in library");
              System.out.println("Wrong details!!!");
              continue;
            }

            String bid = r.getString("id");
            System.out.println("Book id " + bid);
            String k = r.getString("bookname");
            System.out.println("Bookname " + k);
            String name = r.getString("author");
            System.out.println("Author name " + name);
            Integer p = r.getInt("issue");
            System.out.println("Issue status " + p);
            System.out.println("");

          }

          if (choice == 2) {
            System.out.println("Enter Roll Number");
            roll = sc.next();

            String query = "select idnumber,id,rollno,studentname,issue,lastdate from record where rollno=? and issue=1";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, roll);
            ResultSet r = st.executeQuery();

            while (r.next()) {
            	
              int idnum = r.getInt("idnumber");
              String bid = r.getString("id");
              roll = r.getString("rollno");
              String name = r.getString("studentname");
              String date = r.getString("lastdate");
              Integer p = r.getInt("issue");
              
              System.out.println("Transaction number " + idnum);
              System.out.println("Book id " + bid);
              System.out.println("Roll Number " + roll);
              System.out.println("Student name " + name);
              System.out.println("Due date " + date);
              System.out.println("");

            }

          }

          if (choice == 3) {
            System.out.println("Enter Roll Number");
            roll = sc.next();

            String query = "select idnumber,id,rollno,studentname,issue,lastdate from record where rollno=?";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, roll);
            ResultSet r = st.executeQuery();

            while (r.next()) {
            	
              int idnum = r.getInt("idnumber");
              String bid = r.getString("id");
              roll = r.getString("rollno");
              String name = r.getString("studentname");
              String date = r.getString("lastdate");
              Integer p = r.getInt("issue");
              
              System.out.println("Transaction number " + idnum);
              System.out.println("Book id " + bid);
              System.out.println("Roll Number " + roll);
              System.out.println("Student name " + name);
              System.out.println("Due date " + date);
              System.out.println("Issue status " + p);
              System.out.println("");

              if (p == 0) {
                System.out.println("Returned");
              } else {
                System.out.println("Issued at present");
              }

            }
          }

        }

      }

    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      System.out.println("Connection Lost!!");

    }

  }

}