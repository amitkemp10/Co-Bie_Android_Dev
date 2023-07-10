package com.example.co_bie.Event;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.co_bie.R;

public class ChooseEventTypeFragment extends Fragment {

    private Button btnPhysical;
    private Button btnVirtual;

    private ChooseEventTypeListener chooseEventTypeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.chooseEventTypeListener = (ChooseEventTypeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'ChooseEventTypeListener'");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewModelStore store = requireActivity().getViewModelStore();
        store.clear();
        return inflater.inflate(R.layout.fragment_choose_event_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPhysical = view.findViewById(R.id.btn_physical);
        btnVirtual = view.findViewById(R.id.btn_virtual);

        btnPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {chooseEventTypeListener.EventTypeSelected(Utils.EventType.PHYSICAL); }
        });
        btnVirtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {chooseEventTypeListener.EventTypeSelected(Utils.EventType.VIRTUAL); }
        });
    }

    public interface ChooseEventTypeListener {
        public void EventTypeSelected(Utils.EventType type);
    }
}