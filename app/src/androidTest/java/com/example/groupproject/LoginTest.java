package com.example.groupproject;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Tests responsible for checking that sign in is working properly
 * NOTE: On occasion a test will fail due to Robotium being
 * unable to find the text/button it needs to click on. This is generally fixed
 * when you run the program again
 *
 * @author riona
 */
public class LoginTest {
    private Solo solo;
    String mockUsername = "testUser0"; // Coded to be unable to be a username
    String mockPassword = "password";
    String domainToAppend = "@cmput301-c6741.web.app";

    String knownUser = "riona";
    String knownPassword = "password";
    String invalidPassword = "short";

    FireStoreHandler FSH = new FireStoreHandler();

    // Initializes screen to launch on login screen.
    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);

    // Before each test is run it checks that it is on the login screen and the previous user is signed out
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // initialize Login as the test environment
        solo.assertCurrentActivity("Wrong Activity", Login.class);

        FirebaseAuth.getInstance().signOut();
    }
    // Test for when a valid sign in is used, redirects you to main screen
    @Test
    public void SignInTest() {
        solo.enterText((EditText) solo.getView(R.id.username_field), knownUser);
        solo.enterText((EditText) solo.getView(R.id.password_field), knownPassword);
        solo.scrollUp();
        solo.clickOnButton("SIGN IN");
        solo.assertCurrentActivity("Not in main screen", Login.class);
    }

    // Test for when you enter an invalid username (ie. one that does not exist) during sign in
    @Test
    public void SignInInvalidUsername() {
        solo.enterText((EditText) solo.getView(R.id.username_field), mockUsername);
        solo.enterText((EditText) solo.getView(R.id.password_field), knownPassword);
        solo.clickOnButton("SIGN IN");
        assertTrue(solo.waitForLogMessage("loginUserWithEmail:failed"));
        solo.assertCurrentActivity("Must still be on login", Login.class);
    }

    // Test for when you enter an incorrect password during sign in
    @Test
    public void SignInInvalidPassword() {
        solo.enterText((EditText) solo.getView(R.id.username_field), knownUser);
        solo.enterText((EditText) solo.getView(R.id.password_field), invalidPassword);
        solo.scrollUp();
        solo.clickOnButton("SIGN IN");
        assertTrue(solo.waitForLogMessage("loginUserWithEmail:failed"));
        solo.assertCurrentActivity("Must still be on login", Login.class);
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
