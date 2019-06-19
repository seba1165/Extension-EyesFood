package com.example.jonsmauricio.eyesfood.data.api;

import com.example.jonsmauricio.eyesfood.data.api.model.Comment;
import com.example.jonsmauricio.eyesfood.data.api.model.CommentBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentsApi {

    String BASE_URL = "http://190.21.74.176/api.eyesfood.comments.cl/v1/";

    //Retorna los comentarios de un alimento
    @GET("comments/{barcode}")
    Call<List<Comment>> getComments(@Path("barcode") String barcode);

    //Inserta una solicitud de edición de alimento
    @POST("comments/{barcode}")
    Call<Comment> newComment(@Body CommentBody commentBody, @Path("barcode") String barcode);
}
