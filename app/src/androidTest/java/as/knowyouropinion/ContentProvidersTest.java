package as.knowyouropinion;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import as.knowyouropinion.data.QuestionContract;
import as.knowyouropinion.data.QuestionDBHelper;
import as.knowyouropinion.data.QuestionProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

@RunWith(AndroidJUnit4.class)
public class ContentProvidersTest {
    private Context mContext=InstrumentationRegistry.getTargetContext();

    @Before
    public void performCleanUp()
    {   deleteAllRecords();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assert cursor!=null;
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void deleteARecordFromProvider(int quesNo) {
        mContext.getContentResolver().delete(
                QuestionContract.QuestionEntry.buildQuestionNo(Integer.toString(quesNo)),
                null,
                null
        );
    }

    @Test
    public void checkProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), QuestionProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals(   "Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + QuestionContract.CONTENT_AUTHORITY,
                            providerInfo.authority, QuestionContract.CONTENT_AUTHORITY);
        }
        catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    @Test
    public void typeTest() {
        String type = mContext.getContentResolver().getType(QuestionContract.QuestionEntry.CONTENT_URI);
        assertEquals("Error: the QuestionEntry CONTENT_URI should return QuestionEntry.CONTENT_TYPE",
                QuestionContract.QuestionEntry.CONTENT_TYPE, type);

        String testNo = "56";
        type = mContext.getContentResolver().getType(QuestionContract.QuestionEntry.buildQuestionNo(testNo));
        assertEquals("Error: the QuestionEntry CONTENT_URI with location should return QuestionEntry.CONTENT_ITEM_TYPE",
                QuestionContract.QuestionEntry.CONTENT_ITEM_TYPE, type);
    }

    @Test
    public void basicQuestionQueries() {
        QuestionDBHelper dbHelper = new QuestionDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues questionValues = TestUtilities.createQuestionValues(0);

        long questionRowId = db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", questionRowId != -1);

        db.close();

        Cursor questionCursor = mContext.getContentResolver().query(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicWeatherQuery", questionCursor, questionValues);
    }

    @Test
    public void singleUpdateTest() {
        QuestionDBHelper dbHelper = new QuestionDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues questionValues = TestUtilities.createQuestionValues(0);

        long questionRowId = db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", questionRowId != -1);

        db.close();
        ContentValues question1Values = TestUtilities.createRandomValues();

        mContext.getContentResolver().update(
                QuestionContract.QuestionEntry.buildQuestionNo("0"),
                question1Values,
                null,
                null);

        Cursor questionCursor = mContext.getContentResolver().query(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicWeatherQuery", questionCursor, question1Values);
    }

    @Test
    public void singleRecordDeleteTest(){
        QuestionDBHelper dbHelper = new QuestionDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues questionValues = TestUtilities.createQuestionValues(0);
        db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);

        ContentValues questionValues1 = TestUtilities.createQuestionValues(1);
        db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues1);

        ContentValues questionValues2 = TestUtilities.createQuestionValues(2);
        db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues2);
        db.close();

        deleteARecordFromProvider(0);

        Cursor cursor = mContext.getContentResolver().query(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assert cursor!=null;
        assertEquals("Error: Records not deleted from Weather table during delete", 2, cursor.getCount());
        cursor.close();
    }
}
