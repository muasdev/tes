package com.vt.disposisibandung.utils.transformations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by irvan on 6/21/15.
 */
public class RoundedTransformation implements Transformation {

    private final String KEY = "rounded";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        source.recycle();

        return output;
    }
}
