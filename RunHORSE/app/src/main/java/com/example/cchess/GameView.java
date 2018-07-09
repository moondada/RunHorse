package com.example.cchess;

import java.util.List;


import com.algorithm.AlphaBetaEngine;
import com.algorithm.Chessconst;
import com.algorithm.Chessmov;
import com.algorithm.Evaluation;
import com.algorithm.MoveGenerator;
import com.sqlite.DBManager;
import com.sqlite.ModelChessmov;
import com.util.ActivityHelper;
import com.util.TimeUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 该类是整个程序最主要的类，是主游戏的界面 该界面继承自SurfaceView并实现了SurfaceHolder.Callback接口
 * 其中包含了一个刷帧的线程类
 * 
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	DBManager chessmovModelManager ;
	
	private TutorialThread thread;// 刷帧的线程
	TimeThread timeThread;
	MainActivity activity;// 声明Activity的引用

	Bitmap regret;
	Bitmap win;// 胜利的图片
	Bitmap lost;// 失败的图片
	Bitmap ok;// 确定按钮
	Bitmap exit2;// 退出按钮图片
	Bitmap sound2;// 声音按钮图片
	Bitmap sound3;// 当前是否播放了声音
	Bitmap background;// 背景图片
	MediaPlayer go;// 下棋声音
	Paint paint;// 画笔
	boolean isIgo = true;// 是否为玩家走棋
	boolean focus = false;// 当前是否有选中的棋子
	int selectqizi = 0; // 当前选中的棋子

	int startI, startJ;// 记录当前棋子的开始位置
	int endI, endJ;// 记录当前棋子的目标位置

	
	int status = 0;// 游戏状态： 0游戏中， 1胜利,  2失败
	int heiTime = 0;// 黑方总共思考时间
	int hongTime = 0;// 红方总共思考时间
    int NSteps = 0;
    int Score = 0;
    int N = 0;
    int n = 8;
    int DDL = (Math.abs(7-n)+1)*60;
	String nowTime = new TimeUtil().getNowime();
	
	int[][] Q = new int[][] { // 棋盘 10行 9列 ， 初始各位置数字.4,5,6
            { 0, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 2, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 2, 0, 0, 0, 0, 0, 0 },
            { 0, 2, 0, 2, 0, 0, 0, 0, 0 },
            { 7, 0, 2, 0, 8, 0, 0, 10, 0 } };
    int[][]qizi = FirstQizi(Qizi(Q), n);


	int width;// 界面宽度（右边界）
	int height;// 界面高度（下边界）
	float dpvalue;
	int rectWidth, rectHeight, itemwidth; // 棋盘最终宽度
	int left, top, itop, ileft ;
	int radix ;
	int qipanW ;
	int qipanH ;
	
	int bottomHeight ; 
	int jianxi ;
	int bottomY ;
	int y1 ;
	int y2 ;
	int y3 ;
	
	int selectColor = getResources().getColor(R.color.selectColor);
	
	// 搜索引擎 评估函数
	public MoveGenerator pMG;
	public Evaluation pEvel;
	public AlphaBetaEngine pSE;
	
	public ModelChessmov last_black_mov=null;
	
	public GameView(Context context) {
		super(context);
	}

	public GameView(Context context, MainActivity activity) {	// 构造器
		super(context);
		
		chessmovModelManager = new DBManager(context);
	/*	List<ModelChessmov> ml = chessmovModelManager.Chessmov_getAll();
		int count = ml.size();
		Log.i("dwl", ""+count);*/
		
		this.activity = activity;	// 得到Activity的引用
		getHolder().addCallback(this);
		go = MediaPlayer.create(this.getContext(), R.raw.go);// 加载下棋的声音
		this.thread = new TutorialThread(getHolder(), this);// 初始化刷帧线程
		this.timeThread = new TimeThread(this);// 初始化思考时间的线程
		
		init();// 初始化所需资源
		
		
		
		width = activity.screenW;
		height = activity.screenH;
		
		ActivityHelper helper = new ActivityHelper(context);
		dpvalue = helper.convertDpToPixel(1, context);
		left = helper.convertDpToPixel(20, context);// 最小留空区域
		int minl = width > height ? height : width;// 获取宽高中最小项

		rectWidth = minl - left * 2; // 外框 宽
		itemwidth = (rectWidth - left / 2) / 8; // 间距
		rectHeight = itemwidth * 9 + left / 2; //外框 高 
		top = (height - rectHeight) / 4;
		itop = top + left / 4; // 内框距离上方的距离
		ileft = left + left / 4; //内框距离左边的距离
		radix = itemwidth / 2 ; // 棋子半径
		qipanW = 8 * itemwidth ; // 整个棋盘的宽度
		qipanH = 9 * itemwidth ; // 整个棋盘的高度
		
		
		pMG = new MoveGenerator();// move产生器
		pEvel = new Evaluation(); //估值对象
		pSE = new AlphaBetaEngine();//搜索算法
		pSE.setM_nSearchDepth(3);
		pSE.setM_pMG(pMG);
		pSE.setM_pEval(pEvel);	
	}

	public void init() {// 初始化方法
		paint = new Paint();// 初始化画笔
	
		win = BitmapFactory.decodeResource(getResources(), R.drawable.vs_win);// 胜利的图片
		lost = BitmapFactory.decodeResource(getResources(), R.drawable.vs_lost);// 失败的图片
		ok = BitmapFactory.decodeResource(getResources(), R.drawable.ok);// 确定按钮图片
		exit2 = BitmapFactory.decodeResource(getResources(), R.drawable.exit2);// 退出按钮图片
		sound2 = BitmapFactory.decodeResource(getResources(), R.drawable.sound2);// 声音按钮图片
		sound3 = BitmapFactory.decodeResource(getResources(), R.drawable.sound3);
		regret = BitmapFactory.decodeResource(getResources(), R.drawable.regret2);
		
	}

	public int[][] FirstQizi(int[][] qizi, int n){
        int[][] Q = qizi;
        for(int i = 0;i<5;i++){
            int x=(int)(Math.random()*8);
            int y=(int)(Math.random()*3+2);
            while(qizi[y][x] != 0 || (qizi[y+1][x] + qizi[y][x+1] + qizi[y+1][x+1] > 3)){
                x=(int)(Math.random()*8);
                y=(int)(Math.random()*3+2);
            }
            Q[y][x]=2;
        }
        for(int i = 0;i<3;i++){
            int x=(int)(Math.random()*8);
            int y=(int)(Math.random()*3+5);
            while(qizi[y][x] != 0 || (qizi[y-1][x] + qizi[y][x+1] + qizi[y-1][x+1] > 2)
					|| ((x <= 3) && y  >= 7)){
                x=(int)(Math.random()*8);
                y=(int)(Math.random()*3+5);
            }
            Q[y][x]=2;
        }
        return  Q;
    }

    public int[][] Qizi(int[][] qizi) {// 随机投放得分点方法
        int[][] Q = qizi;
        int x=(int)(Math.random()*9);
        int y=(int)(Math.random()*10);
        int D = 0;
        while(qizi[y][x] != 0 || ((x <= 2) && y  >= 7)){
            x=(int)(Math.random()*9);
            y=(int)(Math.random()*10);
        }
        for(int i = 0;i<9;i++){
            for(int j = 0;j<10;j++){
                if(Q[j][i] == 10)
                    D = Math.max(Math.abs(j-y), Math.abs(i-x));//棋盘距离
            }
        }
        if(D<4)
            Q[y][x]=4;
        else if(D>7)
            Q[y][x]=5;
        else
            Q[y][x]=6;
        return Q;
    }


	// 绘制折线
	private void drawItemKH(Canvas c, int li, int ti, Paint p) {
		// 获取中心坐标
		int x = ileft + li * itemwidth;
		int y = itop + ti * itemwidth;
		// 线长
		int len = itemwidth / 4;
		// 距离中心
		int dc = itemwidth / 10;

		// left
		if (li != 0) {
			c.drawLine(x - dc, y + dc, x - dc - len, y + dc, p);
			c.drawLine(x - dc, y - dc, x - dc - len, y - dc, p);
			c.drawLine(x - dc, y + dc, x - dc, y + dc + len, p);
			c.drawLine(x - dc, y - dc, x - dc, y - dc - len, p);
		}
		// right
		if (li != 8) {
			c.drawLine(x + dc, y + dc, x + dc + len, y + dc, p);
			c.drawLine(x + dc, y - dc, x + dc + len, y - dc, p);
			c.drawLine(x + dc, y + dc, x + dc, y + dc + len, p);
			c.drawLine(x + dc, y - dc, x + dc, y - dc - len, p);
		}		
	}
		
	/**
	 * 绘制屏幕 ，该方法是自己定义的并非重写的 
	 */
	public void oonDraw(Canvas canvas) {// 自己写的绘制方法
		if(canvas == null)
			return ;
		
		canvas.drawColor(Color.WHITE);// 设置画布背景颜色黑色
		Paint p = new Paint();// 设置画笔
		p.setAntiAlias(true);// 设置取消锯齿效果 
		p.setStrokeWidth(dpvalue * 2); // 设置宽度
		p.setStyle(Paint.Style.STROKE); // 设置空心
		
		/* 设置颜色及绘制矩形 */
		p.setColor(Color.RED);
		canvas.drawRect(left, top, left + rectWidth, top + rectHeight, p);// 绘制外框

		p.setStrokeWidth(dpvalue * 1); // 设置宽度
		canvas.drawRect(ileft, itop, ileft + itemwidth * 8, itop + 9 * itemwidth, p);// 绘制内框
		
		for (int i = 0; i < 9; i++) // 横线
			canvas.drawLine(ileft, itop + i * itemwidth, ileft + itemwidth * 8 , itop + i * itemwidth, p);
		for (int i = 0; i < 8; i++) {// 竖线  楚河-汉界隔开 了，所以画两条
			canvas.drawLine(ileft + i * itemwidth, itop, ileft + i * itemwidth,itop + 4 * itemwidth, p);
			canvas.drawLine(ileft + i * itemwidth, itop + 5 * itemwidth, ileft + i * itemwidth, itop + 9 * itemwidth, p);
		}
		
		p.setTextSize(itemwidth / 2);
		float textWidth = p.measureText("楚河");
		float textHeight = textWidth / 2;
		int centy = (top * 2 + rectHeight) / 2;
		int centx = (ileft + itemwidth * 2);
		int centx2 = (ileft + itemwidth * 6);
		p.setStyle(Paint.Style.FILL);
		canvas.drawText("楚河", centx - textWidth / 2, centy + textHeight / 3, p); // 画出文字
		canvas.drawText("汉界", centx2 - textWidth / 2, centy + textHeight / 3, p); // 画出文字
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		
		// 斜线
		canvas.drawLine(ileft + 3 * itemwidth, itop + 0 * itemwidth, ileft + 5 * itemwidth, itop + 2 * itemwidth, p);
		canvas.drawLine(ileft + 3 * itemwidth, itop + 2 * itemwidth, ileft + 5 * itemwidth, itop + 0 * itemwidth, p);

		canvas.drawLine(ileft + 3 * itemwidth, itop + 7 * itemwidth, ileft + 5* itemwidth, itop + 9 * itemwidth, p);
		canvas.drawLine(ileft + 3 * itemwidth, itop + 9 * itemwidth, ileft + 5* itemwidth, itop + 7 * itemwidth, p);
		
		// 折线
		drawItemKH(canvas, 0, 3, p);
		drawItemKH(canvas, 1, 2, p);
		drawItemKH(canvas, 7, 2, p);
		drawItemKH(canvas, 2, 3, p);
		drawItemKH(canvas, 4, 3, p);
		drawItemKH(canvas, 6, 3, p);
		drawItemKH(canvas, 8, 3, p);

		drawItemKH(canvas, 0, 6, p);
		drawItemKH(canvas, 1, 7, p);
		drawItemKH(canvas, 7, 7, p);
		drawItemKH(canvas, 2, 6, p);
		drawItemKH(canvas, 4, 6, p);
		drawItemKH(canvas, 6, 6, p);
		drawItemKH(canvas, 8, 6, p);
	
		// 绘制棋子
        int ST = Math.min(DDL, 240) - NSteps - hongTime;
        if(ST < 0 && N == 0){
            qizi[9][2] = 0;
            N++;
        }
		for (int i = 0; i < 10; i++) { // 行
			for (int j = 0; j < 9; j++) { // 列
				if (qizi[i][j] != 0) {
					//canvas.drawBitmap(qizibackground, 9 + j * heng,10 + i * su, null);// 绘制棋子的背景
//					Bitmap bitmap; 
//					if(qizi[i][j] <= 7){
//						bitmap = heiZi[qizi[i][j]-1];
//					}else{
//						bitmap = hongZi[qizi[i][j]-8];
//					}
//					drawUtil.drawChess(canvas, bitmap, qizibackground,ileft+j*itemwidth, itop+i*itemwidth, itemwidth/4+itemwidth/9, paint);
//					
					String chessText = Chessconst.getChessText(qizi[i][j]);
					
					Paint p2 = new Paint();
					p2.setStyle(Paint.Style.FILL);
					p2.setARGB(255, 220, 200, 200);
					canvas.drawCircle(ileft + j * itemwidth,
							itop + i * itemwidth, itemwidth / 2, p2);
					// 设置棋子的颜色
					if (qizi[i][j] == 7 | qizi[i][j] == 1 | qizi[i][j] == 3) {
						p2.setColor(Color.BLACK);
					} else if(qizi[i][j] == 8 | qizi[i][j] == 10 | qizi[i][j] == 14){
						p2.setColor(Color.RED);
					} else if(qizi[i][j] == 2){
						p2.setColor(Color.YELLOW);
					} else {
						p2.setColor(Color.BLUE);
					}
					p2.setTextSize(itemwidth / 2);
					float textWidth2 = p2.measureText("字"); // 一个字的大小
					// 画棋子
					canvas.drawText(chessText, ileft + j * itemwidth
							- textWidth2 / 2, itop + i* itemwidth
							+ textWidth2 / 3, p2); 
					
				}	
			}
		}
		
		// 显示黑方上一次走步的选择颜色
		Paint ppp2 = new Paint(); 
	 	ppp2.setStyle(Paint.Style.STROKE); // 变成空心的圆
	 	ppp2.setStrokeWidth(dpvalue * 2); // 设置宽度
	 	
		if(last_black_mov != null){
			int xx = last_black_mov.fromX;
			int yy = last_black_mov.fromY;
			ppp2.setColor(selectColor);
			canvas.drawCircle(ileft+yy*itemwidth, itop+xx*itemwidth, radix,ppp2);
			
			xx = last_black_mov.toX;
			yy = last_black_mov.toY;
			ppp2.setColor(Color.BLUE);
			canvas.drawCircle(ileft+yy*itemwidth, itop+xx*itemwidth, radix,ppp2);
		}
		
		
		// 自己选中的,要走的棋子
		if(focus){
			if(selectqizi > 7){
				Paint ppp = new Paint(); 
				
				ppp.setColor(selectColor);
			 	ppp.setStyle(Paint.Style.STROKE); // 变成空心的圆
			 	ppp.setStrokeWidth(dpvalue * 2); // 设置宽度
				canvas.drawCircle(ileft+startJ*itemwidth, itop+startI*itemwidth, radix, ppp);
//						canvas.drawRect(startX+startJ*colSpan-radix, startY+startI*rowSpan-radix,
//								startX+startJ*colSpan+radix, startY+startI*rowSpan+radix
//								, ppp);
				Log.i("dwl", "---------------");
				
				/**
				 * 产生下一步棋子走法
				 */
				MoveGenerator mg = new MoveGenerator();
				
				switch (selectqizi) {
					case 14:
						mg.Gen_RPawnMove(qizi, startI, startJ, 0);
						break;
					case 10:
						mg.Gen_HorseMove(qizi, startI, startJ, 0);
						if (mg.m_nMoveCount == 0 && qizi[startI][startJ] != 0){
							qizi[startI][startJ] = 0;
							qizi[9][7] = 10;
							Score -= 5;
						}
						break;
					default:
						break;
				}
				
					/*Log.i("dwl", ""+
					mg.m_MoveList[0][0].To.x + "-" + mg.m_MoveList[0][0].To.y
							);*/
					
                Paint pc = new Paint();
                pc.setStyle(Paint.Style.STROKE); // 变成空心的圆
                pc.setStrokeWidth(dpvalue * 1); // 设置宽度
                pc.setColor(Color.BLUE);
					
                for(int k=0;k<mg.m_nMoveCount;k++){
                    canvas.drawCircle(ileft+mg.m_MoveList[0][k].To.x*itemwidth,
                            itop+mg.m_MoveList[0][k].To.y*itemwidth, radix/2, pc);
                }

            }
					
        }
		
		//绘制当前时间
        Paint pTime = new Paint();
        pTime.setTextSize(itemwidth / 2);
        pTime.setColor(Color.RED);
        canvas.drawText("当前时间："+nowTime, left , (itop - radix)/2, pTime);
		
        bottomHeight = (height - top - qipanH - radix);
        jianxi = bottomHeight / 3 ;
        bottomY = itop + 9*itemwidth + radix;
        y1 = bottomY + jianxi * 1 / 3 ;
        y2 = (int) (y1 + textHeight * 2);
        y3 = (int) (y2 + textHeight * 2);
        // 红方时间
        pTime.setColor(Color.RED);
        int[] hTime = new TimeUtil().getHMS(hongTime);
        int h_hour = hTime[0];
        int h_minute = hTime[1];
        int h_second = hTime[2];
        String hStr =  new TimeUtil().time2Str(h_hour, h_minute, h_second);
        canvas.drawText("红方得分："+Score, left, y1 , pTime);
        // 黑方时间
		if(Math.abs(hongTime)%2 == 1)
        	pTime.setColor(Color.BLACK);
		if(ST <= 0)
			pTime.setColor(Color.YELLOW);
        int[] bTime = new TimeUtil().getHMS(heiTime);
        int b_hour = bTime[0];
        int b_minute = bTime[1];
        int b_second = bTime[2];
        String bStr =  new TimeUtil().time2Str(b_hour, b_minute, b_second);
        canvas.drawText("兵牢将破："+ Math.max(ST, 0), left, y2 , pTime);

        textWidth = p.measureText("兵牢将破：000");
        if (isIgo == true) {// 当该玩家走棋时,即红方走棋
            pTime.setColor(Color.RED);
            canvas.drawText("归红方走", left*4/3 + textWidth, y1 , pTime);
        } else { // 黑方走棋，即电脑走棋时
            pTime.setColor(Color.BLACK);
            canvas.drawText("归黑方走", left*4/3 + textWidth, y2, pTime);
        }

        // 绘制声音
        canvas.drawBitmap(sound2, ileft, y3 , paint);
        if (activity.isSound) {// 如果正在播放声音
            canvas.drawBitmap(sound3, ileft, y3 , paint);
        }
		
		// 绘制退出按钮
		canvas.drawBitmap(exit2, ileft + itemwidth*4, y3 , paint);
		if (status == 1) {// 当胜利时
			pTime.setColor(Color.BLUE);
			canvas.drawBitmap(win, left + itemwidth*3, top + itemwidth*2 , paint);
			canvas.drawText("得分："+Score, left + itemwidth*3, top + itemwidth*3 , pTime);// 绘制胜利图片
			canvas.drawBitmap(ok, left + itemwidth*3 + ok.getWidth()/3, top + itemwidth*5/2 + win.getHeight(), paint);
		}
		if (status == 2) {// 失败后
			canvas.drawBitmap(lost, left + itemwidth*3, top + itemwidth*4 , paint);// 绘制失败图片
			canvas.drawBitmap(ok, left + itemwidth*3+ok.getWidth()/3, top + itemwidth*4 + lost.getHeight(), paint);
		}
		
		//绘制悔棋按钮
		canvas.drawBitmap(regret, ileft + itemwidth*13/2, y3 , paint);
	}

	/**
	 * 	该方法是游戏主要逻辑接口 接受玩家输入 根据点击的位置和当前的游戏状态做出相应的处理
	 * 	而当需要切换View时，通过给Activity发送Handler消息来处理 注意的是只取屏幕被按下的事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if(status == 1) {// 胜利后
				if (event.getX() > left + itemwidth*1 && event.getX() < left + itemwidth*5+ok.getWidth()/3+ok.getWidth()
						&& event.getY() > top + itemwidth*2 + win.getHeight()
						&& event.getY() < top + itemwidth*4 + win.getHeight() + ok.getHeight() ) { //点击了确定按钮
					
					activity.myHandler.sendEmptyMessage(1);// 发送消息，切换到MenuView
					
				}else{
					return false;
				}
			}else if (status == 2) {// 失败后
				if (event.getX() > left + itemwidth*1 && event.getX() < left + itemwidth*5+ok.getWidth()/3+ok.getWidth()
						&& event.getY() > top + itemwidth*2 + lost.getHeight()
						&& event.getY() < top + itemwidth*4 + lost.getHeight() + ok.getHeight() ) { //点击了确定按钮
					
					activity.myHandler.sendEmptyMessage(1);// 发送消息，切换到MenuView
					
				}else{
					return false;
				}
			}
			
			// 按下了声音按钮
			if (event.getX() > ileft && event.getX() < ileft + sound2.getWidth()
					&& event.getY() > y3 && event.getY() < y3 + sound2.getHeight()) {	
				activity.isSound = !activity.isSound;// 声音取反
				if (activity.isSound) {// 当需要放声音时
					if (activity.gamesound != null) {	//gamesound不为空时
						if (!activity.gamesound.isPlaying()) {	// 当前没有音乐时,播放音乐
							activity.gamesound.start(); 
						}
					}
				} else {
					if (activity.gamesound != null) {	//gamesound不为空时
						if (activity.gamesound.isPlaying()) {	// 当前有音乐时,停止音乐
							activity.gamesound.pause();
						}
					}
				}
			}
			
			// 悔棋
			if (event.getX() > ileft+itemwidth*13/2 && event.getX() < ileft+itemwidth*13/2 + regret.getWidth() 
					&& event.getY() > y3 && event.getY() < y3 + regret.getHeight()) {
				
				stepBackTest();
				
			}
			
			//退出
			if (event.getX() > ileft+itemwidth*4 && event.getX() < ileft+itemwidth*4 + exit2.getWidth()
					&& event.getY() > y3
					&& event.getY() < y3 + exit2.getHeight()) {
				
				activity.myHandler.sendEmptyMessage(1);// 发送消息，切换到MenuView
				
			}
			
			
			/**
			 * 游戏过程中的逻辑处理
			 * 当点击棋盘时，先判断当前是否为玩家走棋， 
			 * 				然后再判断当前玩家是否已经有选中的棋子,
			 * 			如果没有  ，则选中
			 * 			如果之前有选中的棋子，再判断点击的位置是空地、对方棋子还是自己的棋子 
			 * 				是空地判断是否可走
			 * 				是对方棋子同样判断是否可以走，能走自然吃子 
			 * 				是自己的棋子则选中该棋子
			 */
			else if (status == 0) {// 游戏中时
				// 点击的位置在棋盘内时
				if (event.getX() > ileft - radix && event.getX() < ileft + qipanW + radix
						&& event.getY() > itop - radix && event.getY() < itop + qipanH + radix) {
					
					//Toast.makeText(getContext(), event.getX()+"-"+event.getY(), Toast.LENGTH_SHORT).show();
					
					if (isIgo == true) {	// 如果是该玩家走棋
						int i = -1, j = -1;
						int[] pos = getPos(event);// 根据坐标换算成所在的行和列
						i = pos[0];
						j = pos[1];
						// Toast.makeText(getContext(), "行："+i+" 列："+j, Toast.LENGTH_SHORT).show();
						if (focus == false) {// 之前没有选中的棋子
							if (qizi[i][j] != 0) {// 点击的位置有棋子
								if (qizi[i][j] > 7) {// 点击的是自己的棋子。即下面的黑色棋子
									selectqizi = qizi[i][j];// 将该棋子设为选中的棋子
									focus = true;	// 标记当前有选中的棋子
									startI = i;
									startJ = j;
								}
							}
						} else {// 之前选中过棋子
							if (qizi[i][j] != 0) {// 点击的位置有棋子
								if (qizi[i][j] > 7) {// 如果是自己的棋子.
									selectqizi = qizi[i][j];// 将该棋子设为选中的棋子
									startI = i;
									startJ = j;
								} else {// 如果是对方的棋子
									endI = i;
									endJ = j;// 保存该点
									//boolean canMove = guiZe.canMove(qizi,startI, startJ, endI, endJ);
									boolean canMove = pMG.IsValidMove(qizi, startJ, startI, endJ, endI);
									if (canMove) {// 如果可以移动过去
                                        NSteps = NSteps + 1;
										isIgo = false;// 不让玩家走了
										if (qizi[endI][endJ] == 1) {// 如果是“帅”或“将”
											this.status = 1 ; // 胜利了
                                            Score += 5;
										} else {
											if (activity.isSound) {
												go.start();// 播放下棋声音
											}
											
											/**数据库处理**/
											chessmovModelManager.chessmov_deleteAll();
											ModelChessmov mc = new ModelChessmov("1", qizi[startI][startJ]
													, startI,startJ, 
													qizi[endI][endJ], endI, endJ);
											chessmovModelManager.chessmov_insertChessmov(mc);

                                            if(qizi[endI][endJ]>3 && qizi[endI][endJ] <7){
                                                qizi = Qizi(qizi);
                                                Score += (qizi[endI][endJ]-3)*10;
                                            }
											qizi[endI][endJ] = qizi[startI][startJ];// 移动棋子
											qizi[startI][startJ] = 0;// 将原来处设空
											startI = -1;
											startJ = -1;
											endI = -1;
											endJ = -1;// 还原保存点
											focus = false;// 标记当前没有选中棋子
/**************/
											pSE.SearchAGoodMove(qizi);// 根据当前局势查询一个最好的走法
											Chessmov cm = pSE.getM_cmBestMove();
											
											/**数据库处理**/
											ModelChessmov mc2 = new ModelChessmov("2", cm.chessId
													, cm.From.y,cm.From.x, 
													cm.chessId2,cm.To.y,cm.To.x );
											chessmovModelManager.chessmov_insertChessmov(mc2);
											
											last_black_mov = mc2 ;
											
											if(cm.chessId2 == Chessconst.R_KING){
												this.status = 2;
											}
											if (activity.isSound) {
												go.start();// 播放下棋声音
											}
											//qizi[cm.To.y][cm.To.x] = qizi[cm.From.y][cm.From.x];// 移动棋子
											//qizi[cm.From.y][cm.From.x] = 0;
											isIgo = true;// 恢复玩家响应
										}
									}
								}
							}// end点击的位置有棋子
							else {// 如果点击的位置没有棋子
								endI = i;
								endJ = j;
								//boolean canMove = guiZe.canMove(qizi, startI,startJ, endI, endJ);// 查看是否可走
								boolean canMove = pMG.IsValidMove(qizi, startJ, startI, endJ, endI);
								if (canMove) {// 如果可以移动
                                    NSteps = NSteps + 1;
									isIgo = false;// 不让玩家走了
									if (qizi[endI][endJ] == 1) {// 如果是“帅”或“将”
										this.status = 1 ; // 胜利了
									} else {
										if (activity.isSound) {
											go.start();// 播放下棋声音
										}
										/**数据库处理**/
										chessmovModelManager.chessmov_deleteAll();
										ModelChessmov mc = new ModelChessmov("1", qizi[startI][startJ]
												, startI,startJ, 
												qizi[endI][endJ], endI, endJ);
										chessmovModelManager.chessmov_insertChessmov(mc);

                                        if(qizi[endI][endJ]>3 && qizi[endI][endJ] <7){
                                            qizi = Qizi(qizi);
                                        }
										qizi[endI][endJ] = qizi[startI][startJ];// 移动棋子
										qizi[startI][startJ] = 0;// 将原来处置空
										startI = -1;
										startJ = -1;
										endI = -1;
										endJ = -1;// 还原保存点
										focus = false;// 标志位设false
	/**************/
										pSE.SearchAGoodMove(qizi);
										Chessmov cm = pSE.getM_cmBestMove();
										/**数据库处理**/
										ModelChessmov mc2 = new ModelChessmov("2", cm.chessId
												, cm.From.y, cm.From.x,
												cm.chessId2,cm.To.y,cm.To.x);
										chessmovModelManager.chessmov_insertChessmov(mc2);
										
										last_black_mov = mc2 ;
										
										if(cm.chessId2 == Chessconst.R_KING){
											this.status = 2;
										}
										
										if (qizi[cm.To.y][cm.To.x] == 8) {// 电脑吃了您的将
											status = 2;// 切换游戏状态为失败
										}
										if (activity.isSound) {// 需要播放声音时
											go.start();// 播放下棋声音
										}
										//qizi[cm.To.x][cm.To.y] = qizi[cm.From.x][cm.From.y];// 移动棋子
										//qizi[cm.From.x][cm.From.y] = 0;
										isIgo = true;// 恢复玩家响应
									}
								}
							}
						}// end 之前选中过棋子
					}
				}// end点击的位置在棋盘内时
			}// end游戏中时
		}
		return super.onTouchEvent(event);
	}

	public int[] getPos(MotionEvent e) {	//将棋盘坐标换算成数组的维数
		int[] pos = new int[2];
		double x = e.getX();// 得到点击位置的x坐标
		double y = e.getY();// 得到点击位置的y坐标
		if (	x > ileft - radix 
				&& y > itop - radix 
				&& x < ileft + qipanW + radix 
				&& y < itop + qipanH + radix) { // 点击的是棋盘时
			// 四舍五入,看离哪个点最近
			pos[0] = (int) Math.round(1.0*(y-itop)/itemwidth);// 取得所在的行
			pos[1] = (int) Math.round( 1.0*(x-ileft)/itemwidth);// 取得所在的列
		} else {// 点击的位置不是棋盘时
			pos[0] = -1; // 将位置设为不可用
			pos[1] = -1;
		}
		return pos;// 将坐标数组返回
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {// 重写的
		this.thread.setFlag(true);
		this.thread.start();// 启动刷帧线程
		
		timeThread.setFlag(true);
		timeThread.start();// 启动思考时间的线程
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {// view被释放时调用的
		
		chessmovModelManager.chessmov_deleteAll(); // 清除数据库里面的数据
		
		boolean retry = true;
		thread.setFlag(false);// 停止刷帧线程
		timeThread.setFlag(false);// 停止思考时间线程
		while (retry) {
			try {
				thread.join();
				timeThread.join();// 等待线程结束
				retry = false;// 设置循环标志位为false
			} catch (InterruptedException e) {// 不断地循环，直到等待的线程结束
			}
		}
	}

	class TutorialThread extends Thread {// 刷帧线程
		private int span = 300;// 睡眠的毫秒数
		private SurfaceHolder surfaceHolder;// SurfaceHolder的引用
		private GameView gameView;// gameView的引用
		private boolean flag = false;// 循环标志位

		public TutorialThread(SurfaceHolder surfaceHolder, GameView gameView) {// 构造器
			this.surfaceHolder = surfaceHolder;// 得到SurfaceHolder引用
			this.gameView = gameView;// 得到GameView的引用
		}

		public void setFlag(boolean flag) {// 设置循环标记
			this.flag = flag;
		}

		public void run() {// 重写的方法
			Canvas c;// 画布
			while (this.flag) {// 循环绘制
				c = null;
				try {
					c = this.surfaceHolder.lockCanvas(null);
					synchronized (this.surfaceHolder) {
						gameView.oonDraw(c);// 调用绘制方法
					}
				} finally {// 用finally保证下面代码一定被执行
					if (c != null) {
						// 更新屏幕显示内容
						this.surfaceHolder.unlockCanvasAndPost(c);
					}
				}
				try {
					Thread.sleep(span);// 睡眠span毫秒
				} catch (Exception e) {// 不会异常信息
					e.printStackTrace();// 打印异常堆栈信息
				}
			}
		}
	}
	
	public void stepBackTest(){
		List<ModelChessmov> ml = chessmovModelManager.Chessmov_getAll();
		if(ml == null || ml.size() != 2){
			return;
		}
		ModelChessmov m2 = ml.get(1);
		ModelChessmov m1 = ml.get(0);
		
		qizi[m2.fromX][m2.fromY] = m2.fromId;
		qizi[m2.toX][m2.toY] = m2.toId;
		
		qizi[m1.fromX][m1.fromY] = m1.fromId;
		qizi[m1.toX][m1.toY] = m1.toId;
        NSteps = NSteps - 1;
	}
	
}