package com.example.energydream;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.example.energydream.ARUtils.Coin;
import com.example.energydream.ARUtils.Demoutils;
import com.example.energydream.Model.StandyPower;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class ArActionActivity extends AppCompatActivity implements Scene.OnUpdateListener{
    private static final String TAG = ArActionActivity.class.getSimpleName();

    private Snackbar loading_message_snackbar = null;
    private boolean is_install;
    private boolean is_make = false;
    private boolean is_pause = false;

    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private Coin coin_ndoe;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Demoutils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        //초기에 들어갈 때 대기전력이 존재하지 않으면 아예 화면 꺼버림
        NavActivity.m_reference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StandyPower standyPower = dataSnapshot.getValue(StandyPower.class);

                    // DB에 존재하지 않으면 NavActivity로 돌아간다.
                    if(standyPower == null){
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);
                        finish();
                        return;
                    }

                    if(!standyPower.isExist()){
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);;
                        finish();
                        return;
                    }

                    if(standyPower.isCalc()){
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);;
                        finish();
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        setContentView(R.layout.activity_ar);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arSceneView = arFragment.getArSceneView();
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);


        // When yu build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        /* ModelRenderable.builder()
        .setSource(this, R.raw.andy)
        .build()
        .thenAccept(renderable -> andyRenderable = renderable)
        .exceptionally(
            throwable -> {
              Toast toast =
                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
              return null;
            });*/

        arSceneView.getScene().addOnUpdateListener(this);

    }

    private void addObject(Frame frame) {

        Point point = getScreenCenter();

        if(frame == null)
            Log.d("프레임 없다","결과");

        if (frame != null) {
            List<HitResult> hits = frame.hitTest((float) point.x, (float) point.y);
            Log.d(Integer.toString(hits.size()),"   결과");

            for (int i = 0; i < hits.size(); i++) {
                Trackable trackable = hits.get(i).getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hits.get(i).getHitPose())) {
                    placeObject(hits.get(i).createAnchor());
                }
            }
        }
    }

    //객체를 만든다.
    void placeObject(Anchor anchor){
        // Create the Anchor.
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        arFragment.getArSceneView().getScene().addChild(anchorNode);

        //탐지된 곳에 Anchor 만든 후 coin_ndoe 합성
        Node node = CreateCoin();
        node.setParent(anchorNode);

        //coin은 하나만 만들겠다.
        is_make = true;
    }

    private Node CreateCoin(){
        Node base = new Node();
        Coin coin = new Coin(arFragment,this);
        coin_ndoe = coin;
        coin.setParent(base);
        return base;
    }


    @Override
    public void onUpdate(FrameTime frameTime) {

        Frame arframe = arFragment.getArSceneView().getArFrame();

        if (arframe.getCamera().getTrackingState() != TrackingState.TRACKING) {

            return;
        }

        for (Plane plane : arframe.getUpdatedTrackables(Plane.class)) {
            // 평면인식 ( 바닥의 평평한 부분을 인식 함 )
            if (plane.getTrackingState() == TrackingState.TRACKING) { 
                hideLoadingMessage();
                
                if(!is_make){ addObject(arframe); }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(arSceneView == null)
            return;

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = Demoutils.createArSession(this, is_install);
                
                if (session == null) {
                    is_install = Demoutils.hasCameraPermission(this);
                    return;
                }else{ 
                    arSceneView.setupSession(session);
                }
                
            } catch (UnavailableException e) {
                Demoutils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Demoutils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage(); 
        }

        if(coin_ndoe != null) { 
            coin_ndoe.resumeEvent(); 
        }

        if(is_pause){
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED,resultIntent);
            finish();
            return;
        }
    }

    //홈키 누르면 Pause 상태로 빠짐
    @Override
    public void onPause() {
        super.onPause();
        
        if (arSceneView != null) {
            arSceneView.pause();
        }
        if(coin_ndoe != null) {
            coin_ndoe.pauseEvent();
        }
        is_pause = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void showLoadingMessage() {
        if (loading_message_snackbar != null && loading_message_snackbar.isShownOrQueued()) {
            return;
        }

        loading_message_snackbar =
                Snackbar.make(
                        ArActionActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loading_message_snackbar.getView().setBackgroundColor(0xbf323232);
        loading_message_snackbar.show();
    }

    private void hideLoadingMessage() {
        if (loading_message_snackbar == null) {
            return;
        }

        loading_message_snackbar.dismiss();
        loading_message_snackbar = null;
    }

    private Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }
}
