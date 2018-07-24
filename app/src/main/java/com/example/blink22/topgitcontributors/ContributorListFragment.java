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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContributorListFragment extends Fragment{

    private String mOwner;
    private String mRepo;
    private ContributorAdapter mAdapter;
    private EditRepoDialog mEditRepoDialog;

    private int REQUEST_CHANGE_REPO = 1;
    public String dialogRepo = "ChangeRepoDialog";
    private static final String TAG = "ContributorListFragment";

    @BindView(R.id.contributors_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.contributor_list_repo_title_text_view)
    TextView mRepoTextView;

    @BindView(R.id.contributors_linear_layout)
    LinearLayout mResultLayout;

    @BindView(R.id.loading_layout)
    LinearLayout mWaitingLayout;

    @BindView(R.id.loading_repo_text_view)
    TextView mLoadingText;

    @OnClick(R.id.loading_not_good_text_view)
    public void onClickNotGood() {
        QueryPreferences.setStoredRepo(getActivity(), null, null);
        getSavedRepo();
        loadContributors();
    }

    @BindView(R.id.welcome_screen)
    LinearLayout mWelcomeScreen;

    @BindView(R.id.welcome_progress_bar)
    ProgressBar mWelcomeProgressBar;

    @BindView(R.id.netowrk_error_text_view)
    TextView mErrorText;

    @OnClick(R.id.netowrk_error_text_view)
    public void onClickError() {
        mErrorText.setVisibility(View.GONE);
        loadContributors();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contributor_list, container, false);
        ButterKnife.bind(this, v);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        Log.d(TAG, "Almost done with onCreateView....");

        if(mAdapter == null){
            Log.d(TAG, "Calling loadContributors from OnCreateView -> adapter is NULL");
            loadContributors();
        }else{
            showWait();
            updateUi(mAdapter.getContributors());
        }

        return v;

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
