package as.knowyouropinion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
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
import java.util.HashSet;
import java.util.TreeMap;

import as.knowyouropinion.model.HomeListAdapter;
import as.knowyouropinion.model.HomeQuestionData;
import as.knowyouropinion.utils.OnRecyclerClickListener;
import as.knowyouropinion.utils.RecyclerTouchHelper;

/**
 * <p>
 * Created by Angad on 23/1/17.
 * </p>
 */

public class HomeFragment extends Fragment implements OnRecyclerClickListener {
    private Context context;
    private ArrayList<HomeQuestionData> homeQuestionData;
    private TreeMap<Integer, Integer> questionMapper;
    private HomeListAdapter adapter;
    private HashSet<Integer> doneKeys;
    private AppCompatTextView status;
    private RecyclerView homeList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        status = (AppCompatTextView) view.findViewById(R.id.status);
        homeList = (RecyclerView) view.findViewById(R.id.homeList);
        homeQuestionData = new ArrayList<>();
        questionMapper = new TreeMap<>();
        doneKeys = new HashSet<>();
        adapter = new HomeListAdapter(homeQuestionData, context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = preferences.getString("EMAIL", "Email");
        String userID = email.split("@")[0].replace(".", ",");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference questions = database.getReference("Questions");
        DatabaseReference answers = database.getReference("Answers");
        DatabaseReference images = database.getReference("Images");
        DatabaseReference user = database.getReference("Users").child(userID);

        user.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                if (key != null && !key.equals("null")) {
                    doneKeys.add(Integer.parseInt(key));
                }
                if (questionMapper.containsKey(Integer.parseInt(key))) {
                    int pos = questionMapper.get(Integer.parseInt(key));
                    homeQuestionData.remove(pos);
                    questionMapper.clear();
                    for (int i = 0; i < homeQuestionData.size(); i++) {
                        questionMapper.put(homeQuestionData.get(i).getQuesNo(), i);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (questionMapper.size() == 0 && doneKeys.size() != 0) {
                    homeList.setVisibility(View.INVISIBLE);
                    if (isAdded()) {
                        status.setText(getString(R.string.label_home_comp));
                        status.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

        questions.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                if (questionMapper.containsKey(quesNo)) {
                    HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setQuestion(dataSnapshot.getValue().toString());
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                } else if (!doneKeys.contains(quesNo)) {
                    HomeQuestionData data = new HomeQuestionData();
                    data.setQuestion(dataSnapshot.getValue().toString());
                    data.setQuesNo(quesNo);
                    addElementToList(data);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
                if (homeList.getVisibility() == View.INVISIBLE) {
                    homeList.setVisibility(View.VISIBLE);
                    status.setVisibility(View.INVISIBLE);
                }
                if (questionMapper.size() == 0 && doneKeys.size() != 0) {
                    homeList.setVisibility(View.INVISIBLE);
                    if (isAdded()) {
                        status.setText(getString(R.string.label_home_comp));
                    }
                    status.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                if (questionMapper.containsKey(quesNo)) {
                    HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
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
                if (isAdded()) {
                    status.setText(getString(R.string.label_home_dc));
                }
            }
        });

        answers.addChildEventListener(new ChildEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                HashMap<String, Long> count = (HashMap<String, Long>) dataSnapshot.getValue();
                long total = 0;
                for (String key : count.keySet()) {
                    total += count.get(key);
                }
                if (questionMapper.containsKey(quesNo)) {
                    HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setPeeps(total);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                } else if (!doneKeys.contains(quesNo)) {
                    HomeQuestionData data = new HomeQuestionData();
                    data.setQuesNo(quesNo);
                    data.setPeeps(total);
                    addElementToList(data);
                    adapter.notifyItemChanged(questionMapper.get(quesNo));
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int quesNo = Integer.parseInt(dataSnapshot.getKey());
                HashMap<String, Long> count = (HashMap<String, Long>) dataSnapshot.getValue();
                long total = 0;
                for (String key : count.keySet()) {
                    total += count.get(key);
                }
                if (questionMapper.containsKey(quesNo) && !doneKeys.contains(quesNo) && questionMapper.get(quesNo) < homeQuestionData.size()) {
                    HomeQuestionData data = homeQuestionData.get(questionMapper.get(quesNo));
                    data.setPeeps(total);
                    adapter.notifyQuestionIsVoted(questionMapper.get(quesNo));
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

        images.addChildEventListener(new ChildEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int i = Integer.parseInt(dataSnapshot.getKey());
                String link = dataSnapshot.getValue().toString();
                if (questionMapper.containsKey(i)) {
                    HomeQuestionData data = homeQuestionData.get(questionMapper.get(i));
                    data.setImgUrl(link);
                    adapter.notifyItemChanged(questionMapper.get(i));
                } else if (!doneKeys.contains(i)) {
                    HomeQuestionData data = new HomeQuestionData();
                    data.setQuesNo(i);
                    data.setImgUrl(link);
                    addElementToList(data);
                    adapter.notifyItemChanged(questionMapper.get(i));
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
        Intent intent = new Intent(context, QuizActivity.class);
        intent.putExtra("qno", homeQuestionData.get(position).getQuesNo());
        startActivity(intent);
        return false;
    }

    synchronized private void addElementToList(HomeQuestionData data) {
        questionMapper.put(data.getQuesNo(), homeQuestionData.size());
        homeQuestionData.add(data);
    }
}
