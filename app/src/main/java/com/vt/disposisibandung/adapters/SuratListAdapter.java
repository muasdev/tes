package com.vt.disposisibandung.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.views.SuratListItemView;
import com.vt.disposisibandung.views.SuratListItemView_;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by irvan on 6/26/15.
 */
public class SuratListAdapter extends RecyclerView.Adapter<SuratListAdapter.SuratListViewHolder> implements SuratListItemView.OnListItemClickListener {

    private List<Surat> suratList = new RealmList<>();

    @Override
    public void onListItemClicked(long id) {
        for (int i = 0; i < suratList.size(); i++) {
            if (suratList.get(i).getId() == id) {
                suratList.get(i).setIsRead(true);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public SuratListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SuratListItemView itemView = SuratListItemView_.build(parent.getContext());
        itemView.setOnListItemClickListener(this);
        SuratListViewHolder holder = new SuratListViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(SuratListViewHolder holder, int position) {
        if (position == suratList.size()) {
            holder.getView().setVisibility(View.INVISIBLE);
        } else {
            holder.getView().setSurat(suratList.get(position));
            holder.getView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return suratList.size() + 1;
    }

    public List<Surat> getSuratList() {
        return suratList;
    }

    public class SuratListViewHolder extends RecyclerView.ViewHolder {

        private SuratListItemView suratListItemView;

        public SuratListViewHolder(SuratListItemView suratListItemView) {
            super(suratListItemView);
            this.suratListItemView = suratListItemView;
        }

        public SuratListItemView getView() {
            return suratListItemView;
        }
    }
}
