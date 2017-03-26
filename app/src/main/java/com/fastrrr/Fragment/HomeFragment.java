package com.fastrrr.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fastrrr.R;
import com.fastrrr.Services.FloatingFullWindow;
import com.fastrrr.Services.FloatingWindow;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardView cardViewBrowser,cardViewWidget,cardViewActive,cardViewApps,cardViewTools,cardViewUrls;


    public void UIReference(View view)
    {
        cardViewBrowser= (CardView) view.findViewById(R.id.cardViewBrowser);
        cardViewWidget= (CardView) view.findViewById(R.id.cardViewWidget);
        cardViewActive= (CardView) view.findViewById(R.id.cardViewActive);
        cardViewApps= (CardView) view.findViewById(R.id.cardViewApps);
        cardViewTools= (CardView) view.findViewById(R.id.cardViewTools);
        cardViewUrls= (CardView) view.findViewById(R.id.cardViewUrls);
    }
    public void UIClick()
    {
        cardViewBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowserFragment browserFragment=new BrowserFragment();
                String backStateName =  browserFragment.getClass().getName();
                String fragmentTag = backStateName;

                FragmentManager manager = getFragmentManager();
                boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

                if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame, browserFragment, fragmentTag);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            }
        });
        cardViewTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolsFragment toolsFragment=new ToolsFragment();
                String backStateName =  toolsFragment.getClass().getName();
                String fragmentTag = backStateName;

                FragmentManager manager = getFragmentManager();
                boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

                if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null)
                {
                    //fragment not in back stack, create it.
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame, toolsFragment, fragmentTag);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            }
        });
        cardViewApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppsFragment appsFragment=new AppsFragment();
                String backStateName =  appsFragment.getClass().getName();
                String fragmentTag = backStateName;

                FragmentManager manager = getFragmentManager();
                boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

                if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frame, appsFragment, fragmentTag);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(backStateName);
                    ft.commit();
                }
            }
        });
        cardViewUrls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startService(new Intent(getActivity(),FloatingFullWindow.class));
            }
        });
    }
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
