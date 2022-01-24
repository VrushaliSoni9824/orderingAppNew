package com.tjcg.menuo.ExpandableList;

//
//public class ExpandableListDataItems1 {
//    public static HashMap<String, List<String>> getData(String jsonData) {
//        HashMap<String, List<String>> expandableDetailList = new HashMap<String, List<String>>();
//
//        // As we are populating List of fruits, vegetables and nuts, using them here
//        // We can modify them as per our choice.
//        // And also choice of fruits/vegetables/nuts can be changed
//
//        ArrayList<String> arrOdate = new ArrayList<>();
//        ArrayList<String> arrOId = new ArrayList<>();
//
//        try {
//            JSONObject jobj = new JSONObject(jsonData);
//            JSONArray jarrResult = jobj.getJSONArray("result");
////            for (order:jarrResult) {
////
////            }
//
//            for (int i=0;i<=jarrResult.length();i++)
//            {
//                JSONObject order =jarrResult.getJSONObject(i);
//                arrOdate.add(order.getString("delivery_datetime"));
//                arrOId.add(order.getString("id"));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        List<String> fruits = new ArrayList<String>();
//        fruits.add("Apple");
//        fruits.add("Orange");
//        fruits.add("Guava");
//        fruits.add("Papaya");
//        fruits.add("Pineapple");
//
//        List<String> vegetables = new ArrayList<String>();
//        vegetables.add("Tomato");
//        vegetables.add("Potato");
//        vegetables.add("Carrot");
//        vegetables.add("Cabbage");
//        vegetables.add("Cauliflower");
//
//        List<String> nuts = new ArrayList<String>();
//        nuts.add("Cashews");
//        nuts.add("Badam");
//        nuts.add("Pista");
//        nuts.add("Raisin");
//        nuts.add("Walnut");
//
//        // Fruits are grouped under Fruits Items. Similarly the rest two are under
//        // Vegetable Items and Nuts Items respecitively.
//        // i.e. expandableDetailList object is used to map the group header strings to
//        // their respective children using an ArrayList of Strings.
//
////        var orderDao: OrderDao = getDatabase(context!!)!!.orderDao()
//
//        expandableDetailList.put("Done", arrOId);
//        expandableDetailList.put("Inprogress Orders", arrOId);
//        expandableDetailList.put("Pending Orders", arrOId);
//
//
//        return expandableDetailList;
//    }
//}

