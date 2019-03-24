package mode1719.student.ju.jade;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Event> mEvent = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<Event> event, Context context) {
        this.mEvent = event;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

            if(mEvent.get(i).getImageUrl() != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(mEvent.get(i).getImageUrl())
                        .into(viewHolder.image);
            }
            else{
                Glide.with(mContext)
                        .asBitmap()
                        .load(R.mipmap.ic_launcher)
                        .into(viewHolder.image);
            }

                viewHolder.eventTitle.setText(mEvent.get(i).getTitle());
                viewHolder.eventTime.setText(mEvent.get(i).getTime());

                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(mContext, DetailEventViewActivity.class);
                        intent.putExtra("value", 1);
                        intent.putExtra("listItem", mEvent.get(i));
                        intent.putExtra("date", mEvent.get(i).getDate().getTime());
                        mContext.startActivity(intent);
                    }
                });
    }


    @Override
    public int getItemCount() {
        return mEvent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView eventTitle;
        TextView eventTime;
        RelativeLayout parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventTime = itemView.findViewById(R.id.eventTime);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
