package org.ftsolutions.schedulingsystem.Subjects;

import android.app.ListFragment;
import android.icu.text.ListFormatter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import org.ftsolutions.schedulingsystem.ActiveAndroidClasses.SubjectList_AA;
import org.ftsolutions.schedulingsystem.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andrewhamili on 9/14/17.
 */

public class SubjectlistFragment extends ListFragment{

    List<SubjectList_AA> productList= SubjectList_AA.getSubjects();

    String[] classcode;

    String[] subjDesc;

    String[] unit;

    ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String, String>>();

    SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        generateInventory();

        HashMap<String, String> map=new HashMap<String, String>();

        for(int i = 0; i< classcode.length; i++){

            map=new HashMap<String, String>();
            map.put("Classcode", classcode[i]);
            map.put("Subject Description", String.valueOf(subjDesc[i]));
            map.put("Unit", String.valueOf(unit[i]));

            data.add(map);
        }

        String[] from={"Classcode", "Subject Description", "Unit"};

        int[] to={R.id.lblClasscode, R.id.lblSubjDesc, R.id.lblUnit};

        adapter=new SimpleAdapter(getActivity(), data, R.layout.fragment_subjectlist, from, to);
        setListAdapter(adapter);


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void generateInventory(){

        ArrayList<String> classcode=new ArrayList<String>();
        ArrayList<String> subjDesc=new ArrayList<String>();
        ArrayList<String> unit=new ArrayList<String>();
        for(int i=0;i<productList.size();i++){

            classcode.add(productList.get(i).classcode);
            subjDesc.add(productList.get(i).subjDesc);
            unit.add(productList.get(i).unit);

            this.classcode =classcode.toArray(new String[0]);
            this.subjDesc =unit.toArray(new String[0]);
            this.unit=unit.toArray(new String[0]);
        }
    }
}
