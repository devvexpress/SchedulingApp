package org.ftsolutions.schedulingsystem.ActiveAndroidClasses;

import android.support.annotation.ColorInt;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.ftsolutions.schedulingsystem.GsonModels.SubjectList;

import java.util.List;

/**
 * Created by Andrew on 2017-09-15.
 */
@Table(name="SubjectList")
public class SubjectList_AA extends Model{

    @Column(name="classcode")
    public String classcode;

    @Column(name="subjDesc")
    public String subjDesc;

    @Column(name="unit")
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
    public static void clean(){
        ActiveAndroid.execSQL("DELETE * FROM SubjectList;");
    }

}
