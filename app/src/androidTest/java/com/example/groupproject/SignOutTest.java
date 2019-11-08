package com.example.groupproject;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


/**
 * Tests for checking the functionality of signing out from
 * the main screen.
 *
 * @author riona
 */
public class SignOutTest {
    private Solo solo;
    String username = "riona";
    String password = "password";
    String domainToAppend = "@cmput301-c6741.web.app";

    FirebaseAuth.AuthStateListener mAuth;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void SignOutTest() {
        solo.clickOnButton("SIGN OUT");
        solo.waitForDialogToOpen();
        solo.clickOnButton("sign out");
        solo.assertCurrentActivity("Go to Login Screen", Login.class);
    }

    @Test
    public void SignOutCancelTest() {
        solo.clickOnButton("SIGN OUT");
        solo.waitForDialogToOpen();
        solo.clickOnButton("CANCEL");
        solo.assertCurrentActivity("Stay on current screen", MainActivity.class);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}