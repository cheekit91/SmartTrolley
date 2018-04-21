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
import android.widget.TextView;

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
public class RecipeList extends Fragment{
    String items=new String();
    String commandType = new String();
    String commandMsg = new String();
    Adapter myAdapter= new Adapter();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view =inflater.inflate(R.layout.itemlist, container,false);
        commandType="getrecipe";
        commandMsg="";

        ((MainActivity)getActivity()).clearRecipeItemNameList();
        ((MainActivity)getActivity()).clearRecipeQuantityList();
        ((MainActivity)getActivity()).setRecipeName("");
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).displaySelectedScreen(R.id.linkRecipe);
            }
        });
        try{
            new SendPostRequest().execute().get();
        }
        catch(Exception e){
        }
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(myAdapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            commandType="removerecipe";
                            commandMsg=myAdapter.getItem(position);
                            new SendPostRequest().execute();
                            myAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            myAdapter.removeItemToList(position);  //then remove item
                            myAdapter.removeItemToList2(position);  //then remove item
                            myAdapter.updateCount();
                            myAdapter.notifyDataSetChanged();
                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            myAdapter.notifyItemRangeChanged(position, myAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                            myAdapter.notifyDataSetChanged();
                            return;
                        }
                    });
                    AlertDialog alert=builder.create();
                    alert.show();  //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler); //set swipe to recylcerview

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
            if(commandType=="getrecipe")
            {
                String[] splited=result.split(",");
                if(splited.length>1) {
                    for (int i = 0; i < splited.length; ) {
                        myAdapter.addItemToList(splited[i]);
                        myAdapter.addItemToList2(splited[i + 1]+" items");
                        i = i + 2;
                    }
                }
                myAdapter.updateCount();
                myAdapter.notifyDataSetChanged();
            }
        }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Recipe List");
    }

    static private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private int COUNT = 0;
        private int[] itemsOffset = new int[30];

        private List<String> arrayList = new ArrayList<String>();
        String getItem(int position){
            return arrayList.get(position);
        }
        void addItemToList(String item){
            arrayList.add(item);
        }
        void removeItemToList(String item){
            arrayList.remove(item);
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

            if (viewHolder.rightView != null) {
                viewHolder.rightView.setClickable(true);
                viewHolder.rightView.setOnClickListener(onClick);
            }

            viewHolder.swipeLayout.setOnClickListener(new SwipeLayout.OnClickListener(){
              public void onClick(View swipeLayout){
              }
            });

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