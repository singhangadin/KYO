package as.knowyouropinion.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class QuestionProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private QuestionDBHelper mOpenHelper;

    static final int QUESTION_WITH_QNO = 100;
    static final int QUESTION = 101;
    private static final SQLiteQueryBuilder sQuestionQueryBuilder;

    static{
        sQuestionQueryBuilder = new SQLiteQueryBuilder();
        sQuestionQueryBuilder.setTables(QuestionContract.QuestionEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuestionContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, QuestionContract.PATH_QUESTION, QUESTION);
        matcher.addURI(authority, QuestionContract.PATH_QUESTION + "/*", QUESTION_WITH_QNO);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new QuestionDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case QUESTION_WITH_QNO:
                return QuestionContract.QuestionEntry.CONTENT_ITEM_TYPE;
            case QUESTION:
                return QuestionContract.QuestionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case QUESTION_WITH_QNO: {
                retCursor = getQuestionFromQuestionNo(uri,projection,sortOrder);
                break;
            }
            case QUESTION: {
                           retCursor = mOpenHelper.getReadableDatabase().query(
                            QuestionContract.QuestionEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case QUESTION: {
                long _id = db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuestionContract.QuestionEntry.buildQuestionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case QUESTION:
                rowsDeleted = db.delete(
                        QuestionContract.QuestionEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case QUESTION_WITH_QNO:
                rowsDeleted = db.delete(
                        QuestionContract.QuestionEntry.TABLE_NAME,
                        QuestionContract.QuestionEntry.TABLE_NAME+"."+
                        QuestionContract.QuestionEntry.COLUMN_QNO+"=?",
                        new String[]{""+QuestionContract.QuestionEntry.getQuestionNoFromURI(uri)});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case QUESTION:
                rowsUpdated = db.update(
                        QuestionContract.QuestionEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case QUESTION_WITH_QNO:
                rowsUpdated = db.update(QuestionContract.QuestionEntry.TABLE_NAME,
                        values,
                        QuestionContract.QuestionEntry.TABLE_NAME+"."+
                        QuestionContract.QuestionEntry.COLUMN_QNO+"=?",
                        new String[]{""+QuestionContract.QuestionEntry.getQuestionNoFromURI(uri)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri,@NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case QUESTION:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getQuestionFromQuestionNo(Uri uri, String[] projection, String sortOrder) {
        int ques_no = QuestionContract.QuestionEntry.getQuestionNoFromURI(uri);
        return sQuestionQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                QuestionContract.QuestionEntry.TABLE_NAME+"."+
                QuestionContract.QuestionEntry.COLUMN_QNO+" = ? ",
                new String[]{Integer.toString(ques_no)},
                null,
                null,
                sortOrder
        );
    }
}
