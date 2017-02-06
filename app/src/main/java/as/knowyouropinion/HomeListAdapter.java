package as.knowyouropinion;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import as.knowyouropinion.model.HomeQuestionData;
import de.hdodenhof.circleimageview.CircleImageView;

/**<p>
 * Created by Angad on 11/01/2017.
 * </p>
 */

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder>
{   private static final int TYPE_ITEM = 1;

    private Context context;
    private ArrayList<HomeQuestionData> ListData;

    public HomeListAdapter(ArrayList<HomeQuestionData> ListData, Context context)
    {   this.ListData = ListData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.question_list_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        else{
//            Glide.with(context).load(profile).into(holder.profile);
//            holder.name.setText(name);
//            holder.email.setText(email);
//        }
        HomeQuestionData data = ListData.get(position);
        holder.quesNo.setText(context.getResources().getString(R.string.label_qno)+data.getQuesNo());
        holder.question.setText(data.getQuestion());
        String text=data.getPeeps()+context.getResources().getString(R.string.label_ppl_think);
        holder.peeps.setText(text);
        holder.imageView.setImageResource(R.mipmap.ic_launcher);
        Glide.with(context).load(data.getImgUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return ListData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
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

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }
}