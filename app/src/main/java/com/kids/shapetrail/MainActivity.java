package com.kids.shapetrail;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.speech.tts.TextToSpeech;
import android.media.ToneGenerator;
import android.media.AudioManager;
import android.os.Handler;
import java.util.*;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
  private TraceView trace; private TextView title, count; private int current=0;
  private TextToSpeech voice; private boolean voiceReady=false; private Handler handler=new Handler(); private ToneGenerator chime;
  private final String[] shapes={"Circle","Square","Triangle","Rectangle","Oval","Diamond","Star","Heart","Pentagon","Hexagon","Octagon","Crescent","Cross","Arrow","Trapezoid","Parallelogram","Quatrefoil","Flower","Spiral"};
  private final String[] sides={"no sides — it is round","4 sides","3 sides","4 sides","no sides — it is round","4 sides","10 points","no sides — it is curved","5 sides","6 sides","8 sides","no sides — it is curved","12 sides","7 sides","4 sides","4 sides","4 rounded lobes","many soft petals","no sides — it curls around"};
  @Override public void onCreate(Bundle b){super.onCreate(b); chime=new ToneGenerator(AudioManager.STREAM_MUSIC,12); voice=new TextToSpeech(this,this); build();}
  @Override public void onInit(int status){voiceReady=status==TextToSpeech.SUCCESS; if(voiceReady){voice.setSpeechRate(.86f); voice.setPitch(1.18f); speakShape();}}
  void speak(String words){if(voiceReady) voice.speak(words,TextToSpeech.QUEUE_FLUSH,null,"shape");}
  void speakShape(){speak("This is a " + shapes[current] + ". It has " + sides[current] + ". Let's trace it!");}
  int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
  TextView label(String s,int size){ TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(Color.rgb(50,61,76));v.setGravity(Gravity.CENTER);return v; }
  GradientDrawable bg(int color,float r){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp((int)r));return g;}
  void build(){
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(14),dp(18),dp(14));root.setBackgroundColor(Color.rgb(255,249,240));
    title=label("Trace the " + shapes[current],28);title.setTypeface(null,1);root.addView(title,new LinearLayout.LayoutParams(-1,dp(45)));
    count=label("Shape "+(current+1)+" of "+shapes.length+"  •  " + sides[current],16);root.addView(count,new LinearLayout.LayoutParams(-1,dp(32)));
    trace=new TraceView(this); trace.setShape(current); root.addView(trace,new LinearLayout.LayoutParams(-1,0,1));
    LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);row.setPadding(0,dp(10),0,0);
    Button clear=button("Try again",Color.rgb(255,205,92));clear.setOnClickListener(v->trace.clear());row.addView(clear,new LinearLayout.LayoutParams(0,dp(52),1));
    Space s=new Space(this);row.addView(s,new LinearLayout.LayoutParams(dp(12),1));
    Button next=button("Next shape  ›",Color.rgb(113,205,177));next.setOnClickListener(v->next());row.addView(next,new LinearLayout.LayoutParams(0,dp(52),1));root.addView(row,new LinearLayout.LayoutParams(-1,dp(68)));
    setContentView(root);
  }
  Button button(String text,int c){Button b=new Button(this);b.setText(text);b.setTextSize(16);b.setTextColor(Color.rgb(45,55,65));b.setAllCaps(false);b.setBackground(bg(c,18));return b;}
  void next(){handler.removeCallbacksAndMessages(null);current=(current+1)%shapes.length;showShapeText();trace.setShape(current);speakShape();}
  void showShapeText(){title.setText("Trace the " + shapes[current]);count.setText("Shape "+(current+1)+" of "+shapes.length+"  •  " + sides[current]);title.setVisibility(View.VISIBLE);count.setVisibility(View.VISIBLE);}
  void completed(){chime.startTone(ToneGenerator.TONE_DTMF_9,120);title.setVisibility(View.INVISIBLE);count.setVisibility(View.INVISIBLE);}
  @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);if(voice!=null)voice.shutdown();if(chime!=null)chime.release();super.onDestroy();}
  class TraceView extends View {
    Paint guide=new Paint(1), ink=new Paint(1), fill=new Paint(1), toy=new Paint(1); Path target=new Path(), drawing=new Path(); int shape=0; boolean done=false; float toyTurn=0;
    TraceView(Activity c){super(c);guide.setStyle(Paint.Style.STROKE);guide.setStrokeWidth(dp(9));guide.setStrokeCap(Paint.Cap.ROUND);guide.setColor(Color.rgb(166,196,221));guide.setPathEffect(new DashPathEffect(new float[]{dp(3),dp(16)},0));ink.setStyle(Paint.Style.STROKE);ink.setStrokeWidth(dp(10));ink.setStrokeCap(Paint.Cap.ROUND);ink.setStrokeJoin(Paint.Join.ROUND);ink.setColor(Color.rgb(255,112,67));fill.setColor(Color.rgb(232,248,241));}
    void setShape(int x){shape=x;drawing.reset();done=false;toyTurn=0;invalidate();animateToy();}
    void clear(){drawing.reset();done=false;showShapeText();invalidate();}
    protected void onDraw(Canvas c){super.onDraw(c); float w=getWidth(),h=getHeight();c.drawRoundRect(dp(6),dp(6),w-dp(6),h-dp(6),dp(28),dp(28),fill); target.reset(); make(target,w,h);c.save();c.rotate(toyTurn,w/2,h/2+dp(20));toy.setStyle(Paint.Style.STROKE);toy.setStrokeWidth(dp(18));toy.setStrokeCap(Paint.Cap.ROUND);toy.setColor(Color.argb(38,255,148,103));c.drawPath(target,toy);c.restore();c.drawPath(target,guide);c.drawPath(drawing,ink);}
    void animateToy(){if(toyTurn<8&&!done){toyTurn+=.5f;invalidate();postDelayed(()->animateToy(),35);}}
    void make(Path p,float w,float h){float cx=w/2,cy=h/2+dp(20),r=Math.min(w,h)*.27f,l=cx-r,t=cy-r,rr=cx+r,bb=cy+r; switch(shape){
      case 0:p.addCircle(cx,cy,r,Path.Direction.CW);break; case 1:p.addRect(l,t,rr,bb,Path.Direction.CW);break; case 2:p.moveTo(cx,t);p.lineTo(rr,bb);p.lineTo(l,bb);p.close();break; case 3:p.addRect(l,t+dp(45),rr,bb-dp(45),Path.Direction.CW);break; case 4:p.addOval(l,t+dp(55),rr,bb-dp(55),Path.Direction.CW);break; case 5:poly(p,cx,cy,r,4,Math.PI/4);break;case 6:star(p,cx,cy,r);break;case 7:heart(p,cx,cy,r);break;case 8:poly(p,cx,cy,r,5,-Math.PI/2);break;case 9:poly(p,cx,cy,r,6,0);break;case 10:poly(p,cx,cy,r,8,Math.PI/8);break;case 11:crescent(p,cx,cy,r);break;case 12:cross(p,cx,cy,r);break;case 13:arrow(p,cx,cy,r);break;case 14:trap(p,cx,cy,r);break;case 15:para(p,cx,cy,r);break;case 16:quatrefoil(p,cx,cy,r);break;case 17:flower(p,cx,cy,r);break;default:spiral(p,cx,cy,r);}}
    void poly(Path p,float x,float y,float r,int n,double a){for(int i=0;i<n;i++){double q=a+i*2*Math.PI/n;float X=x+(float)Math.cos(q)*r,Y=y+(float)Math.sin(q)*r;if(i==0)p.moveTo(X,Y);else p.lineTo(X,Y);}p.close();}
    void star(Path p,float x,float y,float r){for(int i=0;i<10;i++){double q=-Math.PI/2+i*Math.PI/5;float z=i%2==0?r:r*.43f;if(i==0)p.moveTo(x+(float)Math.cos(q)*z,y+(float)Math.sin(q)*z);else p.lineTo(x+(float)Math.cos(q)*z,y+(float)Math.sin(q)*z);}p.close();}
    void heart(Path p,float x,float y,float r){p.moveTo(x,y+r*.9f);p.cubicTo(x-r*1.4f,y+r*.1f,x-r,y-r*.9f,x,y-r*.25f);p.cubicTo(x+r,y-r*.9f,x+r*1.4f,y+r*.1f,x,y+r*.9f);}
    void crescent(Path p,float x,float y,float r){p.moveTo(x+r*.5f,y-r);p.cubicTo(x-r,y-r,x-r,y+r,x+r*.5f,y+r);p.cubicTo(x-r*.1f,y+r*.35f,x-r*.1f,y-r*.35f,x+r*.5f,y-r);}
    void cross(Path p,float x,float y,float r){float a=r*.35f;p.moveTo(x-a,y-r);p.lineTo(x+a,y-r);p.lineTo(x+a,y-a);p.lineTo(x+r,y-a);p.lineTo(x+r,y+a);p.lineTo(x+a,y+a);p.lineTo(x+a,y+r);p.lineTo(x-a,y+r);p.lineTo(x-a,y+a);p.lineTo(x-r,y+a);p.lineTo(x-r,y-a);p.lineTo(x-a,y-a);p.close();}
    void arrow(Path p,float x,float y,float r){p.moveTo(x-r,y-r*.35f);p.lineTo(x,y-r*.35f);p.lineTo(x,y-r);p.lineTo(x+r,y);p.lineTo(x,y+r);p.lineTo(x,y+r*.35f);p.lineTo(x-r,y+r*.35f);p.close();}
    void trap(Path p,float x,float y,float r){p.moveTo(x-r*.65f,y-r);p.lineTo(x+r*.65f,y-r);p.lineTo(x+r,y+r);p.lineTo(x-r,y+r);p.close();}
    void para(Path p,float x,float y,float r){p.moveTo(x-r*.55f,y-r);p.lineTo(x+r,y-r);p.lineTo(x+r*.55f,y+r);p.lineTo(x-r,y+r);p.close();}
    void quatrefoil(Path p,float x,float y,float r){float q=r*.52f;p.addCircle(x-q,y-q,q,Path.Direction.CW);p.addCircle(x+q,y-q,q,Path.Direction.CW);p.addCircle(x+q,y+q,q,Path.Direction.CW);p.addCircle(x-q,y+q,q,Path.Direction.CW);}
    void cloud(Path p,float x,float y,float r){p.moveTo(x-r,y+r*.45f);p.cubicTo(x-r*1.25f,y-r*.1f,x-r*.5f,y-r*.5f,x-r*.15f,y-r*.15f);p.cubicTo(x,y-r*1.1f,x+r*.8f,y-r*.8f,x+r*.6f,y-r*.15f);p.cubicTo(x+r*1.25f,y-r*.05f,x+r*1.1f,y+r*.55f,x+r*.55f,y+r*.45f);p.close();}
    void sun(Path p,float x,float y,float r){for(int i=0;i<16;i++){double q=i*Math.PI/8;float z=i%2==0?r:r*.58f;if(i==0)p.moveTo(x+(float)Math.cos(q)*z,y+(float)Math.sin(q)*z);else p.lineTo(x+(float)Math.cos(q)*z,y+(float)Math.sin(q)*z);}p.close();}
    void flower(Path p,float x,float y,float r){for(int i=0;i<41;i++){double q=i*2*Math.PI/40;float z=r*(.65f+.28f*(float)Math.sin(5*q));if(i==0)p.moveTo(x+(float)Math.cos(q)*z,y+(float)Math.sin(q)*z);else p.lineTo(x+(float)Math.cos(q)*z,y+(float)Math.sin(q)*z);}p.close();}
    void spiral(Path p,float x,float y,float r){for(int i=0;i<120;i++){double q=i*.14;float z=r*i/120f;float X=x+(float)Math.cos(q)*z,Y=y+(float)Math.sin(q)*z;if(i==0)p.moveTo(X,Y);else p.lineTo(X,Y);}}
    public boolean onTouchEvent(android.view.MotionEvent e){if(done)return true;float x=e.getX(),y=e.getY();if(e.getAction()==0){drawing.moveTo(x,y);return true;}if(e.getAction()==2){drawing.lineTo(x,y);invalidate();return true;}if(e.getAction()==1){drawing.lineTo(x,y);done=true;invalidate();completed();return true;}return true;}
  }
}
