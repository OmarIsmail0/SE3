package rmi;

import rmi.Interface.AccountInterface;
import rmi.ReadOnly.ClientReadOnly;
import rmi.ReadOnly.AdminReadOnly;
import rmi.ReadOnly.DriverReadOnly;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.*;

public class Account extends UnicastRemoteObject implements ClientReadOnly, DriverReadOnly, AdminReadOnly, AccountInterface, Serializable {
    int accID;
    String username, password, email, mobile;
    AccType type;

    private ArrayList<Account> acc;
    private ArrayList<BankAccount> bankAcc;
    private ArrayList<Client> client;
    private ArrayList<Driver> driver;

    public static int verificationCode;

    /*Login Info*/
    static String Client_Login_Mail;
    static AccType Client_acc_type;
    static String Driver_Login_Mail;
    static AccType Driver_acc_type;

    public Account() throws RemoteException {

    }

    public Account(int accID, String username, String password, String email, String mobile, AccType type) throws RemoteException {
        this.accID = accID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.type = type;
    }

    public Account(int accID, String username, String password, String email, String mobile, AccType type, ArrayList<Account> acc) throws RemoteException {
        this.accID = accID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.type = type;
        this.acc = acc;
    }

    public int getAccID() {
        return accID;
    }

    public void setAccID(int accID) {
        this.accID = accID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public AccType getType() {
        return type;
    }

    public void setType(AccType type) {
        this.type = type;
    }

    public ArrayList<Account> getAcc() {
        return acc;
    }

    public void setAcc(ArrayList<Account> acc) {
        this.acc = acc;
    }

    public static int getVerificationCode() {
        return verificationCode;
    }

    public static void setVerificationCode(int verificationCode) {
        Account.verificationCode = verificationCode;
    }

    /*Account*/
    @Override
    public void createClientAccount(String username, String password, String email, String mobile, AccType type,
                                    double balance, String CCnumber, int ccv, Date expDate) throws RemoteException {

        Account new_Account = new Account();
        BankAccount new_BankAcc = new BankAccount();
        Client new_Client = new Client();

        DB db = new DB();

        acc = db.retrieveAllAccounts();
        acc = db.retrieveAllAccounts();
        bankAcc = db.retrieveAllBankAccounts();
        client = db.retrieveAllClients();

        Scanner input = new Scanner(System.in);

        boolean unique = false;

        int numOfAttempts = 3;
        int code = 0;

        try {
            for (int i = 0; i < acc.size(); i++) {
                if (acc.get(i).getEmail().equals(email)) {
                    unique = false;
                    break;
                } else {
                    unique = true;
                }
            }

            if (unique || acc.isEmpty()) {
                new_Account.setUsername(username);
                new_Account.setPassword(password);
                new_Account.setEmail(email);
                new_Account.setMobile(mobile);
                new_Account.setType(type);

                new_BankAcc.setMail(email);
                new_BankAcc.setBalance(balance);
                new_BankAcc.setCCnumber(CCnumber);
                new_BankAcc.setCcv(ccv);
                new_BankAcc.setExpDate(expDate);

                new_Client.setNumOfRides(0);
                new_Client.setRating(0);
                new_Client.setAcc(new_Account);
                new_Client.setBankAcc(new_BankAcc);

                sendVerification();
                System.out.print("Enter your verification code: ");
                while (numOfAttempts > 0) {
                    code = input.nextInt();
                    if (code == verificationCode) {
                        acc.add(new_Account);
                        db.insertAccount(new_Account);
                        bankAcc.add(new_BankAcc);
                        db.insertBankAccount(new_BankAcc);
                        client.add(new_Client);
                        db.insertClient(new_Client);

                        System.out.println("Account Registered");
                        JFrame frame = new JFrame();
                        frame.setVisible(true);
                        JOptionPane.showMessageDialog(frame,"Account Registered");
                        break;
                    } else {
                        numOfAttempts--;
                        System.out.println("please try again!");
                    }

                }
            } else {
                System.err.println("this email is already registered");
                JFrame frame = new JFrame();
                frame.setVisible(true);
                JOptionPane.showMessageDialog(frame,"this email is already registered");
            }


        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void createDriverAccount(String username, String password, String email, String mobile
            , AccType type, String driverLicense, ArrayList<AvailableTimes> workingTimes, String carModel,
                                    String plateNum, String carColor, float rating, CurrentArea currentArea) throws RemoteException {


        Account new_Account = new Account();
        Driver new_Driver = new Driver();
        Car new_Car = new Car();

        DB db = new DB();

        acc = db.retrieveAllAccounts();
        driver = db.retrieveAllDrivers();

        Scanner input = new Scanner(System.in);

        boolean unique = false;

        try {
            for (int i = 0; i < acc.size(); i++) {
                if (acc.get(i).getEmail().equals(email)) {
                    unique = false;
                    break;
                } else {
                    unique = true;
                }
            }

            if (unique || acc.isEmpty()) {
                new_Account.setUsername(username);
                new_Account.setPassword(password);
                new_Account.setEmail(email);
                new_Account.setMobile(mobile);
                new_Account.setType(type);

                new_Car.setCarColor(carColor);
                new_Car.setCarModel(carModel);
                new_Car.setPlateNum(plateNum);

                new_Driver.setDriverLicense(driverLicense);
                new_Driver.setWorkingTimes(workingTimes);
                new_Driver.setRating(rating);
                new_Driver.setArea(currentArea);
                new_Driver.setAcc(new_Account);
                new_Driver.setCar(new_Car);


                acc.add(new_Account);
                db.insertAccount(new_Account);
                driver.add(new_Driver);
                db.insertDriver(new_Driver);

                System.out.println("Account Registered");
                JFrame frame = new JFrame();
                frame.setVisible(true);
                JOptionPane.showMessageDialog(frame,"Account Registered");
            } else {
                System.err.println("this email is already registered");
                JFrame frame = new JFrame();
                frame.setVisible(true);
                JOptionPane.showMessageDialog(frame,"this email is already registered");
            }


        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public String viewOwnAccount(String email) throws RemoteException {
        Account account = new Account();
        DB db = new DB();
        //Error
        account = db.retrieveAccount(email);
        String text = account.toString();
        return text;
    }

    @Override
    public String viewAllAccounts() throws RemoteException {
        DB db = new DB();
        acc = db.retrieveAllAccounts();
        String text = " ";
        if (acc.isEmpty()) {
            text = "not found";
        } else {
            for (int i = 0; i < acc.size(); i++) {
                text += acc.get(i).toString() + "\n";
            }
        }
        return text;
    }

    @Override
    public boolean login(String EMAIL, String PW) throws RemoteException {
        DB db = new DB();
        acc = db.retrieveAllAccounts();
        int index = -1;

        for (int i = 0; i < acc.size(); i++) {
            if (acc.get(i).getEmail().equals(EMAIL)) {
                index = i;
            }
        }
        if (index == -1) {
            System.out.println("Incorrect Email");
        } else {
            if (acc.get(index).getPassword().equals(PW)) {
                if (acc.get(index).getType() == AccType.CLIENT) {
                    Client_Login_Mail = acc.get(index).getEmail();
                    Client_acc_type = acc.get(index).getType();
                } else if (acc.get(index).getType() == AccType.DRIVER) {
                    Driver_Login_Mail = acc.get(index).getEmail();
                    Driver_acc_type = acc.get(index).getType();
                }
                return true;
            } else {
                System.out.println("Incorrect Password");
                return false;
            }
        }
        return false;
    }

    @Override
    public void banAccount(String email) throws RemoteException {
        DB db = new DB();
        acc = db.retrieveAllAccounts();
        bankAcc = db.retrieveAllBankAccounts();
        client = db.retrieveAllClients();
        driver = db.retrieveAllDrivers();

        int index = -1;
        for (int i = 0; i < acc.size(); i++) {
            if (acc.get(i).getEmail().equals(email)) {
                index = i;
            }
        }
        if (index == -1) {
            System.out.println("account not found");
        } else if (acc.get(index).getType() == AccType.CLIENT) {
            int idx = -1;
            for (int i = 0; i < client.size(); i++) {
                if (client.get(i).getAcc().getEmail().equals(email)) {
                    idx = i;
                    break;
                }
            }
            int idx3 = -1;
            for (int i = 0; i < bankAcc.size(); i++) {
                if (bankAcc.get(i).getMail().equals(email)) {
                    idx3 = i;
                    break;
                }
            }
            db.deleteAccount(acc.get(index).getEmail());
            db.deleteBankAccount(bankAcc.get(idx3).getMail());
            db.deleteClient(client.get(idx).getAcc().getEmail());
            System.out.println("account deleted");
            JFrame frame = new JFrame();
            frame.setVisible(true);
            JOptionPane.showMessageDialog(frame,"account deleted");
        } else if (acc.get(index).getType() == AccType.DRIVER) {
            int idx = -1;
            for (int i = 0; i < driver.size(); i++) {
                if (driver.get(i).getAcc().getEmail().equals(email)) {
                    idx = i;
                    break;
                }
            }
            db.deleteAccount(acc.get(index).getEmail());
            db.deleteDriverMail(driver.get(idx).getAcc().getEmail());
            System.out.println("account deleted");
            JFrame frame = new JFrame();
            frame.setVisible(true);
            JOptionPane.showMessageDialog(frame,"account deleted");
        }
    }

    public void sendVerification() throws RemoteException {
        Random rand = new Random();
        String id = String.format("%04d", rand.nextInt(10000));
        verificationCode = Integer.parseInt(id);
        System.out.println("your verification code is: " + verificationCode);
    }

    public boolean enterVerificationCode(int code) throws RemoteException {
        if (verificationCode == code) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        DB db = new DB();
        bankAcc = db.retrieveAllBankAccounts();
        int x = -1;
        for (int i = 0; i < bankAcc.size(); i++) {
            if (bankAcc.get(i).getMail().equals(getEmail()))
                x = i;
        }
        String result =
                "Account ID:\t\t" + getAccID()
                        + "\nName:\t\t\t" + getUsername()
                        + "\nEmail:\t\t\t" + getEmail()
                        + "\nMobile:\t\t\t" + getMobile()
                        + "\nType:\t\t\t" + getType()
             /*   + "\nBalance:\t\t" + bankAcc.get(x).getBalance()
                + "\nCCV:\t\t\t" + bankAcc.get(x).getCcv()
                + "\nCredit Card Number : " + bankAcc.get(x).getCCnumber()
                + "\nexpiration Date    : " + bankAcc.get(x).getExpDate()*/
                        + "\n------------------------";
        return result;

    }

    @Override
    public void editAccount() throws RemoteException {

    }

    @Override
    public String viewNotifications(String email) throws RemoteException {
        return null;
    }

    @Override
    public void clearNotification(String email) throws RemoteException {

    }

    /*Ride*/
    @Override
    public void requestRide(CurrentArea PUL, CurrentArea DST, String email, boolean payOnline) throws RemoteException {

    }


    @Override
    public ArrayList<RequestRide> viewRideHistory(String email) throws RemoteException {
        return null;
    }

    /*Compliant*/
    @Override
    public void giveComplaint(String msg, int rideID, String email) throws RemoteException {

    }

    /*Car*/
    @Override
    public void updateCar(String mail, String CM, String PN, String CC) throws RemoteException {

    }


}
