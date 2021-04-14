package com.example.berry.ayuda_workplace.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.berry.ayuda_workplace.R;
import com.example.berry.ayuda_workplace.WaveView;
import com.example.berry.ayuda_workplace.activities.ProfileActivity;
import com.example.berry.ayuda_workplace.activities.SuperActivity;
import com.example.berry.ayuda_workplace.adapters.TenantsAdapter;
import com.example.berry.ayuda_workplace.api.RetrofitClient;
import com.example.berry.ayuda_workplace.image.DownloadImage;
import com.example.berry.ayuda_workplace.interfaces.TenantsCommunication;
import com.example.berry.ayuda_workplace.models.DefaultResponse;
import com.example.berry.ayuda_workplace.models.Location;
import com.example.berry.ayuda_workplace.models.LocationsResponse;
import com.example.berry.ayuda_workplace.models.LoginResponse;
import com.example.berry.ayuda_workplace.models.User;
import com.example.berry.ayuda_workplace.storage.SharedPrefManager;
import com.robotemi.sdk.BatteryData;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SuperFragment extends Fragment implements OnClickListener,
        Robot.TtsListener, OnGoToLocationStatusChangedListener {

    TextView promptLabel, promptNavigationLabel, confirmationLabel, tenantTitleLabel,
            tenantAvailabilityLabel, sorryTextView, differentLocationTextView, noResultsLabel,
            welcomeLabel, passwordLabel, incorrectLogin, chargingTextView;
    EditText passwordEditText;
    Button activateButton, passwordButton, checkButton, clearRedButton, cancelButton, OkButton,
            exitButton1, exitButton2;
    Animation animFadeOut, animFadeIn;
    WaveView waveView;
    ImageButton keyboardButton, clearButton, passwordPromptButton;
    LinearLayout linearLayout, linearLayout2;
    ConstraintLayout constraintLayout;
    ViewGroup.LayoutParams params;
    RecyclerView recyclerView;
    String  robotStatus, locationName;
    ImageView floorPlanImage;
    RelativeLayout loadingPanel;
    List<Location> locationsList;
    TenantsAdapter adapter;
    BatteryData batteryData;
    int batteryPercentage, locationId, isCharging, loopCount;
    SpeechRecognizer speechRecognizer;
    User user;
    private static final String TAG = "SpeechRecognizer";
    String serialNumber;
    Handler handler;
    Runnable r;
    boolean isNavigating;
    boolean isActivated;
    ConnectivityManager connManager;
    NetworkInfo mWifi;

    int userID, activation_count, exitCount1, exitCount2;
    Robot robot;

    public SuperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        robot = Robot.getInstance();

        robot.addTtsListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);

        //robot.goTo("start");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        waveView.destroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        userID = user.getUserID();
        activation_count = 0;
        exitCount1 = 0;
        exitCount2 = 0;
        robotStatus = "none";
        isNavigating = false;
        isActivated = false;


        connManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final View myView = inflater.inflate(R.layout.super_fragment, container, false);
        welcomeLabel = myView.findViewById(R.id.welcomeLabel);
        promptLabel = myView.findViewById(R.id.promptActivationLabel);
        activateButton = myView.findViewById(R.id.buttonActivate);
        waveView = myView.findViewById(R.id.superWaveview);
        linearLayout = myView.findViewById(R.id.linearLayout);
        linearLayout2 = myView.findViewById(R.id.linearLayout2);
        constraintLayout = myView.findViewById(R.id.constraintLayout);
        keyboardButton = myView.findViewById(R.id.keyboardButton);
        passwordEditText = myView.findViewById(R.id.passwordEditText);
        passwordButton = myView.findViewById(R.id.passwordButton);
        passwordLabel = myView.findViewById(R.id.passwordLabel);
        incorrectLogin = myView.findViewById(R.id.incorrectLabel);
        clearButton = myView.findViewById(R.id.clearButton);
        floorPlanImage = myView.findViewById(R.id.floorPlanImage);
        promptNavigationLabel = myView.findViewById(R.id.promptNavigationLabel);
        confirmationLabel = myView.findViewById(R.id.confirmationLabel);
        tenantTitleLabel = myView.findViewById(R.id.tenantTitleLabel);
        tenantAvailabilityLabel = myView.findViewById(R.id.tenantAvailabilityLabel);
        exitButton1 = myView.findViewById(R.id.exitButton1);
        exitButton2 = myView.findViewById(R.id.exitButton2);
        chargingTextView = myView.findViewById(R.id.chargingTextView);

        clearRedButton = myView.findViewById(R.id.clearRedButton);
        checkButton = myView.findViewById(R.id.checkButton);
        cancelButton = myView.findViewById(R.id.cancelButton);
        sorryTextView = myView.findViewById(R.id.sorryTextView);
        differentLocationTextView = myView.findViewById(R.id.differentLocationTextView);
        OkButton = myView.findViewById(R.id.OkButton);
        loadingPanel = myView.findViewById(R.id.loadingPanel);
        noResultsLabel = myView.findViewById(R.id.noResultsLabel);
        passwordPromptButton = myView.findViewById(R.id.passwordPromptButton);


        activateButton.setOnClickListener(this);
        keyboardButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
        constraintLayout.setOnClickListener(this);
        passwordEditText.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        clearRedButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        OkButton.setOnClickListener(this);
        passwordPromptButton.setOnClickListener(this);
        exitButton1.setOnClickListener(this);
        exitButton2.setOnClickListener(this);

        activateButton.setVisibility(View.VISIBLE);
        welcomeLabel.setVisibility(View.VISIBLE);
        promptLabel.setVisibility(View.VISIBLE);

        animFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);


        robot.tiltAngle(45);




        getActivity()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getActivity()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        params = linearLayout.getLayoutParams();


        recyclerView = myView.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()
                .getApplicationContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //initSpruce();
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                Log.d("isActivated", String.valueOf(isActivated));

                if(!isActivated) {

                    robot.tiltAngle(50);



                    robot.hideTopBar();
                    serialNumber = robot.getSerialNumber();
                    batteryData = robot.getBatteryData();
                    batteryPercentage = batteryData.getBatteryPercentage();

                    robot.toggleNavigationBillboard(true);
                    isCharging = 0;
                    if (batteryData.isCharging())
                        isCharging = 1;



                    if (batteryPercentage < 20) {



                        robot.goTo("home base");
                        robotStatus = "low battery";

                        ArrayList<View> everythingDisappear = new ArrayList<View>(Arrays.asList(
                                loadingPanel, linearLayout, linearLayout2, keyboardButton,
                                clearButton, floorPlanImage, promptLabel, activateButton,
                                welcomeLabel, promptNavigationLabel, confirmationLabel,
                                clearRedButton, checkButton, cancelButton, sorryTextView,
                                differentLocationTextView, noResultsLabel, OkButton,
                                passwordLabel, passwordEditText, passwordButton));

                        disappear(everythingDisappear);

                        ArrayList<View> chargingAppear = new ArrayList<View>(Arrays.asList(
                                chargingTextView
                        ));


                        appear(chargingAppear);


                    }
                    Log.d("TESTSuper", "PART 3a");


                    if (batteryPercentage > 75) {


                        robot.goTo("start");
                        robotStatus = "welcome";


                        ArrayList<View> readyDisappear = new ArrayList<View>(Arrays.asList(loadingPanel,
                                linearLayout, linearLayout2, keyboardButton, clearButton,
                                floorPlanImage, promptNavigationLabel, chargingTextView,
                                confirmationLabel, clearRedButton, checkButton, cancelButton,
                                sorryTextView, differentLocationTextView, noResultsLabel, OkButton,
                                passwordLabel, passwordEditText, passwordButton));
                        disappear(readyDisappear);

                        ArrayList<View> readyAppear = new ArrayList<View>(Arrays.asList(promptLabel,
                                welcomeLabel, activateButton, passwordPromptButton));
                        appear(readyAppear);


                    }




                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi()
                            .recordBattery(
                            serialNumber,
                            batteryPercentage,
                            isCharging
                    );
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call,
                                               Response<DefaultResponse> response) {


                            // response.body().getMsg();
                            try {
                                Log.d("battery_data", response.body().getMsg());
                            } catch (Exception e) {
                                Log.d("battery_data", "message is null");

                            }
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            Log.d("battery_data", "failue");
                        }
                    });


                    //sendScreenShot();
                }
            }
        }, 5, 300, TimeUnit.SECONDS);




        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {
                robot.hideTopBar();

                if(!isNavigating && isActivated) {
                    ArrayList<View> timeoutDisappear = new ArrayList<View>(Arrays.asList(
                            loadingPanel, linearLayout, linearLayout2, keyboardButton,
                            clearButton, floorPlanImage, promptNavigationLabel, confirmationLabel,
                            clearRedButton, checkButton, cancelButton, sorryTextView,
                            differentLocationTextView, noResultsLabel, OkButton));
                    disappear(timeoutDisappear);
                    isActivated = false;

                    //closeKeyboard();

                    ArrayList<View> activationPrompt = new ArrayList<View>(Arrays.asList(promptLabel,
                            welcomeLabel, activateButton));

                    appear(activationPrompt);

                    Log.d("UserInteraction", "Logged");
                }
            }
        };
        startHandler();

        ((SuperActivity) getActivity()).setUserInteractionListener(new SuperActivity.UserInteractionListener() {
            @Override
            public void onUserInteraction() {

                stopHandler();//stop first and then start
                startHandler();
            }
        });


        return myView;
    }


    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    public void startHandler() {
        handler.postDelayed(r, 120000);
    }

    @Override
    public void onDestroy() {
        stopHandler();
        super.onDestroy();
    }

    public void onClick(View v) {
        connManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

            Fragment fragment = null;
        switch(v.getId()){
            case R.id.buttonActivate:


                    // Do whatever

                    //listen();
                    isActivated = true;
                    robot.tiltAngle(50);
                    ArrayList<View> buttonActivateDisappear = new ArrayList<View>(Arrays.asList(
                            promptLabel, welcomeLabel, activateButton));
                    disappear(buttonActivateDisappear);

                    if (activation_count == 0) {
                        DisplayMetrics dm = new DisplayMetrics();
                        getActivity()
                                .getWindowManager()
                                .getDefaultDisplay()
                                .getMetrics(dm);
                        waveView.initialize(dm);
                    } else
                        waveView.speechStarted();

                    activation_count++;

                    ArrayList<View> buttonActivateAppear = new ArrayList<View>(Arrays.asList(
                            linearLayout2, waveView));
                    appear(buttonActivateAppear);

                    robotStatus = "welcome";
                    TtsRequest ttsRequest = TtsRequest
                            .create("Welcome to the Technology Innovation Center. " +
                                            "Here's a list of " +
                                            "offices I can guide you to",
                                    false);
                    robot.speak(ttsRequest);
                    onTtsStatusChanged(ttsRequest);

                    user = SharedPrefManager.getInstance(getActivity()).getUser();

                    try {
                        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi()
                                .storeInteraction(
                                user.getUserID(),
                                "ACTIVATION",
                                0,
                                robot.getSerialNumber()
                        );

                        call.enqueue(new Callback<DefaultResponse>() {
                            @Override
                            public void onResponse(Call<DefaultResponse> call,
                                                   Response<DefaultResponse> response) {
                                Log.d("storeInteraction", "successful");
                            }

                            @Override
                            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                Log.d("storeInteraction", "failed");
                            }
                        });
                    } catch (Exception e) {
                        Log.d("storeInteraction", "failed2");
                    }



                break;

            case R.id.passwordEditText:
            case R.id.keyboardButton:
//                Log.d("Keyboard", "pressed");
//
//                ArrayList<View> keyboardButtonAppear = new ArrayList<View>(Arrays.asList(
//                        searchEditText, passwordButton));
//                appear(keyboardButtonAppear);
//
//                searchEditText.requestFocus();
//
//                ArrayList<View> keyboardButtonDisappear = new ArrayList<View>(Arrays.asList(
//                        linearLayout, linearLayout2));
//                disappear(keyboardButtonDisappear);

                //openKeyboard();
                break;

            case R.id.constraintLayout:
                closeKeyboard();
                robot.hideTopBar();

                break;


            case R.id.clearButton:
                closeKeyboard();
                isActivated = false;
                ArrayList<View> clearButtonDisappear = new ArrayList<View>(Arrays.asList(waveView,
                        keyboardButton, recyclerView, linearLayout, linearLayout2, passwordLabel,
                        passwordEditText, passwordButton, passwordLabel, clearButton));
                disappear(clearButtonDisappear);

                ArrayList<View> clearButtonAppear = new ArrayList<View>(Arrays.asList(promptLabel,
                        welcomeLabel, activateButton));
                appear(clearButtonAppear);

                break;

            case R.id.clearRedButton:

                ArrayList<View> clearRedDisappear = new ArrayList<View>(Arrays.asList(floorPlanImage,
                        promptNavigationLabel, confirmationLabel, tenantTitleLabel,
                        tenantAvailabilityLabel, clearRedButton, checkButton));
                disappear(clearRedDisappear);

                ArrayList<View> clearRedAppear = new ArrayList<View>(Arrays.asList(recyclerView,
                        linearLayout, clearButton));
                appear(clearRedAppear);

                break;

            case R.id.checkButton:

                ArrayList<View> checkButtonDisappear = new ArrayList<View>(Arrays.asList(floorPlanImage,
                        promptNavigationLabel, confirmationLabel, tenantTitleLabel,
                        tenantAvailabilityLabel, clearRedButton, checkButton));
                disappear(checkButtonDisappear);


                ArrayList<View> checkButtonAppear = new ArrayList<View>(Arrays.asList(waveView,
                        linearLayout2));
                appear(checkButtonAppear);


                robotStatus = "tenant navigation";
                ttsRequest = TtsRequest
                        .create("Navigation confirmed. Please follow me and remain at least 5 " +
                                "feet behind me.", false);
                robot.speak(ttsRequest);
                waveView.speechStarted();

                onTtsStatusChanged(ttsRequest);


                Call<DefaultResponse> callCheck = RetrofitClient.getInstance()
                        .getApi().storeInteraction(
                        user.getUserID(),
                        "NAVIGATION",
                        locationId,
                        robot.getSerialNumber()
                );

                try {
                    callCheck.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call,
                                               Response<DefaultResponse> response) {
                            Log.d("storeInteraction", "successful");
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            Log.d("storeInteraction", "failed");
                        }
                    });
                } catch (Exception e){
                    Log.d("storeInteraction", "failed");

                }
                Log.d("navigation", locationName.toLowerCase());
                robot.goTo(locationName.toLowerCase().trim());


                break;

            case R.id.OkButton:

                ArrayList<View> okButtonDisappear = new ArrayList<View>(Arrays.asList(sorryTextView,
                        differentLocationTextView, OkButton));
                disappear(okButtonDisappear);

                ArrayList<View> okButtonAppear = new ArrayList<View>(Arrays.asList(recyclerView,
                        linearLayout, clearButton));
                appear(okButtonAppear);
                break;

            case R.id.passwordPromptButton:

                serialNumber = robot.getSerialNumber();
                batteryData = robot.getBatteryData();
                batteryPercentage = batteryData.getBatteryPercentage();

                Log.d("superSerial", serialNumber);
                Log.d("superbatteryPercentage", String.valueOf(batteryPercentage));
                Log.d("superisCharging", String.valueOf(isCharging));

                openKeyboard();
                passwordEditText.requestFocus();
                ArrayList<View> everythingDisappear = new ArrayList<View>(Arrays.asList(loadingPanel,
                        linearLayout, linearLayout2, keyboardButton, clearButton, floorPlanImage,
                        promptLabel, activateButton, welcomeLabel,
                        promptNavigationLabel, confirmationLabel, clearRedButton, checkButton,
                        cancelButton, sorryTextView, differentLocationTextView, noResultsLabel,
                        OkButton, chargingTextView));

                disappear(everythingDisappear);

                ArrayList<View> enterPasswordAppear = new ArrayList<>(Arrays.asList(passwordLabel,
                        passwordEditText, passwordButton, clearButton));

                appear(enterPasswordAppear);

                break;

            case R.id.passwordButton:

                User user = SharedPrefManager.getInstance(getActivity()).getUser();

                String password = String.valueOf(passwordEditText.getText()).trim();

                Log.d("LoginCred", user.getUserEmail());
                Log.d("LoginCred", password);


                Call<LoginResponse> checkPassword = RetrofitClient.getInstance().getApi().userLogin(
                                user.getUserEmail().trim(),
                                password);
                checkPassword.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        if (response.code() == 202){
                            stopHandler();

                            Intent intent;
                            intent = new Intent(getActivity(), ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        } else if (response.code() == 402){
                            incorrectLogin.setText("Incorrect password");
                            incorrectLogin.setVisibility(getView().VISIBLE);
                            incorrectLogin.startAnimation(animFadeIn);
                        } else{
                            incorrectLogin.setText("Sorry an error occurred");
                            incorrectLogin.setVisibility(getView().VISIBLE);
                            incorrectLogin.startAnimation(animFadeIn);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {

                    }
                });

        }
        if(fragment!=null){
            displayFragment(fragment);
        }
        } else{
            Toast.makeText( getActivity(),"Sorry wifi is currently unavailable",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void displayFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout, fragment)
                .commit();
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(),
                        View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 4
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(1 * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    private void openKeyboard(){
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(passwordEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeKeyboard(){
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void disappear(final ArrayList<View> views){

        if (getActivity() != null) {
            Activity act = (Activity) getContext();
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (View view : views) {
                        view.startAnimation(animFadeOut);
                        view.setVisibility(getView().GONE);
                    }
                }
            });
        }



    }

    private void appear(final ArrayList<View> views){

        if (getActivity() != null) {
            Activity act = (Activity) getContext();
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (View view : views) {
                        view.setVisibility(getView().VISIBLE);
                        view.startAnimation(animFadeIn);
                    }
                }
            });
        }


    }

    @Override
    public void onTtsStatusChanged(TtsRequest ttsRequest) {
        switch(ttsRequest.getStatus()){

            case STARTED:
                Log.d("onTtsStatusChanged", "STARTED");
                break;

            case COMPLETED:
                Log.d("onTtsStatusChanged", "COMPLETED");
                waveView.speechPaused();

                switch(robotStatus){
                    case "welcome":
                        Log.d("onTtsStatusChanged", "welcome");


                        Call<LocationsResponse> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .getLocations(userID);

                        call.enqueue(new Callback<LocationsResponse>() {
                            @Override
                            public void onResponse(Call<LocationsResponse> call,
                                                   Response<LocationsResponse> response) {
                                Log.d("SuperFrag", "api response") ;
                                Log.d("SuperFrag", String.valueOf(response.body().isError())) ;
                                Log.d("SuperFrag", String.valueOf(response.body().getLocations()
                                        .size())) ;

                                try {
                                    locationsList = response.body().getLocations();
                                    adapter = new TenantsAdapter(getActivity().getApplicationContext(),
                                            locationsList, communication);
                                    recyclerView.setAdapter(adapter);

                                    ArrayList<View> startSearchingAppear
                                            = new ArrayList<View>(Arrays.asList(recyclerView, linearLayout,
                                            clearButton));

                                    appear(startSearchingAppear);


                                } catch (Exception e){
                                    ArrayList<View> errorOccuredAppear = new ArrayList<>(Arrays
                                            .asList(sorryTextView, clearButton));
                                }
                                ArrayList<View> welcomeDisappear
                                        = new ArrayList<View>(Arrays.asList(linearLayout2));
                                disappear(welcomeDisappear);

                            }

                            @Override
                            public void onFailure(Call<LocationsResponse> call, Throwable t) {
                                Log.d("SuperFrag", "api failure") ;
                                ArrayList<View> errorOccuredAppear = new ArrayList<>(Arrays
                                        .asList(sorryTextView, clearButton));

                                ArrayList<View> welcomeDisappear
                                        = new ArrayList<View>(Arrays.asList(linearLayout2));
                                disappear(welcomeDisappear);
                            }
                        });



                        //openKeyboard();
                        break;
                    case "tenant navigation":

                        break;
                    case "navigation completed":

                        if (!robotStatus.equals("low battery")){
                            robot.goTo("start");
                        }
                        robotStatus = "welcome";

                        Log.d("onTtsStatusChanged", "navigation completed");

                        break;
                }

                break;
        }
    }

    TenantsCommunication communication = new TenantsCommunication() {
        @Override
        public void respond(int comLocationID, String comLocationTitle, String comAvailability) {
            ArrayList<View> navigateSelected = new ArrayList<>(Arrays.asList(waveView, keyboardButton,
                    recyclerView, linearLayout, clearButton));
            disappear(navigateSelected);

            ArrayList<View> tenantAvailable =
                    new ArrayList<View>(Arrays.asList(floorPlanImage,
                            promptNavigationLabel, confirmationLabel,tenantTitleLabel,
                            tenantAvailabilityLabel, clearRedButton, checkButton));

            tenantTitleLabel.setText(comLocationTitle);

            locationId = comLocationID;
            locationName = comLocationTitle;

            if(comAvailability.trim().toLowerCase().equals("available")){
                tenantAvailabilityLabel.setText("Available");
            } else{
                tenantAvailabilityLabel.setText("Unavailable");
            }

            Log.d("InterfaceComs", comLocationTitle);
            new DownloadImage(floorPlanImage)
                    .execute("http://54.185.200.51/ayuda/images/tic.png");


            appear(tenantAvailable);


        }
    };



    private void listen() {
        Log.d("conversation", "listen1");


        Intent intent;
        //get the recognize intent
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify the max number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        //User of SpeechRecognizer to "send" the intent.
        speechRecognizer.startListening(intent);
        Log.d("conversation", "listen2");

    }



    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params)  {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech(){
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB){
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)  {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()  {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)  {
            Log.d(TAG,  "error " +  error);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            //logthis("error " + error);
        }
        public void onResults(Bundle results) {

            Log.d(TAG, "onResults " + results);
            // Fill the list view with the strings the recognizer thought it could have heard, there should be 5, based on the call
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //display results.
            if (matches.isEmpty()){


            }else {
                //logthis(matches.get(0));
            }

            //logthis("results: "+String.valueOf(matches.size()));
            for (int i = 0; i < matches.size(); i++) {
                Log.d(TAG, "result " + matches.get(i));
                //logthis("result " +i+":"+ matches.get(i));
                //logthis( matches.get(i));
            }
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }


    @Override
    public void onGoToLocationStatusChanged(String location, String status, int descriptionId,
                                            String description) {
        switch (status) {
            case "abort":
                isNavigating = false;
                //robot.goTo(location);
                break;

            case "start":
                robot.tiltAngle(50);
                isNavigating = true;

                Log.d("locationChanged", "start");
                //robot.speak(TtsRequest.create("Starting", false));
                break;

            case "calculating":
                isNavigating = true;
                Log.d("locationChanged", "calculating");

                //robot.speak(TtsRequest.create("Calculating", false));
                break;

            case "going":
                isNavigating = true;
                Log.d("locationChanged", "going");
                robot.tiltAngle(50);

                //robot.speak(TtsRequest.create("Going", false));
                break;

            case "complete":
                isNavigating = false;
                robot.tiltAngle(50);

                if(!location.equals("start")){
                    batteryData = robot.getBatteryData();
                    batteryPercentage = batteryData.getBatteryPercentage();

                    if(batteryPercentage > 75 && batteryData.isCharging()) {
                        robot.goTo("start");
                    } else if( batteryPercentage < 20){
                        robotStatus = "low battery";
                    }

                    if(!  location.equals("home base")){
                        robot.speak(TtsRequest.create("Here is the office of " + locationName, false));

                    }
                    robotStatus = "navigation completed";

                    waveView.speechStarted();

                    Log.d("locationChanged", "tenant nav complete");
                } else{

                    ArrayList<View> clearButtonDisappear = new ArrayList<View>(Arrays.asList(waveView,
                            keyboardButton, recyclerView, linearLayout, linearLayout2,
                            clearButton));
                    disappear(clearButtonDisappear);

                    ArrayList<View> clearButtonAppear = new ArrayList<View>(Arrays.asList(promptLabel, welcomeLabel,
                            activateButton));
                    appear(clearButtonAppear);

                    robotStatus = "welcome";

                    Log.d("locationChanged", "start complete");

                }
                Log.d("locationChanged", "complete");
        }
    }
}