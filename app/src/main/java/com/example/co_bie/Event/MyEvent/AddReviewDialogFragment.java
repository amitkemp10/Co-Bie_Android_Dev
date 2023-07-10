package com.example.co_bie.Event.MyEvent;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Event;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AddReviewDialogFragment extends DialogFragment {

    private Event event;
    private DatabaseReference refDatabase;
    private Spinner spinner_type;
    private EditText et_title;
    private EditText et_description;
    private RatingBar bar_eventRating;
    private Button btn_addReview;
    private FireBaseQueries fireBaseQueries;
    private EventReviewAdapter eventReviewAdapter;

    public AddReviewDialogFragment(Event event, DatabaseReference refDatabase, EventReviewAdapter eventReviewAdapter) {
        this.event = event;
        this.refDatabase = refDatabase;
        this.eventReviewAdapter = eventReviewAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_review, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fireBaseQueries = new FireBaseQueries();
        spinner_type = view.findViewById(R.id.add_review_type_spinner);
        et_title = view.findViewById(R.id.add_review_title);
        et_description = view.findViewById(R.id.add_review_description);
        bar_eventRating = view.findViewById(R.id.add_review_rating);
        btn_addReview = view.findViewById(R.id.add_review_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.review_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);

        btn_addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnClickAdd();
            }
        });

    }

    private void handleOnClickAdd() {

        fireBaseQueries.getUserByUUID(fireBaseQueries.getCurrentUserUUID(), new FireBaseQueries.getUserByUUIDCallback() {
            @Override
            public void onCallback(User user) {
                String type = spinner_type.getSelectedItem().toString();
                String title = et_title.getText().toString();
                String description = et_description.getText().toString();
                int rating = (int) bar_eventRating.getRating();
                Review review = new Review(type, title, description, rating, user);
                ArrayList<Review> newReviews = event.getReviews();
                if (newReviews == null) newReviews = new ArrayList<>();
                newReviews.add(review);
                event.setReviews(newReviews);
                refDatabase.setValue(event);
                CustomToast.makeText(getContext(), "Review Added").show();
                eventReviewAdapter.notifyDataSetChanged();
                dismiss();
            }
        });

    }


}
