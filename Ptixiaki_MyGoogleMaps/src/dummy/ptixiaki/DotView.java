package dummy.ptixiaki;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;
//import android.widget.QuickContactBadge;
//import android.widget.TextView;

import dummy.ptixiaki.Dot;
import dummy.ptixiaki.Dots;


public class DotView extends View {

	private final Dots dots;
	//protected final TextView tvDistance;
	//protected final QuickContactBadge badge;
	/**
	* @param context the rest of the application
	* @param dots the dots we draw
	*/
	public DotView(Context context, Dots dots) { //, TextView tvDistance, QuickContactBadge badge) {
		super(context);
		
		this.dots = dots;
		
		//this.tvDistance = tvDistance;
		//addTextView(this.tvDistance);
		
		//this.badge = badge;
		//addQuickContactBadge(this.badge);
		
		setMinimumWidth(180);
		setMinimumHeight(200);
		setFocusable(true);
	}
	
	/** @see android.view.View#onMeasure(int, int) */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		//tvDistance.setX(widthMeasureSpec/2);
		//tvDistance.setY(heightMeasureSpec/2 - (widthMeasureSpec/2 + 20));
	}
	
	/** @see android.view.View#onDraw(android.graphics.Canvas) */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		
		Bitmap b = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas arrowCanvas = new Canvas(b);
		
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(hasFocus() ? Color.BLUE : Color.GRAY);
		canvas.drawRect(0, 0, getWidth() - 1, getHeight() -1, paint);
		
		paint.setStyle(Style.FILL);
		
		// Draw receiver (red arrow)
		drawArrow(arrowCanvas, paint);
		
		canvas.drawBitmap(b, this.getMatrix(), paint);
		// Draw sender (green dot)
		
		for (Dot dot : dots.getDots()) {
			paint.setColor(dot.getColor());
			
			canvas.drawCircle(
				dot.getX(),
				dot.getY(),
				dot.getDiameter(),
				paint);
			
		}

	}
	
	protected void drawArrow(Canvas canvas, Paint paint){
		
		 Paint outline_paint = new Paint() ;
	     outline_paint.setStyle( Paint.Style.STROKE ) ;
	     
	     Paint filling_paint = new Paint();
	     filling_paint.setStyle(Paint.Style.STROKE);
		
		
		int[]  arrow_shape_coordinates_x  =
            { 0,  15,   0,  -15 } ;

		int[]  arrow_shape_coordinates_y  =
            { 0,  40,  30,  40 } ;
     
		Path arrow = new Path();
		arrow.moveTo(arrow_shape_coordinates_x[0], arrow_shape_coordinates_y[0]);
		for ( int point_index  =  1 ; point_index  <  arrow_shape_coordinates_x.length ; point_index  ++ ) {
			arrow.lineTo( arrow_shape_coordinates_x[ point_index ], arrow_shape_coordinates_y[ point_index ] ) ;
		}

		arrow.close();
		
		paint.setColor(Color.RED);
		//filling_paint.setColor(Color.RED);
		canvas.translate(DisplayActivity.x_middle, DisplayActivity.y_middle);
		canvas.rotate(DisplayActivity.arrowRotate);//canvas.rotate(360 - DisplayActivity.arrowRotate);
		canvas.drawPath(arrow, paint);
		
		
		//canvas.translate(0, -200);
		//canvas.drawPath( arrow, filling_paint ) ;
		

		/*
		//canvas.translate( 150, 250 ) ;          // arrow tip to point (150, 250 )
	    canvas.drawPath( arrow, paint ) ; // draw solid arrow
	    
	    canvas.rotate( 45 ) ;                            // 45 degrees clockwise
	    canvas.drawPath( arrow,  outline_paint) ; // draw a hollow arrow
	    canvas.translate( 0, -200 ) ;                    // flying "up" 200 points
	    canvas.drawPath( arrow, paint ) ;
	    
	    canvas.rotate( 45 ) ;                            // 45 degrees clockwise
	      //canvas.translate( 0, -200 ) ;   // flying "up" (i.e. to the right)
	      canvas.drawPath( arrow, paint ) ;

	      //canvas.translate( 0, -100 ) ;   // flying "up" 100 points
	      canvas.rotate( 90 ) ;                            // 90 degrees clockwise
	      canvas.scale( 1.5F, 1.5F ) ;      // magnify everything by 1.5
	      canvas.drawPath( arrow, outline_paint ) ; // draw a hollow arrow
	      canvas.translate( 0, -200 ) ;     // flying "up" (i.e. down) 300 points
	      canvas.drawPath( arrow, paint ) ;
	      */

	}
	/*
	protected void addTextView(View thing){
		thing.setVisibility(android.view.View.INVISIBLE);
		
	}
	
	protected void displayDistance(TextView tvDistance, double distance, float x, float y){
		tvDistance.setX(x);
		tvDistance.setY(y);
		tvDistance.setText("Distance between: " + distance + " meters.");
		tvDistance.setVisibility(android.view.View.VISIBLE);
	}
	
	protected void displaySenderBadge(QuickContactBadge badge, float x, float y){
		badge.setX(x);
		badge.setY(y);
		badge.setVisibility(android.view.View.VISIBLE);
	}
	*/
}
