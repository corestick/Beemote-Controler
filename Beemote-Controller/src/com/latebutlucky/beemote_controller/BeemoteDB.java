package com.latebutlucky.beemote_controller;

import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BeemoteDB {

	private static final String DATABASE_NAME = "beemote.db";

	private static final int DATABASE_VERSION = 4;

	private static final String TABLE_BEEMOTE = "beemote";

	DBHelper dbHelper;

	public BeemoteDB(Context context) {
		dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void insert(ItemInfo info) {
		SQLiteDatabase db;
		String sql;

		db = dbHelper.getWritableDatabase();
		sql = String.format(Locale.US, "INSERT INTO " + TABLE_BEEMOTE
				+ " VALUES (null, " + "%d" + ", " + "%d" + ", " + "%d"
				+ ", " + "%s" + ", " + "'%s'" + ", " + "'%s'" + ", " + "'%s'"
				+ ", " + "'%s'" + ", " + "'%s'" + ");", info.screenIdx,
				info.beemoteIdx, info.beemoteType, info.channelNo, info.appId,
				info.appName, info.contentId, info.keyWord, info.functionKey);
		
		Log.e("RRR", sql);
		db.execSQL(sql);
		
		db.close();
		dbHelper.close();
	}

	public void delete(ItemInfo info) {
		SQLiteDatabase db;
		String sql;

		db = dbHelper.getWritableDatabase();

		sql = "DELETE FROM " + TABLE_BEEMOTE + " WHERE "
				+ ColumnInfo.SCREEN_IDX + " = " + info.screenIdx + " AND "
				+ ColumnInfo.BEEMOTE_IDX + " = " + info.beemoteIdx;
		
		Log.e("RRR", sql);
		db.execSQL(sql);
		
		db.close();
		dbHelper.close();
	}
	
	public void update(ItemInfo info) {
		SQLiteDatabase db;
		String sql;
		
		db = dbHelper.getWritableDatabase();
		
		sql = "UPDATE " + TABLE_BEEMOTE + " SET " + ColumnInfo.BEEMOTE_TYPE + " = " + info.beemoteType + ", "
				+ ColumnInfo.CHANNEL_NUMBER + " = " + info.channelNo + ", "
				+ ColumnInfo.APP_ID + " = '" + info.appId + "', "
				+ ColumnInfo.APP_NAME + " = '" + info.appName + "', "
				+ ColumnInfo.CONTENT_ID + " = '" + info.contentId + "', "
				+ ColumnInfo.KEYWORD + " = '" + info.keyWord + "', "
				+ ColumnInfo.FUNCTION_KEY + " = '" + info.functionKey + "' "
				+ " WHERE " + ColumnInfo.SCREEN_IDX + " = " + info.screenIdx
				+ " AND " + ColumnInfo.BEEMOTE_IDX + " = " + info.beemoteIdx + "; ";
		
		Log.e("RRR", sql);
		db.execSQL(sql);
		
		db.close();
		dbHelper.close();
	}
	
	public Vector<ItemInfo> select() {
		SQLiteDatabase db;
		String sql;
		Vector<ItemInfo> itemList = new Vector<ItemInfo>();
		
		db = dbHelper.getReadableDatabase();
		sql = "SELECT * FROM " + TABLE_BEEMOTE;
		
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				ItemInfo info = new ItemInfo();
				
				info.screenIdx = cursor.getInt(cursor.getColumnIndex(ColumnInfo.SCREEN_IDX));
				info.beemoteIdx = cursor.getInt(cursor.getColumnIndex(ColumnInfo.BEEMOTE_IDX));
				info.beemoteType = cursor.getInt(cursor.getColumnIndex(ColumnInfo.BEEMOTE_TYPE));
				info.channelNo = cursor.getString(cursor.getColumnIndex(ColumnInfo.CHANNEL_NUMBER));
				info.appId = cursor.getString(cursor.getColumnIndex(ColumnInfo.APP_ID));
				info.appName = cursor.getString(cursor.getColumnIndex(ColumnInfo.APP_NAME));
				info.contentId = cursor.getString(cursor.getColumnIndex(ColumnInfo.CONTENT_ID));
				info.keyWord = cursor.getString(cursor.getColumnIndex(ColumnInfo.KEYWORD));
				info.functionKey = cursor.getString(cursor.getColumnIndex(ColumnInfo.FUNCTION_KEY));
				
				itemList.add(info);
			}
		}
		
		cursor.close();
		db.close();
		dbHelper.close();
		
		return itemList;
	}
	
	

	public class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + TABLE_BEEMOTE + "(" + ColumnInfo.ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ColumnInfo.SCREEN_IDX + " INTEGER " + ", "
					+ ColumnInfo.BEEMOTE_IDX + " INTEGER " + ", "
					+ ColumnInfo.BEEMOTE_TYPE + " INTEGER " + ", "
					+ ColumnInfo.CHANNEL_NUMBER + " TEXT " + ", "
					+ ColumnInfo.APP_ID + " TEXT " + ", "
					+ ColumnInfo.APP_NAME + " TEXT " + ", "
					+ ColumnInfo.CONTENT_ID + " TEXT " + ", "
					+ ColumnInfo.KEYWORD + " TEXT " + ", "
					+ ColumnInfo.FUNCTION_KEY + " TEXT " + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEEMOTE);
			onCreate(db);
		}
	}
}
