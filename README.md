# circlepath

自定义Path动画
==
先放个效果图看看<br>
小球沿着圆形轨迹运动，感兴趣的朋友，可以下载源码看看。<br>
* 起因：
    * 我是做蓝牙开发的，就是蓝牙智能锁，项目需要一个‘添加钥匙’效果这个东西很简单，很基础的知识。
    * 如果你是大神，小弟献丑了，如果你想学习Path建议看看，代码不多，但是简单容易理解。

![](https://github.com/jack-veteran/circlepath/raw/master/screenshot/device.png)

逻辑思路：

1：画3个半径不同的大圆（Fill类型），又画3个半径不同的大圆（Stroke类型），Stroke类型覆盖到Fill类型，实现边线效果。

```java
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        // RectF（）中4个参数：left top right bottom
        //思路：可以把圆看成，是在一个矩形中画出来的
        // left top = 矩形左上角的（x，y）； right bottom = 矩形右下角的（x，y）
        RectF rectF = new RectF(w / 9, h / 9
                , w / 9 * 8, h / 9 * 8);
        RectF rectF1 = new RectF(w / 4, h / 4
                , w / 4 * 3, h / 4 * 3);

//        rectF = new RectF(w / circle2ToCircle1_distance, h / circle2ToCircle1_distance
//                , w / circle2ToCircle1_distance * (circle2ToCircle1_distance - 1), h / circle2ToCircle1_distance * (circle2ToCircle1_distance - 1));
//        rectF1 = new RectF(w / circle3ToCircle2_distance, h / circle3ToCircle2_distance
//                , w / circle3ToCircle2_distance * (circle3ToCircle2_distance - 1), h / circle3ToCircle2_distance * (circle3ToCircle2_distance - 1));

        bigPath1.addArc(new RectF(radius, radius, w - radius, h - radius), 0, 360);  // 最大的圆
        bigPath2.addArc(rectF, 0, 360);  // 第二大的圆
        bigPath3.addArc(rectF1, 0, 360); // 最小的圆

        // 同上，这个是为了计算，3个圆圈上移动过程中的（X，Y）坐标，所以这个是关键
        measurePath1.addArc(new RectF(radius, radius, w - radius, h - radius), -90, 359);
        measurePath2.addArc(rectF, -90, 359);
        measurePath3.addArc(rectF1, -90, 359);
    }
```

2：画3个小圆和6个大圆。

```java
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
```

3：最后是动画部分，3个小圆沿着3个大圆运动，其实本质就是移动3个小圆的 x y 坐标，所以计算x y 坐标是关键。

```java
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
  }
```

## 知识扩展：<br>
PathMeasure介绍<br>

构造方法:

| 方法名 | 释义 | 
| --------- | ---------|
| PathMeasure() | 创建一个空的PathMeasure | 
| PathMeasure(Path path, boolean forceClosed) | 创建 PathMeasure 并关联一个指定的Path(Path需要已经创建完成)。| <br>

公共方法：

| 返回值 | 方法名 | 释义 |
| ------ | --------|------|
| void | setPath(Path path, boolean forceClosed) | 关联一个Path |
| boolean	|isClosed()	|是否闭合 |
| float	|getLength()	|获取Path的长度 |
| boolean	|nextContour()	|跳转到下一个轮廓 |
| boolean	|getSegment(float startD, float stopD, Path dst, boolean startWithMoveTo)	|截取片段 |
| boolean	|getPosTan(float distance, float[] pos, float[] tan)	|获取指定长度的位置坐标及该点切线值 |
| boolean	|getMatrix(float distance, Matrix matrix, int flags)	|获取指定长度的位置坐标及该点Matrix |

# 如何使用
	
```java
dependencies {
	       compile 'com.github.jack-veteran:circlepath:1.0.2'
	}
```
# Attributes
-------------
|name	|format	|description|
|-------|-------|-----------|
|circular_big| color| 大圆环的颜色
|circular_middle| color| 中圆环的颜色
|circular_little| color| 小圆环的颜色
|circle_big_stroke| color| 大圆边线颜色
|circle_middle_stroke| color| 中圆边线颜色
|circle_little_stroke| color| 小圆边线颜色
|circle_fill_1| color| 沿着大圆移动的小圆
|circle_fill_2| color| 沿着中圆移动的小圆
|circle_fill_3| color| 沿着小圆移动的小圆
|circle2ToCircle1_distance| integer| 中圆离大圆的距离（值在9-3之间）
|circle3ToCircle2_distance| integer| 小圆离中圆的距离（值在9-3之间）
  

