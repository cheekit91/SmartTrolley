package com.example.cheekit.group_1111_ee6765_iot;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cheekit on 11/16/2017.
 */

public class ItemInfo extends Fragment{

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    Bundle options;
    NfcAdapter.ReaderCallback listener;
    int READER_FLAGS;

    Button buttonScanId;
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
//    NfcAdapter.ReaderCallback callback;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.iteminfo, container,false);
        mTextView = (TextView) view.findViewById(R.id.itemoutput);

        buttonScanId = (Button) view.findViewById(R.id.scanId);

        // when pressed the button, restart nfc
        View.OnClickListener ActionScan = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                onResume();
            }
        };
        buttonScanId.setOnClickListener(ActionScan);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this.getActivity());
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this.getActivity(), "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        listener  = new NfcAdapter.ReaderCallback() {
            public void onTagDiscovered(Tag tag) {
                byte[] id =tag.getId();
                StringBuilder sb = new StringBuilder();
                for (byte b : id) {
                    sb.append(String.format("%02X ", b));
                }
                mTextView.setText("id:"+sb.toString());
            }
            public void onTagRemoved()
            {
                mTextView.setText("Removed");
            }
        };
        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {

            options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
            READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
            mNfcAdapter.enableReaderMode(this.getActivity(), listener, READER_FLAGS, null);
            mTextView.setText("Ok");
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mNfcAdapter.enableReaderMode(this.getActivity(), listener, READER_FLAGS, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableReaderMode(this.getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Item Information");
    }
}
