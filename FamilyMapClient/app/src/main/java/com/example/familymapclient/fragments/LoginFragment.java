package com.example.familymapclient.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.familymapclient.fragments.tasks.LoginTask;
import com.example.familymapclient.fragments.tasks.RegisterTask;
import com.example.familymapclient.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Request.RegisterRequest;

public class LoginFragment extends Fragment {

    private EditText serverHostField;
    private EditText serverPortField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private RadioButton maleButton;
    private RadioButton femaleButton;
    private Button signInButton;
    private Button registerButton;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkLoginFieldsForEmptyValues();
            checkRegisterFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) { this.listener = listener; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostField = view.findViewById(R.id.serverHostField);
        serverPortField = view.findViewById(R.id.serverPortField);
        usernameField = view.findViewById(R.id.usernameField);
        passwordField = view.findViewById(R.id.passwordField);
        firstNameField = view.findViewById(R.id.firstNameField);
        lastNameField = view.findViewById(R.id.lastNameField);
        emailField = view.findViewById(R.id.emailField);
        maleButton = view.findViewById(R.id.maleButton);
        femaleButton = view.findViewById(R.id.femaleButton);
        signInButton = view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);

        serverHostField.addTextChangedListener(textWatcher);
        serverPortField.addTextChangedListener(textWatcher);
        usernameField.addTextChangedListener(textWatcher);
        passwordField.addTextChangedListener(textWatcher);
        firstNameField.addTextChangedListener(textWatcher);
        lastNameField.addTextChangedListener(textWatcher);
        emailField.addTextChangedListener(textWatcher);

        checkLoginFieldsForEmptyValues();
        checkRegisterFieldsForEmptyValues();

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LoginRequest loginRequest = new LoginRequest(usernameField.getText().toString(),
                        passwordField.getText().toString());

                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        if (bundle.getBoolean("success")) {
                            /*
                            String personID = bundle.getString("personID", "");
                            DataCache dataCache = DataCache.getInstance();
                            Person user = dataCache.getPerson(personID);
                            String firstName = user.getFirstName();
                            String lastName = user.getLastName();
                            Toast.makeText(getActivity(), getString(R.string.loginToast, firstName,
                                    lastName), Toast.LENGTH_LONG).show();

                             */
                            listener.notifyDone();
                        } else {
                            Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                LoginTask loginTask = new LoginTask(uiThreadMessageHandler, loginRequest,
                        serverHostField.getText().toString(), serverPortField.getText().toString());
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(loginTask);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String gender = "m";

                if (maleButton.isChecked()) {
                    gender = "m";
                }
                if (femaleButton.isChecked()) {
                    gender = "f";
                }

                RegisterRequest registerRequest = new RegisterRequest(usernameField.getText().toString(),
                        passwordField.getText().toString(), emailField.getText().toString(),
                        firstNameField.getText().toString(), lastNameField.getText().toString(), gender);

                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        if (bundle.getBoolean("success")) {
                            /*
                            String personID = bundle.getString("personID", "");
                            DataCache dataCache = DataCache.getInstance();
                            Person user = dataCache.getPerson(personID);
                            String firstName = user.getFirstName();
                            String lastName = user.getLastName();
                            Toast.makeText(getActivity(), getString(R.string.loginToast, firstName,
                                    lastName), Toast.LENGTH_LONG).show();

                             */
                            listener.notifyDone();
                        } else {
                            Toast.makeText(getActivity(), "Register Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                RegisterTask registerTask = new RegisterTask(uiThreadMessageHandler, registerRequest,
                        serverHostField.getText().toString(), serverPortField.getText().toString());
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(registerTask);
            }
        });

        return view;
    }

    private void checkLoginFieldsForEmptyValues() {
        String serverHost = serverHostField.getText().toString();
        String serverPort = serverPortField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        signInButton.setEnabled(serverHost.length() > 0 && serverPort.length() > 0 &&
                username.length() > 0 && password.length() > 0);
    }

    private void checkRegisterFieldsForEmptyValues() {
        String serverHost = serverHostField.getText().toString();
        String serverPort = serverPortField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();

        registerButton.setEnabled(serverHost.length() > 0 && serverPort.length() > 0 &&
                username.length() > 0 && password.length() > 0 && firstName.length() > 0 &&
                lastName.length() > 0 && email.length() > 0);
    }
}