package mode1719.student.ju.jade;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Event> mEvent = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<Event> event, Context mContext) {
        this.mEvent = event;
        this.mContext = mContext;
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


        Glide.with(mContext)
                .asBitmap()
                .load(mEvent.get(i).getImageUrl())
                .into(viewHolder.image);

        viewHolder.imageName.setText(mEvent.get(i).getTitle());

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ShowEvent.class);
                intent.putExtra("position", i);
                intent.putExtra("value",1);
                intent.putExtra("image_url", mEvent.get(i).getImageUrl());
                intent.putExtra("title", mEvent.get(i).getTitle());
                intent.putExtra("time", mEvent.get(i).getTime());
                intent.putExtra("description", mEvent.get(i).getDescription());
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
        TextView imageName;
        RelativeLayout parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.imageName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
