package com.ferfig.bakingapp;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ferfig.bakingapp.ui.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class RecipesLoadedTest {

    private static final Integer TOTAL_NUM_RECIPES = 4;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkRecipesLoaded(){
        //check that all the 4 recipes are loaded
        onView(withId(R.id.rvMainRecyclerView)).check(
                new RecyclerViewAdapterItemCountAssertion(is(TOTAL_NUM_RECIPES)));
    }

    class RecyclerViewAdapterItemCountAssertion implements ViewAssertion {
        private final Matcher<Integer> mMatcher;

        RecyclerViewAdapterItemCountAssertion(Matcher matcher) {
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
