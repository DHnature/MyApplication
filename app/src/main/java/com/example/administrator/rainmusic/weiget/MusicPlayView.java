package com.example.administrator.rainmusic.weiget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.administrator.rainmusic.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;


public class MusicPlayView extends RelativeLayout {

	private Context mContext;

	private static final int ROTATE_TIME = 12 * 1000;

	private static final int ROTATE_COUNT = 10000;


	private static final int NEEDLE_TIME = 1 * 500;

	private static final int NEEDLE_RADIUS = 30;


	private static final int AVATART_DISC_ALPHA_TIME = 1 * 300;

	private static final float AVATART_DISC_ALPHA_PERCENT = 0.3f;


	private ImageView mBackground;


	private ImageView mNeedle;

//	private ImageView mDisc;

	private CircleImageView mAvatar;

	private boolean isPlay = false;


	ObjectAnimator mAniNeedle;


	//	ObjectAnimator mAniDisc;
	ObjectAnimator mAniAvatar;

//	//�������ʱ�Ľ���Ч��
//	ObjectAnimator mAniAlphaAvatarHide;
//	ObjectAnimator mAniAlphaAvatarShow;
//
//	//��������ʱ�Ľ���Ч��
//	ObjectAnimator mAniAlphaDiscBgHide;
//	ObjectAnimator mAniAlphaDiscBgShow;

	float mValueAvatar;
	float mValueDisc;
	float mValueNeedle;

//	private int mCurrentImageResource = 0;

//	public MusicPlay mMusicPlayListener ;

	public MusicPlayView(Context context, AttributeSet attrs) {
		super(context);
		mContext = context;
	}

	public MusicPlayView(Context context) {
		super(context);
		mContext = context;
	}

	public MusicPlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	/*public interface MusicPlay{
		void onAvatarChange();

		void onDiscbgChange();
	}*/

	/*public void setMusicPlayerListener(MusicPlay listener ){
		this.mMusicPlayListener = listener ;
	}*/


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBackground = (ImageView) findViewById(R.id.bg);

		mAvatar = (CircleImageView) findViewById(R.id.avatar);

//		Bitmap conformBitmap = toConformBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fm_play_disc), mAvatar.getBitmap());
//		mAvatar.setImageBitmap(conformBitmap);
//		mAvatar.setBackgroundDrawable(new BitmapDrawable(conformBitmap));

//		mAvatar.setBackgroundDrawable(getResources().getDrawable(R.drawable.fm_play_disc));
//		mDisc = (ImageView) findViewById(R.id.disc);
		mNeedle = (ImageView) findViewById(R.id.needle);

//		mDisc.setVisibility(View.GONE);

		initAvatarAnimation(0f);
//		initDiscAnimation(0f);
		initNeedleAnimation(0f);

//		//����ҳ�涯��
//		mAniAlphaAvatarHide = ObjectAnimator.ofFloat(mAvatar, "alpha", 1, AVATART_DISC_ALPHA_PERCENT).setDuration(AVATART_DISC_ALPHA_TIME);
//		mAniAlphaAvatarHide.addListener(avatarAlphaHideListener);
//		mAniAlphaAvatarShow = ObjectAnimator.ofFloat(mAvatar, "alpha", AVATART_DISC_ALPHA_PERCENT, 1).setDuration(AVATART_DISC_ALPHA_TIME);
//
//		mAniAlphaDiscBgHide = ObjectAnimator.ofFloat(mBackground, "alpha", 1, AVATART_DISC_ALPHA_PERCENT).setDuration(AVATART_DISC_ALPHA_TIME);
//		mAniAlphaDiscBgHide.addListener(discbgAlphaHideListener);
//		mAniAlphaDiscBgShow = ObjectAnimator.ofFloat(mBackground, "alpha", AVATART_DISC_ALPHA_PERCENT, 1).setDuration(AVATART_DISC_ALPHA_TIME);
	}

	/* AnimatorListener avatarAlphaHideListener = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator arg0) {

		}

		@Override
		public void onAnimationRepeat(Animator arg0) {

		}

		@Override
		public void onAnimationEnd(Animator arg0) {
			mMusicPlayListener.onAvatarChange();
			mAniAlphaAvatarShow.start();

		}

		@Override
		public void onAnimationCancel(Animator arg0) {

		}
	};

	 AnimatorListener discbgAlphaHideListener = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator arg0) {

		}

		@Override
		public void onAnimationRepeat(Animator arg0) {

		}

		@Override
		public void onAnimationEnd(Animator arg0) {
			mMusicPlayListener.onDiscbgChange();
			mAniAlphaDiscBgShow.start();

		}

		@Override
		public void onAnimationCancel(Animator arg0) {

		}
	};*/


	public void setBackgroundDrawable(Drawable d) {
		mBackground.setBackgroundDrawable(d);
	}


	public void setBackgroundResource(int resourece) {

	}

	public void setAvatarImageResource(int resourceid) {
		mAvatar.setImageDrawable(getResources().getDrawable(resourceid));
	}


	public void play() {
		initNeedleAnimation(0f);
		AnimatorSet animSet = new AnimatorSet();
//        animSet.playTogether(mAniAvatar,mAniDisc);
		animSet.play(mAniAvatar).after(mAniNeedle);
		animSet.start();
		setPlay(true);
	}


	public void pause() {
		initNeedleAnimation(NEEDLE_RADIUS);
		mAniNeedle.start();

		mAniAvatar.cancel();
//		mAniDisc.cancel();
		initAvatarAnimation(mValueAvatar);
//		initDiscAnimation(mValueDisc);
		setPlay(false);
	}


	public void next(int resourceId) {
//		mAniAlphaAvatarHide.start();
//		mAniAlphaDiscBgHide.start();
		changeImage(resourceId);
//		pause();
//		initAvatarAnimation(0f);
//		initDiscAnimation(0f);
//		initNeedleAnimation(0f);
//		play();
	}

	//该方法用来切换当前旋转盘中的专辑图片
	public void switchImage(final Drawable d) {

		postDelayed(new Runnable() {
			@Override
			public void run() {
				mAvatar.setImageDrawable(d);

				//			setAvatarImageResource(resourceId);

			}
		}, 0);
	}

	public void changeBackground(int resourceId) {
		setBackgroundResource(resourceId);
	}


	private void changeImage(final int resourceId) {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				setBackgroundResource(resourceId);
				setAvatarImageResource(resourceId);

			}
		}, 0);
	}

	private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
		if (background == null) {
			return null;
		}

		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		//int fgWidth = foreground.getWidth();
		//int fgHeight = foreground.getHeight();
		//create the new blank bitmap ����һ���µĺ�SRC���ȿ��һ����λͼ
		Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.RGB_565);
		Canvas cv = new Canvas(newbmp);
		//draw bg into
		cv.drawBitmap(background, 0, 0, null);
		//draw fg into
		cv.drawBitmap(foreground, 0, 0, null);
		//save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);//����
		//store
		cv.restore();//�洢
		return newbmp;
	}

	//该方法用来设置背景和旋转盘内的图片
	public void previous(int resourceId) {

//		mAniAlphaAvatarHide.start();
//		mAniAlphaDiscBgHide.start();
		pause();
		changeImage(resourceId);
//		pause();
//		initAvatarAnimation(0f);
//		initDiscAnimation(0f);
//		initNeedleAnimation(0f);
//		play();
	}


	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}


	private void initAvatarAnimation(float start) {
		mAniAvatar = ObjectAnimator.ofFloat(mAvatar, "rotation", start, 360f + start);
		mAniAvatar.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mValueAvatar = (Float) animation.getAnimatedValue("rotation");
				Log.e("", "�Ƕ� : " + mValueAvatar);
			}
		});
		mAniAvatar.setDuration(ROTATE_TIME);
		mAniAvatar.setInterpolator(new LinearInterpolator());
		mAniAvatar.setRepeatCount(ROTATE_COUNT);

	}





	private void initNeedleAnimation(float start) {
		mAniNeedle = ObjectAnimator.ofFloat(mNeedle, "rotation", start, NEEDLE_RADIUS - start).setDuration(NEEDLE_TIME);
	}
}


