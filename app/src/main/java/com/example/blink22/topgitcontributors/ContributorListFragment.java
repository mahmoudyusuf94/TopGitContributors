package com.example.blink22.topgitcontributors;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContributorListFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private ContributorAdapter mAdapter;

    private static final String TAG = "ContributorListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contributor_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contributors_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;

    }

    private class ContributorHolder extends RecyclerView.ViewHolder{
        private TextView mNameTextView;
        private TextView mCountTextView;
        private ImageView mAvatarImageView;

        public ContributorHolder(View view) {
            super(view);
            Log.d(TAG, "Creating Holder ......");
            mNameTextView = (TextView) view.findViewById(R.id.list_item_contributor_name_text_view);
            mCountTextView = (TextView) view.findViewById(R.id.list_item_contributor_count_text_view);
            mAvatarImageView = (ImageView) view.findViewById(R.id.list_item_contributor_image_view);
        }

        public void bindContributor(Contributor contributor) {
            Log.d(TAG, "Binding View ......");
            mNameTextView.setText(contributor.getAuthorName());
            mCountTextView.setText(Integer.toString(contributor.getCount()));
            Picasso.get()
                    .load(contributor.getAuthorAvatarUrl())
                    .centerCrop()
                    .resize(40,40)
                    .placeholder(R.drawable.place_holder)
                    .into(mAvatarImageView);
        }
    }
    private class ContributorAdapter extends RecyclerView.Adapter<ContributorHolder>{

        private List<Contributor> mContributors;

        public ContributorAdapter(List<Contributor> contributors) {
            mContributors = contributors;
        }

        @NonNull
        @Override
        public ContributorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.list_item_contributor, parent, false);
            Log.d(TAG, "Creating view holder ....... ");
            return new ContributorHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ContributorHolder holder, int position) {
            Contributor contributor = mContributors.get(position);
            Log.i(TAG,"Binding view : "+ contributor.getAuthorName());
            holder.bindContributor(contributor);
        }

        @Override
        public int getItemCount() {
            return mContributors.size();
        }

        public void setContributors(List<Contributor> contributors){
            mContributors = contributors;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(GetDataService.class);
        Call<List<Contributor>> call = service.getAllContributors();

        Log.i(TAG, "Making the network call async");

        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                updateUi(response.body());
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error, Please Try Again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUi(List<Contributor> contributors){
        Log.i(TAG, "Response received with : "+ contributors.size() +" contributors");
        Log.i(TAG, "Updating UI");
        Log.i(TAG, "Author 1 :" + contributors.get(0).getAuthorName() + " -- count : "+ contributors.get(0).getCount());
        mAdapter = new ContributorAdapter(contributors);
        Log.i(TAG,"********  -> " + mRecyclerView.toString());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
