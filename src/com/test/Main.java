package com.test;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        // ASSIGN CELL DATA HASHMAP TO NEW HASHMAP // TO HELP RETRIEVE MAP KEYS
        HashMap<String, HashMap<String,Integer>> getCellDataMap = retrieveCellData();
        HashMap<String, HashMap<String,Integer>> calculatedCellData = permuteCellData(getCellDataMap);
        HashMap<String, HashMap<String,Integer>> results = SetFrequency(calculatedCellData);
        System.out.println("Final Results: " + results);
    }

    private static String[] getCellKeys() {
        // CREATE NEW ARRAYLIST, DYNAMICALLY ADD STRINGS
        ArrayList<String> locationsCells = new ArrayList<>();

        // ASSIGN CELL DATA HASHMAP TO NEW HASHMAP // TO HELP RETRIEVE MAP KEYS
        HashMap<String, HashMap<String,Integer>> cellDataMap = retrieveCellData();

        // RETRIEVE MAP KEYS, ADD THEM TO ARRAYLIST
        for (Map.Entry<String, HashMap<String,Integer>> entry : cellDataMap.entrySet()) {
            String v = entry.getKey();

            locationsCells.add( v );
        }
        // CONVERT ARRAY LIST TO ARRAY
        String[] arr = new String[locationsCells.size()];
        String[] locationsCellsArray = locationsCells.toArray(arr);
        return locationsCellsArray;

    }

    private static HashMap<String, HashMap<String,Integer>> permuteCellData(HashMap<String, HashMap<String, Integer>> cellDataMap) {

        // RETRIEVE ARRAY WITH KEY LIST
        String[] locationsCellsArray = getCellKeys();

        // CREATE NEW MAP, TO MAP CALCULATED DISTANCES ON EACH CELL
        HashMap<String, HashMap<String,Integer>> calculateDistancesHashMap = new HashMap<>();

        // ITERATE THROUGH KEY ARRAY
        for (int i = 0; i < locationsCellsArray.length; i++) {

            // CHECK IF THERE IS DATA, IF NOT, ADD KEYS AT ROOT LEVEL
            HashMap<String, Integer> inner = calculateDistancesHashMap.get(locationsCellsArray[i]);
            inner = new HashMap<String, Integer>();
            calculateDistancesHashMap.put(locationsCellsArray[i], inner);

            // SEMI PERMUTE ALL CELL KEYS
            for (int j = 0; j < locationsCellsArray.length; j++) {
                String innerKey = locationsCellsArray[i] + locationsCellsArray[j];
                //avoid i.e aa, bb, cc
                if (locationsCellsArray[i] != locationsCellsArray[j] ) {
                    // get inner value of first cell
                    HashMap<String, Integer> innerFirstCell = cellDataMap.get(locationsCellsArray[i]);
                    // get inner value of every other cell
                    HashMap<String, Integer> innerNthCell = cellDataMap.get(locationsCellsArray[j]);
                    inner.put(innerKey, calculateDistance(innerFirstCell, innerNthCell));
                }

            }

        }
        return calculateDistancesHashMap;
    }

    private static HashMap<String, HashMap<String,Integer>> retrieveCellData() {
        // build your data // could source from external data source also
        char c  = 'a';
        int[][] array2D = {{536660,183800},{537032, 184006},{537109,183884}};
        HashMap<String, HashMap<String,Integer>> dictionary = new HashMap<>();
        for (int i = 0; i < array2D.length; i++) {
            HashMap<String, Integer> inner = new HashMap<String, Integer>();
            dictionary.put(Character.toString(c), inner);
            c++;
            for(int j = 0; j < array2D[i].length; j++) {
                inner.put("Northing", array2D[i][1]);
                inner.put("Easting", array2D[i][0]);
            }
        }
        return dictionary;
    }

    private static Integer calculateDistance(HashMap<String, Integer> innerFirstCell, HashMap<String, Integer> innerNthCell) {
        int Ay = innerFirstCell.get("Northing");
        int Ax = innerFirstCell.get("Easting");

        int By = innerNthCell.get("Northing");
        int Bx = innerNthCell.get("Easting");

        int Dx = Bx - Ax;
        int Dy = By - Ay;

        int r = (int) (Math.sqrt(Dx ^ 2) + (Dy ^ 2));

        return r;
    }

    private static int findLargestDistancePerCell(HashMap<String, HashMap<String, Integer>> map, String x){

        HashMap<String, Integer> largestPick = new HashMap<>();
        String[] keys = getCellKeys();

        // grab all key,values containing I
        for (int i = 0; i < keys.length; i++) {
            HashMap<String, Integer> perCellData = map.get(keys[i]);
                for (Map.Entry<String, Integer> entry : perCellData.entrySet()) {
                    String entryKey = entry.getKey();
                    if (entryKey.contains(x)) {
                        largestPick.put(entryKey, entry.getValue());
                    }
                }
        }

        // get the highest value for I
        Map.Entry<String, Integer> maxEntry = getLargestMapEntry(largestPick);

        // determine what frequency to set based on KEY i.e AB
        String largestDistanceSiblingCellID = getLargestDistanceSiblingCellID(x, maxEntry);

        // get frequency value of sibling sell
        HashMap<String, Integer> perCellData = map.get(largestDistanceSiblingCellID);
        Integer getFrequency = perCellData.get("Frequency Unit");
        return getFrequency;
    }

    private static String getLargestDistanceSiblingCellID(String x, Map.Entry<String, Integer> maxEntry) {
        String getLargestDistanceSiblingCell = maxEntry.getKey();
        return getLargestDistanceSiblingCell.replace(x,"");
    }

    private static Map.Entry<String, Integer> getLargestMapEntry(HashMap<String, Integer> largestPick) {
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : largestPick.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry;
    }

    private static HashMap<String, HashMap<String, Integer>> SetFrequency(HashMap<String, HashMap<String, Integer>> data) {
      //set frequency and remove from set
        String[] keys = getCellKeys();
        // ITERATE THROUGH KEY ARRAY
        int MaxFrequency = 1;
        int index = 0;
        for (int i = 0; i < keys.length; i++) {
            HashMap<String, Integer> perCellData = data.get(keys[i]);

            if (i <= MaxFrequency) {
                perCellData.put("Frequency Unit", i + 1);
            }
            else {
                // start sharing frequencies
                // CHECK IF THERE IS DATA, IF NOT, ADD KEYS AT ROOT LEVEL
                Integer getFrequency = perCellData.get("Frequency Unit");
                // find highest distance for each
                if (getFrequency == null) {
                    int largestNumber = findLargestDistancePerCell(data, keys[i]);
                    //convert to string and take second parameter
                    perCellData.put("Frequency Unit", largestNumber);
                }
            }
        }

        return data;
    }

}

