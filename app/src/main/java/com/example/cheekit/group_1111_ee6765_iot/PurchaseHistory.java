package com.example.cheekit.group_1111_ee6765_iot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheekit on 11/16/2017.
 */
public class PurchaseHistory extends Fragment{
    String items=new String();
    String commandType = new String();
    String commandMsg = new String();
    Adapter myAdapter= new Adapter();
    Spinner monthInput;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view =inflater.inflate(R.layout.purchasehistory, container,false);
        monthInput = (Spinner) view.findViewById(R.id.monthspinner);

        monthInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                commandType="getPurchaseHistory";
                commandMsg=String.valueOf(position+1);
                try {
                    Object result = new SendPostRequest().execute().get();
                }
                catch(Exception e){
                }
//                Toast.makeText(getActivity().getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(myAdapter);

        return view;
    }

    // send the request to control the oled
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://ec2-34-204-93-26.compute-1.amazonaws.com/");  // set the url to the board's current url
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("type", commandType);
                postDataParams.put("msg", commandMsg);
                postDataParams.put("userid",((MainActivity)getActivity()).getUserId());
                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(7000 /* milliseconds */);
                conn.setConnectTimeout(7000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(postDataParams.toString());
                wr.flush();
                wr.close();
                os.close();

                // the input message from the board
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                items = sb.toString();
                return items;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
        // set the response message and display it
        @Override
        protected void onPostExecute(String result) {
            String[] splited=result.split(";");
            myAdapter.clearList();
            if(splited.length>1){
                for(int i=0;i<splited.length;i++) {
                    String[] splited2=splited[i].split(",");
                    myAdapter.addItemToList(splited2[1]);
                    myAdapter.addItemToList2(splited2[0]);
                }
            }
            myAdapter.updateCount();
            myAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Purchase History");
    }

    static private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private int COUNT = 0;
        private int[] itemsOffset = new int[30];

        void clearList(){
            arrayList.clear();
            arrayList2.clear();
        }
        private List<String> arrayList = new ArrayList<String>();
        String getItem(int position){
            return arrayList.get(position);
        }
        void addItemToList(String item){
            arrayList.add(item);
        }
        void removeItemToList(int position){
            arrayList.remove(position);
        }
        void updateCount(){
            COUNT=arrayList.size();
        }


        private List<String> arrayList2 = new ArrayList<String>();
        String getItem2(int position){
            return arrayList2.get(position);
        }
        void addItemToList2(String item){
            arrayList2.add(item);
        }
        void removeItemToList2(int position){
            arrayList2.remove(position);
        }
        @Override
        public int getItemViewType(int position) {
            return position % 3;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutId = R.layout.list_item_right;
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            final ViewHolder viewHolder = new ViewHolder(itemView);

            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.swipeLayout.animateReset();
                }
            };

//            if (viewHolder.leftView != null) {
//                viewHolder.leftView.setClickable(true);
//                viewHolder.leftView.setOnClickListener(onClick);
//            }

            if (viewHolder.rightView != null) {
                viewHolder.rightView.setClickable(true);
                viewHolder.rightView.setOnClickListener(onClick);
            }

            viewHolder.swipeLayout.setOnClickListener(new SwipeLayout.OnClickListener(){
              public void onClick(View swipeLayout){
//                  viewHolder.textViewPos.setText("NA");
              }
            });
//            viewHolder.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
//                @Override
//                public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {
//                }
//
//                @Override
//                public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
//                    Toast.makeText(swipeLayout.getContext(),
//                            (moveToRight ? "Left" : "Right") + " clamp reached",
//                            Toast.LENGTH_SHORT)
//                            .show();
//                    viewHolder.textViewPos.setText("TADA!");
//                    arrayList.remove(viewHolder.textViewPos.getText());
//
//                }
//
//                @Override
//                public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
//                }
//
//                @Override
//                public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
//                }
//            });

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(position>=arrayList.size()) {
                holder.textViewPos.setText("NA");
                holder.additional_info.setText("NA");
            }
            else {
                holder.textViewPos.setText(arrayList.get(position));
                holder.additional_info.setText(arrayList2.get(position));
            }
            holder.swipeLayout.setOffset(itemsOffset[position]);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                itemsOffset[holder.getAdapterPosition()] = holder.swipeLayout.getOffset();
            }
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return COUNT;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewPos;
            private final TextView additional_info;
            private final SwipeLayout swipeLayout;
            private final View rightView;
            private final View leftView;

            ViewHolder(View itemView) {
                super(itemView);
                textViewPos = (TextView) itemView.findViewById(R.id.text_view_pos);
                additional_info = (TextView) itemView.findViewById(R.id.text_add_info);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe_layout);
                rightView = itemView.findViewById(R.id.right_view);
                leftView = itemView.findViewById(R.id.left_view);
            }
        }
    }
}