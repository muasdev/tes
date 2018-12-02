package com.vt.disposisibandung.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.adapters.SuratListAdapter;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.RealmList;
import retrofit.RetrofitError;

/**
 * Created by irvan on 6/26/15.
 */
@EFragment(R.layout.fragment_list_surat)
public class ArsipSuratFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener {

    @ViewById(R.id.listSurat)
    protected SuperRecyclerView recyclerViewSurat;

    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;

    @ViewById(R.id.textEmpty)
    protected TextView textViewEmpty;

    private SuratListAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isEndOfList = false;

    @AfterViews
    protected void initViews() {
        adapter = new SuratListAdapter();
        recyclerViewSurat.setAdapter(adapter);
        recyclerViewSurat.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSurat.setRefreshListener(this);
        recyclerViewSurat.setupMoreListener(this, 5);

        performGetArsipSurat();
    }

    @Override
    public void onRefresh() {
        adapter.getSuratList().clear();
        currentPage = 1;
        isLoading = false;
        isEndOfList = false;

        textViewEmpty.setVisibility(View.GONE);
        performGetArsipSurat();
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        if (!isLoading && !isEndOfList) {
            ++currentPage;
            performGetArsipSurat();
        }
    }

    private void performGetArsipSurat() {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getArsipSurat(currentPage, new Callback<RealmList<Surat>>() {
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

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);
            }
        });
    }
}
