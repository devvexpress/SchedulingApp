package org.ftsolutions.schedulingsystem.ActiveAndroidClasses;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Andrew on 2017-09-15.
 */

public class LoadList_AA extends Model {

    public String classcode;

    public String subjectDesc;

    public String days;

    public String timeFrom;

    public String timeTo;

    public static void insert(LoadList_AA param){

        LoadList_AA obj=new LoadList_AA();

        obj.classcode=param.classcode;
        obj.subjectDesc=param.subjectDesc;
        obj.timeFrom=param.timeFrom;
        obj.timeTo=param.timeTo;

        obj.save();

    }

    public static List<LoadList_AA> getLoad(int userId){

        return new Select()
                .from(LoadList_AA.class)
                .where("userId = ?", userId)
                .execute();

    }

}
