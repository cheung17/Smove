package com.ztx.ballmove;

import java.security.PublicKey;
import java.util.Random;

import com.ztx.ballmove.*;

import android.R.color;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//此类用于游戏绘图
public class MySurfaceView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder suf;
	Paint ballPaint; // 白球画笔
	static Thread th; // 线程
	boolean isHard; // 是否困难模式
	private Paint recPaint, linePaint; // 矩形和线条画笔
	private Paint blackBall; // 黑球画笔

	private int x1 = 0; // 用来保存屏幕宽度
	private int y1 = 0;// 保存屏幕高度
	private int whiteBallX = 0; // 白球左边距
	private int whiteBallY = 0; // 白球上边距
	private int setBlackX; // 黑球1左边距
	private int setBlackY;// 黑球1右上距
	static boolean surfaceIsLive = true; // surfaceview是否销毁
	// 黑球1方向标记 1为上 2为下 3为左 4为右
	private int setBlackX2 = -100; // 黑球2左边
	private int setBlackY2 = -100; // 黑球2上边
	private int dir3; // 黑球方向标记
	int dir = 0;
	private int dir2; // 黑球2方向标记
	boolean paintBall = true; // 产生黑球1标记
	boolean paintball2 = true; // 产生黑球2标记
	private boolean gameOver = false; // 游戏结束标记
	private int setStarX = -100; // 初始化得分球
	private int setStarY = -100;
	private boolean paintStar = true; // 产生得分球标记
	int s = 0; // 纪录本局得分
	private Bitmap bitBack; // 返回按钮
	private int backX; // 返回按钮坐标
	private int backY;
	private Bitmap bitNew; // 新纪录图片

	private SoundPool sp; // 声明SoundPool
	// 挂掉音乐文件id
	private int sound_die;
	// 得分音乐文件id
	private int sound_score;
	private boolean bofang = true; // 是否播放标记
	private boolean saveDies = true; // 存放挂掉次数标记
	private int setBlackX3 = -2000;
	private int setBlackY3 = -2000;

	private boolean paintBall3 = true;
	static int dies1; // 存放挂掉次数
	private long firstTime; // 纪录时间
	private long secondTime;
	int step = 10; // 黑球移动步数
	private int recWidth; // 矩形宽度
	private int recWidthDiv; // 矩形宽度除以三
	private float ballRadius;// 黑白球半径
	private int radiusM; // 黑白球半径+5
	private int overWidth; // 结束rect宽度
	private int overWidthDiv;
	private int whiteStep; // 白球移动步数
	private boolean attacked = false; // 黑白球是否相撞
	private int thisDir = 0; // 白球被撞击后移动方向

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		suf = this.getHolder();
		ballPaint = new Paint(); // 白球画笔
		ballPaint.setColor(Color.WHITE);
		ballPaint.setAntiAlias(true); // 抗锯齿
		setFocusable(true);
		suf.addCallback(this);
		blackBall = new Paint(); // 黑球画笔
		blackBall.setColor(Color.BLACK);
		blackBall.setAntiAlias(true);
		recPaint = new Paint(); // 矩形画笔画笔
		recPaint.setStyle(Style.STROKE);// 空心
		recPaint.setColor(Color.WHITE);
		recPaint.setStrokeWidth(7);
		recPaint.setAntiAlias(true);
		linePaint = new Paint();
		linePaint.setStyle(Style.STROKE);
		linePaint.setColor(Color.WHITE);
		linePaint.setStrokeWidth(5);
		linePaint.setAntiAlias(true);
		isHard = SloganAcdtivity.isHard;
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 100); // 播放器
		sound_score = sp.load(context, R.raw.score, 1);
		sound_die = sp.load(context, R.raw.die1, 1);
		dies1 = MainActivity.getMainActivity().dies;
		firstTime = System.currentTimeMillis();
		step = 2;
		bitBack = BitmapFactory.decodeResource(getResources(), R.drawable.back);
		bitNew = BitmapFactory.decodeResource(getResources(), R.drawable.mynew);
	}

	@Override
	public void run() { // 线程run方法
		while (surfaceIsLive) { // 当surfaceView没有销毁时
			logic(); // 黑球产生 逻辑方法
			blackAttackWhite(); // 实时检测黑白球是否相撞
			whiteFly();
			beginDraw(); // 刷新绘图
			if (!gameOver) {
				secondTime = System.currentTimeMillis();
			}

			if (secondTime - firstTime > 2000) {
				step = 4;
			}
			if (secondTime - firstTime > 3000) {
				step = 5;
			}
			if (secondTime - firstTime > 4000) {
				step = 6;
			}
			if (secondTime - firstTime > 5000) {
				step = 7;
			}
			if (secondTime - firstTime > 6000) {
				step = 8;
			}
			if (secondTime - firstTime > 7000) {
				step = 9;
			}
			if (secondTime - firstTime > 8000) {
				step = 10;
			}
			if (secondTime - firstTime > 9000) {
				step = 11;
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void logic() { // 黑球产生逻辑
		if (dir == 1) {
			setBlackY = setBlackY + step;// 黑球1在上方

			if (setBlackY > y1 - 70) {
				paintball2 = true;
				if (paintBall) {
					randomBlack2();
					paintBall = false;
				}
			}
		} else if (dir == 2) { // 黑球在下方
			setBlackY = setBlackY - step;

			if (setBlackY < 70) {
				paintball2 = true;
				if (paintBall) {
					randomBlack2();
					paintBall = false;
				}
			}
		} else if (dir == 3) {// 黑球1在左 往右走
			setBlackX = setBlackX + step;

			if (setBlackX > x1 - 70) {
				paintball2 = true;
				if (paintBall) {
					randomBlack2();
					paintBall = false;
				}
			}
		} else if (dir == 4) { // 黑球1在右 往左走
			setBlackX = setBlackX - step;

			if (setBlackX < 70) {
				paintball2 = true;
				if (paintBall) {
					randomBlack2();
					paintBall = false;
				}
			}
		}

		if (dir2 == 1) {
			setBlackY2 = setBlackY2 + step;// 黑球1在上方

			if (setBlackY2 > y1 - 70) {
				paintBall = true;
				if (paintball2) {
					randomBlack();
					paintball2 = false;
				}

			}
		} else if (dir2 == 2) { // 黑球在下方
			setBlackY2 = setBlackY2 - step;
			if (setBlackY2 < 70) {
				paintBall = true;
				if (paintball2) {
					randomBlack();
					paintball2 = false;
				}
			}
		} else if (dir2 == 3) {// 黑球1在左 往右走
			setBlackX2 = setBlackX2 + step;
			if (setBlackX2 > x1 - 70) {
				paintBall = true;
				if (paintball2) {
					randomBlack();
					paintball2 = false;
				}
			}
		} else if (dir2 == 4) {
			setBlackX2 = setBlackX2 - step;
			if (setBlackX2 < 70) {
				paintBall = true;
				if (paintball2) {
					randomBlack();
					paintball2 = false;
				}
			}
		}

		if (dir3 == 1) {
			setBlackY3 = setBlackY3 + step;// 黑球1在上方

			if (paintBall3) {
				randomBlack3();
				paintBall3 = false;
			}
		} else if (dir3 == 2) { // 黑球在下方
			setBlackY3 = setBlackY3 - step;

			if (paintBall3) {
				randomBlack3();
				paintBall3 = false;
			}
		} else if (dir3 == 3) {// 黑球1在左 往右走
			setBlackX3 = setBlackX3 + step;

			if (paintBall3) {
				randomBlack3();
				paintBall3 = false;
			}
		} else if (dir3 == 4) { // 黑球1在右 往左走
			setBlackX3 = setBlackX3 - step;

			if (paintBall3) {
				randomBlack3();
				paintBall3 = false;
			}

		}
		if (setBlackX3 > x1 + 100 || setBlackX3 < -100 || setBlackY3 > y1 + 100
				|| setBlackY3 < -100) {
			// randomBlack3();
			paintBall3 = true;
		}

	}

	public void randomBlack() { // 随机产生黑球的坐标和方向

		int[] blackX = { (x1 - recWidth) / 2 + radiusM,
				(x1 - recWidth) / 2 + radiusM + recWidthDiv,
				(x1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };
		int[] blackY = { (y1 - recWidth) / 2 + radiusM,
				(y1 - recWidth) / 2 + radiusM + recWidthDiv,
				(y1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };

		int rX = new Random().nextInt(3);
		int rY = new Random().nextInt(3);
		int needX = (int) (Math.random() * 2); // 产生0-1随机数

		if (needX == 0) {
			int needY = (int) (Math.random() * 2);
			if (needY == 1) {
				setBlackX = blackX[rX]; // 出现在屏幕上方
				setBlackY = radiusM;
				dir = 1;
			} else {
				setBlackX = blackX[rX]; // 出现在屏幕下方
				setBlackY = y1 - radiusM;
				dir = 2;
			}

		} else {
			int needh = (int) (Math.random() * 2);
			if (needh == 1) { // 出现在左边
				setBlackX = radiusM;
				setBlackY = blackY[rY];
				dir = 3;
			} else {
				setBlackX = x1 - 50; // 出现在屏幕右边
				setBlackY = blackY[rY];
				dir = 4;
			}

		}
	}

	public void randomBlack2() { // 生成黑球二随机位置
		int[] blackX = { (x1 - recWidth) / 2 + radiusM,
				(x1 - recWidth) / 2 + radiusM + recWidthDiv,
				(x1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };
		int[] blackY = { (y1 - recWidth) / 2 + radiusM,
				(y1 - recWidth) / 2 + radiusM + recWidthDiv,
				(y1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };

		int rX = new Random().nextInt(3);
		int rY = new Random().nextInt(3);
		int needX = (int) (Math.random() * 2); // 产生0-1随机数
		if (needX == 0) {
			int needY = (int) (Math.random() * 2);
			if (needY == 1) {
				setBlackX2 = blackX[rX]; // 出现在屏幕上方
				setBlackY2 = radiusM;
				dir2 = 1;
			} else {
				setBlackX2 = blackX[rX]; // 出现在屏幕下方
				setBlackY2 = y1 - 50;
				dir2 = 2;
			}

		} else {
			int needh = (int) (Math.random() * 2);
			if (needh == 1) { // 出现在左边
				setBlackX2 = radiusM;
				setBlackY2 = blackY[rY];
				dir2 = 3;
			} else {
				setBlackX2 = x1 - 50; // 出现在屏幕右边
				setBlackY2 = blackY[rY];
				dir2 = 4;
			}

		}
	}

	public void randomBlack3() { // 生成黑球3随机位置
		if (isHard) {

			int[] blackX = { (x1 - recWidth) / 2 + radiusM,
					(x1 - recWidth) / 2 + radiusM + recWidthDiv,
					(x1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };
			int[] blackY = { (y1 - recWidth) / 2 + radiusM,
					(y1 - recWidth) / 2 + radiusM + recWidthDiv,
					(y1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };

			int rX = new Random().nextInt(3);
			int rY = new Random().nextInt(3);
			int needX = (int) (Math.random() * 2); // 产生0-1随机数
			if (needX == 0) {
				int needY = (int) (Math.random() * 2);
				if (needY == 1) {
					setBlackX3 = blackX[rX]; // 出现在屏幕上方
					setBlackY3 = radiusM;
					dir3 = 1;
				} else {
					setBlackX3 = blackX[rX]; // 出现在屏幕下方
					setBlackY3 = y1 - 50;
					dir3 = 2;
				}

			} else {
				int needh = (int) (Math.random() * 2);
				if (needh == 1) { // 出现在左边
					setBlackX3 = radiusM;
					setBlackY3 = blackY[rY];
					dir3 = 3;
				} else {
					setBlackX3 = x1 - 50; // 出现在屏幕右边
					setBlackY3 = blackY[rY];
					dir3 = 4;
				}

			}
		}
	}

	public void beginDraw() { // 画图方法
		Canvas canvas = null;
		try {
			canvas = suf.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(Color.parseColor("#00ffff"));
				Rect r1 = new Rect();

				// top-bottom=竖长
				// right-left=横宽

				r1.left = (x1 - recWidth) / 2; // 左边距
				r1.left = (x1 - recWidth) / 2; // 左边距
				r1.top = (y1 - recWidth) / 2 + recWidth;
				r1.top = (y1 - recWidth) / 2 + recWidth;
				r1.bottom = (y1 - recWidth) / 2; // 上边距
				r1.right = (x1 - recWidth) / 2 + recWidth;
				canvas.drawRect(r1, recPaint);
				canvas.drawLine((x1 - recWidth) / 2 + 5, (y1 - recWidth) / 2
						+ recWidthDiv, (x1 - recWidth) / 2 + recWidth - 5,
						(y1 - recWidth) / 2 + recWidthDiv, linePaint);
				canvas.drawLine((x1 - recWidth) / 2 + 5, (y1 - recWidth) / 2
						+ recWidthDiv * 2, (x1 - recWidth) / 2 + recWidth - 5,
						(y1 - recWidth) / 2 + recWidthDiv * 2, linePaint);
				canvas.drawLine((x1 - recWidth) / 2 + recWidthDiv,
						(y1 - recWidth) / 2 + 5, (x1 - recWidth) / 2
								+ recWidthDiv, (y1 - recWidth) / 2 + recWidth
								- 5, linePaint);
				canvas.drawLine((x1 - recWidth) / 2 + recWidthDiv * 2,
						(y1 - recWidth) / 2 + 5, (x1 - recWidth) / 2
								+ recWidthDiv * 2, (y1 - recWidth) / 2
								+ recWidth - 5, linePaint);

				// drawBlack(canvas);
				Paint starPaint = new Paint();
				starPaint.setColor(Color.parseColor("#802A2A"));
				starPaint.setAntiAlias(true);
				canvas.drawCircle(setStarX, setStarY, ballRadius - 10,
						starPaint); // 得分球
				canvas.drawCircle(whiteBallX, whiteBallY, ballRadius, ballPaint);// 白球
				canvas.drawCircle(setBlackX, setBlackY, ballRadius, blackBall);// 黑球1
				canvas.drawCircle(setBlackX2, setBlackY2, ballRadius, blackBall);// 黑二
				canvas.drawCircle(setBlackX3, setBlackY3, ballRadius, blackBall);// 黑三

				// drawWhite(canvas);
				if (paintStar) { // 是否需要重画得分球
					drawStar();

					paintStar = false;
				}
				if (gameOver) { // 是否需要画游戏结束
					drawGameOver(canvas);
				}
			}
		} catch (Exception e) {
		} finally {
			if (canvas != null) {
				suf.unlockCanvasAndPost(canvas);
			}

		}

	}

	public void drawStar() { // 生成星星的随机位置
		int[] starX = { (x1 - recWidth) / 2 + radiusM,
				(x1 - recWidth) / 2 + radiusM + recWidthDiv,
				(x1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };
		int[] starY = { (y1 - recWidth) / 2 + radiusM,
				(y1 - recWidth) / 2 + radiusM + recWidthDiv,
				(y1 - recWidth) / 2 + radiusM + recWidthDiv + recWidthDiv };
		int rX = new Random().nextInt(3);
		int rY = new Random().nextInt(3);
		while (starX[rX] == whiteBallX && starY[rY] == whiteBallY) {
			rX = new Random().nextInt(3);
			rY = new Random().nextInt(3);
		}
		setStarX = starX[rX];
		setStarY = starY[rY];

	}

	public void drawWhite(Canvas canvas) { // 画白球

	}

	public void drawGameOver(Canvas canvas) { // 游戏结束 画白布
		Rect rectOver = new Rect();

		System.out.println(overWidth + "," + y1);
		rectOver.left = 0;
		rectOver.bottom = (y1 - overWidth) / 2;
		rectOver.right = x1;
		rectOver.top = (y1 - overWidth) / 2 + overWidth;

		Paint paintOver = new Paint();
		paintOver.setColor(Color.parseColor("#D4D4D4"));
		canvas.drawRect(rectOver, paintOver);
		Paint textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTextSize(90);
		textPaint.setColor(Color.parseColor("#8B8B83"));
		textPaint.setAntiAlias(true);

		Paint overText = new Paint();
		overText.setTextAlign(Paint.Align.CENTER);
		overText.setTextSize(70);
		overText.setColor(Color.parseColor("#8B8B83"));
		overText.setAntiAlias(true);
		canvas.drawText(
				"SCORE: " + MainActivity.getMainActivity().score.getText(),
				x1 / 2, (y1 - overWidth) / 2 + overWidth / 2, overText);
		canvas.drawText(
				"BEST: " + MainActivity.getMainActivity().best.getText(),
				x1 / 2, (y1 - overWidth) / 2 + overWidth - overWidthDiv,
				overText);
		step = 1;

		if (s >= MainActivity.myBest && s != 0) { // 新纪录
			canvas.drawBitmap(bitNew, 0, (y1 - overWidth) / 2, null);
		}

		canvas.drawText("GameOver", x1 / 2,
				(y1 - overWidth) / 2 + overWidthDiv, textPaint);
		canvas.drawBitmap(bitBack, (x1 - bitBack.getWidth() - 50),
				(y1 - overWidth) / 2
						+ (overWidth - bitBack.getHeight() - overWidthDiv),
				null);// 返回按钮
		// MainActivity.getMainActivity().myscore=0;
		if (saveDies) {
			dies1 = MainActivity.getMainActivity().dies;
			dies1 = dies1 + 1; // 纪录死亡次数
			MainActivity.getMainActivity().saveDies(dies1);
			saveDies = false;
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		x1 = this.getWidth();
		y1 = this.getHeight();
		recWidth = (int) (3 / 8f * x1); // 矩形宽
		recWidthDiv = (int) (recWidth / 3f); // 矩形宽除以三
		ballRadius = (float) (recWidth * 0.15); // 黑白球半径
		radiusM = (int) (ballRadius + 5);
		overWidth = (int) (y1 * 0.4);
		overWidthDiv = overWidth / 4;
		;
		surfaceIsLive = true;
		// drawWhite();
		whiteBallX = (x1 - recWidth) / 2 + radiusM + recWidthDiv;// 初始化白球X位置
		whiteBallY = (y1 - recWidth) / 2 + radiusM + recWidthDiv;// 初始化白球Y位置
		beginDraw();
		randomBlack();// 初始化黑球1位置
		randomBlack3();
		th = new Thread(this);
		th.start();
		backX = (x1 - bitBack.getWidth() - 50); // 初始化返回按妞X坐标
		backY = (y1 - overWidth) / 2
				+ (overWidth - bitBack.getHeight() - overWidthDiv);

		setOnTouchListener(new OnTouchListener() { // 用户滑动屏幕时 屏幕滑动时白球往滑动方向平移一格
													// 不能出格子
			private float startX, startY, offSetX, offSetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) { // 得到用户操作方式
				case MotionEvent.ACTION_DOWN: // 用户按下

					startX = event.getX();
					startY = event.getY();
					if (gameOver) {
						if (startX >= backX
								&& startX <= backX + bitBack.getWidth()
								&& startY >= backY
								&& startY < backY + bitBack.getHeight()) {// 点击了返回重新开始

							beginDraw();
							MainActivity.getMainActivity().best
									.setText(MainActivity.myBest + ""); // 再次显示最高分
							MainActivity.getMainActivity().setScore(0);// 清空得分
							s = 0; // 清空临时分数

							gameOver = false; // 游戏重新开始
							randomBlack();// 随机生成黑球1位置
							setBlackX3 = 3000;
							setBlackY3 = 3000;
							setBlackX2 = 3000; // 黑球2移除屏幕显示区
							setBlackY2 = 3000;
							// surfaceIsLive=true;
							firstTime = System.currentTimeMillis();
							attacked = false;
							whiteBallX = (x1 - recWidth) / 2 + radiusM
									+ recWidthDiv;// 初始化白球X位置
							whiteBallY = (y1 - recWidth) / 2 + radiusM
									+ recWidthDiv;// 初始化白球Y位置
							drawStar();
							bofang = true;
							saveDies = true;
						}
					}
					break;

				case MotionEvent.ACTION_UP:

					offSetX = event.getX() - startX; // 得到x偏移量
					offSetY = event.getY() - startY; // 得到y偏移量

					if (Math.abs(offSetX) > Math.abs(offSetY)) { // 判断是横向滑动是否大于竖向滑动
						if (offSetX > 5 && !attacked) { // 右滑
							if (whiteBallX < (x1 - recWidth) / 2 + radiusM
									+ recWidthDiv + recWidthDiv) {
								whiteBallX = whiteBallX + recWidthDiv;
								beginDraw();
								ifRadiusEqu();// 实时监测有没有得分
							}
						} else if (offSetX < -5 && !attacked) { // 左滑
							if (whiteBallX > (x1 - recWidth) / 2 + radiusM) {
								whiteBallX = whiteBallX - recWidthDiv;
								beginDraw();
								ifRadiusEqu();// 实时监测有没有得分
							}
						}
					} else if (Math.abs(offSetX) < Math.abs(offSetY)) { // 竖向滑动大于横向滑动
						if (offSetY > 5 && !attacked) { // 下滑
							if (whiteBallY < (y1 - recWidth) / 2 + radiusM
									+ recWidthDiv + recWidthDiv) {
								whiteBallY = whiteBallY + recWidthDiv;
								beginDraw();
								ifRadiusEqu();// 实时监测有没有得分
							}

						} else if (offSetY < -5 && !attacked) { // 上滑
							if (whiteBallY > (y1 - recWidth) / 2 + radiusM) {
								whiteBallY = whiteBallY - recWidthDiv;
								beginDraw();
								ifRadiusEqu(); // 实时监测有没有得分
							}

						}
					}

					break;
				}

				return true;
			}

		});

	}

	public void blackAttackWhite() { // 白球与黑球是否相撞
		float disX1 = Math.abs(whiteBallX - setBlackX);
		float disY1 = Math.abs(setBlackY - whiteBallY);
		float disX2 = Math.abs(whiteBallX - setBlackX2);
		float disY2 = Math.abs(setBlackY2 - whiteBallY);
		float disX3 = Math.abs(whiteBallX - setBlackX3);
		float disY3 = Math.abs(whiteBallY - setBlackY3);
		if ((disX1 < recWidthDiv && disY1 < recWidthDiv)) {
			thisDir = dir;
			whiteFly();
			attacked = true;
		}
		if (disX2 < recWidthDiv && disY2 < recWidthDiv) {
			thisDir = dir2;
			whiteFly();
			attacked = true;
		}
		if (disX3 < recWidthDiv && disY3 < recWidthDiv) {
			thisDir = dir3;
			whiteFly();
			attacked = true;
		}

	}

	public void ifRadiusEqu() {
		if (setStarX == whiteBallX && setStarY == whiteBallY) {
			drawStar();

			if (!gameOver) {
				s = s + 1;
				if (!MainActivity.isSilent) { // 可以播放
					sp.play(sound_score, 2, 2, 0, 0, 1); // 播放得分音效
				}

			}
			MainActivity.setScore(s);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		surfaceIsLive = true;

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceIsLive = false;

	}

	public void whiteFly() { // 白球被撞飞
		if (attacked) {
			if (!MainActivity.isSilent) { // 是否静音
				if (!gameOver) { // 游戏是否结束
					if (bofang) { // 是否已经播放过一次
						sp.play(sound_die, 2, 2, 0, 0, 1);
						bofang = false;
					}

				}
			}
			if (thisDir == 1) { // 判断方向
				whiteBallY = whiteBallY + 15;
				if (whiteBallY > y1) {
					gameOver = true;
				}
			} else if (thisDir == 2) {

				whiteBallY = whiteBallY - 15;
				if (whiteBallY < 0) {
					gameOver = true;
				}
			} else if (thisDir == 3) {
				whiteBallX = whiteBallX + 15;
				if (whiteBallX > x1) {
					gameOver = true;
				}

			} else if (thisDir == 4) {
				whiteBallX = whiteBallX - 15;
				if (whiteBallX < 0) {
					gameOver = true;
				}

			}

		}

	}

}
