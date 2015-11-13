package co.touchlab.touchkit.rk.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.step.Step;

/**
 * TODO Consume "onBackPressed" in activity. -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 * Implement method on {@link MultiStateScene}. If current {@link Scene} is 0, and back is pressed,
 * allow activity to swap to previous step. Else, let {@link MultiStateScene} go back a {@link Scene}.
 */

public abstract class MultiStateScene extends Scene
{

    private SceneAnimator animator;

    public MultiStateScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public void onPreInitialized()
    {
        super.onPreInitialized();
        animator = new SceneAnimator(this);
    }

    @Override
    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.fragment_step_multi_scene, parent, true);
    }

    @Override
    public void onSceneCreated(View scene)
    {
        int current = getCurrentPosition();
        showScene(current, false);
    }

    public int getCurrentPosition()
    {
        return getTag() == null ? 0 : (int) getTag();
    }

    public void setCurrentPosition(int pos)
    {
        setTag(pos);
    }

    public abstract int getSceneCount();

    public abstract Scene onCreateScene(LayoutInflater inflater, int scenePos);

    /**
     * This method is responsible for telling any subclasses that this scene was just popped off
     * the view stack and a new one is being added shorty. If you need to get the result and save
     * it, do tha tnow.
     */
    public void onSceneRemoved(Scene scene) { }

    public void showScene(int position, boolean withAnimation)
    {
        // If the count is 0, crash!
        if(getSceneCount() < 1)
        {
            throw new IllegalStateException("Scene count cannot be 0");
        }

        // If the view is animating, ignore that the request was made
        if (animator.isAnimating())
        {
            return;
        }

        int direction = getCurrentPosition() < position ?
                SceneAnimator.SHIFT_LEFT :
                SceneAnimator.SHIFT_RIGHT ;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        Scene oldScene = (Scene) findViewById(R.id.current_child_scene);
        if (oldScene != null) { oldScene.setId(R.id.old_child_scene);}

        Scene newScene = onCreateScene(inflater, position);
        newScene.setId(R.id.current_child_scene);
        newScene.setCallbacks(getCallbacks());

        if (withAnimation && oldScene != null)
        {
            animator.animate(oldScene, newScene, direction);
        }
        else
        {
            animator.setIsAnimating(false);

            removeAllViews();
            addView(newScene);
        }

        if (oldScene != null)
        {
            onSceneRemoved(oldScene);
        }
        
        // Save the current position
        setCurrentPosition(position);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() >= 2) {
            throw new IllegalStateException("Can't add more than 2 views to a " + MultiStateScene.class.getSimpleName());
        }
        super.addView(child, index, params);
    }

    public void loadNextScene()
    {
        int currentScene = getCurrentPosition();
        showScene(currentScene + 1 , true);
    }

    public void loadPreviousScene()
    {
        int currentScene = getCurrentPosition();
        showScene(currentScene - 1 , true);
    }

}