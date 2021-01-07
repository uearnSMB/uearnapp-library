package smarter.uearn.money.chats.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import smarter.uearn.money.R;
import smarter.uearn.money.chats.activity.ChatActivity;
import smarter.uearn.money.chats.adapter.TechMessageAdapter;
import smarter.uearn.money.chats.model.EmitDetails;
import smarter.uearn.money.chats.model.OnDataUpdateListener;
import smarter.uearn.money.chats.model.ReceivedMessageDetails;
import smarter.uearn.money.chats.model.ResponseChannel;
import smarter.uearn.money.chats.model.TechMessage;
import smarter.uearn.money.chats.receiver.ChatNetworkReceiver;
import smarter.uearn.money.chats.utils.ScaleAnim;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.utils.AesEncryption;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;

import static smarter.uearn.money.chats.activity.ChatActivity.chatNetwork;
import static smarter.uearn.money.chats.activity.ChatActivity.socket;
import static smarter.uearn.money.chats.activity.ChatActivity.channelResponse;
import static smarter.uearn.money.chats.activity.ChatActivity.STORAGE_DIR;
import static smarter.uearn.money.chats.activity.ChatActivity.isTechMessageOpen;
import static smarter.uearn.money.chats.activity.ChatActivity.tabLayout;
import static smarter.uearn.money.chats.activity.ChatActivity.unseenGroupMessageArray;
import static smarter.uearn.money.chats.activity.ChatActivity.unseenTechMessageObject;
import static smarter.uearn.money.chats.activity.ChatActivity.unseenTechMessageArray;
import static smarter.uearn.money.chats.activity.ChatActivity.chatMakingCall;

public class TechFragment extends Fragment{

    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView mMessagesView;
    private static List<TechMessage> mMessages = new ArrayList<TechMessage>();
    private EditText mInputMessageView;
    private static String mUsername, userId;
    //private Socket socket;
    private static String userName;
    static Context context;
    ReceivedMessageDetails receivedResponse;
    private BroadcastReceiver mNetworkReceiver;
    private Uri imageFilePath, logoUri;
    protected static final int GALLERY_PICTURE = 3;
    protected static final int PICK_Camera_IMAGE = 0;
    private Bitmap ThumbImage;
    String selectedImagePath;
    private MediaRecorder mediaRecorder;
    private String recordFilePath;
    private boolean isRecording = false;
    private boolean isSpeakButtonLongPressed = false;
    ImageButton sendButton;
    ImageButton micButton;
    ImageButton attachmentButton;
    private ScaleAnim scaleAnim;
    private FloatingActionButton fab_call;
    private ProgressDialog pDialog;
    public TechFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        userName = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
        mAdapter = new TechMessageAdapter(this.context, mMessages, userName);
        pDialog=new ProgressDialog(this.context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.uearn_chat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.chat_call) {

                if(CommonUtils.chatC2C()){
                    chatMakingCall = true;
                    CommonUtils.chatMakeCall(context, channelResponse.getChannels().getTECH().getPhone(), 0, "",
                            "", "", "",
                            "", "");

                }else {
                    Log.e("TechFragment","CommonUtils.chatC2C() :: "+CommonUtils.chatC2C());
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+channelResponse.getChannels().getTECH().getPhone()));

                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    }
                    startActivity(callIntent);
                }
            }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tech_view, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("TechFragment","onResume::"+isAdded());

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new TechMessageAdapter(context, mMessages, userName);
        if (context instanceof Activity){
            this.context = (ChatActivity) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);
        sendButton = (ImageButton) view.findViewById(R.id.send_button);
        micButton = (ImageButton) view.findViewById(R.id.send_voice);
        fab_call = (FloatingActionButton) view.findViewById(R.id.fab_call);
        scaleAnim = new ScaleAnim(view);
        //registerReceiver();
        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            mUsername= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        }
        /*mInputMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    attemptSend();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });*/

        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    micButton.setVisibility(View.VISIBLE);
                    attachmentButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    /*if (sendButton.getVisibility() != View.GONE) {

                        sendButton.setVisibility(View.VISIBLE);
                        sendButton.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                    }*/
                }else{
                    micButton.setVisibility(View.GONE);
                    attachmentButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                    /*if (micButton.getVisibility() != View.GONE) {
                        sendButton.setVisibility(View.GONE);
                        micButton.setVisibility(View.VISIBLE);
                        micButton.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                    }*/
                }
            }
        });

        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CommonUtils.chatC2C()){
                    chatMakingCall = true;
                    CommonUtils.chatMakeCall(context, channelResponse.getChannels().getTECH().getPhone(), 0, "",
                            "", "", "",
                            "", "");

                }else {
                    Log.e("TechFragment","CommonUtils.chatC2C() :: "+CommonUtils.chatC2C());
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+channelResponse.getChannels().getTECH().getPhone()));

                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            }
        });
        //String userName = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
        //if(isAdded()) {
        //if(socket != null) {
        socket.on(channelResponse.getChannels().getTECH().getChannel(), new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            Gson g = new Gson();
                            receivedResponse = g.fromJson(data.toString(), ReceivedMessageDetails.class);
                            try {
                                //extract data from fired event
                                String nickname = data.getString("user");
                                String message = AesEncryption.decrypt(data.getString("from_id"), channelResponse.getGroupid(), data.getString("message"));
                                String time = Utils.getChatTime(data.getString("date"));
                                String type = data.getString("type");
                                String message_type = data.getString("message_type");
                                String userColor = receivedResponse.getUser_color();
                                if(receivedResponse.getFrom_id().equalsIgnoreCase(userId) ||
                                        (receivedResponse.getFrom_id().equalsIgnoreCase(channelResponse.getChannels().getTECH().getUserid()) &&
                                                (receivedResponse.getTo_id().equalsIgnoreCase(userId) || receivedResponse.getTo_id().equalsIgnoreCase(channelResponse.getGroupid())))){
                                    JSONObject jsonObj = new JSONObject();
                                    jsonObj.put("id", data.getString("id"));
                                    jsonObj.put("session_id", data.getString("session_id"));
                                    jsonObj.put("channel", data.getString("channel"));
                                    jsonObj.put("userid", userId);
                                    socket.emit("MESSAGE_DELIVERY",jsonObj);
                                    if(isTechMessageOpen){
                                        Log.e("GroupFragment","true :: ");
                                        JSONObject seenObj = new JSONObject();
                                        seenObj.put("id", data.getString("id"));
                                        seenObj.put("session_id", data.getString("session_id"));
                                        seenObj.put("channel", data.getString("channel"));
                                        seenObj.put("userid", userId);
                                        socket.emit("MESSAGE_SEEN",seenObj);
                                    }else{
                                        Log.e("GroupFragment","false :: ");
                                        unseenTechMessageObject = new JSONObject();
                                        unseenTechMessageObject.put("id", data.getString("id"));
                                        unseenTechMessageObject.put("session_id", data.getString("session_id"));
                                        unseenTechMessageObject.put("channel", data.getString("channel"));
                                        unseenTechMessageObject.put("userid", userId);
                                        unseenTechMessageArray.add(unseenTechMessageObject);
                                        Log.e("GroupFragment","count :: "+unseenTechMessageArray.size());
                                        tabLayout.setBadgeText(0,String.valueOf(unseenTechMessageArray.size()));
                                    }
                                    addMessage(nickname, message, time, type, userId, message_type, userColor);
                                }
                                mAdapter.notifyDataSetChanged();
                                mMessagesView.setAdapter(mAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        });
        //}
        //}


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chatNetwork.equalsIgnoreCase("NO INTERNET")) {
                    try {
                        attemptSend();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getActivity(),"No internet to send",Toast.LENGTH_SHORT).show();
                }
            }
        });
        attachmentButton = (ImageButton) view.findViewById(R.id.send_attachment);
        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chatNetwork.equalsIgnoreCase("NO INTERNET")) {
                    boolean isPermissionEnabled = CommonUtils.permissionsCheck(getActivity());
                    if(isPermissionEnabled) {
                        startDialog();

                    }
                }else{
                    Toast.makeText(getActivity(),"No internet to send",Toast.LENGTH_SHORT).show();
                }
            }
        });


        micButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //scaleAnim.start();
                if(!chatNetwork.equalsIgnoreCase("NO INTERNET")) {
                mInputMessageView.setHint("Recording.....");
                stopAudioRecord();
                startAudioRecord();
                isSpeakButtonLongPressed = true;

                Log.e("GroupFragment","ON long press");
                }else{
                    Toast.makeText(getActivity(),"No internet to send",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // We're only interested in anything if our speak button is currently pressed.
                    if (isSpeakButtonLongPressed) {
                        // Do something when the button is released.
                        Log.e("GroupFragment","ON release ");
                        scaleAnim.stop();
                        stopAudioRecord();
                        isSpeakButtonLongPressed = false;
                        mInputMessageView.setHint("Type a message");
                        mInputMessageView.setText("");
                        ChatActivity.imagePath = recordFilePath;
                        sendAudioDatatoServer(ChatActivity.imagePath);
                    }
                }
                return false;
            }
        });
    }
    private void startDialog() {
        final Dialog networkDialog = CustomTwoButtonDialog.buildChatAttachmentDialog(getActivity(), "Upload Pictures Option", "");

        LinearLayout camera = networkDialog.findViewById(R.id.ll_camera);
        //btnNo.setText("CAMERA");

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values2 = new ContentValues(4);
                values2.put(MediaStore.Images.Media.DISPLAY_NAME, Calendar.getInstance().toString());
                values2.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
                values2.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                try {
                    /*imageFilePath = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values2);

                    //uploadImageFile(imageFilePath.toString());
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
                    startActivityForResult(intent2, PICK_Camera_IMAGE);*/
                    captureImage();

                } catch (SecurityException se) {
                    CommonUtils.displayDialog(getActivity());
                }

            }
        });

        LinearLayout gallery = networkDialog.findViewById(R.id.ll_gallery);
        //btnYes.setText("GALLERY");

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Intent pictureActionIntent = null;

                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(
                        pictureActionIntent,
                        GALLERY_PICTURE);


            }
        });
        Button btnCancel = networkDialog.findViewById(R.id.cancleBtn);
        //btnCancel.setText("CANCEL");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkDialog.dismiss();
            }
        });
        networkDialog.show();
    }

    private static void addMessage(String username, String message, String time, String messageType,
                            String userid, String type, String userColor) {
        mMessages.add(new TechMessage.Builder()
                .username(username).message(message).
                        time(time).
                        type(type).
                        userid(userid).
                        messageType(messageType).
                        userColor(userColor).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void attemptSend() throws GeneralSecurityException {


        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }
        //String encryptedMessage = AesEncryption.encrypt(userId, channelResponse.getGroupid(), message);
        String Username= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
        attemptEmitMessage(channelResponse.getChannels().getTECH().getChannel(), Username,message, "TEXT");
        mInputMessageView.setText("");
        //addMessage(mUsername, message);

    }

    private void attemptEmitMessage(final String channelName, final String userName,
                                    final String messageToSend, final String messageType){

        EmitDetails emitDetails = new EmitDetails();
        emitDetails.setChannel(channelName);
        emitDetails.setUsername(userName);
        emitDetails.setMessage(messageToSend);
        emitDetails.setId(UUID.randomUUID().toString());
        emitDetails.setFrom_id(userId);
        emitDetails.setTo_id(channelResponse.getChannels().getTECH().getUserid());

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", UUID.randomUUID().toString());
            jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE",""));
            jsonObj.put("channel", channelName);
            jsonObj.put("from_id", Integer.parseInt(userId));
            jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getTECH().getUserid()));
            jsonObj.put("username", userName);
            jsonObj.put("message", AesEncryption.encrypt(userId, channelResponse.getGroupid(), messageToSend));
            jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
            jsonObj.put("type", "CHAT_MESSAGE");
            jsonObj.put("message_type", messageType);
            socket.emit("CHAT_MESSAGE",jsonObj);
        } catch (JSONException | GeneralSecurityException e) {
        }

    }

    private static void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if(mNetworkReceiver != null) {
            context.unregisterReceiver(mNetworkReceiver);
        }*/
    }


    public static void getTechData(ResponseChannel channelResponse){
        //Log.e("TechFragment", "onDataAvailable 123:: " + channelResponse.getChannels().getTECH().getChannel());
        mMessages.clear();
        mAdapter = new TechMessageAdapter(context, mMessages, userName);
        //viewPager.setAdapter(adapter);
        if (channelResponse.getChannels().getTECH().getMessages().length != 0) {
            for (int i = 0; i < channelResponse.getChannels().getTECH().getMessages().length; i++) {
                if (null != channelResponse.getChannels().getTECH().getMessages()[i].getUser()) {
                    addMessage(channelResponse.getChannels().getTECH().getMessages()[i].getUser(),
                            AesEncryption.decrypt("","",channelResponse.getChannels().getTECH().getMessages()[i].getMessage()),
                            Utils.getChatTime(channelResponse.getChannels().getTECH().getMessages()[i].getDate()),
                            channelResponse.getChannels().getTECH().getMessages()[i].getType(), userId,
                            channelResponse.getChannels().getTECH().getMessages()[i].getMessage_type(),
                            channelResponse.getChannels().getTECH().getMessages()[i].getUser_color());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_Camera_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(getActivity())) {
                if (data != null) {
                    logoUri = data.getData();
                    ThumbImage = null;
                    if (logoUri != null) {
                        previewImage(logoUri, "Profile");
                    } else {
                        Toast.makeText(getActivity(), "Error retriving image! inside", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (imageFilePath != null) {
                        ThumbImage = null;
                        logoUri = imageFilePath;
                        /*previewImage(logoUri, "Profile");
                        Uri profileUri = imageFilePath;
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(profileUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imgDecodableString = cursor.getString(columnIndex);
                        imagePath = imgDecodableString;*/
                        Log.e("GroupFragment","imagePath :: "+ChatActivity.imagePath);
                        Log.e("GroupFragment","imageFilePath :: "+imageFilePath);
                        //cursor.close();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getActivity().getApplicationContext().getContentResolver(), imageFilePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        showImage(imageFilePath);
                        //new UploadImageToServer().execute();
                        String profileUserID = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                        String profileUrl = profileUserID + "-image.png";

                    } else {
                        Toast.makeText(getActivity(), "Error retriving image!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        }  else if (requestCode == GALLERY_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(getActivity())) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Drawable user_theame_img = Drawable.createFromPath(picturePath);




                    Uri selectedImages = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImages, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int columnIndexs = c.getColumnIndex(filePath[0]);
                    selectedImagePath = c.getString(columnIndexs);
                    ChatActivity.imagePath = selectedImagePath;
                    Log.e("GroupFragment","selectedImagePath :: "+selectedImagePath);
                    //new UploadImageToServer().execute();
                    showImage(selectedImages);
                    c.close();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showImage(Uri imageUri ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Yes Button
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new UploadImageToServer().execute();
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_image_sent, null);
        ImageView imageView = (ImageView) dialoglayout.findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
        builder.setView(dialoglayout);
        builder.show();
    }

    public void previewImage(Uri uri, String imgType) {
        String path = Utils.getPath(uri, getActivity());

        if (ThumbImage == null) {
            if (path != null) {
                ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 96, 96);
            } else {
                //Log.e("Tag", "path is null" + " uri= " + uri);
            }
        }

    }

    class UploadImageToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            uploadImageFile(ChatActivity.imagePath);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("File uploading...");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }
    }

    public void uploadImageFile(String imgFile) {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            String filePath = Utils.compressImage(imgFile, getActivity());
            JSONObject response = DataUploadUtils.uploadImageFileToServer(filePath, Urls.getUploadFileUrl());
            try {
                if (response != null && response.getString("url") != null) {
                    Log.e("GroupFragment","Response :: "+response);
                    String imageUrl = response.getString("url");
                    Log.e("GroupFragment","imageUrl :: "+imageUrl);
                    String Username= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
                    attemptEmitMessage(channelResponse.getChannels().getTECH().getChannel(), Username,imageUrl, "IMAGE");
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "No network available or poor network", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void startAudioRecord() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recordFilePath = Environment.getExternalStorageDirectory() + "/" + STORAGE_DIR + "/" + System.currentTimeMillis() + ".amr.smb";
            mediaRecorder.setOutputFile(recordFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            Log.e("Caught Exception 1 ", "true" + e.toString());
        } catch (IOException e) {
            Log.e("Caught Exception1 ", "true" + e.toString());
        } catch (RuntimeException e) {
            Log.e("Caught Exception1 ", "true");
        }
    }

    public void stopAudioRecord() {
        try {
            if(isRecording()) {
                mediaRecorder.stop();
                mediaRecorder.release();
                isRecording = false;
            }
        } catch (Exception e) {

        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    @SuppressLint("StaticFieldLeak")
    public void sendAudioDatatoServer(final String audioPath) {
        final String[] audioUrlPath = {""};
        new AsyncTask<Void, Void, String>() {
            ProgressDialog progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                String url = Urls.getUploadFileUrl();
                JSONObject responseAudio = DataUploadUtils.uploadNotesAudioFileToServer(audioPath, "live_meeting_rec", "", "", url);
                try {
                    if ((responseAudio != null) && (responseAudio.getString("url") != null)) {
                        String path = responseAudio.getString("url");
                        audioUrlPath[0] = path;
                    }

                } catch (JSONException e) {

                }
                String audioURL = audioUrlPath[0];
                Log.e("GroupFragment","audioURL :: "+audioURL);
                String Username= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
                attemptEmitMessage(channelResponse.getChannels().getTECH().getChannel(), Username,audioURL, "AUDIO");
                return audioURL;
            }

            @Override
            protected void onPostExecute(String jObject) {
                super.onPostExecute(jObject);
            }
        }.execute();
    }

    private void captureImage() {

        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            // We give some instruction to the intent to save the image
            File photoFile = null;

            try {
                // If the createImageFile will be successful, the photo file will have the address of the file
                photoFile = Utils.createImageFile();
                // Here we call the function that will try to catch the exception made by the throw function
            } catch (IOException e) {
                Log.e("TechFragment","Exception error in generating the file");
                e.printStackTrace();
            }
            Log.e("TechFragment","photoFile :: "+photoFile);
            // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
            Uri outputUri = FileProvider.getUriForFile(
                    getActivity(),
                    "smarter.uearn.money.fileprovider",
                    photoFile);
            imageFilePath = outputUri;
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Log.e("TechFragment","Calling the camera App by intent");

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, PICK_Camera_IMAGE);
        }


    }
}
