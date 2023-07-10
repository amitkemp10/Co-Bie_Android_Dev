package com.example.co_bie.Hobby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.R;

import java.util.List;

public class HobbiesAdapter extends RecyclerView.Adapter<HobbiesAdapter.RecyclerAdapterViewHolder> {

    Context mContext;
    List<Hobby> mHobbiesDataList;
    private int selected_index = RecyclerView.NO_POSITION;
    private int arrStateSelected[];
    private SelectedItemListener selectedItemListener;

    public HobbiesAdapter(Context mContext, List<Hobby> mHobbiesDataList) {
        this.mContext = mContext;
        this.mHobbiesDataList = mHobbiesDataList;
        try {
            this.selectedItemListener = (SelectedItemListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " +
                    mContext.getClass().getName() +
                    " must implements the interface 'SelectedItemListener'");
        }
        arrStateSelected = new int[8];
    }

    @NonNull
    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View template_hobbie = LayoutInflater.from(mContext).inflate(R.layout.template_hobby, parent, false);
        RecyclerAdapterViewHolder recyclerAdapterViewHolder = new RecyclerAdapterViewHolder(template_hobbie);
        return recyclerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterViewHolder holder, int position) {
        Hobby hobby = mHobbiesDataList.get(position);
        if (arrStateSelected[position] == 1)
            holder.itemView.setSelected(selected_index == position);
        else
            holder.itemView.setSelected(false);
        holder.fillData(hobby, mContext);
    }

    @Override
    public int getItemCount() {
        return mHobbiesDataList.size();
    }


    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_hobbie_name;
        private ImageView img_hobbie;

        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_hobbie_name = itemView.findViewById(R.id.tv_hobbies_name);
            img_hobbie = itemView.findViewById(R.id.img_view_ic_hobbies);
        }

        public void fillData(final Hobby hobby, Context mContext) {
            tv_hobbie_name.setText(hobby.getHobby_name());
            img_hobbie.setImageResource(hobby.getImg_hobby());
        }

        @Override
        public void onClick(View view) {
            selected_index = getLayoutPosition();
            arrStateSelected[selected_index] = 1 - arrStateSelected[selected_index];
            notifyItemChanged(selected_index);
            if (arrStateSelected[selected_index] == 1)
                selectedItemListener.onSelectedItem(mHobbiesDataList.get(selected_index), "YES");
            else
                selectedItemListener.onSelectedItem(mHobbiesDataList.get(selected_index), "NO");
        }
    }

    public interface SelectedItemListener {
        public void onSelectedItem(Hobby selected_hobbie, String state);
    }
}
