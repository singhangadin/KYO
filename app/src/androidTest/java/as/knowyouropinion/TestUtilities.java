package as.knowyouropinion;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import as.knowyouropinion.data.QuestionContract;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * <p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class TestUtilities {
    private static String LOG_TAG = ContentProvidersTest.class.getName();

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    private static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            Log.e(LOG_TAG, columnName + ":" + entry.getValue().toString());
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createQuestionValues(long quesRowId) {
        ContentValues questionValues = new ContentValues();
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_QNO, quesRowId);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_QUES, "ABCD");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1, "A");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2, "B");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3, "C");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4, "D");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1V, 23);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2V, 45);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3V, 12);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4V, 43);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE, 1);

        return questionValues;
    }

    static ContentValues createRandomValues() {
        ContentValues questionValues = new ContentValues();
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_QUES, "HFSFGSSGFSG");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1, "A");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2, "B");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3, "C");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4, "D");
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1V, 23);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2V, 45);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3V, 12);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4V, 43);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE, 1);

        return questionValues;
    }

}
