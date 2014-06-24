package net.londatiga.android;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently supports vertical 
 * and horizontal layout.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 */
//public class QuickAction extends PopupWindows implements OnDismissListener {
public class QuickAction implements OnDismissListener {
	
	protected Context mContext;
	protected PopupWindow mWindow;
	//protected View mRootView;
	protected Drawable mBackground = null;
	protected WindowManager mWindowManager;
	
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private ImageView mArrowLeft;
	private ImageView mArrowRight;
	private LayoutInflater mInflater;
	private LinearLayout mTrack;
	//private FrameLayout mTrack;
	private FrameLayout mScroller;
	private OnDismissListener mDismissListener;
	
//	private List<ActionItem> actionItems = new ArrayList<ActionItem>();
	
	private boolean mDidAction;
	
	private int mChildPos;
    private int mInsertPos;
    private int mAnimStyle;
    private int mOrientation;
    private int rootWidth=0;
    private int rootHeight=0;
    
    private boolean mArrowVerticalOrientatiom;
    //orientation
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    //animation
    public static final int ANIM_VERTICALARROW_GROW_FROM_LEFT = 1;
	public static final int ANIM_VERTICALARROW_GROW_FROM_RIGHT = 2;
	public static final int ANIM_VERTICALARROW_GROW_FROM_CENTER = 3;
	public static final int ANIM_VERTICALARROW_REFLECT = 4;
	public static final int ANIM_VERTICALARROW_AUTO = 5;
	
	public static final int ANIM_HORIZONTALARROW_GROW_FROM_TOP = 1;
	public static final int ANIM_HORIZONTALARROW_GROW_FROM_BOTTOM = 2;
	public static final int ANIM_HORIZONTALARROW_GROW_FROM_CENTER = 3;
	public static final int ANIM_HORIZONTALARROW_REFLECT = 4;
	public static final int ANIM_HORIZONTALARROW_AUTO = 5;
	//pos arrow
	
    /**
     * Constructor for default vertical layout
     * 
     * @param context  Context
     */
    public QuickAction(Context context) {
        this(context, VERTICAL,true);
    }

    /**
     * Constructor allowing orientation override
     * 
     * @param context    Context
     * @param orientation Layout orientation, can be vartical or horizontal
     */
    public QuickAction(Context context, int orientation, boolean arrowVerticalOrientation) {
       //super(context);
    	
    	mContext	= context;
		mWindow 	= new PopupWindow(context);

		mWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        
        mOrientation = orientation;
        mArrowVerticalOrientatiom = arrowVerticalOrientation;
        
        mInflater 	 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        if(mArrowVerticalOrientatiom){
			if (mOrientation == HORIZONTAL) {
				setRootViewId(R.layout.popup_horizontal_vertical_arrow);
			} else {
				setRootViewId(R.layout.popup_vertical_vertical_arrow);
			}
			mAnimStyle 	= ANIM_VERTICALARROW_AUTO;
        } else {
        	if (mOrientation == HORIZONTAL) {
				setRootViewId(R.layout.popup_horizontal_horizontal_arrow);
			} else {
				setRootViewId(R.layout.popup_vertical_horizontal_arrow);
			}
        	mAnimStyle 	= ANIM_HORIZONTALARROW_AUTO;
        }
        
        mChildPos 	= 0;
    }

//    /**
//     * Get action item at an index
//     * 
//     * @param index  Index of item (position from callback)
//     * 
//     * @return  Action Item at the position
//     */
//    public ActionItem getActionItem(int index) {
//        return actionItems.get(index);
//    }
    
	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootView	= (ViewGroup) mInflater.inflate(id, null);
		mTrack 		= (LinearLayout) mRootView.findViewById(R.id.tracks);
		//mTrack 		= (FrameLayout) mRootView.findViewById(R.id.tracks);
		
		if(mArrowVerticalOrientatiom){
			mArrowDown 	= (ImageView) mRootView.findViewById(R.id.arrow_down);
			mArrowUp 	= (ImageView) mRootView.findViewById(R.id.arrow_up);
		} else {
			mArrowLeft 	= (ImageView) mRootView.findViewById(R.id.arrow_left);
			mArrowRight 	= (ImageView) mRootView.findViewById(R.id.arrow_right);
		}

		mScroller	= (FrameLayout) mRootView.findViewById(R.id.scroller);
		
		//This was previously defined on show() method, moved here to prevent force close that occured
		//when tapping fastly on a view to show quickaction dialog.
		//Thanx to zammbi (github.com/zammbi)
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		setContentView(mRootView);
	}
	
	 public void setContentView(View root) {
			mRootView = root;
			
			mWindow.setContentView(root);
	}
	
	/**
	 * Set animation style
	 * 
	 * @param mAnimStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}
	
	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem(View view) {
		
		if (mOrientation == HORIZONTAL && mChildPos != 0) {
            View separator = mInflater.inflate(R.layout.horiz_separator, null);
            
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
            
            separator.setLayoutParams(params);
            separator.setPadding(5, 0, 5, 0);
            
            mTrack.addView(separator, mInsertPos);
            
            mInsertPos++;
        }
		
		mTrack.addView(view, mInsertPos);
		
		mChildPos++;
		mInsertPos++;
	}
	
	public void setBackgroundDrawable(Drawable background) {
		mBackground = background;
	}
	
	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */
	public void show (View anchor) {
		//preShow();
		
		if (mRootView == null) 
			throw new IllegalStateException("setContentView was not called with a view to display.");
	
		if (mBackground == null) 
			mWindow.setBackgroundDrawable(new BitmapDrawable());
		else 
			mWindow.setBackgroundDrawable(mBackground);

		mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setTouchable(true);
		mWindow.setFocusable(true);
		mWindow.setOutsideTouchable(true);

		mWindow.setContentView(mRootView);	
		
		boolean flag;
		
		int xPos=0, yPos=0, arrowPos=0;

		mDidAction = false;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
		
		int dyTop;
		int dyBottom;
		int dxLeft;
		int dxRight;
		
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		int dH = (int) (15 * dm.scaledDensity);

		boolean onTop;
		boolean onLeft;
		
		//mRootView.setLayoutParams(new LayoutParams(rootWidth, rootHeight));
		
		if (mArrowVerticalOrientatiom) {
			
			mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			//mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			if(VERTICAL==mOrientation)
				mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.AT_MOST));
			else
				mRootView.measure(MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			//mRootView.measure(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			rootHeight = mRootView.getMeasuredHeight();
			rootWidth = mRootView.getMeasuredWidth();

			dyTop = anchorRect.top;
			dyBottom = screenHeight - anchorRect.bottom;
			dxLeft = anchorRect.right;
			dxRight = screenWidth - anchorRect.left;

			onTop = (dyTop > dyBottom) ? true : false;
			onLeft = (dxLeft > dxRight) ? true : false;
			
			rootHeight = mRootView.getMeasuredHeight();
			rootWidth = mRootView.getMeasuredWidth();

			if (rootWidth >= screenWidth) {
				xPos = 0;
				arrowPos = anchorRect.centerX();
				rootWidth=screenWidth;
			} else {
				if (onLeft) {
					if (rootWidth > dxLeft) {
						xPos = 0;
						arrowPos = anchorRect.centerX();
					} else {
						xPos = anchorRect.right - rootWidth;
						arrowPos = anchorRect.centerX() - xPos;
					}
				} else {
					xPos = anchorRect.left;

					if (rootWidth > dxRight) {
						xPos = screenWidth - 15 - rootWidth;
					}
					arrowPos = anchorRect.centerX() - xPos;
				}

			}
			if (onTop) {
				if (rootHeight > dyTop) {
					yPos = 0;
					LayoutParams l = mScroller.getLayoutParams();
					l.height = dyTop - (2*dH);
					rootHeight=dyTop;
				} else {
					yPos = anchorRect.top - rootHeight;
				}
			} else {
				yPos = anchorRect.bottom;

				if (rootHeight > dyBottom) {
					LayoutParams l = mScroller.getLayoutParams();
					l.height = dyBottom - 2*dH;
					rootHeight=dyBottom;
				}
			}
			
			
			

			showVerticalArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up),
					arrowPos);

			setAnimationStyleVerticalArrow(screenWidth, anchorRect.centerX(),
					onTop);

			mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
			mWindow.update(rootWidth, rootHeight);

			
		} else {
			
			if(VERTICAL==mOrientation)
				mRootView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.AT_MOST));
			else
				mRootView.measure(MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST),MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			//mRootView.measure(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			rootHeight = mRootView.getMeasuredHeight();
			rootWidth = mRootView.getMeasuredWidth();
			
			dyTop = anchorRect.bottom;
			dyBottom = screenHeight - anchorRect.top;
			dxLeft = anchorRect.left;
			dxRight = screenWidth - anchorRect.right;
			
			onTop = (dyTop > dyBottom) ? true : false;
			onLeft = (dxLeft > dxRight) ? true : false;

			
			
			// automatically get Y coord of popup (top left)
			
			if(rootHeight>=screenHeight) {
				yPos = 0;
				arrowPos = anchorRect.centerY();
				rootHeight=screenHeight;
			} else {
				if (onTop) {
					if (rootHeight > dyTop) {
						yPos = 0;
						arrowPos=anchorRect.centerY()-yPos;
					} else {
						yPos = (int) (anchorRect.bottom - rootHeight+0.5*dH);
						arrowPos=anchorRect.centerY()-yPos;
					}
				} else {
					yPos = anchorRect.top-dH;

					if (rootHeight > dyBottom) {
						yPos=screenHeight - rootHeight-dH;
						yPos=(yPos<0?0:yPos);						
					}
					arrowPos=anchorRect.centerY()-yPos;
				}
				
			}			
			
			if (onLeft) {
				if (rootWidth > dxLeft) {
					xPos = 0;					
					rootWidth = dxLeft;
				} else {
					xPos = anchorRect.left - rootWidth;
				}
				LayoutParams l = mScroller.getLayoutParams();
				//LayoutParams l = mTrack.getLayoutParams();
				l.width = rootWidth - (2 * dH);
			} else {
				xPos = anchorRect.right;

				if (rootWidth > dxRight) {
					LayoutParams l = mScroller.getLayoutParams();
					l.width = dxRight - (2 * dH);
					rootWidth=dxRight;
				}
			}

			showHorizontalArrow(((onLeft) ? R.id.arrow_right : R.id.arrow_left), (int) (arrowPos));//+ dY 

			setAnimationStyleHorizontalArrow(screenWidth, anchorRect.centerY(), onLeft);

			mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);//- dY+ dY+dY
			mWindow.update(rootWidth, rootHeight);

		}
		
	}
	
	/**
	 * Set animation style
	 * 
	 * @param screenWidth screen width
	 * @param requestedX distance from left edge
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
	 * 		  and vice versa
	 */
	private void setAnimationStyleVerticalArrow(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		switch (mAnimStyle) {
		case ANIM_VERTICALARROW_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;
					
		case ANIM_VERTICALARROW_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;
					
		case ANIM_VERTICALARROW_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;
			
		case ANIM_VERTICALARROW_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
		break;
		
		case ANIM_VERTICALARROW_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			}
					
			break;
		}
	}
	
	private void setAnimationStyleHorizontalArrow(int screenHeight, int requestedY, boolean onLeft) {
		int arrowPos = requestedY - mArrowLeft.getMeasuredHeight()/2;

		switch (mAnimStyle) {
		case ANIM_HORIZONTALARROW_GROW_FROM_TOP:
			mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Left : R.style.Animations_PopRightMenu_Left);
			break;
					
		case ANIM_HORIZONTALARROW_GROW_FROM_BOTTOM:
			mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Right : R.style.Animations_PopRightMenu_Right);
			break;
					
		case ANIM_HORIZONTALARROW_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Center : R.style.Animations_PopRightMenu_Center);
		break;
			
		case ANIM_HORIZONTALARROW_REFLECT:
			mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Reflect : R.style.Animations_PopRightMenu_Reflect);
		break;
		
		case ANIM_HORIZONTALARROW_AUTO:
			if (arrowPos <= screenHeight/4) {
				mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Left : R.style.Animations_PopRightMenu_Left);
			} else if (arrowPos > screenHeight/4 && arrowPos < 3 * (screenHeight/4)) {
				mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Center : R.style.Animations_PopRightMenu_Center);
			} else {
				mWindow.setAnimationStyle((onLeft) ? R.style.Animations_PopLeftMenu_Right : R.style.Animations_PopRightMenu_Right);
			}
					
			break;
		}
	}
	
	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	private void showVerticalArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
       
        param.leftMargin = requestedX - arrowWidth / 2;
        
        hideArrow.setVisibility(View.INVISIBLE);
    }
	
	private void showHorizontalArrow(int whichArrow, int requestedY) {
        final View showArrow = (whichArrow == R.id.arrow_left) ? mArrowLeft : mArrowRight;
        final View hideArrow = (whichArrow == R.id.arrow_left) ? mArrowRight : mArrowLeft;

        final int arrowHeight = mArrowLeft.getMeasuredHeight();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
       
        param.topMargin = requestedY - arrowHeight / 2;
        
        hideArrow.setVisibility(View.INVISIBLE);
    }
	
	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 * 
	 * 
	 */
	
	/**
	 * Set content view.
	 * 
	 * @param layoutResID Resource id
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * Set listener on window dismissed.
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mWindow.setOnDismissListener(listener);  
	}

	/**
	 * Dismiss the popup window.
	 */
	public void dismiss() {
		mWindow.dismiss();
	}
	
	public void setOnDismissListener(QuickAction.OnDismissListener listener) {
		setOnDismissListener(this);
		
		mDismissListener = listener;
	}
	
	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}
	
	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction source, int pos, int actionId);
	}
	
	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
}