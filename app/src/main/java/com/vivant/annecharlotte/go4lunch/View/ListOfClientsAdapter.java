package com.vivant.annecharlotte.go4lunch.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.vivant.annecharlotte.go4lunch.NeSertPlusARienJeCrois.ClientsToday;
import com.vivant.annecharlotte.go4lunch.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Anne-Charlotte Vivant on 18/02/2019.
 */
public class ListOfClientsAdapter extends FirestoreRecyclerAdapter<ClientsToday, com.vivant.annecharlotte.go4lunch.View.ListOfClientsAdapter.ListOfClientsHolder> {

    private static final String TAG = "ListOfClientsAdapter";
        String text;
        private RequestManager glide;
        Context context;

        public ListOfClientsAdapter(@NonNull FirestoreRecyclerOptions<ClientsToday> options, RequestManager glide) {
            super(options);
            this.glide = glide;
            Log.d(TAG, "ListOfClientsAdapter: constructeur");
        }

        @Override
        protected void onBindViewHolder(@NonNull com.vivant.annecharlotte.go4lunch.View.ListOfClientsAdapter.ListOfClientsHolder listOfClientsHolder, int i, @NonNull ClientsToday clientsToday) {

            if (clientsToday.getUsername()!= null && !clientsToday.getUsername().isEmpty()) {
                Log.d(TAG, "onBindViewHolder: " + clientsToday.getUsername());
                text = clientsToday.getUrlPicture() + context.getString(R.string.isjoining);
                listOfClientsHolder.textUser.setTypeface(null, Typeface.NORMAL);
                listOfClientsHolder.textUser.setTextColor(context.getResources().getColor(R.color.colorMyBlack));
            }
            listOfClientsHolder.textUser.setText(text);

            // Images
           /* if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()){
                String urlPhoto = user.getUrlPicture();
                glide.load(urlPhoto)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userHolder.imageUser);
            } else {
                userHolder.imageUser.setImageResource(R.drawable.baseline_people_24);
            }*/
        }

        @NonNull
        @Override
        public com.vivant.annecharlotte.go4lunch.View.ListOfClientsAdapter.ListOfClientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates, parent, false);
            context = parent.getContext();
            return new com.vivant.annecharlotte.go4lunch.View.ListOfClientsAdapter.ListOfClientsHolder(view);
        }

        class ListOfClientsHolder extends RecyclerView.ViewHolder {

            TextView textUser;
            ImageView imageUser;

            public ListOfClientsHolder(@NonNull View itemView) {
                super(itemView);
                textUser = itemView.findViewById(R.id.workmates_TextView);
                imageUser = itemView.findViewById(R.id.workmates_ImageView);
                Log.d(TAG, "ListOfClientsHolder");
            }
        }
    }