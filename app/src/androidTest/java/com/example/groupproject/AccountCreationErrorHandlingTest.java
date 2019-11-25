package com.example.groupproject;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.groupproject.data.firestorehandler.FireStoreHandler;
import com.example.groupproject.ui.login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;

/**
 * Responsible for testing error handling of createAccountDialog.
 * NOTE: The testing for actual account creation is in AccountCreationTest
 * due to it requiring a different set of rules.
 * NOTE: On occasion a test will fail due to Robotium being
 * unable to find the text/button it needs to click on. This is generally fixed
 * when you run the program again
 *
 * @author riona
 *
 */
public class AccountCreationErrorHandlingTest extends FireStoreHandler {
    private Solo solo;
    String mockUsername = "testUser";
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

    // Test for when you try to create an account that's name is already taken at account creation
    @Test
    public void AccountUnavailableTest(){
        solo.scrollDown();
        solo.clickOnText("Create one");
        solo.enterText((EditText) solo.getView(R.id.new_username), knownUser);
        solo.enterText((EditText) solo.getView(R.id.new_password), knownPassword);
        solo.enterText((EditText) solo.getView(R.id.confirm_password), knownPassword);
        solo.clickOnButton("REGISTER");
        assertTrue(solo.waitForLogMessage("loginUserWithEmail:usernameUnavailable"));
    }

    // Test for when you input an invalid password at account creation
    @Test
    public void PasswordInvalidTest(){
        solo.scrollDown();
        solo.clickOnText("Create one");
        solo.enterText((EditText) solo.getView(R.id.new_username), knownUser);
        solo.enterText((EditText) solo.getView(R.id.new_password), invalidPassword);
        solo.enterText((EditText) solo.getView(R.id.confirm_password), invalidPassword);
        solo.clickOnButton("REGISTER");
        assertTrue(solo.waitForLogMessage("loginUserWithEmail:weakPassword"));
    }

    // Test for when you entered passwords that do not match in account creation
    @Test
    public void MismatchedPasswordTest(){
        solo.scrollDown();
        solo.clickOnText("Create one");
        solo.enterText((EditText) solo.getView(R.id.new_username), knownUser);
        solo.enterText((EditText) solo.getView(R.id.new_password), invalidPassword);
        solo.enterText((EditText) solo.getView(R.id.confirm_password), knownPassword);
        solo.clickOnButton("REGISTER");
        assertTrue(((EditText) solo.getView(R.id.confirm_password)).getError().toString().equals("Passwords must match"));
    }
}
