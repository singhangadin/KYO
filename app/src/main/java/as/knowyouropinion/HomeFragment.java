package as.knowyouropinion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import as.knowyouropinion.data.model.HomeQuestionData;
import as.knowyouropinion.utils.OnRecyclerClickListener;
import as.knowyouropinion.utils.RecyclerTouchHelper;

/**<p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class HomeFragment extends Fragment implements OnRecyclerClickListener {
    private Context context;
    private ArrayList<HomeQuestionData> homeQuestionData;
    private TreeMap<Integer, Integer> questionMapper;
    private HomeListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        homeQuestionData = new ArrayList<>();
        questionMapper = new TreeMap<>();
        adapter = new HomeListAdapter(homeQuestionData, context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference questions = database.getReference("Questions");
        DatabaseReference answers = database.getReference("Answers");
        DatabaseReference images = database.getReference("Images");
        questions.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                if(questionMapper.containsKey(quesNo))
                {   HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setQuestion(dataSnapshot.getValue().toString());
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
                else
                {   HomeQuestionData data = new HomeQuestionData();
                    data.setQuestion(dataSnapshot.getValue().toString());
                    data.setQuesNo(quesNo);
                    addElementToList(data);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                if(questionMapper.containsKey(quesNo))
                {   HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setQuestion(dataSnapshot.getValue().toString());
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        answers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                HashMap<String, Long> count = (HashMap<String, Long>) dataSnapshot.getValue();
                long total = 0;
                for(String key: count.keySet())
                {   total+=count.get(key);
                }
                if(questionMapper.containsKey(quesNo))
                {   HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setPeeps(total);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
                else
                {   HomeQuestionData data = new HomeQuestionData();
                    data.setQuesNo(quesNo);
                    data.setPeeps(total);
                    addElementToList(data);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                HashMap<String, Long> count = (HashMap<String, Long>) dataSnapshot.getValue();
                long total = 0;
                for(String key: count.keySet())
                {   total+=count.get(key);
                }
                if(questionMapper.containsKey(quesNo))
                {   HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setPeeps(total);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        images.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.e("TAG", "String:"+dataSnapshot.getKey() +" Value: "+dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.e("TAG", "String:"+dataSnapshot.getKey() +" Value: "+dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.e("TAG", "String:"+dataSnapshot.getKey() +" Value: "+dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        for(int i=0; i<10; i++)
//        {   HomeQuestionData data = new HomeQuestionData();
//            data.setPeeps(50);
//            data.setQuestion("Question No."+i);
//            data.setQuesNo(i);
//            homeQuestionData.add(data);
//        }
        RecyclerView homeList=(RecyclerView)view.findViewById(R.id.homeList);
        homeList.setLayoutManager(new LinearLayoutManager(context));
        homeList.setAdapter(adapter);
        RecyclerTouchHelper helper = new RecyclerTouchHelper(context);
        helper.setOnRecyclerClickListener(this);
        homeList.addOnItemTouchListener(helper);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public boolean onClick(View child, int position) {
        Intent intent=new Intent(context,QuizActivity.class);
        intent.putExtra("qno",homeQuestionData.get(position).getQuesNo());
        startActivity(intent);
        return false;
    }

    synchronized private void addElementToList(HomeQuestionData data)
    {   questionMapper.put(data.getQuesNo(),homeQuestionData.size());
        homeQuestionData.add(data);
    }
}
