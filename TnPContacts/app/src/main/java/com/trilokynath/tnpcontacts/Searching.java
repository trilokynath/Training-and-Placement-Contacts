package com.trilokynath.tnpcontacts;

import java.util.*;
import java.util.regex.Pattern;

public class Searching{

    public ArrayList<HR> onSearch(ArrayList<HR> hrList,String search){

        ArrayList<DATA> add = new ArrayList<>();

        search = search.toLowerCase();

        Pattern p = Pattern.compile("\\s*(\\s|-|,)\\s*");

        //searching by city
        int index = 0;
        for(HR hr : hrList) {
            add.add(new DATA(index, hr.getCity().toLowerCase(), hr.getName().toLowerCase(), hr.getCompany().toLowerCase()));
            index++;
        }

        String input[] = p.split(search);

        index=0;
        for(DATA data : add){
            String mainAdd[] = p.split(data.address);
            String[] name = p.split(data.name);
            String[] company = p.split(data.company);

            //search by city
            for(String s : mainAdd){
                for (String anInput : input) {
                    if (s.contains(anInput)) {
            //            System.out.println("accuracy : "+data.accuracy);
                        data.accuracy++;
                        add.set(index, data);
                    }
                }
            }

            //search by name
            for(String s : name){
                for (String anInput : input) {
                    if (s.contains(anInput)) {
            //            System.out.println("accuracy : "+data.accuracy);
                        data.accuracy++;
                        add.set(index, data);
                    }
                }
            }

            //search by company
            for(String s : company){
                for (String anInput : input) {
                    if (s.contains(anInput)) {
                        data.accuracy++;
                        add.set(index, data);
                    }
                }
            }

            index++;
        }



        Collections.sort(add, new Comparator<DATA>(){
            @Override
            public int compare(DATA data, DATA t1) {
                return (t1.accuracy-data.accuracy);
            }
        });

    //    for(DATA d : add) {
    //        System.out.println("Address: '"+d.address+"'\tAccuracy: '"+d.accuracy+"'");
    //    }

        ArrayList<HR> newhrList = new ArrayList<>();
        index = 0;
        for(DATA data : add){
        //    Log.d("DATA", data.name+" Accuracy:"+data.accuracy);
            if(data.accuracy>0) {
                newhrList.add(hrList.get(data.ID));
                index++;
            }
        }
        return newhrList;
    }

}
class DATA{
    int ID;
    String address;
    int accuracy = 0;
    String name;
    String company;

    DATA(int ID, String address,String name, String company){
        this.address = address;
        this.ID = ID;
        this.name = name;
        this.company = company;
    }
}