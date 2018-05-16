package com.ferfig.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ferfig.bakingapp.ui.MainActivity;
import com.ferfig.bakingapp.ui.adapter.MainActivityRecipesAdapter;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public class RecipesLoadedTest {

    private static final Integer TOTAL_NUM_RECIPES = 4;
    private CountingIdlingResource mMainActivityIdlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setIdlingResources(){
        MainActivity mainActivity = mainActivityActivityTestRule.getActivity();
        if ( mainActivity != null) {
            mMainActivityIdlingResource = mainActivity.getIdlingResourceCounter();
            IdlingRegistry.getInstance().register(mMainActivityIdlingResource);
        }
    }

    @Test
    public void checkRecipesLoaded(){
        //check that all the recipes are loaded
        onView(withId(R.id.rvMainRecyclerView)).check(
                new RecyclerViewAdapterItemCountAssertion(is(TOTAL_NUM_RECIPES)));

        //Another way of doing the adapter count without the custom ViewAssertion
        MainActivity mainActivity = mainActivityActivityTestRule.getActivity();
        View genericView = mainActivity.findViewById(R.id.rvMainRecyclerView);
        assertThat(genericView, notNullValue());
        assertThat(genericView, instanceOf(RecyclerView.class));
        RecyclerView recyclerView = (RecyclerView) genericView;
        RecyclerView.Adapter recipesAdapter = recyclerView.getAdapter();
        assertThat(recipesAdapter, instanceOf(MainActivityRecipesAdapter.class));
        assertThat(recipesAdapter.getItemCount(), equalTo(TOTAL_NUM_RECIPES));
    }

    @After
    public void unsetIdlingResources(){
        IdlingRegistry.getInstance().unregister(mMainActivityIdlingResource);
    }

    class RecyclerViewAdapterItemCountAssertion implements ViewAssertion {
        private final Matcher<Integer> mMatcher;

        RecyclerViewAdapterItemCountAssertion(Matcher<Integer> matcher) {
            this.mMatcher = matcher;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            RecyclerView mainRecipesRecyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = mainRecipesRecyclerView.getAdapter();
            assertThat(adapter.getItemCount(), mMatcher);
        }
    }
}
