package com.mw.iresh.identifyobjects;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mw.iresh.identifyobjects.customRecognitionListener.CustomRecognitionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class GameFace extends AppCompatActivity implements TextToSpeech.OnInitListener {

    ImageButton fatherBtn, motherBtn, daughterBtn, sonBtn;
    ImageView baseImg;
    TextView memberText;
    Map<String, ImageButton> map;
    boolean endingAnimations  = false;
    String[] member = {"Father","Mother","Daughter","Son"};
    ArrayList<Integer> objectCount = new ArrayList<>();
    Handler handler;
    TextView spText;
    Switch aSwitch;
    SpeechRecognizer mySp;
    CustomRecognitionListener recognitionListener;
    Intent recognizeIntent;
    private TextToSpeech tts;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_face);

        fatherBtn = (ImageButton) findViewById(R.id.fatherBtn);
        sonBtn = (ImageButton) findViewById(R.id.sonBtn);
        daughterBtn = (ImageButton) findViewById(R.id.daughterBtn);
        motherBtn = (ImageButton) findViewById(R.id.motherBtn);
        memberText = (TextView) findViewById(R.id.memberText);
        baseImg = (ImageView) findViewById(R.id.baseImg);
        handler = new Handler(getApplicationContext().getMainLooper());



//        recognizeIntent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
//        recognizeIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
//
//        mySp = SpeechRecognizer.createSpeechRecognizer(this);
//        recognitionListener = new CustomRecognitionListener();
//        mySp.setRecognitionListener(recognitionListener);


        tts = new TextToSpeech(this,this);
        tts.setLanguage(Locale.US);

        map = new HashMap<>();
        map.put("Father", fatherBtn);
        map.put("Mother", motherBtn);
        map.put("Daughter", daughterBtn);
        map.put("Son", sonBtn);

        randomObjectGen(4);
        btnMapLooper();


    }


    public void randomObjectGen(int numbersOfObjects){
        Random rand = new Random();
        int randNum = rand.nextInt(4);
        boolean state = false;

            while (!state){
                if (objectCount.size() == 0) {
                    objectCount.add(randNum);
                }
                for (int i=0;i<objectCount.size();i++){
                    if (objectCount.get(i)== randNum) {
                        randNum = rand.nextInt(4);
                        break;
                    }
                    if (i+1 == objectCount.size()) {
                        if (objectCount.size() == numbersOfObjects-1) {
                            state = true;
                        }
                        objectCount.add(randNum);

                    }
                }

            }

        System.out.println("object counts........."+objectCount);
    }



    public void btnMapLooper(){

        System.out.println("top of the btnMapLooper..............");

        Thread btnMapLoop = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside the btnMapLooper.............");
                int x=0;
                while (x<5){
                    //String key = member[1];
                    //ImageButton nextKey = map.get(key);
                    if (flag){
                        flag = false;
                        System.out.println("inside the btnMapLooper if statement .............");
                            final String key =  member[x];
                            String nextKey="";
                            if (x!=member.length){
                                nextKey = member[x];
                            }
                            //imageBtnAnimate(key, map.get(key),x);
                        final int finalX = x;
                        final String finalNextKey = nextKey;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                objectAnimating(key, map.get(key),map.get(finalNextKey), finalX);
                                System.out.println("after calling the objectAminating.............");
                            }
                        });
//                            objectAnimating(key, map.get(key),map.get(nextKey),x);
//                        System.out.println("after calling the objectAminating.............");
                            x++;

                    }
                }
            }
        });

        btnMapLoop.start();

    }


    private void imageBtnAnimate(final String name, final ImageButton member, final int delay) {

        final ViewPropertyAnimator anim = member.animate();
        System.out.println("inside the imageBtnAnimate ....name......" + name +".....delay....."+ delay);
        anim.scaleX(1.7f).scaleY(1.7f).setDuration(2000).translationX(0).translationY(400).setStartDelay(delay * 3000).withStartAction(new Runnable() {
            @Override
            public void run() {
                say("this is " + name);
                memberText.setText(name);
            }
        })
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {


                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        anim.setStartDelay(2000);
                        anim.scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(1000).setStartDelay(1000);
                        if (delay==map.size()){
                            endingAnimations = true;

                            fatherBtn.setVisibility(View.INVISIBLE);
                            motherBtn.setVisibility(View.INVISIBLE);
                            daughterBtn.setVisibility(View.INVISIBLE);
                            sonBtn.setVisibility(View.INVISIBLE);
                            baseImg.setVisibility(View.INVISIBLE);


                        }

                        //state[0] = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }


    public boolean objectAnimating(final String name, final ImageButton currantObject, ImageButton netObject, int delay){
        System.out.println("inside the the objectAnimating.............");

        recognizeIntent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        recognizeIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

        mySp = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionListener = new CustomRecognitionListener();
        mySp.setRecognitionListener(recognitionListener);



        final ViewPropertyAnimator animator = currantObject.animate();
        //say("who is this?");
        animator.scaleX(1.7f).scaleY(1.7f).setDuration(2000).translationX(0).translationY(400).setStartDelay(3000)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    System.out.println("inside the animation cancel ...............");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    mySp.startListening(recognizeIntent);
                    String v = recognitionListener.getReturnedText();
                    System.out.println("inside the onend..1.." + v);

                    //isRecognize(animator,name);


                    final Thread thread = new Thread() {

                        public void run() {
                            System.out.println("thread started.............");
                            final boolean[] retState = {false};
                            boolean st = true;
                            while (st) {
                                // mySp.startListening(recognizeIntent);
                                //String v =recognitionListener.getReturnedText();
                                //System.out.println("inside the onend..1.." +v);

                                System.out.println("inside the loop.....................");

                                final String result = recognitionListener.getReturnedText();
                                if (result == "default") {
                                    st = true;
                                    continue;
                                }
                                else {
                                    //st = false;

                                    if (result.equalsIgnoreCase(name)){
                                        st=false;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                System.out.println("name found about to call the say method...............");
                                                say("well done! ");
                                                //tts.stop();
                                                //mySp.stopListening();
                                                animator.scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(1000).setStartDelay(1000)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        flag =true;
                                                        mySp.cancel();
                                                        animator.cancel();
                                                    }
                                                });
                                                memberText.setText(recognitionListener.getReturnedText());
                                                return;
                                            }
                                        });
                                        break;
                                    }
                                     else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                say("please try again.");
                                                memberText.setText(recognitionListener.getReturnedText());
                                                return;
                                            }
                                        });

                                        continue;
                                    }


                                }
                            }

                            return;
                        }
                    };

                    thread.start();
                    System.out.println("after the calling loop...............");
                    mySp.stopListening();
                    //String v =recognitionListener.getReturnedText();
                    memberText.setText(v);
                    System.out.println("inside the onend..2.." + v);
                    animatorClose(animator);
                    System.out.println("end of the animation...");
                    System.out.println("ending words....."+recognitionListener.getReturnedText());

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    System.out.println("inside the the objectAnimating onstart.............");

                }

                @Override
                public void onAnimationPause(Animator animation) {
                    super.onAnimationPause(animation);
                }

                @Override
                public void onAnimationResume(Animator animation) {
                    super.onAnimationResume(animation);
                }
            }).start();



        return true;
    }


    public void animatorClose(ViewPropertyAnimator animator) {
        animator.cancel();
    }


    public String[] callTread(){

        final String[] st = {""};

        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){

                    String res = recognitionListener.getReturnedText();
                    if (res.equalsIgnoreCase("mother")){
                        System.out.println("inside the if before return............");
                        st[0] = "found";
                        break;
                        //return;
                    }
                    else {

                        System.out.println("this is not found...........");
                    }
                }
                System.out.println("after braking the loop......");
                return;
            }

        });

        System.out.println("from outside of the thread.......");
        td.start();
        return st;
    }


    @Override
    public void onInit(int i) {
        say("Identify the family members!");
    }

    public void say(String text2say) {
        System.out.println("inside the say() ....name......" + text2say);
        tts.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
        mySp.destroy();
        //Log.i(LOG_TAG, "destroy");
    }


}
