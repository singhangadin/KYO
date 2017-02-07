package as.knowyouropinion.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import as.knowyouropinion.R;
import as.knowyouropinion.data.QuestionContract;
import de.hdodenhof.circleimageview.CircleImageView;

/**<p>
 * Created by Angad on 28/1/17.
 * </p>
 */

public class HistoryCursorAdapter extends CursorRecyclerViewAdapter<HistoryCursorAdapter.ViewHolder> {
    private Context context;

    public HistoryCursorAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        holder.imageView.setImageResource(R.mipmap.ic_launcher);
        holder.imageView.setContentDescription(cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_QUES)));
        String text = context.getString(R.string.label_qno) + 
                cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_QNO));
        holder.quesNo.setText(text);
        holder.question.setText(cursor.getString(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_QUES)));
        int opt = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CHOICE));
        int A = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS1V));
        int B = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS2V));
        int C = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS3V));
        int D = cursor.getInt(cursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_ANS4V));
        int match = 0;
        switch(opt)
        {   case 1:   match = A;
                        break;

            case 2:   match = B;
                        break;

            case 3:   match = C;
                        break;

            case 4:   match = D;
                        break;

        }
        int total = A + B + C + D;
        text = ((match*100)/total)+context.getString(R.string.label_ppl_think);
        holder.peeps.setText(text);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {   View itemView;
        CircleImageView imageView;
        AppCompatTextView question, peeps, quesNo;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            Typeface medium = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Roboto-Medium.ttf");
            quesNo = (AppCompatTextView) itemView.findViewById(R.id.questionNo);
            question = (AppCompatTextView) itemView.findViewById(R.id.question);
            peeps = (AppCompatTextView) itemView.findViewById(R.id.peeps);
            quesNo.setTypeface(medium);
            question.setTypeface(medium);
            peeps.setTypeface(medium);
            imageView = (CircleImageView) itemView.findViewById(R.id.rowIcon);
        }
    }
}
