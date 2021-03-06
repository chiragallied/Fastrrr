package com.fastrrr.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastrrr.R;
import com.fastrrr.Services.FloatingCalculator;
import com.fastrrr.Services.FloatingCamera;
import com.fastrrr.Services.FloatingContactList;
import com.fastrrr.Services.FloatingDialer;
import com.fastrrr.Services.FloatingGallery;
import com.fastrrr.Services.FloatingMediaPlayer;
import com.fastrrr.Services.FloatingMusicPlayer;
import com.fastrrr.Services.FloatingNewVoiceRecorder;
import com.fastrrr.Services.FloatingNotes;
import com.fastrrr.Services.FloatingPDFViewer;
import com.fastrrr.Services.FloatingStopWatch;
import com.fastrrr.Services.FloatingTimer;
import com.fastrrr.Services.FloatingVoiceRecorder;

public class ToolsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardView cardViewStopwatch,cardViewCalculator,cardViewMusic,cardViewTimer,cardViewVideo,cardViewVoiceRecord;
    private CardView cardViewContacts,cardViewGallery,cardViewPdfView,cardViewNotes,cardViewDialer,cardViewCameraRecord;

    public void UIReference(View view)
    {
        cardViewStopwatch= (CardView) view.findViewById(R.id.cardViewStopwatch);
        cardViewCalculator= (CardView) view.findViewById(R.id.cardViewCalculator);
        cardViewMusic= (CardView) view.findViewById(R.id.cardViewMusic);
        cardViewTimer= (CardView) view.findViewById(R.id.cardViewTimer);
        cardViewVideo= (CardView) view.findViewById(R.id.cardViewVideo);
        cardViewVoiceRecord= (CardView) view.findViewById(R.id.cardViewVoiceRecord);
        cardViewContacts= (CardView) view.findViewById(R.id.cardViewContacts);
        cardViewGallery= (CardView) view.findViewById(R.id.cardViewGallery);
        cardViewPdfView= (CardView) view.findViewById(R.id.cardViewPdfView);
        cardViewNotes= (CardView) view.findViewById(R.id.cardViewNotes);
        cardViewDialer= (CardView) view.findViewById(R.id.cardViewDialer);
        cardViewCameraRecord= (CardView) view.findViewById(R.id.cardViewCameraRecord);
    }
    public void UIClick()
    {
        cardViewStopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingStopWatch.class));
            }
        });
        cardViewCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingCalculator.class));
            }
        });
        cardViewMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingMusicPlayer.class));
            }
        });

        cardViewTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingTimer.class));
            }
        });
        cardViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingMediaPlayer.class));
            }
        });
        cardViewVoiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingNewVoiceRecorder.class));
            }
        });
        cardViewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingContactList.class));
            }
        });

        cardViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().startService(new Intent(getActivity(),FloatingGallery.class));
            }
        });

        cardViewPdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingPDFViewer.class));
            }
        });
        cardViewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingNotes.class));
            }
        });

        cardViewDialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingDialer.class));
            }
        });
        cardViewCameraRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingCamera.class));
            }
        });
    }

    public ToolsFragment() {
        // Required empty public constructor
    }
    public static ToolsFragment newInstance(String param1, String param2) {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tools, container, false);
        UIReference(view);
        UIClick();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
