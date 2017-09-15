package org.ftsolutions.schedulingsystem.ActiveAndroidClasses;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import org.ftsolutions.schedulingsystem.GsonModels.SubjectList;

import java.util.List;

/**
 * Created by Andrew on 2017-09-15.
 */

public class SubjectList_AA extends Model{

    public String classcode;

    public String subjDesc;

    public String unit;

    public static void insert(SubjectList_AA param){

        SubjectList_AA obj=new SubjectList_AA();

        obj.classcode=param.classcode;

        obj.subjDesc=param.subjDesc;

        obj.unit=param.unit;

        obj.save();

    }

    public static List<SubjectList_AA> getSubjectList(){

        return new Select()
                .from(SubjectList_AA.class)
                .execute();

    }

}
