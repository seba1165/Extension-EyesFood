package com.example.jonsmauricio.eyesfood.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Comment;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.api.model.User;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by JonásMauricio on 04-11-2017.
 */

public class CommentsAdapter extends ArrayAdapter<Comment> {

    final String baseFotoUsuario = EyesFoodApi.BASE_URL+"img/users/";
    private String session;
    private Retrofit mRestAdapter;
    private EyesFoodApi mEyesFoodApi;

    public CommentsAdapter(Context context, List<Comment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Crear conexión al servicio REST EyesFood
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        session = SessionPrefs.get(getContext()).getUserSession();

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.list_comments_item,
                    parent,
                    false);
        }

        // Referencias UI.
        final ImageView avatar = convertView.findViewById(R.id.ivCommentsAvatar);
        final TextView name = convertView.findViewById(R.id.tvCommentsName);
        final TextView comment = convertView.findViewById(R.id.tvCommentsComment);
        final TextView date = convertView.findViewById(R.id.tvCommentsDate);
        final RatingBar ratingBar = convertView.findViewById(R.id.rbCommentsRating);

        // Comentario actual.
        final Comment currentComment = getItem(position);

        // Setup.
        //Cargo avatar de usuario actual
        // TODO: 07-11-2017 VER COMO ARREGLAR ESTO
        /*if(currentComment.getUserPhoto().contains("facebook") || currentComment.getUserPhoto().contains("google")) {
            Picasso.with(getContext()).load(currentComment.getUserPhoto()).resize(800, 800).into(avatar);
        }*/
        Call<User> call = mEyesFoodApi.getUser(currentComment.getIdUsuario());
        call.enqueue(new Callback<User>() {
                         @Override
                         public void onResponse(Call<User> call, Response<User> response) {
                             if (!response.isSuccessful()) {
                                 // TODO: Procesar error de API
                                 Log.d("myTag", "hola"+response.errorBody().toString());
                                 return;
                             }
                             User user = response.body();
                             Picasso.with(getContext()).load(baseFotoUsuario + "default.png").resize(800, 800).into(avatar);
                             name.setText(user.getName() + " " + user.getSurName());
                             ratingBar.setRating(Float.parseFloat(user.getReputation()));
                             comment.setText(currentComment.getComment());
                             date.setText(currentComment.getDate());
                         }

                         @Override
                         public void onFailure(Call<User> call, Throwable t) {

                         }
                     });
        return convertView;
    }
}