package tb.circlepath;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by tb on 2018/3/1.
 * 功能描述：小球沿着圆形轨迹 运动
 */

public class CircleFollowPathAnimationView extends View {

    private float littleCircleX1, littleCircleY1;
    private float littleCircleX2, littleCircleY2;
    private float littleCircleX3, littleCircleY3;

    private int radius = 20;

    private int circle2ToCircle1_distance = 9;  // 圆圈之间的距离，值通过比例计算，建议范围 4-9
    private int circle3ToCircle2_distance = 4;  // 值通过比例计算，建议范围 4-2

    private float[] currentXY_1 = new float[2];
    private float[] tan_1 = new float[2];

    private float[] currentXY_2 = new float[2];
    private float[] tan_2 = new float[2];

    private float[] currentXY_3 = new float[2];
    private float[] tan_3 = new float[2];

    private PathMeasure pathMeasure1;
    private PathMeasure pathMeasure2;
    private PathMeasure pathMeasure3;

    private ValueAnimator valueAnimator1;
    private ValueAnimator valueAnimator2;
    private ValueAnimator valueAnimator3;

    private Path bigPath1;
    private Path bigPath2;
    private Path bigPath3;

    private Path measurePath1;
    private Path measurePath2;
    private Path measurePath3;

    private Paint bigCirclePaint1;
    private Paint bigCirclePaint2;
    private Paint bigCirclePaint3;

    private Paint strokePaint1;
    private Paint strokePaint2;
    private Paint strokePaint3;

    private Paint littleCirclePaint1;
    private Paint littleCirclePaint2;
    private Paint littleCirclePaint3;
    private AnimatorSet animatorSet;
    private RectF rectF;
    private RectF rectF1;

    private int viewWidth;
    private int viewHeight;

    public CircleFollowPathAnimationView(Context context) {
        this(context, null);
    }

    public CircleFollowPathAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleFollowPathAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleFollowPathAnimationView);

        int bigColor1 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circular_big, Color.parseColor("#E1F1ff"));
        int bigColor2 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circular_middle, Color.parseColor("#F1F9ff"));
        int bigColor3 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circular_little, Color.WHITE);

        int circleStrokeColor1 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circle_big_stroke, Color.parseColor("#56AEFF"));
        int circleStrokeColor2 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circle_middle_stroke, Color.parseColor("#CAE6FF"));
        int circleStrokeColor3 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circle_little_stroke, Color.parseColor("#7DC0FF"));

        int circleFill_color_1 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circle_fill_1, Color.parseColor("#0586FF"));
        int circleFill_color_2 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circle_fill_2, Color.parseColor("#BCDFFF"));
        int circleFill_color_3 = typedArray.getColor(R.styleable.CircleFollowPathAnimationView_circle_fill_3, Color.parseColor("#4EA4F4"));

        circle3ToCircle2_distance = typedArray.getInteger(R.styleable.CircleFollowPathAnimationView_circle2ToCircle1_distance, 9);
        circle2ToCircle1_distance = typedArray.getInteger(R.styleable.CircleFollowPathAnimationView_circle3ToCircle2_distance, 4);

        littleCirclePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        littleCirclePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        littleCirclePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);

        littleCirclePaint1.setStyle(Paint.Style.FILL);
        littleCirclePaint1.setColor(circleFill_color_1);

        littleCirclePaint2.setStyle(Paint.Style.FILL);
        littleCirclePaint2.setColor(circleFill_color_2);

        littleCirclePaint3.setStyle(Paint.Style.FILL);
        littleCirclePaint3.setColor(circleFill_color_3);

        bigCirclePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        bigCirclePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        bigCirclePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);


        bigCirclePaint1.setStyle(Paint.Style.FILL);
        bigCirclePaint1.setColor(bigColor1);

        bigCirclePaint2.setStyle(Paint.Style.FILL);
        bigCirclePaint2.setColor(bigColor2);

        bigCirclePaint3.setStyle(Paint.Style.FILL);
        bigCirclePaint3.setColor(bigColor3);

        strokePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);

        strokePaint1.setStyle(Paint.Style.STROKE);
        strokePaint2.setStyle(Paint.Style.STROKE);
        strokePaint3.setStyle(Paint.Style.STROKE);

        strokePaint1.setStrokeWidth(2);
        strokePaint2.setStrokeWidth(2);
        strokePaint3.setStrokeWidth(2);

        strokePaint1.setColor(circleStrokeColor1);
        strokePaint2.setColor(circleStrokeColor2);
        strokePaint3.setColor(circleStrokeColor3);

        bigPath1 = new Path();
        bigPath2 = new Path();
        bigPath3 = new Path();

        measurePath1 = new Path();
        measurePath2 = new Path();
        measurePath3 = new Path();

    }

//    /**
//     * 圆圈之间的距离，值通过比例计算，建议范围 5-9
//     */
//    public void circleToCirclr_distance_ratio(int circle2ToCircle1, int circle3ToCircle2) {
//        circle2ToCircle1_distance = circle2ToCircle1;
//        circle3ToCircle2_distance = circle3ToCircle2;
//
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        // RectF（）中4个参数：left top right bottom
        //思路：可以把圆看成，是在一个矩形中画出来的
        // left top = 矩形左上角的（x，y）； right bottom = 矩形右下角的（x，y）
//        RectF rectF = new RectF(w / 9, h / 9
//                , w / 9 * 8, h / 9 * 8);
//        RectF rectF1 = new RectF(w / 4, h / 4
//                , w / 4 * 3, h / 4 * 3);

        rectF = new RectF(w / circle2ToCircle1_distance, h / circle2ToCircle1_distance
                , w / circle2ToCircle1_distance * (circle2ToCircle1_distance - 1), h / circle2ToCircle1_distance * (circle2ToCircle1_distance - 1));
        rectF1 = new RectF(w / circle3ToCircle2_distance, h / circle3ToCircle2_distance
                , w / circle3ToCircle2_distance * (circle3ToCircle2_distance - 1), h / circle3ToCircle2_distance * (circle3ToCircle2_distance - 1));

        bigPath1.addArc(new RectF(radius, radius, w - radius, h - radius), 0, 360);  // 最大的圆
        bigPath2.addArc(rectF, 0, 360);  // 第二大的圆
        bigPath3.addArc(rectF1, 0, 360); // 最小的圆

        // 同上，这个是为了计算，3个圆圈上移动过程中的（X，Y）坐标，所以这个是关键
        measurePath1.addArc(new RectF(radius, radius, w - radius, h - radius), -90, 359);
        measurePath2.addArc(rectF, -90, 359);
        measurePath3.addArc(rectF1, -90, 359);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 3个大圆
        canvas.drawPath(bigPath1, bigCirclePaint1);
        canvas.drawPath(bigPath2, bigCirclePaint2);
        canvas.drawPath(bigPath3, bigCirclePaint3);
        // 3个大圆的边线
        canvas.drawPath(bigPath1, strokePaint1);
        canvas.drawPath(bigPath2, strokePaint2);
        canvas.drawPath(bigPath3, strokePaint3);

        if (littleCircleX1 == 0 && littleCircleY1 == 0)
            startAnimation();
        // 3个不同（x，y）坐标的小圆，radius = 半径，littleCirclePaint1 = 画笔
        canvas.drawCircle(littleCircleX1, littleCircleY1, radius, littleCirclePaint1);
        canvas.drawCircle(littleCircleX2, littleCircleY2, radius, littleCirclePaint2);
        canvas.drawCircle(littleCircleX3, littleCircleY3, radius, littleCirclePaint3);
    }

    public void startAnimation() {
        // PathMeasure是一个用来测量Path的类
        pathMeasure1 = new PathMeasure(measurePath1, false);
        // 动画执行 值变化的范围
        valueAnimator1 = ValueAnimator.ofFloat(0, pathMeasure1.getLength());
        valueAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator1.setDuration(1000 * 4);
        valueAnimator1.setInterpolator(new LinearInterpolator());  // 插值器
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                // onAnimationUpdate 会执行很多次
                // 获取指定长度的位置坐标及该点切线值 ， currentXY_1 = x y 值（小圆的中心点坐标），tan_1 = 切线值 暂时不用
                pathMeasure1.getPosTan(value, currentXY_1, tan_1);
                littleCircleX1 = currentXY_1[0];
                littleCircleY1 = currentXY_1[1];
                invalidate();  // 每次调用都会走 onDraw 所有 小圆就动了
            }
        });

        pathMeasure2 = new PathMeasure(measurePath2, false);
        valueAnimator2 = ValueAnimator.ofFloat(pathMeasure2.getLength(), 0);
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.setDuration(1000 * 3);
        valueAnimator2.setInterpolator(new LinearInterpolator());
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                pathMeasure2.getPosTan(value, currentXY_2, tan_2);
                littleCircleX2 = currentXY_2[0];
                littleCircleY2 = currentXY_2[1];
                invalidate();
            }
        });

        pathMeasure3 = new PathMeasure(measurePath3, false);
        valueAnimator3 = ValueAnimator.ofFloat(0, pathMeasure3.getLength());
        valueAnimator3.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator3.setDuration(1000 * 2);
        valueAnimator3.setInterpolator(new LinearInterpolator());
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                pathMeasure3.getPosTan(value, currentXY_3, tan_3);
                littleCircleX3 = currentXY_3[0];
                littleCircleY3 = currentXY_3[1];
                invalidate();
            }
        });

        if (animatorSet == null)
            animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator1, valueAnimator2, valueAnimator3);
        animatorSet.start();
    }

    public void stopAnimation() {
        if (animatorSet != null)
            animatorSet.cancel();
    }

}
