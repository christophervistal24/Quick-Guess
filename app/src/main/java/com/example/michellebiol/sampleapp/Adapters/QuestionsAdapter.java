package com.example.michellebiol.sampleapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.AnswerQuestion;
import com.example.michellebiol.sampleapp.Models.QuestionsItem;
import com.example.michellebiol.sampleapp.R;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private List<QuestionsItem> questionsItems;
    private Context context;

    public QuestionsAdapter(List<QuestionsItem> questionsItems, Context context) {
        this.questionsItems = questionsItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final QuestionsItem questionsItem = questionsItems.get(position);
        holder.textViewId.setText(questionsItem.getId());
        holder.textViewQuest.setText(questionsItem.getQuest());
        holder.textViewResult.setText(questionsItem.getQuestion_result());
        if (holder.textViewResult.getText().equals("wrong") || holder.textViewResult.getText().equals("correct"))
        {

        } else
        {
            holder.questionsLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , AnswerQuestion.class);
                    intent.putExtra("question_id",questionsItem.getId());
                    intent.putExtra("question",questionsItem.getQuest());
                    intent.putExtra("choice_a",questionsItem.getChoice_a());
                    intent.putExtra("choice_b",questionsItem.getChoice_b());
                    intent.putExtra("choice_c",questionsItem.getChoice_c());
                    intent.putExtra("choice_d",questionsItem.getChoice_d());
                    intent.putExtra("correct_answer",questionsItem.getCorrect_answer());
                    intent.putExtra("fun_facts",questionsItem.getFun_facts());
                    Toast.makeText(context, "Position :" + position, Toast.LENGTH_SHORT).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return questionsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewId;
        public TextView textViewQuest;
        public TextView textViewResult;
        public LinearLayout questionsLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewId = (TextView) itemView.findViewById(R.id.textViewId);
            textViewQuest = (TextView) itemView.findViewById(R.id.textViewQuest);
            textViewResult = (TextView) itemView.findViewById(R.id.textViewResult);
            questionsLinearLayout = (LinearLayout) itemView.findViewById(R.id.questionsLinearLayout);
        }
    }
}
