package com.example.jonsmauricio.eyesfood.data.api.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
    Define un objeto alimento incompleto
    Clase utilizada para mostrar la información general de un alimento en el historial
*/

public class ShortFood{
    @SerializedName("idUsuario")
    private String userId;
    @SerializedName("codigoBarras")
    private String barCode;
    @SerializedName("nombre")
    private String name;
    @SerializedName("peligroAlimento")
    private float foodHazard;
    @SerializedName("fechaEscaneo")
    private String date;
    @SerializedName("fotoOficial")
    private String officialPhoto;
    @SerializedName("meGusta")
    private int like;
    @SerializedName("escaneo")
    private int list;

    public ShortFood(String userId, String barCode, String name, float foodHazard, String date, String officialPhoto, int like, int list) {
        this.userId = userId;
        this.barCode = barCode;
        this.name = name;
        this.foodHazard = foodHazard;
        this.date = date;
        this.officialPhoto = officialPhoto;
        this.like = like;
        this.list = list;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    //No se si lo uso
    public void setName(String name) {
        this.name = name;
    }

    public float getFoodHazard() {
        return foodHazard;
    }

    public String getDate() {
        return date;
    }

    public String getOfficialPhoto() {
        return officialPhoto;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getList() {
        return list;
    }

    @Override
    public String toString(){
        return name + " - " + foodHazard + " - " + date;
    }
}
