package com.example.jonsmauricio.eyesfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jonsmauricio.eyesfood.R;
import com.example.jonsmauricio.eyesfood.data.api.EyesFoodApi;
import com.example.jonsmauricio.eyesfood.data.api.OpenFoodFactsApi;
import com.example.jonsmauricio.eyesfood.data.api.model.Food;
import com.example.jonsmauricio.eyesfood.data.api.model.NewFoodBody;
import com.example.jonsmauricio.eyesfood.data.api.model.Product;
import com.example.jonsmauricio.eyesfood.data.api.model.ProductResponse;
import com.example.jonsmauricio.eyesfood.data.api.model.ShortFood;
import com.example.jonsmauricio.eyesfood.data.prefs.SessionPrefs;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    MaterialSearchView searchView;
    private List<NewFoodBody> pendientes;
    private List<ShortFood> accepted;
    private ListView resultFoodsAccepted;
    private ListView resultFoodsPending;
    TextView searchProgressText;
    TextView searchEmptyState;
    TextView searchPendientesHeader;
    TextView searchAceptadosHeader;
    private View searchProgress;
    private String userIdFinal;

    Retrofit mRestAdapter;
    Retrofit mOpenRestAdapter;
    EyesFoodApi mEyesFoodApi;
    OpenFoodFactsApi mOpenFoodApi;

    private ArrayAdapter<NewFoodBody> adaptadorPendientes;
    private ArrayAdapter<ShortFood> adaptadorAceptados;

    boolean noFoodsPending;
    boolean noFoodsAccepted;

    int like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view_search);
        resultFoodsAccepted = (ListView) findViewById(R.id.lvResultAceptados);
        resultFoodsPending = (ListView) findViewById(R.id.lvResultPendientes);
        searchProgress = findViewById(R.id.pbSearchProgress);
        searchProgressText = (TextView) findViewById(R.id.tvSearchProgressText);
        searchEmptyState = (TextView) findViewById(R.id.tvSearchEmptyState);
        searchPendientesHeader = (TextView) findViewById(R.id.tvPendientesHeader);
        searchAceptadosHeader = (TextView) findViewById(R.id.tvAceptadosHeader);

        userIdFinal = SessionPrefs.get(this).getUserId();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(EyesFoodApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mOpenRestAdapter = new Retrofit.Builder()
                .baseUrl(OpenFoodFactsApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear conexión a la API de EyesFood
        mEyesFoodApi = mRestAdapter.create(EyesFoodApi.class);

        mOpenFoodApi = mOpenRestAdapter.create(OpenFoodFactsApi.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultFoodsPending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                NewFoodBody currentSearch = adaptadorPendientes.getItem(position);
                showFood(currentSearch);
                //isFoodInHistory(userIdFinal, currentSearch.getBarCode(), currentSearch);
            }
        });

        resultFoodsAccepted.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ShortFood currentSearch = adaptadorAceptados.getItem(position);
                showFood(currentSearch);
                //isFoodInHistory(userIdFinal, currentSearch.getBarCode(), currentSearch);
            }
        });

        Intent i = getIntent();
        Bundle b = i.getExtras();
        showLists(false);
        showProgress(true);
        //query = (String) b.get("query");
        showFoodsPending();
        showFoodsAccepted();

    }

    private void showFood(ShortFood currentSearch) {
        //loadFoods(currentSearch.getBarCode());
        like = currentSearch.getLike();
        Intent i = new Intent(getApplicationContext(), FoodsActivity.class);
        i.putExtra("pendiente", (String) null);
        Call<ProductResponse> call = mOpenFoodApi.obtenerProducto(currentSearch.getBarCode());
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "Error api OpenFood");
                    return;
                }
                final Product product = response.body().getProduct();
                if (response.body().getProduct()!=null){product.setCodigo(response.body().getCode());}
                Call<Food> call2 = mEyesFoodApi.getFood(product.getCodigo());
                call2.enqueue(new Callback<Food>() {
                    @Override
                    public void onResponse(Call<Food> call, Response<Food> response) {
                        if (!response.isSuccessful()) {
                            // TODO: Procesar error de API
                            Log.d("myTag", "Error: API EYESFOOD");
                            return;
                        }
                        Food alimento = response.body();
                        showFoodFinal(product,alimento, like);
                    }

                    @Override
                    public void onFailure(Call<Food> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {

            }
        });
       /* i.putExtra("Alimento", food);
        i.putExtra("Product", (String) null);
        i.putExtra("MeGusta", (String) null);*/
    }

    private void showFoodFinal(Product product, Food alimento, int like) {
        Intent i = new Intent(this, FoodsActivity.class);
        i.putExtra("Product",product);
        i.putExtra("Alimento",alimento);
        i.putExtra("MeGusta",like);
        //progressDialog.dismiss();
        startActivity(i);
    }

    private void showFood(NewFoodBody pendiente) {
        Intent i = new Intent(getApplicationContext(), FoodsActivity.class);
        i.putExtra("pendiente", pendiente);
        i.putExtra("Alimento", (String) null);
        i.putExtra("Product", (String) null);
        i.putExtra("MeGusta", (String) null);
        startActivity(i);
    }

    private void showFoodsPending() {
        Call<List<NewFoodBody>> call = mEyesFoodApi.getNewFoods(userIdFinal);
        call.enqueue(new Callback<List<NewFoodBody>>() {
            @Override
            public void onResponse(Call<List<NewFoodBody>> call, Response<List<NewFoodBody>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "Error en getNewFoods");
                    return;
                }
                pendientes = response.body();
                Log.d("myTag", "pendientes: "+pendientes.size());
                showListPending(pendientes);
            }

            @Override
            public void onFailure(Call<List<NewFoodBody>> call, Throwable t) {
                Log.d("myTag", "Falla API" + t.getMessage());
            }
        });
    }

    private void showListPending(List<NewFoodBody> lista) {
        int tamanoLista = lista.size();
        //Log.d("myTag", "tamano Lista" + lista.size());
        if(tamanoLista > 0) {
            noFoodsPending = false;
            adaptadorPendientes = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    lista);
            resultFoodsPending.setAdapter(adaptadorPendientes);
        }
        else{
            noFoodsPending = true;
        }
    }

    private void showFoodsAccepted() {
        Call<List<ShortFood>> call = mEyesFoodApi.getNewFoodsAccepted(userIdFinal);
        call.enqueue(new Callback<List<ShortFood>>() {
            @Override
            public void onResponse(Call<List<ShortFood>> call, Response<List<ShortFood>> response) {
                if (!response.isSuccessful()) {
                    // TODO: Procesar error de API
                    Log.d("myTag", "Error en getNewFoodsAccepted");
                    return;
                }
                accepted = response.body();
                showListAccepted(accepted);
            }

            @Override
            public void onFailure(Call<List<ShortFood>> call, Throwable t) {

            }
        });
    }

    private void showListAccepted(List<ShortFood> lista) {
        int tamanoLista = lista.size();
        if(tamanoLista > 0) {
            noFoodsAccepted = false;
            adaptadorAceptados = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    lista);
            resultFoodsAccepted.setAdapter(adaptadorAceptados);
        }
        else{
            noFoodsAccepted = true;
            showEmptyState(noFoodsPending, noFoodsAccepted);
        }
        showProgress(false);
        if(!noFoodsAccepted || !noFoodsPending){
            showLists(true);
        }
    }

    private void showLists(boolean show){
        if(!show){
            resultFoodsPending.setVisibility(View.GONE);
            searchPendientesHeader.setVisibility(View.GONE);
            resultFoodsAccepted.setVisibility(View.GONE);
            searchAceptadosHeader.setVisibility(View.GONE);
        }
        else{
            resultFoodsPending.setVisibility(View.VISIBLE);
            searchPendientesHeader.setVisibility(View.VISIBLE);
            resultFoodsAccepted.setVisibility(View.VISIBLE);
            searchAceptadosHeader.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress(boolean show) {
        if(show) {
            showLists(false);
            showEmptyState(false, false);
        }
        else{
            showEmptyState(noFoodsPending, noFoodsAccepted);
        }
        searchProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        searchProgressText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showEmptyState(boolean noFoodsPending, boolean noFoodsAccepted){
        if(noFoodsPending && noFoodsAccepted){
            searchEmptyState.setVisibility(View.VISIBLE);
        }
        else{
            searchEmptyState.setVisibility(View.GONE);
        }
    }
}
