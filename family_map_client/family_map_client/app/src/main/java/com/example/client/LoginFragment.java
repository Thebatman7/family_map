package com.example.client;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.LoginResult;
import Service.Result.RegisterResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment} factory method to create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // the fragment initialization parameters
    private static final String SUCCESS = "Success";
    private static final String ALL_SUCCESS = "All was successful";
    //we add constants for the two states of the ratio button
    private static final int MALE = 0;
    private static final int FEMALE = 1;

    private EditText serverHostField;
    private String serverHost;
    private EditText serverPortField;
    private String serverPort;
    //we create an EditText object for the user name input
    private EditText usernameField;
    private String username;
    private EditText passwordField;
    private String password;
    private EditText firstNameField;
    private String firstName;
    private EditText lastNameField;
    private String lastName;
    private EditText emailField;
    private String email;
    //we add a variable for the ratio button selection
    private String gender;
    //we create a RadioGroup for the selection of male or female
    private RadioGroup radioGroup;
    //we create a Button object for the sign in option
    private Button signInButton;
    private Button registerButton;
    //we create a Handler object to send a message back to the MainActivity
    private Handler messageHandler;
    private boolean allSuccess;
    //access to singleton
    DataCache dataCache;

    //default constructor
    public LoginFragment() {}

    //constructor with parameter to be able to send a message back to activity that contains this fragment
    public LoginFragment(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //this method is where we inflate the layout for the fragment's view and where we wire up widgets
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        We explicitly inflate the fragment's view by calling LayoutInflater.inflate(...) and passing
        in the layout resources ID. The second parameter is our view's parent, which is usually needed
        to configure the widgets properly. The third parameter tell the layout inflater whether to
        add the inflated view to the view's parent. We pass false because we will add that view
        in the activity's code.
        R is short for resources file where all the ids and layouts are.
        */
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //we get a reference to the EditText object. This is done differently in a fragment than it's in an activity
        serverHostField = view.findViewById(R.id.serverHostField);//this is how we get the reference to the EditText in .xml file
        //we add a listener with an anonymous class that implements the verbose TextWatcher interface
        serverHostField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //we can leave this blank since we do not do anything before the user'input
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //CharSequence is the user's input
                serverHost = s.toString();
                //the user can type inside the fields in any order so we call this method for all fields
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //we can leave this blank since we do not do anything after the user'input
            }
        });

        serverPortField = view.findViewById(R.id.serverPortField);
        serverPortField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverPort = s.toString();
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //we get a reference to the EditText object. This is done differently in a fragment than it's in an activity
        usernameField = view.findViewById(R.id.usernameField);//this is how we get the reference to the EditText in .xml file
        //we add a listener with an anonymous class that implements the verbose TextWatcher interface
        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //we can leave this blank since we do not do anything before the user'input
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //CharSequence is the user's input
                username = s.toString();
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //we can leave this blank since we do not do anything after the user'input
            }
        });

        passwordField = view.findViewById(R.id.passwordField);
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        firstNameField = view.findViewById(R.id.firstNameField);
        firstNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstName = s.toString();
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lastNameField = view.findViewById(R.id.lastNameField);
        lastNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastName = s.toString();
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        emailField = view.findViewById(R.id.emailField);
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
                checkUserEntries();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //to get the info of a radio button we use the radio group reference
        radioGroup = view.findViewById(R.id.radioGroup);
        /*
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
        gender = (String)radioButton.getText();
        We have an anonymous inner class (the OnCheckedChangeListener for the RadioGroup)
        */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //radioButton will get the info of the button that was selected
                View radioButton = radioGroup.findViewById(checkedId);
                //we simply get the index of the radio button selected
                int index = radioGroup.indexOfChild(radioButton);
                //for this case we use a switch to assign the gender based on the button clicked
                switch(index) {
                    case MALE:
                        gender = "M";
                        break;
                    case FEMALE:
                        gender = "F";
                        break;

                    default:
                        break;
                }
                checkUserEntries();
            }
        });

        //SIGN IN button wire up
        signInButton = (Button) view.findViewById(R.id.signInButton);
        signInButton.setEnabled(false);
        //we set a listener on the button to perform actions if clicked
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up a handler that will process messages from the task and make updates on the UI/Main thread
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean(SUCCESS);

                        if (success == true) {
                            //we set up another handler to call our GetDataTask and receive a message
                            Handler uiThreadDataMessage = new Handler(Looper.getMainLooper()){
                                @Override
                                public void handleMessage(Message message) {
                                    Bundle bundle = message.getData();
                                    allSuccess = bundle.getBoolean(ALL_SUCCESS);
                                    if (allSuccess == true) { //??? how to show the user name and last name
                                        dataCache = DataCache.getInstance();
                                        Toast.makeText(getContext(), dataCache.getUserFirstName() +
                                                " " + dataCache.getUserLastName(), Toast.LENGTH_LONG).show();
                                        //LoginFragment.this.looks to the other class so we can use the method
                                        LoginFragment.this.sendMessage(allSuccess);
                                    }
                                }
                            };
                            GetDataTask task = new GetDataTask(uiThreadDataMessage);
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.submit(task);
                        }
                        else {
                            /*
                            A toast is a little different in a fragment than it is in an activity, in
                            activities we can use activityName.this as the first parameter, in fragments
                            we use getContext() to identify where this toast will be displayed.
                            */
                            Toast.makeText(getContext(), R.string.loginFail, Toast.LENGTH_LONG).show();
                        }
                    }
                };
                LoginRequest signInRequest = new LoginRequest(username, password);
                //create and execute login task on a separate thread
                LoginTask task = new LoginTask(uiThreadMessageHandler, signInRequest);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            }
        });


        //REGISTER button wire up
        registerButton = (Button) view.findViewById(R.id.registerButton);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set up a handler that will process messages from the task and make updates on the UI/Main thread
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        boolean success = bundle.getBoolean(SUCCESS);

                        if (success == true) {
                            //we set up another handler to call our GetDataTask and receive a message
                            Handler uiThreadDataMessage = new Handler(Looper.getMainLooper()){
                                @Override
                                public void handleMessage(Message message) {
                                    Bundle bundle = message.getData();
                                    allSuccess = bundle.getBoolean(ALL_SUCCESS);
                                    if (allSuccess == true) { //??? how to show the user name and last name
                                        dataCache = DataCache.getInstance();
                                        Toast.makeText(getContext(), dataCache.getUserFirstName() +
                                                " " + dataCache.getUserLastName(), Toast.LENGTH_LONG).show();
                                        //LoginFragment.this.looks to the other class so we can use the method
                                        LoginFragment.this.sendMessage(allSuccess);
                                    }
                                }
                            };
                            GetDataTask task = new GetDataTask(uiThreadDataMessage);
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.submit(task);
                        }
                        else {
                            /*
                            A toast is a little different in a fragment than it is in an activity, in
                            activities we can use activityName.this as the first parameter, in fragments
                            we use getContext() to identify where this toast will be displayed
                            */
                            Toast.makeText(getContext(), R.string.registerFail, Toast.LENGTH_LONG).show();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(username, password, email,
                        firstName, lastName, gender);
                //create and execute register task on a separate thread
                RegisterTask task = new RegisterTask(uiThreadMessageHandler, registerRequest);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            }
        });

        return view;//we return the the inflated View object to the hosting activity
    }

    //we check we have the user filled the required fields
    private void checkUserEntries() {
        /*
        We check the length of the user's input. Because we get the strings from s which can return
        empty string instead of null we have to check that the length of the string is greater than 0
        to know that they are not empty so we can enable the buttons accordingly. We can get a null if
        the user never types anything inside a field. The user can also type something, erase it and
        leave it blank which would make s not null but empty.
        */
        if (serverHost != null && serverHost.length() > 0 &&
                serverPort != null && serverPort.length() > 0 &&
                username != null && username.length() > 0 &&
                password != null && password.length() > 0) {
            //depending on user's input we enable the button
            signInButton.setEnabled(true);
            setServerPortAndHost();
        }
        else {
            //if user deletes the input of a field and leaves it blank we disable button
            signInButton.setEnabled(false);
        }
        if (serverHost != null && serverHost.length() > 0 &&
                serverPort != null && serverPort.length() > 0 &&
                username != null && username.length() > 0 &&
                password != null && password.length() > 0 &&
                firstName != null && firstName.length() > 0 &&
                lastName != null && lastName.length() > 0 &&
                email != null && email.length() > 0 && gender != null) {
            //depending on user's input we enable the button
            registerButton.setEnabled(true);
            setServerPortAndHost();
        }
        else {
            //if user deletes the input of a field and leaves it blank we disable button
            registerButton.setEnabled(false);
        }
    }

    private void sendMessage(boolean success) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(ALL_SUCCESS, success);

        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }

    private void setServerPortAndHost(){
        DataCache.getInstance().setServePort(serverPort);
        DataCache.getInstance().setServerHost(serverHost);
    }
}