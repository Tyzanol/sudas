package com.example.sudas.positions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sudas.talents.TalentActivity;
import com.example.sudas.R;

public class PositionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mPositionId;
    public TextView mRecruiterId;
    public TextView mPositionTitle;

    public PositionsViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mRecruiterId = (TextView) itemView.findViewById(R.id.recruiterId);
        mPositionId = (TextView) itemView.findViewById(R.id.positionId);
        mPositionTitle = (TextView) itemView.findViewById(R.id.positionTitle);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), TalentActivity.class);
        Bundle b = new Bundle();
        b.putString("positionId", mPositionId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
