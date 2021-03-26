package com.example.surfaceviewex;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ResourceBundle;

public class GameView extends SurfaceView implements  KeyEvent.Callback{
    private  ImageThread thread;
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder=getHolder();
        thread=new ImageThread(holder, context);
        setFocusable(true);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                thread.start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                boolean retry=true;
                while(retry){
                    try {
                        thread.join();
                        retry=false;
                    } catch (InterruptedException e){

                    }
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        thread.setX((int) event.getX());
        thread.setY((int) event.getY());
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_DPAD_LEFT){
            thread.setXPos(-10);
        } else if(keyCode==event.KEYCODE_DPAD_RIGHT){
            thread.setXPos(10);
        } else if(keyCode==event.KEYCODE_DPAD_DOWN) {
            thread.setYPos(10);
        } else if(keyCode==event.KEYCODE_DPAD_UP) {
            thread.setYPos(-10);
        }
        return super.onKeyDown(keyCode, event);
    }

    //내부 클래스
    class ImageThread extends Thread{
        Bitmap bitmap;
        SurfaceHolder surfaceHolder;
        Drawable manImaged[]=new Drawable[2];
        int cnt=0;
        int xPos=0, yPos=0;

        public ImageThread(SurfaceHolder surfaceHolder, Context context) {
            this.surfaceHolder = surfaceHolder;
            Resources res=context.getResources();
            bitmap= BitmapFactory.decodeResource(res, R.drawable.bg);
            manImaged[0]=res.getDrawable(R.drawable.man1);
            manImaged[1]=res.getDrawable(R.drawable.man2);
        }

        @Override
        public void run() {
            while (true){
                Canvas canvas=null;
                try {
                    canvas=surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder){
                        canvas.drawBitmap(bitmap, 0,0, null);
                        cnt++;
                        manImaged[cnt%2].setBounds(xPos+125, yPos+300,xPos+220, yPos+500);
                        manImaged[cnt%2].draw(canvas);
                        sleep(100);
                    }
                } catch (InterruptedException e){

                } finally {
                    if(canvas!=null){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
        void setXPos(int val){
            xPos+=val;
        }
        void setYPos(int val){
            yPos+=val;
        }
        void setX(int val){
            xPos=val-35;
        }
        void setY(int val){
            yPos=val-100;
        }
    }

}
