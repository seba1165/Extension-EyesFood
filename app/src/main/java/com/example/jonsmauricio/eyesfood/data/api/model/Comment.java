package com.example.jonsmauricio.eyesfood.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JonásMauricio on 04-11-2017.
 */

public class Comment implements Serializable {
    @SerializedName("idComentario")
    private String id;
    @SerializedName("idUsuario")
    private String idUsuario;
    @SerializedName("comentario")
    private String comment;
    @SerializedName("fecha")
    private String date;

    public Comment(String id, String idUsuario, String comment, String date) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.comment = comment;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
