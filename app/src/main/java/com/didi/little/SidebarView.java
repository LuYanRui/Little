package com.didi.little;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SidebarView extends View {
	public String[] arrLetters = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private OnLetterClickedListener listener = null;
	private TextView textView_dialog;
	private int isChoosedPosition = -1;

	public void setTextView(TextView textView) {
		textView_dialog = textView;
	}

	public SidebarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		int singleTextHeight = height / arrLetters.length;

		for (int i = 0; i < arrLetters.length; i++) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(50);
			//paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setColor(ContextCompat.getColor(getContext(),R.color.colorAt));
			if (i == isChoosedPosition) {
				paint.setColor(Color.WHITE);
				paint.setFakeBoldText(true);
			}
			float x = (width - paint.measureText(arrLetters[i])) / 2;
			float y = singleTextHeight * (i + 1);
			canvas.drawText(arrLetters[i], x, y, paint);
			paint.reset();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		textView_dialog.setBackgroundColor(getResources().getColor(R.color.halftransparent));
		textView_dialog.setPadding(15,15,15,15);
		float y = event.getY();
		int position = (int) (y / getHeight() * arrLetters.length);
		int lastChoosedPosition = isChoosedPosition;
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			setBackgroundColor(Color.WHITE);
			if (textView_dialog != null) {
				textView_dialog.setVisibility(View.GONE);
			}
			isChoosedPosition = -1;
			invalidate();
			break;
		default:
			if (lastChoosedPosition != position) {
				if (position >= 0 && position < arrLetters.length) {
					if (listener != null) {
						listener.onLetterClicked(arrLetters[position]);
					}
					if (textView_dialog != null) {
						textView_dialog.setVisibility(View.VISIBLE);
						textView_dialog.setText(arrLetters[position]);
					}
					isChoosedPosition = position;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	public interface OnLetterClickedListener {
		public void onLetterClicked(String str);
	}

	public void setOnLetterClickedListener(OnLetterClickedListener listener) {
		this.listener = listener;
	}

}
