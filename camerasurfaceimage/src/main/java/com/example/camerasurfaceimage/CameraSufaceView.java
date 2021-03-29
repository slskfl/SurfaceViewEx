package com.example.camerasurfaceimage;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pedro.library.AutoPermissions;

import java.io.IOException;

public class CameraSufaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    Camera camera=null;
    public CameraSufaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder=getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        //서피스 뷰 생성
        camera=Camera.open();
        try{
            camera.setPreviewDisplay(holder);
        }catch (Exception e){
            showToast("카메라 미리보기 실패");
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            try {
                /*Camera.Parameters parameters=camera.getParameters();
                parameters.setRotation(90);
                camera.setParameters(parameters);*/
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(holder);
            }catch (IOException e){
                camera.release();
            }
            camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        camera.stopPreview();
        camera.setPreviewCallback(null);
        camera.release();
        camera=null;
    }

    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
