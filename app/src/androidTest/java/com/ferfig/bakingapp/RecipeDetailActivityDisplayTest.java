package com.ferfig.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ferfig.bakingapp.ui.MainActivity;
import com.ferfig.bakingapp.ui.adapter.MainActivityRecipesAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityDisplayTest {

    private static final String RECIPE_NAME = "Nutella Pie";

    private CountingIdlingResource mMainActivityIdlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivity =
        new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setIdlingResources(){
        MainActivity mainActivity = mMainActivity.getActivity();
        if ( mainActivity != null) {
            mMainActivityIdlingResource = mainActivity.getIdlingResourceCounter();
            IdlingRegistry.getInstance().register(mMainActivityIdlingResource);
        }
    }

    @Test
    public void display1stRecipeDetailsScreen(){
        // get the recyclerview and click the first recipe: nutella pie
        onView(withId(R.id.rvMainRecyclerView)).perform(
                RecyclerViewActions.<MainActivityRecipesAdapter.RecipsViewHolder>actionOnItemAtPosition(0, click()));
        // check if the details activity is displayed
        onView(withText(RECIPE_NAME)).check(matches(isDisplayed()));
    }

    @After
    public void unsetIdlingResources(){
        IdlingRegistry.getInstance().unregister(mMainActivityIdlingResource);
    }
}
