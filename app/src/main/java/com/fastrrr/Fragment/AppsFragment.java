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
import com.fastrrr.Services.FloatingAppFacebook;
import com.fastrrr.Services.FloatingAppForbes;
import com.fastrrr.Services.FloatingAppGmail;
import com.fastrrr.Services.FloatingAppInsta;
import com.fastrrr.Services.FloatingAppTranslate;
import com.fastrrr.Services.FloatingAppTwitter;
import com.fastrrr.Services.FloatingAppWikipedia;
import com.fastrrr.Services.FloatingAppYoutube;

public class AppsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardView cardViewFacebook,cardViewMaps,cardViewTwitter,cardViewInstagram,cardViewForbes;
    private CardView cardViewGmail,cardViewWikipedia,cardViewGoogle,cardViewTranslate,cardViewYoutube;

    public void UIReference(View view)
    {
        cardViewFacebook= (CardView) view.findViewById(R.id.cardViewFacebook);
        cardViewMaps= (CardView) view.findViewById(R.id.cardViewMaps);
        cardViewTwitter= (CardView) view.findViewById(R.id.cardViewTwitter);
        cardViewInstagram= (CardView) view.findViewById(R.id.cardViewInstagram);
        cardViewForbes= (CardView) view.findViewById(R.id.cardViewForbes);

        cardViewGmail= (CardView) view.findViewById(R.id.cardViewGmail);
        cardViewWikipedia= (CardView) view.findViewById(R.id.cardViewWikipedia);
        cardViewGoogle= (CardView) view.findViewById(R.id.cardViewGoogle);
        cardViewTranslate= (CardView) view.findViewById(R.id.cardViewTranslate);
        cardViewYoutube= (CardView) view.findViewById(R.id.cardViewYoutube);
    }
    public void UIClick()
    {
        cardViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppFacebook.class));
            }
        });
        cardViewMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().startService(new Intent(getActivity(),FloatingAppFacebook.class));
            }
        });
        cardViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppTwitter.class));
            }
        });
        cardViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppInsta.class));
            }
        });
        cardViewForbes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppForbes.class));
            }
        });
        cardViewGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppGmail.class));
            }
        });
        cardViewWikipedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppWikipedia.class));
            }
        });
        cardViewGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().startService(new Intent(getActivity(),FloatingAppForbes.class));
            }
        });
        cardViewTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppTranslate.class));
            }
        });
        cardViewYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingAppYoutube.class));
            }
        });
    }

    public AppsFragment() {
        // Required empty public constructor
    }

    public static AppsFragment newInstance(String param1, String param2) {
        AppsFragment fragment = new AppsFragment();
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
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
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
        void onFragmentInteraction(Uri uri);
    }
}
