package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHandle;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends DatabaseHandle implements AccountDAO {
    public PersistentAccountDAO(Context context) {
        super(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNo = new ArrayList<>();  //To store account numbers list
        SQLiteDatabase myDatabase = getReadableDatabase();
        String getAccountNumbersListQuery = "SELECT * FROM " + ACCOUNT_TABLE;
        Cursor mycur = myDatabase.rawQuery(getAccountNumbersListQuery,null);    //Bundle to store data from the database

        if(mycur.getCount() != 0) {
            while (mycur.moveToNext()) {
                accountNo.add(mycur.getString(0));  //add account numbers to the list
            }
        }
        mycur.close();
        myDatabase.close();
        return accountNo;
    }

    @Override
    public List<Account> getAccountsList() {
        System.out.println("inside the get accounts method000");
        List<Account> accountsList = new ArrayList<Account>();      //List to store the accounts list
        SQLiteDatabase myDatabase = getReadableDatabase();
        String getAccountsListQuery = "SELECT * FROM " + ACCOUNT_TABLE;
        Cursor mycur = myDatabase.rawQuery(getAccountsListQuery,null);

        if(mycur.getCount() != 0) {
            while (mycur.moveToNext()) {
                //taking the account info then make a acoount and add to the accounts list
                String acc_no = mycur.getString(0);
                String bank_name = mycur.getString(1);
                String acc_holder_name = mycur.getString(2);
                double acc_balance = mycur.getDouble(3);
                Account account = new Account(acc_no, bank_name, acc_holder_name, acc_balance);
                accountsList.add(account);
            }
        }
        mycur.close();
        myDatabase.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase myDatabase = getReadableDatabase();
        String getAccountQuery = "SELECT * FROM " + ACCOUNT_TABLE + "WHERE"  + ACCOUNT_NO + "=" + accountNo;
        Cursor mycur = myDatabase.rawQuery(getAccountQuery,null);

        if(mycur.getCount() == 0){
            throw new InvalidAccountException("Invalid Account Number");
        }
        String acc_no = "";
        String bank_name = "";
        String acc_holder_name = "";
        double acc_balance = 0;

        while (mycur.moveToNext()) {
            //getting the account what user requested
            acc_no = mycur.getString(0);
            bank_name = mycur.getString(1);
            acc_holder_name = mycur.getString(2);
            acc_balance = mycur.getDouble(3);
        }

        mycur.close();
        Account account = new Account(acc_no, bank_name, acc_holder_name, acc_balance);
        myDatabase.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        //adding a new account to the database
        SQLiteDatabase myDatabase = getWritableDatabase();
        ContentValues content_values = new ContentValues();

        content_values.put(ACCOUNT_NO,account.getAccountNo());
        content_values.put(BANK_NAME,account.getBankName());
        content_values.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        content_values.put(ACCOUNT_BALANCE,account.getBalance());

        myDatabase.insert(ACCOUNT_TABLE,null,content_values);
        myDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        //Removing an account from the database

        SQLiteDatabase myDatabase = getWritableDatabase();
        myDatabase.delete(ACCOUNT_TABLE,ACCOUNT_NO + " = ?",new String[] {String.valueOf(accountNo)});
        myDatabase.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase myDataBase = getWritableDatabase();
        String[] projection = {
                ACCOUNT_BALANCE
        };
        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = myDataBase.query(
                ACCOUNT_TABLE, projection, selection, selectionArgs, null, null, null
        );
        double balance;

        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues content_values = new ContentValues();
        switch (expenseType) {
            //If the transaction is an expense type
            case EXPENSE:
                content_values.put(ACCOUNT_BALANCE, balance - amount);
                break;
            //If the transaction is an income type
            case INCOME:
                content_values.put(ACCOUNT_BALANCE, balance + amount);
                break;
        }

        //Update the raw
        myDataBase.update(ACCOUNT_TABLE, content_values, ACCOUNT_NO + " = ?",
                new String[] { accountNo });

        myDataBase.close();

    }
}
