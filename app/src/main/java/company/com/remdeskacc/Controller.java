package company.com.remdeskacc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class Controller extends Activity implements View.OnTouchListener, View.OnKeyListener {

    double x, y = 0;

    View touchView;

    Button Left;
    Button Right;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        Left = (Button) findViewById(R.id.LeftClickButton);
        Right =  (Button) findViewById(R.id.RightClickButton);

        Left.setWidth(width/2);
        Right.setWidth(width/2);

        Left.setOnTouchListener(this);
        Right.setOnTouchListener(this);

        touchView = (View) findViewById(R.id.TouchPad);
        touchView.setOnTouchListener(this);

        EditText editText = (EditText) findViewById(R.id.KeyBoard);
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(new TextWatcher(){
            public void  afterTextChanged (Editable s){
                s.clear();
            }

            public void  beforeTextChanged  (CharSequence s, int start, int fcount, int after){}
            public void  onTextChanged  (CharSequence s, int start, int before, int count) {
                try{
                Log.i("Point keyboard", s.toString());
                if(count > 0)
                sendToAppDel(Constants.KEYBOARD + s.toString());
            } catch(IndexOutOfBoundsException e){}
            }
        });

        AppDelegate appDel = ((AppDelegate)getApplicationContext());
        appDel.setController( this );
    }

    public void setImage(final Bitmap bit){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            public void run() {
                LinearLayout layout = (LinearLayout) findViewById(R.id.TouchPad);
                BitmapDrawable drawable = new BitmapDrawable( bit );
                layout.setBackgroundDrawable( drawable );
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v == Left){
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN: sendToAppDel(Constants.LEFTMOUSEDOWN); break;
                case MotionEvent.ACTION_UP: sendToAppDel(Constants.LEFTMOUSEUP); break;
            }
        }else if( v == Right){
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN: sendToAppDel(Constants.RIGHTMOUSEDOWN); break;
                case MotionEvent.ACTION_UP: sendToAppDel(Constants.RIGHTMOUSEUP); break;
            }
        }
        else
            mousePadHandler(event);

        return true;
    }

    // detect keyboard event
    // and send to delegate
    //@Override
    public boolean onKey(View v, int c, KeyEvent event){
        // c is the event keycode
        if(event.getAction() == 1)
        {
            Log.i("Point keycode", new String(""+c));
            sendToAppDel( "" + Constants.KEYCODE + c);
        }
        // this will prevent the focus from moving off the text field
        if(		c == KeyEvent.KEYCODE_DPAD_UP   ||
                c == KeyEvent.KEYCODE_DPAD_DOWN ||
                c == KeyEvent.KEYCODE_DPAD_LEFT ||
                c == KeyEvent.KEYCODE_DPAD_RIGHT
                )
            return true;

        return false;
    }

    public void keyClickHandler(View v){
        EditText editText = (EditText) findViewById(R.id.KeyBoard);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Log.i("Keyboard", "work");
            mgr.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED, 0);
    }

    private void mousePadHandler(MotionEvent event) {
        int action = event.getAction();

        int touchCount = event.getPointerCount();

        if (touchCount == 1) {
            switch (action) {
                case 0:    // touch down
                    x = event.getX();
                    y = event.getY();
                    Log.i("Point down", "hh");
//                    double tempX = event.getX();
//                    x = (tempX * 1280) / touchView.getWidth();
//                    double tempY = event.getY();
//                    y = (tempY * 720) / touchView.getHeight();

//                    Log.i("Point x", Double.toString(tempX));
//                    sendToAppDel(""+ "p" + x + "/" + y);
//                    sendToAppDel("" + Constants.MOVEMOUSE + x + Constants.DELIMITER + y);
                    break;
                case 1:	// touch up
                    Log.i("Point up", "jh");
                    long deltaTime = event.getEventTime() - event.getDownTime();
                    if(deltaTime < 250)
                        sendToAppDel(Constants.LEFTCLICK);
                        break;
                case 2: // moved
                    Log.i("Point curr x ", Double.toString(x));
                    Log.i("Point curr y ", Double.toString(y));
//                    int deltaX = (int) (x - event.getX()) * -1;
//                    int deltaY = (int) (y - event.getY()) * -1;
                    Log.i("Point offset x", Double.toString(event.getX()));
                    Log.i("Point offset y", Double.toString(event.getY()));

//                    int lastX = (int) (deltaX * 1280) / touchView.getWidth();
//                    int lastY = (int) (deltaY * 720) / touchView.getHeight();
                    int lastX = (int) (event.getX() * 1280) / touchView.getWidth();
                    int lastY = (int) (event.getY() * 720) / touchView.getHeight();

//                    int deltaX = (int)(x - lastX) * -1;
//                    int deltaY = (int)(y - lastY) * -1;

//                    Log.i("old point", Double.toString());
//                    Log.i("new point", Double.toString(lastX));

                    sendToAppDel("" + Constants.MOVEMOUSE + lastX + Constants.DELIMITER + lastY);

                    x = event.getX();
                    y = event.getY();
                    break;
                default:
                    break;
            }
        }
    }

    private void sendToAppDel(char c){
        sendToAppDel(""+c);
    }

    private void sendToAppDel(String message){
        AppDelegate appDel = ((AppDelegate)getApplicationContext());

        Log.i("Point", message);
        appDel.sendMessage(message);
    }
}
