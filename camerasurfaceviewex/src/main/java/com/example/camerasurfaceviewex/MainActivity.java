package com.example.camerasurfaceviewex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener{

    Button btnCamera;
    FrameLayout framePreview;
    CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoPermissions.Companion.loadAllPermissions(this,100);
        btnCamera=findViewById(R.id.btnCarmera);
        framePreview=findViewById(R.id.frameCameraPreview);
        cameraView=new CameraView(this);
        framePreview.addView(cameraView);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    //사진 찍어주는 메서드
    void takePicture(){
        cameraView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try{
                    Bitmap bitmap= BitmapFactory.decodeByteArray(data, 0, data.length);
                    String outUriStr= MediaStore.Images.Media.insertImage(getContentResolver(),
                            bitmap, "Capture Image", "Captured Image using Camera");
                    if(outUriStr==null){
                        //저장공간이 없음
                        showToast("갤러리에 이미지 저장 실패");
                        return;
                    }else{
                        Uri uri=Uri.parse(outUriStr);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        camera.startPreview();
                    }
                } catch (Exception e){
                    showToast("사진을 찍을 수 없습니다.");
                }
            }
        });
    }

    void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, 100, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

    class CameraView extends SurfaceView implements SurfaceHolder.Callback {
        SurfaceHolder holder;
        Camera camera=null;

        public CameraView(Context context) {
            super(context);
            holder=getHolder();
            holder.addCallback(this);
        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            camera= Camera.open();
            setCameraOrientation();
            try {
                camera.setPreviewDisplay(holder);
            }catch (Exception e){
                //카메라를 사용할 수 없습니다.
            }
        }

        //카메라 세로 모드로 바꾸기
        public  void setCameraOrientation(){
            if(camera==null){
                return;
            }
            Camera.CameraInfo info=new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            WindowManager manager=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
            int rocation=manager.getDefaultDisplay().getRotation(); //회전에 대한 정보 확인
            int degrees=0;
            switch (rocation){
                case Surface.ROTATION_0:
                    degrees=0;
                    break;
                case Surface.ROTATION_90:
                    degrees=90;
                    break;
                case Surface.ROTATION_180:
                    degrees=180;
                    break;
                case Surface.ROTATION_270:
                    degrees=270;
                    break;
            }
            int result;
            if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                result=(info.orientation+degrees)%360;
                result=(360-result)%360;
            } else{
                result=(info.orientation-degrees+360)%360;
            }
            camera.setDisplayOrientation(result);
        }
        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            camera.stopPreview();
            camera.release();
            camera=null;
        }

        public boolean capture(Camera.PictureCallback callback){
            if(camera!=null){
                camera.takePicture(null, null ,callback);
                return true;
            } else {
                return false;
            }
        }
    }
}