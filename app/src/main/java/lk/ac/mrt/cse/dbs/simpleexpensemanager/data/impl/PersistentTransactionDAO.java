package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHandle;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends DatabaseHandle implements TransactionDAO {

    public PersistentTransactionDAO(Context context) {
        super(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase myDataBase = getWritableDatabase();
        ContentValues content_values = new ContentValues();
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");

        content_values.put(DATE,date_format.format((date)));
        content_values.put(ACCOUNT_NO,accountNo);
        content_values.put(EXPENSE_TYPE, String.valueOf(expenseType));
        content_values.put(AMOUNT, amount);

        myDataBase.insert(TRANSACTION_TABLE, null, content_values);
        myDataBase.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs()  {

        List<Transaction> transactions = new ArrayList<Transaction>();
        SQLiteDatabase myDataBase = getReadableDatabase();

        String get_transactions = "SELECT * FROM " + TRANSACTION_TABLE;
        Cursor mycur = myDataBase.rawQuery(get_transactions,null);


        if (mycur.moveToFirst()){
            do {
                SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
                Date curr_date = null;
                try {
                    curr_date = date_format.parse(mycur.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ExpenseType expenseType;
                if(Objects.equals(mycur.getString(3), "EXPENSE")){
                    expenseType = ExpenseType.EXPENSE;
                }else{
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(curr_date, mycur.getString(2), expenseType,mycur.getDouble(4));
                transactions.add(transaction);
            }while(mycur.moveToNext());
        }
        myDataBase.close();
        return transactions;

    }


    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase myDataBase = getReadableDatabase();

        String get_transactions = "SELECT * FROM " + TRANSACTION_TABLE;
        Cursor mycur = myDataBase.rawQuery(get_transactions,null);

        if (mycur.moveToFirst()){
            int i = 0;
            while(mycur.moveToNext() && i < limit) {
                SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = date_format.parse(mycur.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ExpenseType expenseType;
                if(Objects.equals(mycur.getString(3), "EXPENSE")){
                    expenseType = ExpenseType.EXPENSE;
                }else{
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(date, mycur.getString(2), expenseType,mycur.getDouble(4));
                transactions.add(transaction);
                i += 1;
            } ;
        }
        myDataBase.close();
        return transactions;

    }

}
