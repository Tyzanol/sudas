package com.example.sudas.positions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sudas.R;

import java.util.List;

public class PositionsAdapter extends RecyclerView.Adapter<PositionsViewHolder> {

    private List<Position> positionsList;
    private Context context;

    public PositionsAdapter(List<Position> positionsList, Context context) {
        this.positionsList = positionsList;
        this.positionsList.add(new Position("a", "b", "1", "2"));
        this.context = context;
    }

    @NonNull
    @Override
    public PositionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_positions, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        PositionsViewHolder rcv = new PositionsViewHolder((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull PositionsViewHolder holder, int position) {
        holder.mPositionTitle.setText(positionsList.get(position).getTitle());
        holder.mPositionId.setText(positionsList.get(position).getPositionId());
        holder.mRecruiterId.setText(positionsList.get(position).getRecruiterId());
    }

    @Override
    public int getItemCount() {
        return this.positionsList.size();
    }
}
