package ru.yandex.dunaev.mick.myumorili;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stone.vega.library.VegaLayoutManager;

import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static UmiriliApi sUmiriliApi;
    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://umorili.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sUmiriliApi = mRetrofit.create(UmiriliApi.class);

        sUmiriliApi.getData("bash",50).enqueue(new Callback<List<PostModel>>() {
            @Override
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                Log.v("Responce","Данные пришли");
                if(response.body() == null){
                    Log.v("Responce","но их нету");
                    return;
                }

                Log.v("Responce","Данные в наличии");

                final List<PostModel> list = response.body();
                //Список есть

                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler);
                recyclerView.setAdapter(new RecyclerView.Adapter() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        CardView cv = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card,viewGroup,false);
                        return new RecyclerView.ViewHolder(cv) {};
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                        CardView cv = (CardView)viewHolder.itemView;
                        TextView desc = (TextView)cv.findViewById(R.id.desc);
                        TextView title = (TextView)cv.findViewById(R.id.title);
                        desc.setText(Html.fromHtml(list.get(i).getElementPureHtml()));
                        title.setText(list.get(i).getName());
                    }

                    @Override
                    public int getItemCount() {
                        return list.size();
                    }
                });
                //recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setLayoutManager(new VegaLayoutManager());

            }

            @Override
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                Log.v("Responce","Данные не пришли");
            }
        });
    }

    public static UmiriliApi getApi() {
        return sUmiriliApi;
    }
}
