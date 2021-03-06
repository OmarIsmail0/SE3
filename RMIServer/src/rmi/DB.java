/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.util.*;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

public class DB {

    private MongoClient db;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    private Gson gson = new Gson();

    public DB() {
        // Disables Mongo Logs
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        // Initialize
        String connectionString = "mongodb+srv://AOOPSE:123@cluster0.g6utq.mongodb.net/<dbname>?retryWrites=true&w=majority";
        db = new MongoClient(new MongoClientURI(connectionString));
        database = db.getDatabase("OnTheMove2"); // Database name

    }

    /*-----------------Account-----------------*/
    public void insertAccount(Account acc) {
        collection = database.getCollection("Account");
        ArrayList<Account> result = new ArrayList();
        result = retrieveAllAccounts();
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            index = result.get(i).getAccID();
        }
        acc.setAccID(index + 1);
        collection.insertOne(Document.parse(gson.toJson(acc)));
        System.out.println("Account inserted.");
    }

    public ArrayList<Account> retrieveAllAccounts() {
        collection = database.getCollection("Account");
        ArrayList<Account> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        if (docs.isEmpty()) {
            return result;
        } else {
            for (int i = 0; i < docs.size(); i++) {
                result.add(gson.fromJson(docs.get(i).toJson(), Account.class));
            }
        }
        return result;
    }

    public Account retrieveAccount(String mail) {
        collection = database.getCollection("Account");
        Document doc = collection.find(Filters.eq("email", mail)).first();
        Account result = gson.fromJson(doc.toJson(), Account.class);
        return result;
    }


    public Account retrieveOneAccount(String email) {
        collection = database.getCollection("Account");
        Document doc = collection.find(Filters.eq("email", email)).first();
        Account result = gson.fromJson(doc.toJson(), Account.class);
        return result;
    }

    public void deleteAccount(String email) {
        collection = database.getCollection("Account");
        collection.deleteOne(Filters.eq("email", email));
    }

    /*-----------------Bank-----------------*/
    public void insertBankAccount(BankAccount bankAcc) {
        collection = database.getCollection("BankAccount");
        collection.insertOne(Document.parse(gson.toJson(bankAcc)));

    }

    public ArrayList<BankAccount> retrieveAllBankAccounts() {
        collection = database.getCollection("BankAccount");
        ArrayList<BankAccount> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), BankAccount.class));
        }
        return result;
    }

    public void deleteBankAccount(String email) {
        collection = database.getCollection("BankAccount");
        collection.deleteOne(Filters.eq("mail", email));
    }

    public void updateBalance(BankAccount acc, Client c) {
        collection = database.getCollection("BankAccount");
        Document doc = Document.parse(gson.toJson(acc));
        collection.replaceOne(Filters.eq("mail",acc.getMail()), doc);

        collection = database.getCollection("Client");
        doc = Document.parse(gson.toJson(c));
        collection.replaceOne(Filters.eq("bankAcc.mail", c.getAcc().getEmail()), doc);
    }

    /*-----------------Client-----------------*/
    public void insertClient(Client client) {
        collection = database.getCollection("Client");
        collection.insertOne(Document.parse(gson.toJson(client)));

    }

    public ArrayList<Client> retrieveAllClients() {
        collection = database.getCollection("Client");
        ArrayList<Client> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), Client.class));
        }
        return result;
    }


    public Client retrieveClientByMail() {
        collection = database.getCollection("Client");
        Document doc = collection.find(Filters.eq("acc.email", Account.Client_Login_Mail)).first();
        Client result = gson.fromJson(doc.toJson(), Client.class);
        return result;
    }

    public Client retrieveClientByMail2(String mail) {
        collection = database.getCollection("Client");
        Document doc = collection.find(Filters.eq("acc.email", mail)).first();
        Client result = gson.fromJson(doc.toJson(), Client.class);
        return result;
    }

    public void deleteClient(String email) {
        collection = database.getCollection("Client");
        collection.deleteOne(Filters.eq("acc.email", email));
    }

    public void updateClient(Account client) {
        collection = database.getCollection("Client");
        Document doc = Document.parse(gson.toJson(client));
        collection.replaceOne(Filters.eq("acc.email", client.getEmail()), doc);

        collection = database.getCollection("Account");
        collection.replaceOne(Filters.eq("email", client.getEmail()), doc);
    }

    /*-----------------Driver-----------------*/
    public void insertDriver(Driver driver) {
        collection = database.getCollection("Driver");
        collection.insertOne(Document.parse(gson.toJson(driver)));

    }

    public ArrayList<Driver> retrieveAllDrivers() {
        collection = database.getCollection("Driver");
        ArrayList<Driver> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), Driver.class));
        }
        return result;
    }

    public ArrayList<Driver> retrieveAllDriversByCurrArea(CurrentArea pickUp) {
        collection = database.getCollection("Driver");
        ArrayList<Driver> result = new ArrayList();
        ArrayList<Document> docs = collection.find(Filters.eq("area", pickUp)).into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), Driver.class));
        }
        return result;
    }

    public Driver retrieveDriverByMail(String mail) {
        collection = database.getCollection("Driver");
        Document doc = collection.find(Filters.eq("acc.email", mail)).first();
        Driver result = gson.fromJson(doc.toJson(), Driver.class);
        return result;
    }

    public void deleteDriverMail(String email) {
        collection = database.getCollection("Driver");
        collection.deleteOne(Filters.eq("acc.email", email));
    }

    public void updateDriver(Driver driver) {
        collection = database.getCollection("Driver");
        Document doc = Document.parse(gson.toJson(driver));
        collection.replaceOne(Filters.eq("acc.email", driver.getAcc().getEmail()), doc);
    }

    /*-----------------Ride-----------------*/
    public void insertComplaint(Complaint comp) {
        collection = database.getCollection("Complaint");
        collection.insertOne(Document.parse(gson.toJson(comp)));
    }

    public RequestRide retrieveRide(int id) {
        collection = database.getCollection("RequestRide");
        Document doc = collection.find(Filters.eq("ride_id", id)).first();
        RequestRide result = gson.fromJson(doc.toJson(), RequestRide.class);
        return result;
    }

    public void insertRide(RequestRide ride) {
        collection = database.getCollection("RequestRide");
        collection.insertOne(Document.parse(gson.toJson(ride)));
    }

    public ArrayList<RequestRide> retrieveAllRequestedRides() {
        collection = database.getCollection("RequestRide");
        ArrayList<RequestRide> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), RequestRide.class));
        }
        return result;
    }

    /*RequestRide*/
    public void updateRequestRide(RequestRide ride, int id) {
        collection = database.getCollection("RequestRide");
        Document doc = Document.parse(gson.toJson(ride));
        collection.replaceOne(Filters.eq("ride_id", id), doc);
    }
    public ArrayList<RequestRide> retrieveAllRequestedRidesByMail(String mail) {
        collection = database.getCollection("RequestRide");
        ArrayList<RequestRide> result = new ArrayList();
        ArrayList<Document> docs = collection.find(Filters.eq("acc.email", mail)).into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), RequestRide.class));
        }
        return result;
    }
    public ArrayList<RequestRide> retrieveAllRides() {
        collection = database.getCollection("RequestRide");
        ArrayList<RequestRide> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        if (docs.isEmpty()) {
            return result;
        } else {
            for (int i = 0; i < docs.size(); i++) {
                result.add(gson.fromJson(docs.get(i).toJson(), RequestRide.class));
            }
        }
        return result;
    }

    /*-----------------Notification-----------------*/

    public void insertNotification(Notification notif) {
        collection = database.getCollection("Notification");
        collection.insertOne(Document.parse(gson.toJson(notif)));
    }

    public ArrayList<Notification> retrieveAllNotifications() {
        collection = database.getCollection("Notification");
        ArrayList<Notification> result = new ArrayList();
        ArrayList<Document> docs = collection.find().into(new ArrayList<Document>());
        if (docs.isEmpty()) {
            return result;
        } else {
            for (int i = 0; i < docs.size(); i++) {
                result.add(gson.fromJson(docs.get(i).toJson(), Notification.class));
            }
        }
        return result;
    }

    public ArrayList<Notification> retrieveAllNotificationByMail(String mail) {
        collection = database.getCollection("Notification");
        ArrayList<Notification> result = new ArrayList();
        ArrayList<Document> docs = collection.find(Filters.eq("client_email", mail)).into(new ArrayList<Document>());
        for (int i = 0; i < docs.size(); i++) {
            result.add(gson.fromJson(docs.get(i).toJson(), Notification.class));
        }
        return result;
    }

    public void deleteNotification(int id) {
        collection = database.getCollection("Notification");
        collection.deleteOne(Filters.eq("id", id));
    }

}
