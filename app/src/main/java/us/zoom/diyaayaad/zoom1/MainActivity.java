package us.zoom.diyaayaad.zoom1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingEvent;
import us.zoom.sdk.MeetingOptions;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainActivity extends AppCompatActivity implements MeetingServiceListener, ZoomSDKInitializeListener {

    int STYPE = MeetingService.USER_TYPE_ZOOM; // there is three types of users differs with privileges

    Button instant, custom, join;

    EditText meeting_id, meeting_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initZoomSDK();

        meeting_id = (EditText) findViewById(R.id.meetingID);
        meeting_password=(EditText) findViewById(R.id.meetingPassword);

        instant = (Button) findViewById(R.id.instant);
        instant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startInstantMeeting();

            }
        });


        custom = (Button) findViewById(R.id.custom);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startCustomMeeting(meeting_id.getText().toString().trim());

            }
        });


        join = (Button) findViewById(R.id.join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                joinMeeting(meeting_id.getText().toString().trim(),meeting_password.getText().toString().trim());

            }
        });

    }

    @Override
    public void onMeetingEvent(int meetingEvent, int errorCode, int internalErrorCode) {

        if (meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            Toast.makeText(getApplicationContext(), "Version of ZoomSDK is too low!", Toast.LENGTH_LONG).show();
        }

        if (meetingEvent == MeetingEvent.MEETING_DISCONNECTED || meetingEvent == MeetingEvent.MEETING_CONNECT_FAILED) {
            Toast.makeText(getApplicationContext(), "MEETING ENDED", Toast.LENGTH_LONG).show();

        }
    }




    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(getApplicationContext(),
                    "Failed to initialize Zoom SDK. Error: " + errorCode +
                            ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(), "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
            ZoomSDK sdk = ZoomSDK.getInstance();
            MeetingService meetingService = sdk.getMeetingService();
            if (meetingService != null) meetingService.addListener(this);
        }
    }


    public void initZoomSDK() {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (!sdk.isInitialized()) {

            sdk.initialize(this, Keys.APP_KEY,Keys.APP_SECRET,Keys.WEB_DOMAIN, this);
            //set your own keys for dropbox , oneDrive and googleDrive
            sdk.setDropBoxAppKeyPair(this, null/*DROPBOX_APP_KEY*/, null/*DROPBOX_APP_SECRET*/);
            sdk.setOneDriveClientId(this, null/*ONEDRIVE_CLIENT_ID*/);
            sdk.setGoogleDriveClientId(this, null /*GOOGLE_DRIVE_CLIENT_ID*/);
        } else {

            MeetingService meetingService = sdk.getMeetingService();
            if (meetingService != null) meetingService.addListener(this);
        }
    }


    @Override
    protected void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (zoomSDK.isInitialized()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            meetingService.removeListener(this);
        }

        super.onDestroy();
    }


    public void startInstantMeeting() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {

            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();
        MeetingOptions opts = new MeetingOptions();
//        // opts.no_driving_mode = true;
//		  //opts.no_meeting_end_message = true;
//        //opts.no_titlebar = true;
//        //opts.no_bottom_toolbar = true;
//        //opts.no_invite = true;

        int ret = meetingService.startInstantMeeting
                (this, Keys.USER_ID, Keys.ZOOM_TOKEN, MeetingService.USER_TYPE_ZOOM, "DisplayName", opts);
    }


    public void startCustomMeeting(String meetingNo) {

        if (meetingNo == null || meetingNo.length() == 0) {
            Toast.makeText(this, "You need to enter a scheduled meeting number.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();
        MeetingOptions opts = new MeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;

        int ret = meetingService.startMeeting(this, Keys.USER_ID, Keys.ZOOM_TOKEN, STYPE, meetingNo, "DisplayName", opts);

    }


    public void joinMeeting(String meetingNo, String password) {

        if (meetingNo == null || meetingNo.length() == 0) {
            Toast.makeText(this, "You need to enter a scheduled meeting number.", Toast.LENGTH_LONG).show();
            return;
        }

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();

        MeetingOptions opts = new MeetingOptions();
//		opts.no_driving_mode = true;
//		opts.no_invite = true;
//		opts.no_meeting_end_message = true;
//		opts.no_titlebar = true;
//		opts.no_bottom_toolbar = true;
//		opts.no_dial_in_via_phone = true;
//		opts.no_dial_out_to_phone = true;
//		opts.no_disconnect_audio = true;

        int ret = meetingService.joinMeeting(this, meetingNo, "DisplayName", password, opts);

    }


}
