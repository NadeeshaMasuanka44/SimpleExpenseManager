package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHandle extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "200614N";

    //Account tables column
    public static final String ACCOUNT_NO = "accountNo";
    public static final String BANK_NAME = "bankName";
    public static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final  String ACCOUNT_BALANCE = "balance";

    //Transaction table columns
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String DATE = "date";
    public static final String ACCOUNT_TYPE = "accountType";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";

    //Tables
    public static final String ACCOUNT_TABLE = "accounts";
    public static final String TRANSACTION_TABLE = "transactions";


    protected Context context;


    public DatabaseHandle(@Nullable Context context) {

        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Query for creating the account table
        String create_account = "CREATE TABLE " + ACCOUNT_TABLE + " (" +
                ACCOUNT_NO + " TEXT PRIMARY KEY," +
                BANK_NAME + " TEXT," +
                ACCOUNT_HOLDER_NAME + " TEXT," +
                ACCOUNT_BALANCE + " REAL)";
        sqLiteDatabase.execSQL(create_account);

        //Query for creating the transaction table
        String create_transaction = "CREATE TABLE " + TRANSACTION_TABLE + " (" +
                TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DATE + " TEXT," +
                ACCOUNT_TYPE + " TEXT," +
                EXPENSE_TYPE + " TEXT," +
                AMOUNT + "REAL" +");";

        sqLiteDatabase.execSQL(create_transaction);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Drop old tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        //Create new tables
        onCreate(sqLiteDatabase);
    }
}
