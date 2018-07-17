package com.example.blink22.topgitcontributors;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class ContributorListFragment extends Fragment{

    private List<Contributor> mContributors;

    private RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contributor_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contributors_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private class ContributorHolder extends RecyclerView.ViewHolder{
        private TextView mNameTextView;
        private TextView mCountTextView;
        private ImageView mAvatarImageView;

        public ContributorHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.list_item_contributor_name_text_view);
            mCountTextView = itemView.findViewById(R.id.list_item_contributor_count_text_view);
            mAvatarImageView = itemView.findViewById(R.id.list_item_contributor_image_view);
        }

        public void bindContributor(Contributor contributor) {
            mNameTextView.setText(contributor.getAuthorName());
            mCountTextView.setText(contributor.getCount());
            Picasso.get()
                    .load(contributor.getAuthorAvatarUrl())
                    .centerCrop()
                    .resize(40,40)
                    .placeholder(R.drawable.place_holder)
                    .into(mAvatarImageView);
        }
    }
    private class ContributorAdapter extends RecyclerView.Adapter<ContributorHolder>{

        @NonNull
        @Override
        public ContributorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View itemView = inflater.inflate(R.layout.list_item_contributor, parent, false);

            return new ContributorHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ContributorHolder holder, int position) {
            Contributor contributor = mContributors.get(position);
            holder.bindContributor(contributor);
        }

        @Override
        public int getItemCount() {
            return mContributors.size();
        }
    }
}
