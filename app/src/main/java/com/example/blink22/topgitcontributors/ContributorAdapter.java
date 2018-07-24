package com.example.blink22.topgitcontributors;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.ContributorHolder> {

    private List<Contributor> mContributors;

    class ContributorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Contributor mContributor;

        @BindView(R.id.list_item_contributor_name_text_view)
        TextView mNameTextView;

        @BindView(R.id.list_item_contributor_count_text_view)
        TextView mCountTextView;

        @BindView(R.id.list_item_contributor_image_view)
        ImageView mAvatarImageView;


        public ContributorHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void bindContributor(Contributor contributor) {
            mContributor = contributor;
            mNameTextView.setText(contributor.getAuthorName());
            mCountTextView.setText(Integer.toString(contributor.getCount()));
            Picasso.get()
                    .load(contributor.getAuthorAvatarUrl())
                    .centerCrop()
                    .resize(50,50)
                    .placeholder(R.drawable.place_holder)
                    .into(mAvatarImageView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            Intent intent = ContributorPageActivity.newIntent( view.getContext(),
                    Uri.parse(mContributor.getAuthorUrl()));
            view.getContext().startActivity(intent);
        }
    }

    public ContributorAdapter(List<Contributor> contributors) {
        mContributors = contributors;
    }

    @NonNull
    @Override
    public ContributorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
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

    public void setContributors(List<Contributor> contributors){
        mContributors = contributors;
    }

    public List<Contributor> getContributors(){
        return mContributors;
    }


}
