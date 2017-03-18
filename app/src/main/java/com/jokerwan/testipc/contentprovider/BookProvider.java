package com.jokerwan.testipc.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ${JokerWan} on 2017/3/18.
 * WeChat：wjc398556712
 * Function：
 */

public class BookProvider extends ContentProvider {

    private static final String TAG = BookProvider.class.getSimpleName();
    public static final String AUTHORITY = "com.jokerwan.testipc.book.contentprovider";

    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");

    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;
    private static final UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {//关联Uri 和 Uri_Code
        sUrimatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUrimatcher.addURI(AUTHORITY,"user",USER_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        Log.d(TAG,"onCreate, current thread:" + Thread.currentThread().getName());
        mContext = getContext();

        //初始化数据库（这里方便延时，实际使用中不推荐在主线程进行耗时的数据库的操作）
        initProviderData();

        return true;
    }

    private void initProviderData() {
        mDatabase = new DbOpenHelper(mContext).getReadableDatabase();
        //清空表
        mDatabase.execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME);
        mDatabase.execSQL("delete from " + DbOpenHelper.USER_TABLE_NAME);

        mDatabase.execSQL("insert into book values(3,'Android');");
        mDatabase.execSQL("insert into book values(4,'IOS');");
        mDatabase.execSQL("insert into book values(5,'Html5');");
        mDatabase.execSQL("insert into user values(1,'jake',1);");
        mDatabase.execSQL("insert into user values(2,'jasmine',0);");

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String table = getTableName(uri);
        if(table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        mDatabase.insert(table,null,contentValues);

        //insert delete update 需要通过ContentResolver的notifyhange方法
        // 来通知外界当前ContentProvider中的数据已经发生改变
        mContext.getContentResolver().notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        String table = getTableName(uri);
        if(table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        int count = mDatabase.delete(table,s,strings);
        if(count > 0 ) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        String table = getTableName(uri);
        if(table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int row = mDatabase.update(table,contentValues,s,strings);
        if(row > 0 ) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return row;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOder) {
        Log.d(TAG,"query, current thread:" + Thread.currentThread().getName());
        String table = getTableName(uri);
        if(table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return mDatabase.query(table,projection,selection,selectionArgs,null,null,sortOder,null);
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUrimatcher.match(uri)) {
            case BOOK_URI_CODE :
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE :
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
        }
        return tableName;
    }

}
