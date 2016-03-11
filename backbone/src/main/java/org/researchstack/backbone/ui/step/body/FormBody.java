package org.researchstack.backbone.ui.step.body;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class FormBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private FormStep               step;
    private StepResult<StepResult> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private List<StepBody> formStepChildren;

    public FormBody(Step step, StepResult result)
    {
        this.step = (FormStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        // Inflate our container for each compact child StepBody
        LinearLayout body = (LinearLayout) inflater.inflate(R.layout.step_layout_form_body,
                parent,
                false);

        List<QuestionStep> questionSteps = step.getFormSteps();
        formStepChildren = new ArrayList<>(questionSteps.size());

        // Iterate through all steps and generate each compact view. Store each StepBody child in a
        // list to iterate over (e.g. within getStepResult())
        for(QuestionStep questionStep : questionSteps)
        {
            StepBody stepBody = createStepBody(questionStep);
            View bodyView = stepBody.getBodyView(VIEW_TYPE_COMPACT, inflater, body);
            body.addView(bodyView);

            formStepChildren.add(stepBody);
        }

        return body;
    }

    @Override
    public StepResult getStepResult()
    {
        for(StepBody child : formStepChildren)
        {
            StepResult childResult = child.getStepResult();
            if(childResult != null)
            {
                this.result.setResultForIdentifier(childResult.getIdentifier(), childResult);
            }
        }

        return this.result;
    }

    @Override
    public BodyAnswer getBodyAnswerState()
    {
        for(StepBody formStepBody : formStepChildren)
        {
            BodyAnswer bodyAnswer = formStepBody.getBodyAnswerState();
            if(! bodyAnswer.isValid())
            {
                return bodyAnswer;
            }
        }

        return BodyAnswer.VALID;
    }

    @NonNull
    private StepBody createStepBody(QuestionStep questionStep)
    {
        StepResult childResult = result.getResultForIdentifier(questionStep.getIdentifier());

        try
        {
            Class cls = questionStep.getStepBodyClass();
            Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(questionStep, childResult);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}