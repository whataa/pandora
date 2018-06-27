package tech.linjiang.pandora.inspector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 11/06/2018.
 */

abstract class HintView extends View {
    public HintView(Context context) {
        super(context);
    }

    protected String getViewHint() {
        return null;
    }

    private boolean firstHint = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (firstHint) {
            firstHint = false;
            String hintText = getViewHint();
            if (TextUtils.isEmpty(hintText)) {
                return;
            }
            hintText = "========🍺TIPS🍺========\n\n" + hintText + "\n\n===== Press back to exit =====";

            TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(ViewKnife.dip2px(14));
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            StaticLayout layout = new StaticLayout(hintText, textPaint,
                    canvas.getWidth() / 2, Layout.Alignment.ALIGN_NORMAL, 1.4f, 0.0f, false);


            canvas.save();
            canvas.translate(canvas.getWidth() / 2 - layout.getWidth() / 2,
                    canvas.getHeight() / 2 - layout.getHeight() / 2);

            canvas.save();
            int bgPadding = ViewKnife.dip2px(16);
            canvas.translate(-bgPadding, -bgPadding);
            RectF rect = new RectF();
            rect.set(0, 0, layout.getWidth() + 2 * bgPadding, layout.getHeight() + 2 * bgPadding);
            Paint paint = new Paint();
            paint.setColor(0x88000000);
            canvas.drawRoundRect(rect, ViewKnife.dip2px(4), ViewKnife.dip2px(4), paint);
            canvas.restore();

            layout.draw(canvas);
            canvas.restore();
        }
    }
}
