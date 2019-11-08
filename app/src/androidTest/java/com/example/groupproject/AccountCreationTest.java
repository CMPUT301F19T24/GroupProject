package com.example.groupproject;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;

public class AccountCreationTest extends FireStoreHandler {
    private Solo solo;
    String mockUsername = "testUser";
    String mockPassword = "password";
    String domainToAppend = "@cmput301-c6741.web.app";

    String knownUser = "riona";
    String knownPassword = "password";
    String invalidPassword = "short";

    FireStoreHandler FSH = new FireStoreHandler();

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // initialize Login as the test environment
        solo.assertCurrentActivity("Wrong Activity", Login.class);
    }

    // Test for when you try to create an account that's name is already taken
    @Test
    public void AccountUnavailableTest(){
        solo.clickOnText("Create one", 1, true);
        solo.enterText((EditText) solo.getView(R.id.new_username), knownUser);
        solo.enterText((EditText) solo.getView(R.id.new_password), knownPassword);
        solo.enterText((EditText) solo.getView(R.id.confirm_password), knownPassword);
        solo.clickOnButton("REGISTER");
        assertTrue(solo.waitForLogMessage("loginUserWithEmail:usernameUnavailable"));
    }

    @Test
    public void PasswordInvalidTest(){
        solo.clickOnText("Create one", 1, true);
        solo.enterText((EditText) solo.getView(R.id.new_username), knownUser);
        solo.enterText((EditText) solo.getView(R.id.new_password), invalidPassword);
        solo.enterText((EditText) solo.getView(R.id.confirm_password), invalidPassword);
        solo.clickOnButton("REGISTER");
        assertTrue(solo.waitForLogMessage("loginUserWithEmail:weakPassword"));
    }

    @Test
    public void MismatchedPasswordTest(){
        solo.clickOnText("Create one", 1, true);
        solo.enterText((EditText) solo.getView(R.id.new_username), knownUser);
        solo.enterText((EditText) solo.getView(R.id.new_password), invalidPassword);
        solo.enterText((EditText) solo.getView(R.id.confirm_password), knownPassword);
        solo.clickOnButton("REGISTER");
        assertTrue(((EditText) solo.getView(R.id.confirm_password)).getError().toString() == "Passwords must match");
    }

    @Test
    public void SignInTest() {
        solo.clickOnButton("SIGN IN");
        solo.enterText((EditText) solo.getView(R.id.username_field), knownUser);
        solo.enterText((EditText) solo.getView(R.id.password_field), knownPassword);
        solo.assertCurrentActivity("Not in main screen", Login.class);

    }
}
