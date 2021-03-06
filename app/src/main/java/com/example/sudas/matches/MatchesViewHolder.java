package com.example.sudas.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sudas.chat.ChatActivity;
import com.example.sudas.R;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId;
    public TextView mPositionId;
    public TextView mMatchName;
    public ImageView mMatchImage;

    public MatchesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMatchId = (TextView) itemView.findViewById(R.id.matchId);
        mPositionId = (TextView) itemView.findViewById(R.id.positionId);
        mMatchName = (TextView) itemView.findViewById(R.id.matchName);
        mMatchImage = (ImageView) itemView.findViewById(R.id.matchImage);    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        b.putString("positionId", mPositionId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
