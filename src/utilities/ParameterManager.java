package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Memo Akten on 16/06/2015.
 */

public class ParameterManager {

    // TODO: make private
    public Map<String, DoubleWithRange> param_map = new HashMap<String, DoubleWithRange>(); // see initParams

    // add a parameter
    public void add(String name, double default_value, double min, double max) {
        param_map.put(name, new DoubleWithRange(name, default_value, min, max));
    }

    // get parameter as int
    public int getInt(String name) {
        return param_map.get(name).getInt();
    }

    // get parameter as double
    public double getDouble(String name) {
        return param_map.get(name).getDouble();
    }

    // randomize all parameters
    public void randomize() {
//        System.out.println("ParameterManager::randomize");
        for (DoubleWithRange param : param_map.values()) {
            param.randomize();
//            System.out.println(param.toString());
        }
    }

    // reset all parameters to default
    public void reset() {
//        System.out.println("ParameterManager::reset");
        for (DoubleWithRange param : param_map.values()) {
            param.reset();
//            System.out.println(param.toString());
        }
    }
/*
    public void getData(ArrayList<String> names, ArrayList<double> values) {

        int i=0;
        for (Map.Entry<String, DoubleWithRange> entry : param_map.entrySet()) {
            names.add( entry.getKey() );
            values.add( entry.getValue().getDouble() );
        }
    }
    */
}



