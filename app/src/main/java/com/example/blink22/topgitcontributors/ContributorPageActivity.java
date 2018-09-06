package com.example.blink22.topgitcontributors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class ContributorPageActivity extends SingleFragmentActivity {
    private ContributorPageFragment mFragment;

    public static Intent newIntent (Context context, Uri uri){
        Intent i = new Intent(context, ContributorPageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    public Fragment createFragment() {
        mFragment = ContributorPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        if(mFragment.canGoBack()){
            mFragment.goBack();
        }
        else{
            super.onBackPressed();
        }
    }

}
