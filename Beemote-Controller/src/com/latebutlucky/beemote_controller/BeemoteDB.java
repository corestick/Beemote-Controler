package com.latebutlucky.beemote_controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
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

	final String serverUrl = "http://beemote-controller.appspot.com/beemoteservlet";	

	DBHelper dbHelper;

	public BeemoteDB(Context context) {
		dbHelper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void insert(ItemInfo info) {
		SQLiteDatabase db;

		db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(ColumnInfo.SCREEN_IDX, info.screenIdx);
		cv.put(ColumnInfo.BEEMOTE_IDX, info.beemoteIdx);
		cv.put(ColumnInfo.BEEMOTE_TYPE, info.beemoteType);
		cv.put(ColumnInfo.CHANNEL_NUMBER, info.channelNo);
		cv.put(ColumnInfo.APP_ID, info.appId);
		cv.put(ColumnInfo.APP_NAME, info.appName);
		cv.put(ColumnInfo.CONTENT_ID, info.contentId);
		cv.put(ColumnInfo.APP_IMG, info.appImg);
		cv.put(ColumnInfo.KEYWORD, info.keyWord);
		cv.put(ColumnInfo.FUNCTION_KEY, info.functionKey);

		db.insert(TABLE_BEEMOTE, null, cv);

		Log.e("RRR", "Insert");
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

		db = dbHelper.getWritableDatabase();

		String strWhere = ColumnInfo.SCREEN_IDX + " = " + info.screenIdx
				+ " AND " + ColumnInfo.BEEMOTE_IDX + " = " + info.beemoteIdx
				+ "; ";

		ContentValues cv = new ContentValues();
		cv.put(ColumnInfo.BEEMOTE_TYPE, info.beemoteType);
		cv.put(ColumnInfo.CHANNEL_NUMBER, info.channelNo);
		cv.put(ColumnInfo.APP_ID, info.appId);
		cv.put(ColumnInfo.APP_NAME, info.appName);
		cv.put(ColumnInfo.CONTENT_ID, info.contentId);
		cv.put(ColumnInfo.APP_IMG, info.appImg);
		cv.put(ColumnInfo.KEYWORD, info.keyWord);
		cv.put(ColumnInfo.FUNCTION_KEY, info.functionKey);

		db.update(TABLE_BEEMOTE, cv, strWhere, null);

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

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				ItemInfo info = new ItemInfo();

				info.screenIdx = cursor.getInt(cursor
						.getColumnIndex(ColumnInfo.SCREEN_IDX));
				info.beemoteIdx = cursor.getInt(cursor
						.getColumnIndex(ColumnInfo.BEEMOTE_IDX));
				info.beemoteType = cursor.getInt(cursor
						.getColumnIndex(ColumnInfo.BEEMOTE_TYPE));
				info.channelNo = cursor.getString(cursor
						.getColumnIndex(ColumnInfo.CHANNEL_NUMBER));
				info.appId = cursor.getString(cursor
						.getColumnIndex(ColumnInfo.APP_ID));
				info.appName = cursor.getString(cursor
						.getColumnIndex(ColumnInfo.APP_NAME));
				info.contentId = cursor.getString(cursor
						.getColumnIndex(ColumnInfo.CONTENT_ID));
				info.appImg = cursor.getBlob(cursor
						.getColumnIndex(ColumnInfo.APP_IMG));
				info.keyWord = cursor.getString(cursor
						.getColumnIndex(ColumnInfo.KEYWORD));
				info.functionKey = cursor.getString(cursor
						.getColumnIndex(ColumnInfo.FUNCTION_KEY));

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
					+ ColumnInfo.APP_ID + " TEXT " + ", " + ColumnInfo.APP_NAME
					+ " TEXT " + ", " + ColumnInfo.CONTENT_ID + " TEXT " + ", "
					+ ColumnInfo.APP_IMG + " BLOB " + "," + ColumnInfo.KEYWORD
					+ " TEXT " + ", " + ColumnInfo.FUNCTION_KEY + " TEXT "
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEEMOTE);
			onCreate(db);
		}
	}

	public void write_DB(int screenIdx) {
		SQLiteDatabase db;
		String sql;

		db = dbHelper.getWritableDatabase();

		sql = "DELETE FROM " + TABLE_BEEMOTE + " WHERE "
				+ ColumnInfo.SCREEN_IDX + " = " + screenIdx;

		Log.e("RRR", sql);
		db.execSQL(sql);

		final ContentValues cv = new ContentValues();

		String str = JSONfunctions.getJSONfromURL(serverUrl, String.valueOf(1));

		String[] jsonArr = getJSONString(str);

		try {
			for (int i = 0; i < jsonArr.length; i++) {
				if (jsonArr[i] == null) {
					break;
				}
				JSONObject json = new JSONObject(jsonArr[i]);
				cv.put(ColumnInfo.SCREEN_IDX, screenIdx);
				cv.put(ColumnInfo.BEEMOTE_IDX, json.getString("idx"));
				cv.put(ColumnInfo.BEEMOTE_TYPE, json.getString("type"));
				cv.put(ColumnInfo.CHANNEL_NUMBER, json.getString("channelNo"));
				cv.put(ColumnInfo.APP_ID, json.getString("appId"));
				cv.put(ColumnInfo.APP_NAME, json.getString("appName"));
				cv.put(ColumnInfo.CONTENT_ID, json.getString("contentId"));
				cv.put(ColumnInfo.KEYWORD, json.getString("keyWord"));
				cv.put(ColumnInfo.FUNCTION_KEY, json.getString("functionKey"));
				db.insert(TABLE_BEEMOTE, null, cv);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.close();
		dbHelper.close();
	}

	public String[] getJSONString(String str) {
		int bracketCnt = 0;
		int arrCnt = 0;
		int tmpCnt = 0;
		String[] strArr = new String[100];
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '{') {
				bracketCnt++;
			}
			if (str.charAt(i) == '}') {
				bracketCnt--;
				if (bracketCnt == 0) {
					strArr[arrCnt] = str.substring(tmpCnt, i + 1);
					arrCnt++;
					tmpCnt = i + 1;
				}
			}
		}
		return strArr;
	}

	public void get_DB(int screenIdx) {
		// 핸드폰 번호 읽어오기
		// TelephonyManager telManager =
		// (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		// phoneNum = telManager.getLine1Number();
		Log.e("DBIN", "dbin");
		SQLiteDatabase db;
		String sql;

		db = dbHelper.getReadableDatabase();
		sql = "SELECT * FROM " + TABLE_BEEMOTE + " WHERE "
				+ ColumnInfo.SCREEN_IDX + " = " + screenIdx;

		final Cursor c = db.rawQuery(sql, null);

		final int idxIndex = c.getColumnIndexOrThrow("idx");
		final int typeIndex = c.getColumnIndexOrThrow("type");
		final int channelNoIndex = c.getColumnIndexOrThrow("channelNo");
		final int appIdIndex = c.getColumnIndexOrThrow("appId");
		final int appNameIndex = c.getColumnIndexOrThrow("appName");
		final int contentIdIndex = c.getColumnIndexOrThrow("contentId");
		final int keyWordIndex = c.getColumnIndexOrThrow("keyWord");
		final int functionKeyIndex = c.getColumnIndexOrThrow("functionKey");

		while (c.moveToNext()) {

			String idx = c.getString(idxIndex);
			String type = c.getString(typeIndex);
			String channelNo = c.getString(channelNoIndex);
			String appId = c.getString(appIdIndex);
			String appName = c.getString(appNameIndex);
			String contentId = c.getString(contentIdIndex);
			String keyWord = c.getString(keyWordIndex);
			String functionKey = c.getString(functionKeyIndex);

			Map<String, Object> map = new HashMap<String, Object>();

			map.put("user", "1");

			map.put("idx", idx);
			map.put("type", type);
			map.put("channelNo", channelNo);
			map.put("appId", appId);
			map.put("appName", appName);
			map.put("contentId", contentId);
			map.put("keyWord", keyWord);
			map.put("functionKey", functionKey);

			JSONfunctions.postSONfromURL(serverUrl, map);
			Log.e("DBout", "dbout");
		}
		c.close();
	}
}
