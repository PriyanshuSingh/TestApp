package com.example.priyanshu.testapp;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Created by priyanshu on 21/3/15.
 */
@IBMDataObjectSpecialization("Item")
public class Item extends IBMDataObject {
    public static final String CLASS_NAME = "Item";
    public static final String NAME = "name";

    public String getName() {
        return (String) getObject(NAME);
    }

    public void setName(String itemName) {
        setObject(NAME, (itemName != null) ? itemName : "");
    }
}
