package com.vivant.annecharlotte.go4lunch.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vivant.annecharlotte.go4lunch.models.User;
import com.vivant.annecharlotte.go4lunch.R;
import com.vivant.annecharlotte.go4lunch.utils.DateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 13/02/2019.
 */
public class ListOfWorkmatesAdapter extends FirestoreRecyclerAdapter<User, ListOfWorkmatesAdapter.UserHolder> {

    private static final String TAG = "ListOfWorkmatesAdapter";
    String text;
    private RequestManager glide;
    Context context;
    private OnItemClickListener mListener;
    private String today;

    public ListOfWorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder userHolder, int i, @NonNull User user) {
        Log.d(TAG, "onBindViewHolder: username " + user.getUsername());
        DateFormat forToday = new DateFormat();
        today = forToday.getTodayDate();

        String registeredDate = user.getRestoDate();
        Log.d(TAG, "onBindViewHolder: registeredDate "+ registeredDate);
        Log.d(TAG, "onBindViewHolder: email "+ user.getUserEmail());
        Log.d(TAG, "onBindViewHolder: resto " + user.getRestoTodayName());
        Log.d(TAG, "onBindViewHolder: today " + today);

        // Valeurs par défaut
        Log.d(TAG, "onBindViewHolder: username " + user.getUsername());
        Log.d(TAG, "onBindViewHolder: pas encore décidé");
        text = user.getUsername() + context.getString(R.string.not_decided);
        userHolder.textUser.setTypeface(null, Typeface.ITALIC);
        userHolder.textUser.setTextColor(context.getResources().getColor(R.color.colorMyGrey));

        // Spécifications si un restaurant a été choisi pour aujourd'hui
        if (user.getRestoTodayName()!= null && !user.getRestoTodayName().isEmpty()) {
            if(registeredDate.equals(today)){
                Log.d(TAG, "onBindViewHolder: username " + user.getUsername());
                Log.d(TAG, "onBindViewHolder: userResto " + user.getRestoTodayName());
                text = user.getUsername() + context.getString(R.string.decided) + user.getRestoTodayName();
                userHolder.textUser.setTypeface(null, Typeface.NORMAL);
                userHolder.textUser.setTextColor(context.getResources().getColor(R.color.colorMyBlack));
            }
        }
        userHolder.textUser.setText(text);

        // Images
        if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()){
            String urlPhoto = user.getUrlPicture();
            glide.load(urlPhoto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(userHolder.imageUser);
        } else {
            userHolder.imageUser.setImageResource(R.drawable.baseline_people_24);
        }
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates, parent, false);
        context = parent.getContext();
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder {

        TextView textUser;
        ImageView imageUser;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            textUser = itemView.findViewById(R.id.workmates_TextView);
            imageUser = itemView.findViewById(R.id.workmates_ImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                   if (mListener!=null){
                       mListener.onItemClick(getSnapshots().getSnapshot(position), position);
                   }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;

    }

}