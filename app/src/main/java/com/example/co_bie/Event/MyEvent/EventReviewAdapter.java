package com.example.co_bie.Event.MyEvent;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.Event.Event;
import com.example.co_bie.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventReviewAdapter extends RecyclerView.Adapter<EventReviewAdapter.RecyclerAdapterViewHolder>{

    private Context context;
    private Event event;
    //private List<Review> reviewList;

    public EventReviewAdapter(Context context, Event event){
        this.context = context;
        this.event = event;
        if(event.getReviews() == null)
            event.setReviews(new ArrayList<>());
    }

    @NonNull
    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View template_event_review = LayoutInflater.from(context).inflate(R.layout.template_event_review, parent, false);
        RecyclerAdapterViewHolder recyclerAdapterViewHolder = new RecyclerAdapterViewHolder(template_event_review);
        return recyclerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterViewHolder holder, int position) {
        Review review = event.getReviews().get(position);
        holder.fillData(review);
    }

    @Override
    public int getItemCount() {
        return event.getReviews().size();
    }

    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title, tv_description, tv_type, tv_reviewer_name;
        private ImageView reviewer_pic;
        private RatingBar ratingBar;
        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_review_title);
            tv_description = itemView.findViewById(R.id.tv_review_description);
            ratingBar = itemView.findViewById(R.id.tv_rating);
            tv_type = itemView.findViewById(R.id.tv_review_type);
            tv_reviewer_name = itemView.findViewById(R.id.reviewer_name);
            reviewer_pic =  itemView.findViewById(R.id.reviewer_pic);
        }

        public void fillData(Review review) {
            tv_title.setText(review.getTitle());
            tv_description.setText(review.getDescription());
            ratingBar.setRating(review.getRating());
            tv_type.setText(review.getType());
            tv_reviewer_name.setText(review.getWriter().getFull_name());
            if (!review.getWriter().getProfile_img().equals(""))
                Picasso.get().load(review.getWriter().getProfile_img()).into(reviewer_pic);
            else if (review.getWriter().getGender().equals("Male"))
                reviewer_pic.setImageResource(R.drawable.ic_boy);
            else reviewer_pic.setImageResource(R.drawable.ic_girl);
        }
    }
}


