package com.example.blink22.topgitcontributors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
    private TextView mRepoTextView;
    private LinearLayout mResultLayout;

    private LinearLayout mWaitingLayout;
    private TextView mLoadingText;
    private TextView mNotGoodTextView;

    private String mOwner;
    private String mRepo;

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

        mResultLayout = (LinearLayout) v.findViewById(R.id.contributors_linear_layout);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contributors_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mRepoTextView = (TextView) v.findViewById(R.id.contributor_list_repo_title_text_view);

        mWelcomeScreen = v.findViewById(R.id.welcome_screen);
        mWelcomeProgressBar = mWelcomeScreen.findViewById(R.id.welcome_progress_bar);
        mWaitingLayout = v.findViewById(R.id.loading_layout);
        mLoadingText = mWaitingLayout.findViewById(R.id.loading_repo_text_view);
        mNotGoodTextView = mWaitingLayout.findViewById(R.id.loading_not_good_text_view);

        mNotGoodTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                QueryPreferences.setStoredRepo(getActivity(), null, null);
                getSavedRepo();
                loadContributors();
            }
        });


        mErrorText = mWelcomeScreen.findViewById(R.id.netowrk_error_text_view);
        mErrorText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mErrorText.setVisibility(View.GONE);
                loadContributors();
            }
        });
        Log.d(TAG, "Almost done with onCreateView....");

        if(mAdapter == null){
            Log.d(TAG, "Calling loadContributors from OnCreateView -> adapter is NULL");
            loadContributors();
        }else{
            showWait();
            updateUi(mAdapter.mContributors);
        }

        return v;

    }

    private class ContributorHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Contributor mContributor;
        private TextView mNameTextView;
        private TextView mCountTextView;
        private ImageView mAvatarImageView;


        public ContributorHolder(View view) {
            super(view);
            mNameTextView = (TextView) view.findViewById(R.id.list_item_contributor_name_text_view);
            mCountTextView = (TextView) view.findViewById(R.id.list_item_contributor_count_text_view);
            mAvatarImageView = (ImageView) view.findViewById(R.id.list_item_contributor_image_view);
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
            Intent intent = ContributorPageActivity.newIntent( getActivity(),
                    Uri.parse(mContributor.getAuthorUrl()));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
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
        getSavedRepo();

    }

    private void getSavedRepo() {
        mRepo = QueryPreferences.getStoredRepo(getActivity());
        mOwner = QueryPreferences.getStoredOwner(getActivity());
    }

    public void loadContributors() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(GetDataService.class);
        Call<List<Contributor>> call = service.getAllContributors(mOwner, mRepo);

        Log.d(TAG, "Making the network call async ...");
        showWait();

        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                Log.d(TAG, "Response Received. calling UpdateUi .....");
                updateUi(response.body());
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                Log.d(TAG, "HTTP REQUEST FAILED .... On Failure ... calling  show Error !");
                showError();
            }
        });
    }

    private void showError() {
        mWelcomeScreen.setVisibility(View.VISIBLE);
        mWaitingLayout.setVisibility(View.VISIBLE);
        mWelcomeProgressBar.setVisibility(View.GONE);
        mErrorText.setVisibility(View.VISIBLE);
        mResultLayout.setVisibility(View.GONE);
    }

    private void showWait(){
        mWelcomeScreen.setVisibility(View.VISIBLE);
        mLoadingText.setText(getResources().getString(R.string.loading_string, mOwner, mRepo));
        mWaitingLayout.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.GONE);
        mResultLayout.setVisibility(View.GONE);
        mWelcomeProgressBar.setVisibility(View.VISIBLE);
    }

    private void showResult(){
        mWelcomeScreen.setVisibility(View.GONE);
        mRepoTextView.setText(getResources().getString(R.string.repo_link,mOwner,mRepo));
        mResultLayout.setVisibility(View.VISIBLE);
    }

    private void updateUi(List<Contributor> contributors){
        if(contributors != null && contributors.size() > 0 ){
            showResult();
        }
        else{
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
            QueryPreferences.setStoredRepo(getActivity(), mOwner, mRepo);
            showWait();
            loadContributors();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
