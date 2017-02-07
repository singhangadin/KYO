package com.github.angads25.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**<p>
 * Created by Angad on 7/2/17.
 * </p>
 */

public class BarGraphView extends View {
    private Paint paint;
    private int width, height, refDim;
    private RectF A, B, C, D;
    private int wPartition, xMargin;
    private int answer;
    private Context context;

    public BarGraphView(Context context) {
        super(context);
        this.context = context;
        initValues();
    }

    public BarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initValues();
    }

    private void initValues()
    {   A = new RectF();
        B = new RectF();
        C = new RectF();
        D = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String label, maxLabel="100%";
        canvas.drawColor(Color.WHITE);
        paint.setTextSize(refDim/15f);
        paint.setStrokeWidth(refDim/175f);

        label = getResources().getString(R.string.label_zero_perc);
        canvas.drawText(label, 0, height + (paint.measureText(maxLabel)/2), paint);
        canvas.drawLine(0, height, width, height, paint);

        label = getResources().getString(R.string.label_two_five_perc);
        canvas.drawText(label, 0, (0.75f * height) + (paint.measureText(maxLabel)/2), paint);
        canvas.drawLine(0, 0.75f * height, width, 0.75f * height, paint);

        label = getResources().getString(R.string.label_fifty_perc);
        canvas.drawText(label, 0, (0.5f * height) + (paint.measureText(maxLabel)/2), paint);
        canvas.drawLine(0, 0.5f * height, width, 0.5f * height, paint);

        label = getResources().getString(R.string.label_seven_five_perc);
        canvas.drawText(label, 0, (0.25f * height) + (paint.measureText(maxLabel)/2), paint);
        canvas.drawLine(0, 0.25f * height, width, 0.25f * height, paint);

        label = getResources().getString(R.string.label_hundred_perc);
        canvas.drawText(label, 0, 0 + (paint.measureText(label)/2), paint);
        canvas.drawLine(0, 0, width, 0, paint);

        canvas.drawLine(width, 0, width, height, paint);
        canvas.drawLine(wPartition - xMargin, 0, wPartition - xMargin, height, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            paint.setColor(getResources().getColor(R.color.colorAccent,context.getTheme()));
        }
        else
        {   paint.setColor(getResources().getColor(R.color.colorAccent));
        }
        paint.setStyle(Paint.Style.FILL);

        switch (answer)
        {   case 1: canvas.drawRect(A, paint);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paint.setColor(getResources().getColor(R.color.colorPrimary,context.getTheme()));
                    }
                    else
                    {   paint.setColor(getResources().getColor(R.color.colorPrimary));
                    }
                    canvas.drawRect(B, paint);
                    canvas.drawRect(C, paint);
                    canvas.drawRect(D, paint);
                    break;

            case 2: canvas.drawRect(B, paint);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paint.setColor(getResources().getColor(R.color.colorPrimary,context.getTheme()));
                    }
                    else
                    {   paint.setColor(getResources().getColor(R.color.colorPrimary));
                    }
                    canvas.drawRect(A, paint);
                    canvas.drawRect(C, paint);
                    canvas.drawRect(D, paint);
                    break;

            case 3: canvas.drawRect(C, paint);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paint.setColor(getResources().getColor(R.color.colorPrimary,context.getTheme()));
                    }
                    else
                    {   paint.setColor(getResources().getColor(R.color.colorPrimary));
                    }
                    canvas.drawRect(A, paint);
                    canvas.drawRect(B, paint);
                    canvas.drawRect(D, paint);
                    break;

            case 4: canvas.drawRect(D, paint);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paint.setColor(getResources().getColor(R.color.colorPrimary,context.getTheme()));
                    }
                    else
                    {   paint.setColor(getResources().getColor(R.color.colorPrimary));
                    }
                    canvas.drawRect(A, paint);
                    canvas.drawRect(B, paint);
                    canvas.drawRect(C, paint);
                    break;
        }
        paint.setColor(Color.BLACK);
        paint.setTextSize(refDim/10f);
        paint.setStrokeWidth(refDim/75f);
        paint.setStyle(Paint.Style.STROKE);
        String LARGE_ALPHA = "M";

        canvas.save();
        canvas.rotate(45,
                (wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/2),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2));
        canvas.drawText("A",
                (wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/1.5f),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2), paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(45,
                (2 * wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/2),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2));
        canvas.drawText("B",
                (2 * wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/1.5f),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2), paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(45,
                (3 * wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/2),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2));
        canvas.drawText("C",
                (3 * wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/1.5f),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2), paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(45,
                (4 * wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/2),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2));
        canvas.drawText("D",
                (4 * wPartition) + (wPartition/2) - (paint.measureText(LARGE_ALPHA)/1.5f),
                height + (wPartition/2) + (paint.measureText(LARGE_ALPHA) / 2), paint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        refDim = Math.min(width, height);
        wPartition = width / 5;
        height-= wPartition;
        xMargin = (int)(0.1f * wPartition);
    }

    public void setPerc(short percA, short percB, short percC, short percD) {
        A.set((wPartition) + xMargin,
                (((100 - percA) * (float)(height)/100)),
                (2 * wPartition) - xMargin,
                height);

        B.set((2 * wPartition) + xMargin,
                (((100 - percB) * (float)(height)/100)),
                (3 * wPartition) - xMargin,
                height);

        C.set((3 * wPartition) + xMargin,
                (((100 - percC) * (float)(height)/100)),
                (4 * wPartition) - xMargin,
                height);

        D.set((4 * wPartition) + xMargin,
                (((100 - percD) * (float)(height)/100)),
                width - (2*xMargin),
                height);
        invalidate();
    }

    public void setAnswer(int answer) {
        this.answer = answer;
        invalidate();
    }
}
