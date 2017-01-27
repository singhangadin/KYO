package as.knowyouropinion;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**<p>
 * Created by Angad on 11/01/2017.
 * </p>
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder>
{   private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private String profile;
    private String email;
    private Context context;

    public DrawerAdapter(String Titles[], int Icons[], String Name, String Email, String Profile,Context context)
    {   this.mNavTitles = Titles;
        this.mIcons = Icons;
        this.name = Name;
        this.email = Email;
        this.profile = Profile;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
        {   View v = LayoutInflater.from(context).inflate(R.layout.drawer_list_item,parent,false);
            return new ViewHolder(v,viewType);
        }
        else if (viewType == TYPE_HEADER)
        {   View v = LayoutInflater.from(context).inflate(R.layout.header,parent,false);
            return new ViewHolder(v,viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder.Holderid ==1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position -1]);
        }
        else{
            Glide.with(context).load(profile).into(holder.profile);
            holder.name.setText(name);
            holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {   int Holderid;
        View itemView;

        ImageView imageView, profile;
        AppCompatTextView name, email, textView;

        ViewHolder(View itemView,int ViewType) {
            super(itemView);
            this.itemView=itemView;
            if(ViewType == TYPE_ITEM) {
                Typeface medium = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Roboto-Medium.ttf");
                textView = (AppCompatTextView) itemView.findViewById(R.id.rowText);
                textView.setTypeface(medium);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 1;
            }
            else{
                Typeface medium = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Roboto-Medium.ttf");
                Typeface regular = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Roboto-Regular.ttf");
                name = (AppCompatTextView) itemView.findViewById(R.id.name);
                name.setTypeface(medium);
                email = (AppCompatTextView) itemView.findViewById(R.id.email);
                email.setTypeface(regular);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                Holderid = 0;
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }
}