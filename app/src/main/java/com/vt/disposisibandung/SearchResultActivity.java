package com.vt.disposisibandung;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.vt.disposisibandung.adapters.SearchResultAdapter;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import io.realm.RealmList;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Irvan on 7/23/2015.
 */
@EActivity(R.layout.activity_search_result)
public class SearchResultActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener {

    private static final String EXTRA_QUERY = "query";

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.listSurat)
    protected SuperRecyclerView recyclerViewSurat;

    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;

    @ViewById(R.id.textEmpty)
    protected TextView textViewEmpty;

    @Extra(EXTRA_QUERY)
    protected String query;

    private SearchResultAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isEndOfList = false;

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_search_result);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (query != null) {
            getSupportActionBar().setSubtitle(query);

            adapter = new SearchResultAdapter();
            recyclerViewSurat.setAdapter(adapter);
            recyclerViewSurat.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewSurat.setRefreshListener(this);
            recyclerViewSurat.setupMoreListener(this, 5);

            performSearchSurat();
        }
    }

    @Override
    public void onRefresh() {
        adapter.getSuratList().clear();
        currentPage = 1;
        isLoading = false;
        isEndOfList = false;

        textViewEmpty.setVisibility(View.GONE);
        performSearchSurat();
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        if (!isLoading && !isEndOfList) {
            ++currentPage;
            performSearchSurat();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void performSearchSurat() {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().searchSurat(query, 0, currentPage, new Callback<RealmList<Surat>>() {
            @Override
            public void success(RealmList<Surat> suratList) {
                if (suratList.isEmpty()) {
                    if (currentPage == 1) {
                        textViewEmpty.setVisibility(View.VISIBLE);
                    } else {
                        isEndOfList = true;
                    }
                } else {
                    for (Surat surat : suratList) {
                        switch (surat.getJenisSuratSearch()) {
                            case 1: {
                                surat.setType(Surat.TYPE_MASUK);
                                break;
                            }
                            case 2: {
                                surat.setType(Surat.TYPE_KELUAR);
                                break;
                            }
                        }

                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) surat.setIsRead(savedSurat.isRead());
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();


                    adapter.getSuratList().addAll(suratList);
                    adapter.notifyDataSetChanged();
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(SearchResultActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);
            }
        });
    }
}
