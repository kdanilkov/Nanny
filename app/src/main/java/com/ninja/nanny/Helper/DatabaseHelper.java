package com.ninja.nanny.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.Wish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";
	// Database Version
	private static final int DATABASE_VERSION = 1;
	//Database Name
	private static final String DATABASE_NAME = "Nanny";

	//table names
	private static final String TBL_BANKS = "banks";
	private static final String TBL_TRANSACTION = "transactions";
	private static final String TBL_WISH = "wish";

	//bank table keys
	private static final String KEY_ID = "id";
	private static final String KEY_ACCOUNT_NAME = "account_name";
	private static final String KEY_BANK = "bank";
	private static final String KEY_ACCOUNT_TYPE = "account_type";
	private static final String KEY_BALANCE = "balance";
	private static final String KEY_NOTIFICATION_MODE = "notification_mode";
	private static final String KEY_FLAG_ACTIVE = "flag_active";

	//transaction table keys
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_USAGE = "usage";
	private static final String KEY_CREATEDAT = "created_at";

	//wish table keys
	private static final String KEY_TITLE = "title";
	private static final String KEY_TOTAL_AMOUNT = "total_amount";
	private static final String KEY_MONTHLY_PAYMENT = "monthly_payment";
	private static final String KEY_SAVED_AMOUNT = "saved_amount";
	private static final String KEY_UPDATED_AT = "updated_at";

	// Banks table create statement
	private static final String CREATE_TABLE_BANKS = "CREATE TABLE IF NOT EXISTS "
			+ TBL_BANKS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ACCOUNT_NAME
			+ " TEXT," + KEY_BANK + " TEXT," + KEY_ACCOUNT_TYPE + " TEXT," + KEY_BALANCE + " INTEGER,"
			 + KEY_NOTIFICATION_MODE + " INTEGER," + KEY_FLAG_ACTIVE + " INTEGER" + ")";

	// Transactions table create statement
	private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE IF NOT EXISTS "
			+ TBL_TRANSACTION + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ACCOUNT_NAME
			+ " TEXT," + KEY_USAGE + " TEXT," + KEY_AMOUNT + " INTEGER,"
			+ KEY_CREATEDAT + " TEXT" + ")";

	// Wish table create statement
	private static final String CREATE_TABLE_WISH = "CREATE TABLE IF NOT EXISTS "
			+ TBL_WISH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE
			+ " TEXT," + KEY_TOTAL_AMOUNT + " INTEGER," + KEY_MONTHLY_PAYMENT + " INTEGER,"
			+ KEY_SAVED_AMOUNT + " INTEGER," + KEY_UPDATED_AT + " TEXT," + KEY_FLAG_ACTIVE + " INTEGER" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_BANKS);
		db.execSQL(CREATE_TABLE_TRANSACTIONS);
		db.execSQL(CREATE_TABLE_WISH);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TBL_BANKS);
		db.execSQL("DROP TABLE IF EXISTS " + TBL_TRANSACTION);
		db.execSQL("DROP TABLE IF EXISTS " + TBL_WISH);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "banks" table methods ----------------//

	/*
	 * Creating a banks
	 */
	public int createBank(Bank bank) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT_NAME, bank.getAccountName());
		values.put(KEY_BANK, bank.getBank());
		values.put(KEY_ACCOUNT_TYPE, bank.getAccountType());
		values.put(KEY_BALANCE, bank.getBalance());
		values.put(KEY_NOTIFICATION_MODE, bank.getNotificationMode());
		values.put(KEY_FLAG_ACTIVE, bank.getFlagActive());

		// insert row
		long bank_id = db.insert(TBL_BANKS, null, values);


		return (int)bank_id;
	}

	/*
	 * get single bank
	 */
	public Bank getBank(int bank_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TBL_BANKS + " WHERE "
				+ KEY_ID + " = " + bank_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Bank bank = new Bank();
		bank.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		bank.setAccountName(c.getString(c.getColumnIndex(KEY_ACCOUNT_NAME)));
		bank.setBank(c.getString(c.getColumnIndex(KEY_BANK)));
		bank.setAccountType(c.getString(c.getColumnIndex(KEY_ACCOUNT_TYPE)));
		bank.setBalance(c.getInt(c.getColumnIndex(KEY_BALANCE)));
		bank.setNotificationMode(c.getInt(c.getColumnIndex(KEY_NOTIFICATION_MODE)));
		bank.setFlagActive(c.getInt(c.getColumnIndex(KEY_FLAG_ACTIVE)));

		return bank;
	}

	/**
	 * getting all banks
	 * */
	public List<Bank> getAllBanks() {
		List<Bank> banks = new ArrayList<Bank>();
		String selectQuery = "SELECT  * FROM " + TBL_BANKS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Bank bank = new Bank();
				bank.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				bank.setAccountName(c.getString(c.getColumnIndex(KEY_ACCOUNT_NAME)));
				bank.setBank(c.getString(c.getColumnIndex(KEY_BANK)));
				bank.setAccountType(c.getString(c.getColumnIndex(KEY_ACCOUNT_TYPE)));
				bank.setBalance(c.getInt(c.getColumnIndex(KEY_BALANCE)));
				bank.setNotificationMode(c.getInt(c.getColumnIndex(KEY_NOTIFICATION_MODE)));
				bank.setFlagActive(c.getInt(c.getColumnIndex(KEY_FLAG_ACTIVE)));

				// adding to bank list
				banks.add(bank);
			} while (c.moveToNext());
		}

		return banks;
	}

	/*
	 * getting bank count
	 */
	public int getBankCount() {
		String countQuery = "SELECT  * FROM " + TBL_BANKS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a bank
	 */
	public int updateBank(Bank bank) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT_NAME, bank.getAccountName());
		values.put(KEY_BANK, bank.getBank());
		values.put(KEY_ACCOUNT_TYPE, bank.getAccountType());
		values.put(KEY_BALANCE, bank.getBalance());
		values.put(KEY_NOTIFICATION_MODE, bank.getNotificationMode());
		values.put(KEY_FLAG_ACTIVE, bank.getFlagActive());

		// updating row
		return db.update(TBL_BANKS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(bank.getId()) });
	}

	/*
	 * Deleting a bank
	 */
	public void deleteBank(int bank_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TBL_BANKS, KEY_ID + " = ?",
				new String[] { String.valueOf(bank_id) });
	}

	// ------------------------ "wish" table methods ----------------//
	/*
	 * Creating a wish
	 */

	public int createWish(Wish wish) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, wish.getTitle());
		values.put(KEY_TOTAL_AMOUNT, wish.getTotalAmount());
		values.put(KEY_MONTHLY_PAYMENT, wish.getMonthlyPayment());
		values.put(KEY_SAVED_AMOUNT, wish.getSavedAmount());
		values.put(KEY_UPDATED_AT, getDateTime());
		values.put(KEY_FLAG_ACTIVE, wish.getFlagActive());

		// insert row
		long wish_id = db.insert(TBL_WISH, null, values);

		return (int)wish_id;
	}

	/*
	 * get single wish
	 */
	public Wish getWish(int wish_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TBL_WISH + " WHERE "
				+ KEY_ID + " = " + wish_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Wish wish = new Wish();
		wish.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		wish.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
		wish.setTotalAmount(c.getInt(c.getColumnIndex(KEY_TOTAL_AMOUNT)));
		wish.setMonthlyPayment(c.getInt(c.getColumnIndex(KEY_MONTHLY_PAYMENT)));
		wish.setSavedAmount(c.getInt(c.getColumnIndex(KEY_SAVED_AMOUNT)));
		wish.setUpdatedAt(c.getString(c.getColumnIndex(KEY_UPDATED_AT)));
		wish.setFlagActive(c.getInt(c.getColumnIndex(KEY_FLAG_ACTIVE)));

		return wish;
	}

	/**
	 * getting all wishes
	 * */
	public List<Wish> getAllWishes() {
		List<Wish> wishes = new ArrayList<Wish>();
		String selectQuery = "SELECT  * FROM " + TBL_WISH;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Wish wish = new Wish();
				wish.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				wish.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
				wish.setTotalAmount(c.getInt(c.getColumnIndex(KEY_TOTAL_AMOUNT)));
				wish.setMonthlyPayment(c.getInt(c.getColumnIndex(KEY_MONTHLY_PAYMENT)));
				wish.setSavedAmount(c.getInt(c.getColumnIndex(KEY_SAVED_AMOUNT)));
				wish.setUpdatedAt(c.getString(c.getColumnIndex(KEY_UPDATED_AT)));
				wish.setFlagActive(c.getInt(c.getColumnIndex(KEY_FLAG_ACTIVE)));

				// adding to transaction list
				wishes.add(wish);
			} while (c.moveToNext());
		}

		return wishes;
	}

	/**
	 * getting active wishes
	 * */
	public List<Wish> getActiveWishes() {
		List<Wish> wishes = new ArrayList<Wish>();
		String selectQuery = "SELECT  * FROM " + TBL_WISH + " WHERE " + KEY_FLAG_ACTIVE + "=1";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Wish wish = new Wish();
				wish.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				wish.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
				wish.setTotalAmount(c.getInt(c.getColumnIndex(KEY_TOTAL_AMOUNT)));
				wish.setMonthlyPayment(c.getInt(c.getColumnIndex(KEY_MONTHLY_PAYMENT)));
				wish.setSavedAmount(c.getInt(c.getColumnIndex(KEY_SAVED_AMOUNT)));
				wish.setUpdatedAt(c.getString(c.getColumnIndex(KEY_UPDATED_AT)));
				wish.setFlagActive(c.getInt(c.getColumnIndex(KEY_FLAG_ACTIVE)));

				// adding to transaction list
				wishes.add(wish);
			} while (c.moveToNext());
		}

		return wishes;
	}

	/**
	 * getting active wishes
	 * */
	public List<Wish> getFinishedWishes() {
		List<Wish> wishes = new ArrayList<Wish>();
		String selectQuery = "SELECT  * FROM " + TBL_WISH + " WHERE " + KEY_FLAG_ACTIVE + "=0";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Wish wish = new Wish();
				wish.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				wish.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
				wish.setTotalAmount(c.getInt(c.getColumnIndex(KEY_TOTAL_AMOUNT)));
				wish.setMonthlyPayment(c.getInt(c.getColumnIndex(KEY_MONTHLY_PAYMENT)));
				wish.setSavedAmount(c.getInt(c.getColumnIndex(KEY_SAVED_AMOUNT)));
				wish.setUpdatedAt(c.getString(c.getColumnIndex(KEY_UPDATED_AT)));
				wish.setFlagActive(c.getInt(c.getColumnIndex(KEY_FLAG_ACTIVE)));

				// adding to transaction list
				wishes.add(wish);
			} while (c.moveToNext());
		}

		return wishes;
	}

	/*
	 * getting wish count
	 */
	public int getWishesCount() {
		String countQuery = "SELECT  * FROM " + TBL_WISH;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a wish
	 */
	public int updateWish(Wish wish) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, wish.getTitle());
		values.put(KEY_TOTAL_AMOUNT, wish.getTotalAmount());
		values.put(KEY_MONTHLY_PAYMENT, wish.getMonthlyPayment());
		values.put(KEY_SAVED_AMOUNT, wish.getSavedAmount());
		values.put(KEY_UPDATED_AT, getDateTime());
		values.put(KEY_FLAG_ACTIVE, wish.getFlagActive());

		// updating row
		return db.update(TBL_WISH, values, KEY_ID + " = ?",
				new String[] { String.valueOf(wish.getId()) });
	}

	/*
	 * Deleting a wish
	 */
	public void deleteWish(int wish_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TBL_WISH, KEY_ID + " = ?",
				new String[] { String.valueOf(wish_id) });
	}

	// ------------------------ "transactions" table methods ----------------//

	/*
	 * Creating a transactions
	 */
	public int createTransaction(Transaction transaction) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT_NAME, transaction.getAccountName());
		values.put(KEY_USAGE, transaction.getUsage());
		values.put(KEY_AMOUNT, transaction.getAmount());
		values.put(KEY_CREATEDAT, getDateTime());

		// insert row
		long transaction_id = db.insert(TBL_TRANSACTION, null, values);

		return (int)transaction_id;
	}

	/*
	 * get single transaction
	 */
	public Transaction getTransaction(int transaction_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TBL_TRANSACTION + " WHERE "
				+ KEY_ID + " = " + transaction_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Transaction transaction = new Transaction();
		transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		transaction.setAccountName(c.getString(c.getColumnIndex(KEY_ACCOUNT_NAME)));
		transaction.setUsage(c.getString(c.getColumnIndex(KEY_USAGE)));
		transaction.setAmount(c.getInt(c.getColumnIndex(KEY_AMOUNT)));
		transaction.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATEDAT)));

		return transaction;
	}

	/**
	 * getting all transactions
	 * */
	public List<Transaction> getAllTransactions() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		String selectQuery = "SELECT  * FROM " + TBL_TRANSACTION;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Transaction transaction = new Transaction();
				transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				transaction.setAccountName(c.getString(c.getColumnIndex(KEY_ACCOUNT_NAME)));
				transaction.setUsage(c.getString(c.getColumnIndex(KEY_USAGE)));
				transaction.setAmount(c.getInt(c.getColumnIndex(KEY_AMOUNT)));
				transaction.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATEDAT)));

				// adding to transaction list
				transactions.add(transaction);
			} while (c.moveToNext());
		}

		return transactions;
	}

	/*
	 * getting transaction count
	 */
	public int getTransactionCount() {
		String countQuery = "SELECT  * FROM " + TBL_TRANSACTION;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a transaction
	 */
	public int updateTransaction(Transaction transaction) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT_NAME, transaction.getAccountName());
		values.put(KEY_USAGE, transaction.getUsage());
		values.put(KEY_AMOUNT, transaction.getAmount());
		values.put(KEY_CREATEDAT, getDateTime());

		// updating row
		return db.update(TBL_TRANSACTION, values, KEY_ID + " = ?",
				new String[] { String.valueOf(transaction.getId()) });
	}

	/*
	 * Deleting a transaction
	 */
	public void deleteTransaction(int transaction_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TBL_TRANSACTION, KEY_ID + " = ?",
				new String[] { String.valueOf(transaction_id) });
	}

	/**
	 * get datetime
	 * */
	public String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
