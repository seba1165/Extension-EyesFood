package com.example.jonsmauricio.eyesfood.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Comment;
import com.example.jonsmauricio.eyesfood.data.api.model.Consult;
import com.example.jonsmauricio.eyesfood.data.api.model.Expert;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.NewFoodBody;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpertDetailFragment extends DialogFragment {
    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    final String baseFotoAlimento = EyesFoodApi.BASE_URL+"img/experts/";
    Expert Experto;
    private ImageView expertPhoto;
    private TextView expertName, expertSpecialty, expertDescription;
    private RatingBar expertRating;
    Button btFoods, btConsulta;
    private Retrofit mRestAdapter;
    private EyesFoodApi mEyesFoodApi;
    private String userIdFinal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.fragment_expert_detail, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarExpertDetail);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setHasOptionsMenu(true);

        expertPhoto = view.findViewById(R.id.ivExpertDetail);
        expertName = view.findViewById(R.id.tvExpertDetailName);
        expertSpecialty = view.findViewById(R.id.tvExpertDetailSpecialty);
        expertRating = view.findViewById(R.id.rbExpertDetailRating);
        expertDescription = view.findViewById(R.id.tvExpertDetailDescription);
        /*btFoods = view.findViewById(R.id.btFoods);*/
        btConsulta = view.findViewById(R.id.btConsulta);
        userIdFinal = SessionPrefs.get(getActivity()).getUserId();

        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Experto = (Expert) bundle.getSerializable("Experto");
            toolbar.setTitle(Experto.getName() + " " + Experto.getLastName());
            showExpertData(Experto);
        }

        /*btFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Experto.getFoods()>0){

                }else{
                    Toast.makeText(getActivity(), "El experto no ha aprobado alimentos", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        btConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(null)
                        .setTitle(getResources().getString(R.string.title_new_consult_question))
                        .setMessage(getResources().getString(R.string.message_new_consult_question))
                        .setPositiveButton(getResources().getString(R.string.possitive_dialog), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                crearConsulta();
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.negative_dialog), null)
                        .show();
            }
        });

        return view;
    }

    private void crearConsulta() {
        Call<Consult> call = mEyesFoodApi.insertConsult(new Consult(String.valueOf(Experto.getExpertId()), userIdFinal));
        call.enqueue(new Callback<Consult>() {
            @Override
            public void onResponse(Call<Consult> call, Response<Consult> response) {
                if (!response.isSuccessful()) {
                    Log.d("myTag", "Mala respuesta en insertConsult" + response.toString());
                    return;
                }
                else {
//                    Log.d("myTag", "Mostrar Producto leido");
//                    progressDialog.setMessage("Cargando Producto");
                    Toast.makeText(getActivity(), "Su solicitud ha sido creada con exito", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Consult> call, Throwable t) {

            }
        });
    }

    private void showExpertData(Expert experto) {
        Picasso.with(getContext())
                .load(baseFotoAlimento + experto.getPhoto())
                .into(expertPhoto);

        expertName.setText(experto.getName() + " " + experto.getLastName());
        expertSpecialty.setText("Especialidad: " + experto.getSpecialty());
        expertRating.setRating(experto.getReputation());
        expertDescription.setText(experto.getDescription());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.searchHistory).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            ExpertsFragment expertsFragment = new ExpertsFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(android.R.id.content, expertsFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}