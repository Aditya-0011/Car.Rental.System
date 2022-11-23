import java.util.*;
import org.sqlite.SQLiteException;
import java.sql.*;
import java.time.LocalDate;
class sqlite
{
    Connection connect()
    {
        Connection c = null;
        try
        {
            String url = "jdbc:sqlite:D:/Work/Java/Project/Database.sqlite";
            c = DriverManager.getConnection(url);
            //System.out.println("Connection to SQLite has been established.");
            c.setAutoCommit(false);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return c;
    }
}

class userExist extends sqlite 
{
    String name, car, date, sql;
    Long mobile, aadhar;
    Scanner sc = new Scanner(System.in);
    userExist()
    {
        name = "null";
        car = "No Car rented";
        date = "No due date";
        mobile = 0L;
        aadhar = 0L;
    }

    void display(Long a) throws SQLException
    {
        sql = "Select * From User Where Aadhar = ?";
        Connection conn = this.connect();
        PreparedStatement pstmt  = conn.prepareStatement(sql);
        pstmt.setLong(1, a);
        ResultSet r = pstmt.executeQuery();
        System.out.println("\nName: " + r.getString("Name") + "\nAadhar Number: " + r.getLong("Aadhar") + "\nMobile Number: " + r.getLong("Mobile") + "\nCar currently rented: " + r.getString("Car").substring(0, 1).toUpperCase() + r.getString("Car").substring(1) + "\nDue Date: " + r.getString("Date"));
        conn.close();
    }

    void registered()
    {
        try
        {
            System.out.println("\nEnter 12-digit Aadhar Number: ");
            aadhar = sc.nextLong();
            String str = Long.toString(aadhar);
            if(str.length() != 12)
            {
                System.out.println("\nAadhar Number must be of 12 digits");
                sc.nextLine();
                ask();
            }
            sql = "Select * From User Where Aadhar = ?";
            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setLong(1, aadhar);
            ResultSet r = pstmt.executeQuery();
            if(r.getLong("Aadhar") != 0)
            {
                
                conn.close();
                sc.nextLine();
                display(aadhar);
                new Menu().menu(aadhar);
            }
            else
            {
                System.out.println("\nCustomer not found. Want to register?(Y/N): ");
                char c = sc.next().toLowerCase().charAt(0);
                if(c == 'y')
                {
                    conn.close();
                    sc.nextLine();
                    register();
                }
                else if(c == 'n')
                {
                    sc.nextLine();
                    ask();
                }
                else
                {
                    System.out.println("\nIncorrrect Choice!");
                    conn.close();
                    sc.nextLine();
                    ask();
                }
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch(InputMismatchException e)
        {
            System.out.println("\nInvalid Input.");
            sc.nextLine();
            ask();
        }
    }

    void register() 
    {
        try
        {
            String str;
            System.out.println("\nEnter Name: ");
            name  = sc.nextLine();
            
            try
            {
                System.out.println("\nEnter 12-digit Aadhar Number:");
                aadhar = sc.nextLong();
                str = Long.toString(aadhar);
                if(str.length() != 12)
                {
                    System.out.println("\nAadhar Number must be of 12 digits");
                    sc.nextLine();
                    ask();
                }
                
                sql = "Select * From User Where Aadhar = ?";
                Connection conn = this.connect();
                PreparedStatement pstmt  = conn.prepareStatement(sql);
                pstmt.setLong(1, aadhar);
                ResultSet r = pstmt.executeQuery();
                if(r.getLong("Aadhar") != 0)
                {
                    System.out.println("\nUser Already Exist!");
                    conn.close();
                    sc.nextLine();
                    ask();
                }
                else
                {
                    conn.close();
                }
            }
            catch(InputMismatchException e)
            {
                System.out.println("\nInvalid Input!");
                sc.nextLine();
                ask();
            }

            try
            {
                System.out.println("\nEnter Mobile Number: ");
                mobile = sc.nextLong();
                str = Long.toString(mobile);
                if(str.length() != 10)
                {
                    System.out.println("\nMobile Number must be of 10 digits.");
                    sc.nextLine();
                    ask();
                }
                sql = "Select * From User Where Mobile = ?";
                Connection conn = this.connect();
                PreparedStatement pstmt  = conn.prepareStatement(sql);
                pstmt.setLong(1, mobile);
                ResultSet r = pstmt.executeQuery();
                if(r.getLong("Mobile") != 0)
                {
                    System.out.println("\nUser Already Exist!");
                    conn.close();
                    sc.nextLine();
                    ask();
                }
                else
                {
                    conn.close();
                }
            }
            catch(InputMismatchException e)
            {
                System.out.println("\nMobile Number Must be of 10 digits.");
                sc.nextLine();
                ask();
            }
            sql = "Insert Into User (Name, Aadhar, Mobile, Car, Date) Values( ?, ?, ?, ?, ? )";
            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setLong(2, aadhar);
            pstmt.setLong(3, mobile);
            pstmt.setString(4, car);
            pstmt.setString(5, date);
            pstmt.executeUpdate();
            conn.commit();
            conn.close();
            sc.nextLine();
            display(aadhar);
            new Menu().menu(aadhar);

        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    void ask()
    {
        try
        {
            char c;
            System.out.println("\nAre you registered Customer?(Y/N)\n");
            c = sc.next().toLowerCase().charAt(0);
            
            if(c == 'y')
            {
                sc.nextLine();
                registered();
            }
            else if(c == 'n')
            {
                sc.nextLine();
                register();
            }
            else
            {
                System.out.println("\nIncorrect Choice!"); 
                sc.nextLine(); 
                ask();
            }
            
        }
        catch(InputMismatchException e)
        {
            System.out.println("Invalid Input");
            sc.nextLine();
            ask();
        }
        

    }
}

class vehicle extends sqlite
{
    String sql;

    List<String> cars = new ArrayList<String>();

    void check(Long a, String n) throws SQLException
    {
        sql = "Select * From Cars";
        Connection conn = this.connect();
        PreparedStatement pstmt  = conn.prepareStatement(sql);
        ResultSet r = pstmt.executeQuery();
        while(r.next())
            cars.add(r.getString("Name"));
        boolean car = cars.contains(n);
        if(car)
        {
            conn.close();
        }
        else
        {
            n = n.substring(0, 1).toUpperCase() + n.substring(1);
            System.out.println("\n" + n + " is not available. Check if you have entered coorect spelling.");
            conn.close();
            new Menu().menu(a);
        }

    }

    void displayAvailable(Long a) throws SQLException
    {
        String s;
        sql = "Select * From Cars Where Available = 0";
        Connection conn = this.connect();
        Statement stmt  = conn.createStatement();
        ResultSet r = stmt.executeQuery(sql);
        if (r.next() == false)
        {
            System.out.println("\nNo car is available to rent. Please check later.");
            conn.close();
            new Menu().menu(a);
        }
        else
        {   
            System.out.println("\nCars available to rent are: ");
            while(r.next())
            {
                s = r.getString("Name");
                s = s.substring(0, 1).toUpperCase() + s.substring(1);
                System.out.println(s);
            }
            conn.close();
        }
    }
}

class Menu extends sqlite
{
    String sql;
    Scanner sc = new Scanner(System.in);
    void rent(Long a)
    {
        try
        {
            String name;
            new vehicle().displayAvailable(a);
            System.out.println("\nEnter car name or 1 to return to main menu: ");
            name = sc.next();
            if(name.equals("1"))
            {
                sc.nextLine();
                menu(a);
            }
            else
            {
                name = name.toLowerCase();
                char c;
                new vehicle().check(a, name);
                System.out.println("\nDo you wish to continue?(Y/N): ");
                c = sc.next().toLowerCase().charAt(0);
                if(c == 'y')
                {
                    int m, money, day;
                    System.out.println("\nEnter number of day(s): ");
                    day = sc.nextInt();
                    if(day<1)
                    {
                        System.out.println("\nMinimum number of day(s) must be greater than or equal to 1");
                        sc.nextLine();
                        rent(a);
                    }
                    System.out.println("\nChoose your Payement method:\nOnline transaction is supported for following banks:\n1. Kotak\n2. SBI\n3. HDFC\n4. ICICI\n5. IDBI\nCash payment is also available:\n6. Cash");
                    m = sc.nextInt();
                    switch(m)
                    {
                        case(1):
                        {
                            money = new Kotak().convenience(day);
                            System.out.println("\nYou selected Online transaction with Kotak bank as your payement preference.\nConvenience fee charged by bank is 2%.\nYour total for " + day+ " day(s) is Rs."+money);
                            System.out.println("\nDo you wish to continue?(Y/N): ");
                            c = sc.next().toLowerCase().charAt(0);
                            if(c == 'y')
                            {    
                                sc.nextLine();
                                new changeIn().Update(name);
                                new changeIn().Update(a, name, day);
                            }
                            else if(c == 'n')
                            {
                                sc.nextLine();
                                return;
                            }
                            else
                            {
                                System.out.println("\nIncorrect choice!");
                                sc.nextLine();
                                rent(a);
                            }
                            break;
                        }
                        case(2):
                        {
                            money = new SBI().convenience(day);
                            System.out.println("\nYou selected Online transaction with SBI bank as your payement preference.\nConvenience fee charged by bank is 4%.\nYour total for " + day+ " day(s) is Rs."+money);
                            System.out.println("\nDo you wish to continue?(Y/N): ");
                            c = sc.next().toLowerCase().charAt(0);
                            if(c == 'y')
                            {    
                                sc.nextLine();
                                new changeIn().Update(name);
                                new changeIn().Update(a, name, day);
                            }
                            else if(c == 'n')
                            {
                                sc.nextLine();
                                return;
                            }
                            else
                            {
                                System.out.println("\nIncorrect choice!");
                                sc.nextLine();
                                rent(a);
                            }
                            break;
                        }
                        case(3):
                        {
                            money = new HDFC().convenience(day);
                            System.out.println("\nYou selected Online transaction with HDFC bank as your payement preference.\nConvenience fee charged by bank is 6%.\nYour total for " + day+ " day(s)ys is Rs."+money);
                            System.out.println("\nDo you wish to continue?(Y/N): ");
                            c = sc.next().toLowerCase().charAt(0);
                            if(c == 'y')
                            {    
                                sc.nextLine();
                                new changeIn().Update(name);
                                new changeIn().Update(a, name, day);
                            }
                            else if(c == 'n')
                            {
                                sc.nextLine();
                                return;
                            }
                            else
                            {
                                System.out.println("\nIncorrect choice!");
                                sc.nextLine();
                                rent(a);
                            }
                            break;
                        }
                        case(4):
                        {
                            money = new ICICI().convenience(day);
                            System.out.println("\nYou selected Online transaction with ICICI bank as your payement preference.\nConvenience fee charged by bank is 8%.\nYour total for " + day+ " day(s) is Rs."+money);
                            System.out.println("\nDo you wish to continue?(Y/N): ");
                            c = sc.next().toLowerCase().charAt(0);
                            if(c == 'y')
                            {    
                                sc.nextLine();
                                new changeIn().Update(name);
                                new changeIn().Update(a, name, day);
                            }
                            else if(c == 'n')
                            {
                                sc.nextLine();
                                return;
                            }
                            else
                            {
                                System.out.println("\nIncorrect choice!");
                                sc.nextLine();
                                rent(a);
                            }
                            break;
                        }
                        case(5):
                        {
                            money = new IDBI().convenience(day);
                            System.out.println("\nYou selected Online transaction with IDBI bank as your payement preference.\nConvenience fee charged by bank is 10%.\nYour total for " + day+ " day(s) is Rs."+money);
                            System.out.println("\nDo you wish to continue?(Y/N): ");
                            c = sc.next().toLowerCase().charAt(0);
                            if(c == 'y')
                            {    
                                sc.nextLine();
                                new changeIn().Update(name);
                                new changeIn().Update(a, name, day);
                            }
                            else if(c == 'n')
                            {
                                sc.nextLine();
                                return;
                            }
                            else
                            {
                                System.out.println("\nIncorrect choice!");
                                sc.nextLine();
                                rent(a);
                            }
                            break;
                        }
                        case(6):
                        {
                            money = new Cash().convenience(day);
                            System.out.println("\nYou selected cash as your payement preference.\nNo convenience fee is charged.\nYour total for " + day+ " day(s) is Rs."+money);
                            System.out.println("\nDo you wish to continue?(Y/N): ");
                            c = sc.next().toLowerCase().charAt(0);
                            if(c == 'y')
                            {    
                                sc.nextLine();
                                new changeIn().Update(name);
                                new changeIn().Update(a, name, day);
                            }
                            else if(c == 'n')
                            {
                                sc.nextLine();
                                return;
                            }
                            else
                            {
                                System.out.println("\nIncorrect choice!");
                                sc.nextLine();
                                rent(a);
                            }
                            break;
                        }
                        default:
                        {
                            System.out.println("\nIncorrect option!");
                            sc.nextLine();
                            rent(a);
                        }

                    }
                }
                else if(c == 'n')
                {
                    sc.nextLine();
                    return;
                }
                else
                {
                    System.out.println("\nIncorrect choice!");
                    sc.nextLine();
                    rent(a);
                }
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch(InputMismatchException e)
        {
            System.out.println("Invalid Input");
            sc.nextLine();
            rent(a);
        }
    }

    void unRent(Long a)
    {
        try
        {
            String name;
            System.out.println("\nEnter car name or 1 to return to main menu:\n");
            name = sc.next();
            if(name.equals("1"))
            {
                sc.nextLine();
                menu(a);
            }
            else
            {
                name = name.toLowerCase();
                char c;
                new vehicle().check(a, name);
                System.out.println("\nDo you wish to continue?(Y/N): ");
                c = sc.next().toLowerCase().charAt(0);
                if(c == 'y')
                {
                    LocalDate t =  LocalDate.now();
                    String sql = "Select Date from User Where Aadhar = ?";
                    Connection conn = this.connect();
                    PreparedStatement pstmt  = conn.prepareStatement(sql);
                    pstmt.setLong(1, a);
                    ResultSet r = pstmt.executeQuery();
                    String dd = r.getString("Date");
                    LocalDate d = LocalDate.parse(dd);
                    if(t.isAfter(d))
                        System.out.println("\nCar returned past due date.");
                    
                    System.out.println("\nCar returned successfully. Your refund will be proceeded after inspection of car.");
                    conn.close();
                    sc.nextLine();
                    new changeReset().Update(name);
                    new changeReset().Update(a, "No Car rented", "No due date");
                }
                else if(c == 'n')
                {
                    sc.nextLine();
                    return;
                }
                else
                {
                    System.out.println("\nIncorrect choice!");
                    sc.nextLine();
                    unRent(a);
                }

            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        catch(InputMismatchException e)
        {
            System.out.println("Invalid Input");
            sc.nextLine();
            unRent(a);
        }
        
    }

    static void Start()
    {
        System.gc();
        final String[] args = {};
        new Main().main(args);
    }

    void menu(Long a) throws SQLException
    {
        try
        {
            int c;
            System.out.println("\nServices Available:-\n1. Display Cars available.\n2. Rent a Car.\n3. Return Rented Car.\n4. Show Customer details.\n5. Exit.\n");
            c = sc.nextInt();
            
            if(c == 1)
            {
                new vehicle().displayAvailable(a);
                sc.nextLine();
                menu(a);
            }
            else if(c == 2)
            {

                sql = "Select * From User Where Aadhar = ?";
                Connection conn = this.connect();
                PreparedStatement pstmt  = conn.prepareStatement(sql);
                pstmt.setLong(1, a);
                ResultSet r = pstmt.executeQuery();
                if("No Car rented".equals(r.getString("Car")))
                {
                    conn.close();
                    sc.nextLine();
                    rent(a);
                    menu(a);
                }
                else
                {
                    System.out.println("\nOnly 1 car can be rented on each Aadhar Number.");
                    conn.close();
                    sc.nextLine();
                    menu(a);  
                }
            }
            else if (c == 3)
            {
                sql = "Select * From User Where Aadhar = ?";
                Connection conn = this.connect();
                PreparedStatement pstmt  = conn.prepareStatement(sql);
                pstmt.setLong(1, a);
                ResultSet r = pstmt.executeQuery();
                if("No Car rented".equals(r.getString("Car")))
                {
                    System.out.println("\nService not available.");
                    conn.close();
                    sc.nextLine();
                    menu(a);  
                }
                else
                {
                    conn.close();
                    sc.nextLine();
                    unRent(a);
                    menu(a);
                }
            }
            else if (c == 4)
            {
                sc.nextLine();
                new userExist().display(a);
                menu(a);
            }
            else if (c == 5)
            {
                sc.nextLine();
                Start();
            }
            else
            {
                System.out.println("\nInvalid Option!");
                sc.nextLine();
                menu(a);
            }
            
            
        }
        catch(InputMismatchException e)
        {
            System.out.println("Invalid Input");
            sc.nextLine();
            menu(a);
        }
    }
}

class update extends sqlite
{
    void Update(String n){;}
    void Update(Long a, String n, int d){;}
    void Update(Long a, String n, String d){;}
}

class changeIn extends update
{
    String sql;
    void Update(String n)
    {
        try
        {
            sql = "Update Cars Set Available = 1 Where Name = ?";
            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1, n);
            pstmt.executeUpdate();
            conn.commit();
            conn.close();
        }
        catch(SQLException e){;}
    }
    void Update(Long a, String n, int d)
    {
        try
        {
            LocalDate t =  LocalDate.now().plusDays(d);
            String nd = t.toString();
            sql = "Update User Set Car = ?, Date = ? Where Aadhar = ?";
            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1,n);
            pstmt.setString(2, nd);
            pstmt.setLong(3, a);
            pstmt.executeUpdate();
            conn.commit();
            conn.close();
        }
        catch(SQLException e){;}
    }
}
class changeReset extends update
{
    String sql;
    void Update(String n)
    {
        try
        {
            sql = "Update Cars Set Available = 0 Where Name = ?";
            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1, n);
            pstmt.executeUpdate();
            conn.commit();
            conn.close();
        }
        catch(SQLException e){;}
    }
    void Update(Long a, String n, String d)
    {
        try
        {
            sql = "Update User Set Car = ?, Date = ? Where Aadhar = ?";
            Connection conn = this.connect();
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1,n);
            pstmt.setString(2, d);
            pstmt.setLong(3, a);
            pstmt.executeUpdate();
            conn.commit();
            conn.close();
        }
        catch(SQLException e){;}
    }
}

abstract class Bank
{
    abstract int convenience(int d);
}
class Kotak extends Bank
{    
    int money;
    @Override int convenience(int d)
    {
        if(d<=15)
        {
            money = 2000*d + 20000;
            money *= 1.02;
        }
        else
        {
            money = 1800*d + 20000;
            money *= 1.02;
        }
        return money;
    }    
}    
class SBI extends Bank
{    
    int money;
    @Override int convenience(int d)
    {
        if(d<=15)
        {
            money = 2000*d + 20000;
            money *= 1.04;
        }
        else
        {
            money = 1800*d + 20000;
            money *= 1.04;
        }
        return money;
    }    
}    
class HDFC extends Bank
{    
    int money;
    @Override int convenience(int d)
    {
        if(d<=15)
        {
            money = 2000*d + 20000;
            money *= 1.06;
        }
        else
        {
            money = 1800*d + 20000;
            money *= 1.06;
        }
        return money;
    }     
}   
class ICICI extends Bank
{    
    int money;
    @Override int convenience(int d)
    {
        if(d<=15)
        {
            money = 2000*d + 20000;
            money *= 1.08;
        }
        else
        {
            money = 1800*d + 20000;
            money *= 1.08;
        }
        return money;
    }     
}
class IDBI extends Bank
{    
    int money;
    @Override int convenience(int d)
    {
        if(d<=15)
        {
            money = 2000*d + 20000;
            money *= 1.1;
        }
        else
        {
            money = 1800*d + 20000;
            money *= 1.1;
        }
        return money;
    }     
}
class Cash extends Bank
{
    int money;
    @Override int convenience(int d)
    {
        if(d<=15)
            money = 2000*d + 20000;
        else
            money = 1800*d + 20000;
        return money;
    } 
}      


class Main
{
    public static void main(String[] args)
    {
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
        System.out.println("\n\t\t\t\t\t--------------------Welcome to Car Rents Pvt Limited--------------------");
        System.out.println("\nPrice:\nRs.2000 per day if car is booked for less than or equal to 15 days + Rs.20000 Caution Money + Convenience fee of bank.\nRs.1800 per day if car is booked for more than 15 days + Rs.20000 Caution Money.");
        System.out.println("\n1. Caution Money includes damages to vehicle and late return of vehicle and will be refunded after complete inspection of rented vehicle.\n2. Only 1 car can be rented on each Aadhar Number.\n3. Press Ctrl + C to exit at any moment.");
        new userExist().ask();
    }
}
