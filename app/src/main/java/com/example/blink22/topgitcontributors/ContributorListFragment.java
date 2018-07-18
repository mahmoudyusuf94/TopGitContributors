package com.example.blink22.topgitcontributors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContributorListFragment extends Fragment{


    private RecyclerView mRecyclerView;
    private ContributorAdapter mAdapter;

    private String mOwner = "ruby" ;
    private String mRepo = "ruby" ;

    private LinearLayout mWelcomeScreen;
    private ProgressBar mWelcomeProgressBar;
    private TextView mErrorText;
    private EditRepoDialog mEditRepoDialog;

    private int REQUEST_CHANGE_REPO = 1;
    public String dialogRepo = "ChangeRepoDialog";

    private static final String TAG = "ContributorListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_contributor_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contributors_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mWelcomeScreen = v.findViewById(R.id.welcome_screen);
        mWelcomeProgressBar = mWelcomeScreen.findViewById(R.id.welcome_progress_bar);

        mErrorText = mWelcomeScreen.findViewById(R.id.netowrk_error_text_view);
        mErrorText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mErrorText.setVisibility(View.GONE);
                loadContributors();
                showWait();
            }
        });
        Log.i(TAG, "Almost done with onCreateView....");

        if(mAdapter == null){
            Log.i(TAG, "Calling loadContributors from OnCreateView -> adapter is NULL");
            loadContributors();
            showWait();
        }else{
            showWait();
            updateUi(mAdapter.mContributors);
        }

        return v;

    }

    private class ContributorHolder extends RecyclerView.ViewHolder{
        private TextView mNameTextView;
        private TextView mCountTextView;
        private ImageView mAvatarImageView;

        public ContributorHolder(View view) {
            super(view);
            mNameTextView = (TextView) view.findViewById(R.id.list_item_contributor_name_text_view);
            mCountTextView = (TextView) view.findViewById(R.id.list_item_contributor_count_text_view);
            mAvatarImageView = (ImageView) view.findViewById(R.id.list_item_contributor_image_view);
        }

        public void bindContributor(Contributor contributor) {
            mNameTextView.setText(contributor.getAuthorName());
            mCountTextView.setText(Integer.toString(contributor.getCount()));
            Picasso.get()
                    .load(contributor.getAuthorAvatarUrl())
                    .centerCrop()
                    .resize(50,50)
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    public void loadContributors() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(GetDataService.class);
        Call<List<Contributor>> call = service.getAllContributors(mOwner, mRepo);

        Log.i(TAG, "Making the network call async ...");

        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                Log.i(TAG, "Response Received. calling UpdateUi .....");
                updateUi(response.body());
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                Log.i(TAG, "HTTP REQUEST FAILED .... On Failur ... calling  show Error !");
                showError();
            }
        });
    }

    private void showError() {
        mWelcomeScreen.setVisibility(View.VISIBLE);
        mWelcomeProgressBar.setVisibility(View.GONE);
        mErrorText.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

    }

    private void showWait(){
        mWelcomeScreen.setVisibility(View.VISIBLE);
        mWelcomeProgressBar.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showResult(){
        mWelcomeScreen.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void updateUi(List<Contributor> contributors){
        if(contributors != null && contributors.size() > 0 ){
            showResult();
        }
        else{
            Log.i(TAG, "Owner is: " + mOwner + ", and Repo is:"+ mRepo);
            if(contributors == null){
                Log.i(TAG, "Contributor is NULL !!!!!!!!");
            }else{
                Log.i(TAG, "Contributor is EEEEEEEMPTYYYYYYYYYY !!!!!!!!");
            }
            showError();
            return;
        }
        Collections.sort(contributors);
        if(mAdapter == null){
            mAdapter = new ContributorAdapter(contributors);
        }
        else{
            mAdapter.setContributors(contributors);
            mAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_contributor_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.menu_item_change_repo:
               if(mEditRepoDialog == null){
                   mEditRepoDialog = new EditRepoDialog();
               }
               mEditRepoDialog.setTargetFragment(ContributorListFragment.this, REQUEST_CHANGE_REPO );
               mEditRepoDialog.show(getFragmentManager(), dialogRepo);
               return true;
           default:return super.onOptionsItemSelected(item);
       }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CHANGE_REPO){
            mOwner = data.getStringExtra(EditRepoDialog.EXTRA_OWNER);
            mRepo = data.getStringExtra(EditRepoDialog.EXTRA_REPO);
            showWait();
            loadContributors();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
