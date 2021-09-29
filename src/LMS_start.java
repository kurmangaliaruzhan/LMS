import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class LMS_start {
    static Scanner scnLine = new Scanner(System.in);
    static Scanner scnDigit = new Scanner(System.in);
    static int choose;


    public static void main(String[] args) {
        chooseAction(); //1 командаларды тандау үшін методты шакырдым
    }
    static void chooseAction(){//2 сандар бойынша шарттармен тексеру үшін құрылған метод
        System.out.println("LMS ке қош келдіңіз!");

        while (true) { //бұл команда бесконечна орындала береді себебі true
            System.out.println("1 - Add book");
            System.out.println("2 - Delete book");
            System.out.println("3 - Rename book");
            System.out.println("4 - Find book");
            System.out.println("5 - Book List");
            System.out.println("6 - Exit");
            System.out.println("Команданы таңдаңыз ");

            try { // тек кана санды енгізуге тексеру үшін
                choose = scnDigit.nextInt();
                System.out.println("Вы выбрали команду "+choose);
                if (choose == 1) { // әр санга байланысты цифрларды тандау шарттарын орындау үшін if косылды
                    addBook(); // кітапты қосу үшін метод шақырылды
                } else if (choose == 2) {
                    deleteBook(); //кітапты өшіру үшін метод шақырылды
                } else if (choose == 3) {
                    renameBook();
                } else if (choose == 4) {
                    findBook();
                } else if (choose == 5) {
                    listBook(); // списогын шығару үшін метод шақырылды
                } else if (choose == 6) {
                    System.out.println("Выход");
                    break;
                } else{
                    System.out.println("Выбрать между 1-6");
                } // if else бойынша тандау шартының соңгы бөлігі
            } catch (InputMismatchException e){// тек кана санды енгізуге тексеру үшін
                System.out.println("Тек санды енгізу керек");
                scnDigit.nextLine();
            }
        }// while бітті
    }

    public static Connection getConnection(){
        String user ="postgres";
        String password = "12344321";
        String url = "jdbc:postgresql://localhost:5432/lms";

        try{
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url,user, password);
            return conn;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    static void addBook(){ // кітапты косу үшін addBook методы құрылды

        System.out.println("Кітаптың атын енгізіңіз:");
        String bookName = scnLine.nextLine();
        System.out.println("Кітаптың авторын енгізіңіз:");
        String bookAuthor = scnLine.nextLine();
        System.out.println("Кітаптың бағасы:");
        int price = scnDigit.nextInt();

        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO booklist (namebook, nameauthor,price) Values (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, bookName);
            ps.setString(2, bookAuthor);
            ps.setInt(3, price);

            int rows = ps.executeUpdate();

            System.out.printf("%d кітап қосылды\n", rows);
            conn.close();
        } catch (Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex);
        }
    }
    static void renameBook(){
        while (true) {
            System.out.println("1 - Кітапты атауы бойынша өзгерту");
            System.out.println("2 - Кітапты авторы бойынша өзгерту");
            System.out.println("3 - Кітапты бағасы бойынша өзгерту");
            System.out.println("4 - Бастапқы бетке өту");
            try { // тек кана санды енгізуге тексеру үшін
                choose = scnDigit.nextInt();
                System.out.println("Вы выбрали команду " + choose);
                if (choose == 1) { // әр санга байланысты цифрларды тандау шарттарын орындау үшін if косылды
                    renameByName(); // кітапты қосу үшін метод шақырылды
                } else if (choose == 2) {
                    renameByAuthor();
                } else if (choose == 3) {
                    renameByPrice();
                } else if(choose == 4){
                    chooseAction();
                } else{
                    System.out.println("Выбрать между 1-4");
                } // if else бойынша тандау шартының соңгы бөлігі
            } catch (InputMismatchException e) {// тек кана санды енгізуге тексеру үшін
                System.out.println("Тек санды енгізу керек");
                scnDigit.nextLine();
            }
        } // while бітті
    }
    static void renameByName() {
        System.out.println("Атын өзгерту үшін ескі атауын енгіз");
        String oldName = scnLine.nextLine();
        Connection conn = getConnection();
        String sql = "select * from booklist where namebook like '%" + oldName + "%'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                count++;
            }
            if (count == 1) {
                System.out.println("Атын өзгерту үшін жаңа атауын енгіз");
                String newname = scnLine.nextLine();
                st.executeUpdate("update booklist set namebook = '"+newname+"' where namebook = '"+oldName+"'");
                conn.close();
                System.out.println("Кітап атауы бойынша өзгертілді");
                chooseAction();
            } else if (count >1){
                renameFromSublist(sql, "name");
            } else if (count ==0){
                System.out.println("Кітап жоқ");
            } else {
                System.out.println("что то");
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed...");
            System.out.println(e);
        }
    } //renameByName методы жабылды
    static void renameByAuthor(){
        System.out.println("Атын өзгерту үшін ескі авторын енгіз");
        String oldauthor = scnLine.nextLine();
        Connection conn = getConnection();
        String sql = "select * from booklist where nameauthor like '%" + oldauthor + "%'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                count++;
            }
            if (count == 1) {
                System.out.println("Атын өзгерту үшін жаңа авторын енгіз");
                String newname = scnLine.nextLine();
                st.executeUpdate("update booklist set nameauthor = '"+newname+"' where nameauthor = '"+oldauthor+"'");
                conn.close();
                System.out.println("Кітап авторы бойынша өзгертілді");
                chooseAction();
            } else if (count >1){
                renameFromSublist(sql, "author");
            } else if (count ==0){
                System.out.println("Кітап жоқ");
            } else {
                System.out.println("что то");
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed...");
            System.out.println(e);
        }
    }
    static void renameByPrice(){
        System.out.println("Атын өзгерту үшін ескі бағасын енгіз");
        int oldprice = scnDigit.nextInt();
        Connection conn = getConnection();
        String sql = "select * from booklist where price = " + oldprice;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                count++;
            }
            if (count == 1) {
                System.out.println("Өзгерту үшін жаңа бағасын енгіз");
                int newname = scnDigit.nextInt();
                st.executeUpdate("update booklist set price = '"+newname+"' where price = " + oldprice);
                conn.close();
                System.out.println("Кітап бағасы бойынша өзгертілді");
                chooseAction();
            } else if (count >1){
                renameFromSublist(sql, "price");
            } else if (count ==0){
                System.out.println("Кітап жоқ");
            } else {
                System.out.println("что то");
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed...");
            System.out.println(e);
        }
    }
    static void renameFromSublist(String rsql, String renameType){
        try {
            String updateSql;

            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(rsql);
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String author = rs.getString(3);
                int genre = rs.getInt(4);
                System.out.println(id + " " + name + " " + author + " " + genre);
            }

            System.out.println("Өзгерту үшін таңдаңыз");
            int id = scnDigit.nextInt();
            if(renameType.equalsIgnoreCase("name")){
                updateSql = "update booklist set namebook=?  where id=" + id;
            }else if(renameType.equalsIgnoreCase("author")){
                updateSql = "update booklist set nameauthor=?  where id=" + id;
            }else{
                updateSql = "update booklist set price=?  where id=" + id;
            }

            PreparedStatement ps = conn.prepareStatement(updateSql);

            if(renameType.equalsIgnoreCase("price")){
                System.out.println("Кітаптың жаңа бағасын енгізіңіз: ");
                int newPrice =scnDigit.nextInt();
                ps.setInt(1,newPrice);
            } else {
                System.out.println("Кітаптың жаңа атауын енгізіңіз: ");
                String param = scnLine.nextLine();
                ps.setString(1, param);
            }
//            System.out.println("Кітаптың жаңа авторын енгізіңіз: ");
//            String newAuthor = scnLine.nextLine();
//            System.out.println("Кітаптың жаңа бағасын енгізіңіз: ");
//            int newPrice = scnDigit.nextInt();
            System.out.println("Кітап атауы өзгертілді");
            ps.executeUpdate();
            conn.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void deleteBook() {
        while (true) {
            System.out.println("1 - Кітапты атауы бойынша өшіру");
            System.out.println("2 - Кітапты авторы бойынша өшіру");
            System.out.println("3 - Кітапты бағасы бойынша өшіру");
            System.out.println("4 - Бастапқы бетке өту");

            try
            { // тек кана санды енгізуге тексеру үшін
                choose = scnDigit.nextInt();
                System.out.println("Вы выбрали команду " + choose);
                if (choose == 1) { // әр санга байланысты цифрларды тандау шарттарын орындау үшін if косылды
                    deleteByName(); // кітапты қосу үшін метод шақырылды
                } else if (choose == 2) {
                    deleteByAuthor();
                } else if (choose == 3) {
                    deleteByPrice();
                } else if(choose == 4){
                    chooseAction();
                } else{
                    System.out.println("Выбрать между 1-4");
                } // if else бойынша тандау шартының соңгы бөлігі
            } catch (InputMismatchException e) {// тек кана санды енгізуге тексеру үшін
                System.out.println("Тек санды енгізу керек");
                scnDigit.nextLine();
            }
        } // while бітті
    }
    static void deleteByName(){
        Connection conn = getConnection();
        System.out.println("Өшіру үшін кітаптың атауын енгіз:");
        String name = scnLine.nextLine();
        String sql = "select * from booklist where namebook like '%" + name + "%'";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int count=0;
            while (rs.next()){
                count++;
            }
            if(count==1){
                st.executeUpdate("delete from booklist where namebook like '%" + name + "%'");
                System.out.println("Атауы бойынша кітап өшірілді");
                conn.close();
            } else if(count >1){
                deleteFromSublist(sql);
            } else if (count ==0){
                System.out.println("Кітап жоқ");
            } else {
                System.out.println("что то");
            }
        } catch (SQLException e){
            System.out.println("Connection failed...");
            System.out.println(e);
        }
    }
    static void deleteByAuthor(){
        Connection conn = getConnection();
        System.out.println("Өшіру үшін кітаптың авторын енгіз:");
        String author = scnLine.nextLine();
        String sql1 = "select * from booklist where nameauthor like '%" + author + "%'";

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql1);
            int count=0;
            while (rs.next()){
                count++;
            }
            if(count==1){
                st.executeUpdate("delete from booklist where nameauthor like '%" + author + "%'");
                System.out.println("Атауы бойынша кітап өшірілді");
                conn.close();
            } else if(count >1){
                deleteFromSublist(sql1);
            } else if (count ==0){
                System.out.println("Кітап жоқ");
            } else {
                System.out.println("что то");
            }
        } catch (SQLException e){
            System.out.println("Connection failed...");
            System.out.println(e);
        }
    }
    static void deleteByPrice(){
        Connection conn = getConnection();
        System.out.println("Өшіру үшін кітаптың бағасын енгіз:");
        int price = scnDigit.nextInt();
        String sql2 = "select * from booklist where price ="+price;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql2);
            int count=0;
            while (rs.next()){
                count++;
            }
            if(count==1){
                st.executeUpdate("delete from booklist where price =" + price);
                System.out.println("Атауы бойынша кітап өшірілді");
                conn.close();
            } else if(count >1){
                deleteFromSublist(sql2);
            } else if (count ==0){
                System.out.println("Кітап жоқ");
            } else {
                System.out.println("что то");
            }
        } catch (SQLException e){
            System.out.println("Connection failed...");
            System.out.println(e);
        }
    }
    static void deleteFromSublist(String sql){
        try{
            Connection conn1 = getConnection();
            Statement st1 = conn1.createStatement();
            ResultSet rs1 = st1.executeQuery(sql);
            while (rs1.next()){
                int id = rs1.getInt(1);
                String name1 = rs1.getString(2);
                String author1 = rs1.getString(3);
                int price1 = rs1.getInt(4);
                System.out.println(id + " "+name1+" "+author1+" "+price1);
            }
            System.out.println("Кітапты өшіру үшін таңдаңыз: ");
            int iddelete = scnDigit.nextInt();

            st1.executeUpdate("Delete from booklist where id = "+iddelete );
            System.out.println("Кітап өшірілді");
            conn1.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void listBook(){ // кітаптың списогын шығару үшін listBook методы құрылды
        try{
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            String sql ="select * from booklist";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()){
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String author = rs.getString(3);
                int price = rs.getInt(4);
                System.out.println(id + " "+name+" "+author+" "+price);
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    static void findBook() {
        System.out.println("1 - by name");
        System.out.println("2 - by author");
        System.out.println("3 - by price");
        int choose = scnDigit.nextInt();
        try {
            if (choose == 1) {
                findByName();
            } else if (choose == 2) {
                findByAuthor();
            } else if (choose == 3) {
                findByPrice();
            } else {
                System.out.println("Vibor mejdu 1 - 3 ");
                renameBook();
            }
        } catch (InputMismatchException e) {
            System.out.println("Only digits");
            scnDigit.nextLine();
        }
    }
    static void findByName() {
        System.out.println("Кітапты іздеу үшін атын енгіз: ");
        String findBook = scnLine.nextLine();
        Connection conn = getConnection();
        String sql = "select * from booklist where namebook like '%" + findBook + "%'";

        try {
            Statement st = conn.createStatement();
            ResultSet rs1 = st.executeQuery(sql);
            boolean ff = false;
            while (rs1.next()){
                int id = rs1.getInt(1);
                String name1 = rs1.getString(2);
                String author1 = rs1.getString(3);
                int price1 = rs1.getInt(4);
                System.out.println(id + " "+name1+" "+author1+" "+price1);
                ff=true;
            }
            if(ff==false){
                System.out.println("Ондай кітап жоқ");
                chooseAction();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    static void findByAuthor() {
        System.out.println("Кітапты іздеу үшін авторын енгіз: ");
        String findBook = scnLine.nextLine();
        Connection conn = getConnection();
        String sql = "select * from booklist where nameauthor like '%" + findBook + "%'";

        try {
            Statement st = conn.createStatement();
            ResultSet rs1 = st.executeQuery(sql);
            boolean ff2 = false;
            while (rs1.next()){
                int id = rs1.getInt(1);
                String name1 = rs1.getString(2);
                String author1 = rs1.getString(3);
                int price1 = rs1.getInt(4);
                System.out.println(id + " "+name1+" "+author1+" "+price1);
            }
            if(ff2==false){
                System.out.println("Ондай автор жоқ");
                chooseAction();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    static void findByPrice() {
        System.out.println("Кітапты іздеу үшін бағасын енгіз: ");
        int findBook = scnDigit.nextInt();
        Connection conn = getConnection();
        String sql = "select * from booklist where price ="+ findBook;

        try {
            Statement st = conn.createStatement();
            ResultSet rs1 = st.executeQuery(sql);
            boolean ff3 = false;
            while (rs1.next()){
                int id = rs1.getInt(1);
                String name1 = rs1.getString(2);
                String author1 = rs1.getString(3);
                int price1 = rs1.getInt(4);
                System.out.println(id + " "+name1+" "+author1+" "+price1);
            }
            if(ff3==false){
                System.out.println("Ондай баға жоқ");
                chooseAction();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}