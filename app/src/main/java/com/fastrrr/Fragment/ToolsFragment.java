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
import com.fastrrr.Services.FloatingDialer;
import com.fastrrr.Services.FloatingMusicPlayer;
import com.fastrrr.Services.FloatingStopWatch;
import com.fastrrr.Services.FloatingWindow;

public class ToolsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardView cardViewStopwatch,cardViewCalculator,cardViewMusic;

    public void UIReference(View view)
    {
        cardViewStopwatch= (CardView) view.findViewById(R.id.cardViewStopwatch);
        cardViewCalculator= (CardView) view.findViewById(R.id.cardViewCalculator);
        cardViewMusic= (CardView) view.findViewById(R.id.cardViewMusic);
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
