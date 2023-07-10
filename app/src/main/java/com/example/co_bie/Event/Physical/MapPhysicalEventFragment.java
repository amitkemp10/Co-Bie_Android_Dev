package com.example.co_bie.Event.Physical;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.JoinEventDialogFragment;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.example.co_bie.Event.SharedViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapPhysicalEventFragment extends Fragment {

    private String selected_location_name = "", name, date, time, duration, description, hobbyName;
    private LatLng selected_location;
    private Button btn_select;
    private Button openOptionsBtn;
    private Button option1Btn;
    private Button option2Btn;
    private Button menuBtn;
    private CardView cv_map;
    private GoogleMap nMap;
    private GetEventSelectedLocationListener getEventSelectedLocationListener;
    private SharedViewModel sharedViewModel;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private List<LatLng> mLocationList;
    private List<String> hobbyNameForImage;
    private int i;
    BitmapDescriptor markerIcon;
    HashMap<Marker, PhysicalEvent> marker2EventMap;
    private FireBaseQueries fireBaseQueries;
    String currUserUUID;
    private User currUser;
    private boolean isPlaceSelected = false;

    @Override
    public void onResume() {
        super.onResume();
        if (nMap != null && !isPlaceSelected) {
            nMap.clear();
            getAllPhysicalEventLocation();
        }
        isPlaceSelected = false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.getEventSelectedLocationListener = (GetEventSelectedLocationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'GetEventSelectedLocationListener'");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        database = FirebaseDatabase.getInstance();
        fireBaseQueries = new FireBaseQueries();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        marker2EventMap = new HashMap<>();
        Bundle args = getArguments();
        handleArgsMarkersOnMap(args, view);
        currUserUUID = fireBaseQueries.getCurrentUserUUID();
        sharedViewModel.getDataFields().observe(getViewLifecycleOwner(), new Observer<Bundle>() {
            @Override
            public void onChanged(Bundle bundle) {
                // Handle the data received from the NewFragment
                name = bundle.getString("Name");
                date = bundle.getString("Date");
                time = bundle.getString("Time");
                duration = bundle.getString("Duration");
                description = bundle.getString("Description");
                hobbyName = bundle.getString("HobbyName");
            }
        });
    }

    private void handleArgsMarkersOnMap(Bundle args, View view) {
        String value;
        if (args != null) {
            value = args.getString("is_show_select_button");
            if (value.equals("YES")) return;
        } else {
            fireBaseQueries.getUserByUUID(fireBaseQueries.getCurrentUserUUID(), new FireBaseQueries.getUserByUUIDCallback() {
                @Override
                public void onCallback(User user) {
                    currUser = user;
                    getAllPhysicalEventLocation();
                }
            });
        }
    }

    private void sendResultBack(String selected_location_name, LatLng selected_location) {
        Bundle data = new Bundle();
        data.putString("selected_location_name", selected_location_name);
        data.putParcelable("selected_location", selected_location);
        data.putString("Name", name);
        data.putString("Date", date);
        data.putString("Time", time);
        data.putString("HobbyName", hobbyName);
        data.putString("Duration", duration);
        data.putString("Description", description);
        sharedViewModel.setLocationBundle(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_physical_event, container, false);
        cv_map = view.findViewById(R.id.cv_map);
        btn_select = view.findViewById(R.id.btn_select_map_location);
        openOptionsBtn = view.findViewById(R.id.open_options_btn);
        option1Btn = view.findViewById(R.id.btn_option_1);
        option2Btn = view.findViewById(R.id.btn_option_2);
        menuBtn = view.findViewById(R.id.btn_drawer_menu);
        mLocationList = new ArrayList<>();
        hobbyNameForImage = new ArrayList<>();

        openOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOptionsClick();
            }
        });

        option1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventSelectedLocationListener.onClickLocationSelect(2);
            }
        });

        option2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventSelectedLocationListener.onClickLocationSelect(3);
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventSelectedLocationListener.onClickLocationSelect(4);
            }
        });

        handleArgs();

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_location_name.equals("")) {
                    CustomToast.makeText(getContext(), "Please select location").show();
                    return;
                }
                sendResultBack(selected_location_name, selected_location);
                getEventSelectedLocationListener.onClickLocationSelect(1);
            }
        });

        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(getContext());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(new LatLng(29.5065, 34.2672), new LatLng(33.3152, 35.8921)));

        autocompleteFragment.setCountries("IL");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                isPlaceSelected = true;
                LatLng location = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                nMap.addMarker(markerOptions);
                nMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

                selected_location = location;
                String[] address_city = getAddressCity();
                selected_location_name = place.getName();
                for (String i : address_city) {
                    if (!selected_location_name.contains(i)) selected_location_name += " ," + i;
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("err", "An error occurred: " + status);
            }
        });


        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                nMap = googleMap;
                nMap.getUiSettings().setMapToolbarEnabled(false);
                LatLng israel = new LatLng(31.0461, 34.8516);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(israel, 8));

                LatLngBounds israelBounds = new LatLngBounds(new LatLng(29.5065, 34.2672), new LatLng(33.3152, 35.8921));
                googleMap.setLatLngBoundsForCameraTarget(israelBounds);
                googleMap.setMinZoomPreference(8);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        nMap.addMarker(markerOptions);
                        nMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private String[] getAddressCity() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(selected_location.latitude, selected_location.longitude, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] arr = new String[2];
        arr[0] = addresses.get(0).getAddressLine(0);
        arr[1] = addresses.get(0).getLocality();
        return arr;
    }

    private void getAllPhysicalEventLocation() {
        refDatabase = database.getReference("events").child("Physical_Events");
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PhysicalEvent pe = ds.getValue(PhysicalEvent.class);
                    if (pe.isPassed()) continue;
                    LatLng latLng = new LatLng(pe.getLocation().getLatitude(), pe.getLocation().getLongitude());
                    markerIcon = makeAnIcon(markerIcon, pe.getHobby().getHobby_name(), isUserContainsHobby(pe.getHobby()));
                    Marker marker = nMap.addMarker(new MarkerOptions().position(latLng).icon(markerIcon));
                    marker2EventMap.put(marker, pe);
                }

                nMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        handleEventMarkerClick(marker);
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean isUserContainsHobby(Hobby hobby) {
        if (currUser == null) return false;
        for (Hobby hobbyFromList : currUser.getHobbiesList()) {
            if (hobbyFromList.getHobby_name().equals(hobby.getHobby_name())) {
                return true;
            }
        }
        return false;
    }

    private BitmapDescriptor makeAnIcon(BitmapDescriptor markerIcon, String hobbyNameForImage, boolean isUserHobby) {
        hobbyNameForImage = hobbyNameForImage.replace('-', '_');
        if (getActivity() != null) {
            View markerLayout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_marker_layout, null);
            ImageView markerIcon1 = markerLayout.findViewById(R.id.marker_image_view_outside);
            if (isUserHobby) markerIcon1.setImageResource(R.drawable.ic_location_map2);
            else markerIcon1.setImageResource(R.drawable.ic_location_map);

            ImageView markerIcon2 = markerLayout.findViewById(R.id.marker_image_view_inside);
            int resourceId = getResources().getIdentifier("ic_" + hobbyNameForImage.toLowerCase(), "drawable", getContext().getPackageName());
            markerIcon2.setImageResource(resourceId);

            markerIcon = BitmapDescriptorFactory.fromBitmap(viewToBitmap(markerLayout));
        }
        return markerIcon;
    }

    private void handleEventMarkerClick(Marker marker) {
        if (!marker2EventMap.containsKey(marker)) return;
        PhysicalEvent pe = marker2EventMap.get(marker);
        if (pe.getManagerUid().equals(currUserUUID)) {
            CustomToast.makeText(getContext(), "You manage this event").show();
            return;
        }
        JoinEventDialogFragment dialogFragment = new JoinEventDialogFragment(pe, Utils.EventType.PHYSICAL);
        dialogFragment.show(getChildFragmentManager(), "");
    }

    public static Bitmap viewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void handleArgs() {
        String value = null;
        Bundle args = getArguments();
        if (args != null) {
            value = args.getString("is_show_select_button");
            if (value.equals("YES")) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cv_map.getLayoutParams();
                params.topMargin = dpToPx(0);
                cv_map.setRadius(0);
                cv_map.setLayoutParams(params);
                btn_select.setVisibility(View.VISIBLE);
                openOptionsBtn.setVisibility(View.GONE);
                menuBtn.setVisibility(View.GONE);
            }
        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void handleOptionsClick() {
        if (option1Btn.getVisibility() == View.GONE) {
            // Slide up the buttons
            ObjectAnimator openMenuBtnAnimator = ObjectAnimator.ofFloat(openOptionsBtn, "rotation", 0, 45);
            openMenuBtnAnimator.setDuration(500);
            ObjectAnimator option1Animator = ObjectAnimator.ofFloat(option1Btn, "translationY", 0f, -350f);
            option1Animator.setDuration(500);
            option1Animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    option1Btn.setVisibility(View.VISIBLE);
                }
            });
            openMenuBtnAnimator.start();
            option1Animator.start();

            ObjectAnimator option2Animator = ObjectAnimator.ofFloat(option2Btn, "translationY", 0f, -180f);
            option2Animator.setDuration(500);
            option2Animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    option2Btn.setVisibility(View.VISIBLE);
                }
            });
            option2Animator.start();
        } else {
            // Slide down the buttons
            ObjectAnimator openMenuBtnAnimator = ObjectAnimator.ofFloat(openOptionsBtn, "rotation", 45, 0);
            openMenuBtnAnimator.setDuration(500);
            ObjectAnimator option1Animator = ObjectAnimator.ofFloat(option1Btn, "translationY", -350f, 0f);
            option1Animator.setDuration(500);
            option1Animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    option1Btn.setVisibility(View.GONE);
                }
            });
            option1Animator.start();
            openMenuBtnAnimator.start();

            ObjectAnimator option2Animator = ObjectAnimator.ofFloat(option2Btn, "translationY", -180f, 0f);
            option2Animator.setDuration(500);
            option2Animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    option2Btn.setVisibility(View.GONE);
                }
            });
            option2Animator.start();
        }
    }

    public interface GetEventSelectedLocationListener {
        public void onClickLocationSelect(int option);
    }
}