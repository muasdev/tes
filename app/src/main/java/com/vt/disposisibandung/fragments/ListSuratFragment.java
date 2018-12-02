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
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import io.realm.RealmList;
import retrofit.RetrofitError;

/**
 * Created by irvan on 6/26/15.
 */
@EFragment(R.layout.fragment_list_surat)
public class ListSuratFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener {

    private static final String ARG_SURAT_TYPE = "surat_type";

    @ViewById(R.id.listSurat)
    protected SuperRecyclerView recyclerViewSurat;

    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;

    @ViewById(R.id.textEmpty)
    protected TextView textViewEmpty;

    @FragmentArg(ARG_SURAT_TYPE)
    protected int suratType;

    private SuratListAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isEndOfList = false;

    @AfterViews
    protected void initViews() {
        adapter = new SuratListAdapter();
        recyclerViewSurat.setAdapter(adapter);
        recyclerViewSurat.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSurat.getSwipeToRefresh().setColorSchemeResources(R.color.colorPrimary);
        recyclerViewSurat.setRefreshListener(this);
        recyclerViewSurat.setupMoreListener(this, 5);

        performGetListSurat();
    }

    @Override
    public void onRefresh() {
        adapter.getSuratList().clear();
        currentPage = 1;
        isLoading = false;
        isEndOfList = false;

        textViewEmpty.setVisibility(View.GONE);
        performGetListSurat();
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        if (!isLoading && !isEndOfList) {
            ++currentPage;
            performGetListSurat();
        }
    }

    private void performGetListSurat() {
        switch (suratType) {
            case Surat.TYPE_MASUK: {
                performGetListSuratMasuk(true);
                break;
            }
            case Surat.TYPE_UNDANGAN: {
                performGetListSuratUndangan(true);
                break;
            }
            case Surat.TYPE_AUDIENSI: {
                performGetListAudiensi(true);
                break;
            }
            case Surat.TYPE_UMUM: {
                performGetListSuratUmum(true);
                break;
            }
            case Surat.TYPE_KELUAR: {
                performGetListSuratKeluar(true);
                break;
            }
            case Surat.TYPE_BELUM_SELESAI: {
                performGetListSuratBelumSelesai(true);
                break;
            }
            case Surat.TYPE_SUDAH_SELESAI: {
                performGetListSuratSudahSelesai(true);
                break;
            }
            case Surat.TYPE_DISPOSISI_MASUK: {
                performGetListDisposisiMasuk(true);
                break;
            }
            case Surat.TYPE_DISPOSISI_KELUAR: {
                performGetListDisposisiKeluar(true);
                break;
            }
        }

    }

    private void performGetListSuratMasuk(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratMasuk(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_MASUK);
                        surat.setSuratMasuk(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setType(savedSurat.getType());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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
                ////Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);
            }
        });
    }

    private void performGetListSuratUndangan(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratUndangan(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_UNDANGAN);
                        surat.setSuratUndangan(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            ;
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }
                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListAudiensi(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratAudiensi(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_AUDIENSI);
                        surat.setSuratAudiensi(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }
                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListSuratUmum(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratUmum(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_UMUM);
                        surat.setSuratUmum(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }
                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListSuratKeluar(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratKeluar(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_KELUAR);
                        surat.setSuratKeluar(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListSuratBelumSelesai(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratBelumSelesai(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_BELUM_SELESAI);
                        surat.setSuratBelum(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setType(surat.getType());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }

                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListSuratSudahSelesai(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListSuratSudahSelesai(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_SUDAH_SELESAI);
                        surat.setSuratSelesai(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setType(surat.getType());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }

                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListDisposisiMasuk(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListDisposisiMasuk(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_MASUK);
                        surat.setDisposisiMasuk(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setType(savedSurat.getType());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiKeluar(savedSurat.isDisposisiKeluar());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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

    private void performGetListDisposisiKeluar(final Boolean progress) {
        isLoading = true;

        WebServiceHelper.getInstance().getServices().getListDisposisiKeluar(currentPage, new Callback<RealmList<Surat>>() {
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
                        surat.setType(Surat.TYPE_KELUAR);
                        surat.setDisposisiKeluar(true);
                        Surat savedSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", surat.getId()).findFirst();
                        if (savedSurat != null) {
                            surat.setIsRead(savedSurat.isRead());
                            surat.setType(savedSurat.getType());
                            surat.setSuratMasuk(savedSurat.isSuratMasuk());
                            surat.setSuratUndangan(savedSurat.isSuratUndangan());
                            surat.setSuratAudiensi(savedSurat.isSuratAudiensi());
                            surat.setSuratUmum(savedSurat.isSuratUmum());
                            surat.setSuratBelum(savedSurat.isSuratBelum());
                            surat.setSuratSelesai(savedSurat.isSuratSelesai());
                            surat.setSuratKeluar(savedSurat.isSuratKeluar());
                            surat.setDisposisiMasuk(savedSurat.isDisposisiMasuk());
                        }
                    }

                    DatabaseHelper.getInstance().getRealm().beginTransaction();
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(suratList);
                    DatabaseHelper.getInstance().getRealm().commitTransaction();

                    if (progress == true) {
                        adapter.getSuratList().addAll(suratList);
                        adapter.notifyDataSetChanged();
                    }
                }

                isLoading = false;
                progressBarLoading.setVisibility(View.GONE);
                recyclerViewSurat.hideMoreProgress();
                recyclerViewSurat.getSwipeToRefresh().setRefreshing(false);

                ((DrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.drawerFrame)).refreshData();
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