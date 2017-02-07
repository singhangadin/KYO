package as.knowyouropinion.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import as.knowyouropinion.R;
import as.knowyouropinion.ResultActivity;
import as.knowyouropinion.data.QuestionContract;

/**<p>
 * Created by Angad on 28/1/17.
 * </p>
 */

public class KyoWidgetRemoteViewService extends RemoteViewsService {
    private final int INDEX_ID = 0;
    private final int INDEX_QUES_NO = 1;
    private final int INDEX_QUES = 2;
    private final int INDEX_ANS1V = 7;
    private final int INDEX_ANS2V = 8;
    private final int INDEX_ANS3V = 9;
    private final int INDEX_ANS4V = 10;
    private final int INDEX_CHOICE = 11;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        QuestionContract.QuestionEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        QuestionContract.QuestionEntry.COLUMN_QNO+" asc");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                views.setTextViewText(R.id.questionNo,getResources().getString(R.string.label_qno)+ data.getString(INDEX_QUES_NO));
                views.setTextViewText(R.id.question, data.getString(INDEX_QUES));

                int opt = data.getInt(INDEX_CHOICE);
                int A = data.getInt(INDEX_ANS1V);
                int B = data.getInt(INDEX_ANS2V);
                int C = data.getInt(INDEX_ANS3V);
                int D = data.getInt(INDEX_ANS4V);
                int match = 0;
                switch(opt)
                {   case 1: match = A;
                            break;

                    case 2: match = B;
                            break;

                    case 3: match = C;
                            break;

                    case 4: match = D;
                            break;

                }
                int total = A + B + C + D;
                String text = ((match*100)/total)+getString(R.string.label_ppl_think);
                views.setTextViewText(R.id.peeps, text);

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                int qno = data.getInt(INDEX_QUES_NO);
                intent.putExtra("qno", qno);
                views.setOnClickFillInIntent(R.id.list_item, intent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}